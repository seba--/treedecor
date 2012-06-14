(ns parsing-aas.core
  (:use ring.adapter.jetty
        ring.middleware.params
        ring.middleware.stacktrace
        compojure.core
        [clojure.java.shell :only [sh]])
  (:require [clojure.java.io :as io]
            [clojure.core.cache :as cache])
  (:import java.util.UUID
           org.treedecor.Parser))

(def config (atom {:s2t-exec          "/usr/bin/sdf2table"
                   :port              "8080"
                   :parser-cache-size "20"}))

(def parser-cache (atom {}))

(defn uuid [] (.toString (java.util.UUID/randomUUID)))

(defn get-parser [grammar-hash]
  (get @parser-cache grammar-hash))

(defn parse [table-id stream]
  (if-let [parser (get-parser table-id)]
    (str (.parse ^Parser (get-parser table-id) ^java.io.InputStream stream))
    {:status 410 ; Gone
     :body "Please (re-)register your grammar or table."}))

(defn sdf-to-table
  ([def module]
     (sh (:s2t-exec @config) "-m" module :in def :out-enc :bytes)))

(defn make-parser [tbl]
  (Parser. tbl))

(defn register-table [id tbl]
  (swap! parser-cache assoc id (make-parser tbl))
  {:status 201 ; Created
   :headers {"Location" (str "/table/" id)}
   :body id})

(defn register-grammar [in-stream module]
  (let [def (slurp in-stream)
        hash (str (hash def))]
    (if (= "" def)
      {:status 400
       :body "No content. Fix your client. Try using Content-Type:application/x-sdf"}
      (if (get-parser hash)
        hash
        (register-table hash (:out (sdf-to-table def module)))))))

(defroutes handler
  (POST "/grammar" {body   :body
                    params :params} (register-grammar body (get "module" params "Main")))
  (GET "/parse/:table-id" {{table-id :table-id} :params
                           body :body} (parse table-id body))
  (POST "/table" {body :body} (register-table (uuid) body)))

(def app
  (-> #'handler
      (wrap-params)
      (wrap-stacktrace)))

(comment ;; use this instead of defonce for deployment
  (defn -main [& args]
    (swap! config merge (read-string (first args)))
    (swap! parser-cache #(cache/lru-cache-factory (Integer/parseInt (:parser-cache-size @config) 10) %))
    (reinit!)
    (run-jetty #'app {:port port})))

(swap! parser-cache #(cache/lru-cache-factory (Integer/parseInt (:parser-cache-size @config) 10) %))
(defonce server
  (run-jetty #'app {:port (Integer/parseInt (:port @config) 10)}))


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
;; $ curl -X POST -H'Content-Type: application/binary' -d @- http://127.0.0.1:8080/table/

;; post xml table (note uuid)
;; $ curl -X POST -H'Content-Type:application/binary' --data-binary @/home/stefan/Work/treedecor/syntax.xml/xml.tbl http://127.0.0.1:8080/table

;; parse xml document replace $TABLE with uuid noted before
;; $ curl -X GET -H'Content-Type: application/binary' -d @/home/stefan/Work/treedecor/renderer.imp/plugin.xml http://127.0.0.1:8080/parse/$TABLE
;; after warmup sub 20ms for project.xml from renderer.imp


;; Register grammar
;;   curl -X POST -H'Content-Type:application/x-sdf' --data-binary @xml.def http://127.0.0.1:8080/grammar?module=xml
;; Parse file (doesn't work... probably encoding stuff)
;;   curl -X GET -H'Content-Type: application/binary' -d @/home/stefan/Work/treedecor/renderer.imp/plugin.xml http://127.0.0.1:8080/parse/-993726346