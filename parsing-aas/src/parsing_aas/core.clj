(ns parsing-aas.core
  (:use ring.adapter.jetty
        ring.middleware.params
        ring.middleware.stacktrace
        compojure.core
        [compojure.route :only [files]]
        [clojure.java.shell :only [sh]])
  (:require [clojure.java.io :as io]
            [clojure.core.cache :as cache])
  (:import java.util.UUID
           java.io.File
           org.treedecor.Parser))

(def config (atom {:s2t-exec         nil
                   :port             55123
                   :table-cache-size 20}))

(def table-cache (atom {}))

(defn uuid [] (.toString (java.util.UUID/randomUUID)))

(defmacro log [message & body]
  `(let [start# (System/nanoTime)
         ~'_ (println "Start" ~message "at" start#)
         ret# (do ~@body)
         end# (System/nanoTime)]
     (println "End" ~message "at" end# "Duration:" (/ (- end# start#) 1000000.0) "ms")
     ret#))

(defn get-table [grammar-hash]
  (get @table-cache grammar-hash))

(defn make-parser [tbl]
  (Parser. ^bytes tbl))

(defn parse [table-id stream do-not-annotate pretty-print]
  (log "parse request"
       (if-let [table (log "lookup table in cache" (get-table table-id))]
         (try
           (let [parse-result (log "actually parsing" (.parse ^Parser (log "create parser" (make-parser table)) ^java.io.InputStream stream))
                 maybe-annotated (if do-not-annotate
                                   parse-result
                                   (log "annotate source location information" (Parser/annotateSourceLocationInformation parse-result)))]
             (if pretty-print
               (log "pretty print" (Parser/prettyPrint maybe-annotated))
               (str maybe-annotated)))
           (catch Exception e
             {:status 400
              :body (str e)}))
         {:status 410 ; Gone
          :body "Please (re-)register your grammar or table."})))

(defmacro with-temp-file
  "Create a temporary file and delete it after executing `body`.
   You can refer to the temporary file with the symbol `name`.
   Note that `name` will be bound to a java.io.File object, call .getAbsolutePath to get it's path.
   The file will named prefixSomethingPostfix and be in the system's temp dir.
   Refer to java.io.File/createTempFile for details."
  [name prefix postfix & body]
  `(let [~name (java.io.File/createTempFile ~prefix ~postfix)]
     (try (do~@body)
        (finally (.delete ~name)))))

(defn sdf-to-table [def module]
  ;; HACK: We need to create a temporary file because the sdf2table version
  ;; from the native bundle is broken when using standard input
  ;; HACK2: We need to create a second temporary file for the output, because
  ;; apparently, standard output handling is broken as soon as you do not use
  ;; standard input but input from file. You couldn't make this stuff up...
  (with-temp-file def-tmp-file "def-tmp-file" ".def"
    (with-temp-file tbl-tmp-file "tbl-tmp-file" ".tbl"
      (spit def-tmp-file def)
      (let [ext-call (sh (or (:s2t-exec @config) "sdf2table")
                         "-m" module
                         "-i" (.getAbsolutePath def-tmp-file)
                         "-o" (.getAbsolutePath tbl-tmp-file))
            tbl (byte-array (.length tbl-tmp-file))]
        (with-open [reader (io/input-stream tbl-tmp-file)]
          (.read reader tbl))
        (assoc ext-call :out tbl)))))

(defn created-table-response [id]
  {:status 201 ; Created
   :headers {"Location" (str "/table/" id)}
   :body id})

(defn register-table [id tbl]
  (swap! table-cache assoc id tbl)
  id)

(defn register-grammar [in-stream module]
  (let [def (slurp in-stream)
        hash (str module (hash def))]
    (if (= "" def)
      {:status 400
       :body "No content. Fix your client. Try using Content-Type:application/x-sdf"}
      (if (get-table hash)
        (created-table-response hash)
        (let [ext-call (sdf-to-table def module)]
          (if (= (:exit ext-call) 0)
            (do
              (register-table hash (:out ext-call))
              (created-table-response hash))
            {:status 422
             :body (:err ext-call)}))))))

(defroutes handler
  (POST "/grammar" {body                                  :body
                    {module "module" :or {module "Main"}} :params}
        (register-grammar body module))
  (GET "/table" [] (apply str (interpose "\n" (keys @table-cache))))
  (ANY "/parse/:table-id" {body :body
                           {table-id :table-id
                            do-not-annotate "disableSourceLocationInformation"
                            pretty-print "prettyPrint"} :params}
       (parse table-id body (= do-not-annotate "true") (= pretty-print "true")))
  (POST "/table" {body :body} (register-table (uuid) body))
  (files "/" {:root "web"})
)

(def app
  (-> #'handler
      (wrap-params)
      (wrap-stacktrace)))

(defn -main [& args]
  (println "Config string:" args)
  (let [new-config (if args (read-string (first args)) {})]
    (swap! config merge new-config))
  (println "Effective config:" @config)
  (swap! table-cache #(cache/lru-cache-factory (:table-cache-size @config) %))
  (run-jetty #'app {:port (:port @config)}))

(comment
  (defonce server
    (run-jetty #'app {:port (:port @config)})))


;; install localrepo leiningen plugin:
;; put the following in .lein/profiles.clj
;; {:user {:plugins [
;;                   [lein-localrepo "0.4.0"]
;;                   [lein-swank "1.4.3"]
;;                   ]}}

;; install current parser to local repo using 
;; $ lein localrepo install `lein localrepo coords treedecor-parser-0.0.2.jar`

;; make sure to use proper content type for sending post stuff. wrap-params eats
;; form-url-encoded bodies as sent by web browsers. Not sure whether multipart params is needed
;; use e.g. this for testing (reads post data from stdin, use --data-binary @filename to read and send a file as is)

;; Register grammar
;;   curl -X POST -H 'Content-Type:application/x-sdf' --data-binary @xml.def http://127.0.0.1:8080/grammar?module=xml
;; Parse file
;;   curl -X GET -H 'Content-Type: application/binary' -d @/home/stefan/Work/treedecor/renderer.imp/plugin.xml http://127.0.0.1:8080/parse/-993726346
