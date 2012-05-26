(ns parsing-aas.core
  (:use ring.adapter.jetty
        ring.middleware.params
        ring.middleware.stacktrace
;        ring.middleware.multipart-params
        compojure.core
        )
  (:require [clojure.java.io :as io]
            [clojure.core.memoize :as memo])
  (:import java.util.UUID
           org.treedecor.Parser))

(def port 8080)
(def table-base-path "/tmp/parsing-aas/tables")
(def parser-cache-size 10)

(defn uuid [] (.toString (java.util.UUID/randomUUID)))

(defn table-file [name] (io/file table-base-path (str name ".tbl")))

(defn create-table! [stream]
  (let [name (uuid)]
    (io/copy stream (table-file name))
    name))

(defn delete-table! [name]
  (io/delete-file (table-file name))
  (str "Deleted table " name))

(defn delete-all-tables! []
  (doseq [f (file-seq (io/file table-base-path))]
    (if (.endsWith (str f) ".tbl")
      (io/delete-file f :silently))))

(def make-parser
  (memo/memo-lru
   (fn [table-name]
     (Parser. (str (table-file table-name))))
   parser-cache-size))

(defn parse [table stream]
  (str (.parse ^Parser (make-parser table) ^java.io.InputStream stream)))

(defn reinit! []
  ;; clear parser cache
  (memo/memo-clear! make-parser)
  ;; create table base directory if needed
  (io/make-parents (table-file (uuid)))  
  ;; delete existing .tbl files
  (delete-all-tables!)
  "Reinitialized")

(defroutes handler
  (GET "/" {params :params
            body :body} (str "hello" (type body)))
  (GET "/parse/:table" {{table :table} :params
                        body :body} (parse table body))
  (POST "/table" {body :body} (create-table! body))
  (DELETE "/table/:id" [id] (delete-table! id))
  (DELETE "/table" [] (delete-all-tables!))
  (ANY "/reinit!" [] (reinit!)))

(def app
  (-> #'handler
      (wrap-params)
      (wrap-stacktrace)
      ;(wrap-multipart-params)
      ))

(comment ;; use this instead of defonce for deployment
  (defn -main [& args]
    (reinit!)
    (run-jetty #'app {:port port})))

(defonce server
  (run-jetty #'app {:port port}))


;; TODO use hash of the .tbl for identification instead of UUID? what does sugarj use for grammars?

;; install localrepo leiningen plugin:
;; put the following in .lein/profiles.clj
;; {:user {:plugins [
;;                   [lein-localrepo "0.4.0"]
;;                   [lein-swank "1.4.3"]
;;                   ]}}

;; install current parser to local repo using 
;; $ lein localrepo install `lein localrepo coords treedecor-parser-0.0.1.jar`

;; make sure to use proper content type for sending post stuff. wrap-params eats
;; form-url-encoded bodies as sent by web browsers. Not sure whether multipart params is needed
;; use e.g. this for testing (reads post data from stdin, use --data-binary @filename to read and send a file as is)
;; $ curl -X POST -H'Content-Type: application/binary' -d @- http://127.0.0.1:8080/table/

;; post xml table (note uuid)
;; $ curl -X POST -H'Content-Type:application/binary' --data-binary @/home/stefan/Work/treedecor/syntax.xml/xml.tbl http://127.0.0.1:8080/table

;; parse xml document replace $TABLE with uuid noted before
;; $ curl -X GET -H'Content-Type: application/binary' -d @/home/stefan/Work/treedecor/renderer.imp/plugin.xml http://127.0.0.1:8080/parse/$TABLE
;; after warmup sub 20ms for project.xml from renderer.imp
