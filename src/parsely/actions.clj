(ns parsely.actions
  (:require [hoot.ontology :as ont]
            [hoot.rdf :as rdf]
            [hoot.csv :as csv]
            [parsely.config :as cfg]
            [clj-jargon.jargon :as jg]
            [clj-http.client :as client]
            [clojure.java.io :as io]
            [clojure.string :as string])
  (:use [slingshot.slingshot :only [throw+]]))

(defn protocol?
  [uri-str]
  (try
    (.getScheme (java.net.URI. uri-str))
    (catch Exception e
      false)))

(defn protocol
  [uri-str]
  (.getScheme (java.net.URI. uri-str)))

(defn irods-path
  [uri-str]
  (.getPath (java.net.URI. uri-str)))

(defn irods-uri?
  [uri-str]
  (cond
    (not (protocol? uri-str))      true
    (= (protocol uri-str) "irods") true
    :else                          false))

(defn rdf-source
  [user src]
  (jg/with-jargon (cfg/jargon-cfg) [cm]
    (when-not (jg/user-exists? cm user)
      (throw+ {:error_code "ERR_NOT_A_USER"
               :user user}))
    (when-not (jg/exists? cm src)
      (throw+ {:error_code "ERR_DOES_NOT_EXIST"
               :path src}))
    (when-not (jg/is-readable? cm user src)
      (throw+ {:error_code "ERR_NOT_READABLE"
               :user user
               :path src}))
    (jg/input-stream cm (irods-path src))))

(defn download-file
  [file-url]
  (let [resp (client/get file-url)]
    (if-not (<= 200 (:status resp) 299)
      (throw+ {:error_code "ERR_BAD_STATUS"
               :status     (:status resp)
               :url        file-url})
      (:body resp))))

(defn get-ontology
  [user source]
  (try
    (ont/ontology (ont/ontology-manager) source (rdf/model (rdf-source user source)))
    (catch Exception e
          (throw+ {:error_code "ERR_PARSE_FAILED"
                   :url source}))))

(defn parse
  [user source]
  (get-ontology user source)
  {:url source})

(defn classes
  [user ontology]
  {:classes (mapv ont/class->map (ont/classes (get-ontology user ontology)))})

(defn properties
  [user ontology class]
  (let [ont (get-ontology user ontology)
        cls (ont/uri->class ont class)]
    (when-not cls
      (throw+ {:error_code "ERR_NOT_A_CLASS"
               :url ontology
               :class class}))
    {:properties
       (mapv ont/class->map (ont/possible-class-properties ont cls))}))

(defn csv?
  [doc-type]
  (or (= (.toLowerCase doc-type) "tsv") (= (.toLowerCase doc-type) "csv")))

(defn get-csv-doc
  [user doc-uri]
  (if (irods-uri? doc-uri)
    (rdf-source user doc-uri)
    (io/reader (download-file doc-uri))))

(defn csv-triples
  [user doc-uri doc-type]
  {:triples (csv/statements (get-csv-doc user doc-uri) (string/lower-case doc-type))})

(defn rdf-triples
  [user triple-doc doc-type]
  (let [m (rdf/model (rdf-source user triple-doc) doc-type)]
    (when-not m
      (throw+ {:error_code "ERR_PARSE_FAILED"
               :url triple-doc}))
    {:triples (rdf/statements m)}))

(defn triples
  [user triple-doc doc-type]
  (if (csv? doc-type)
    (csv-triples user triple-doc doc-type)
    (rdf-triples user triple-doc doc-type)))

