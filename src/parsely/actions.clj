(ns parsely.actions
  (:require [hoot.ontology :as ont]
            [hoot.rdf :as rdf])
  (:use [slingshot.slingshot :only [throw+]]))

(defn get-ontology
  [source]
  (try
    (ont/ontology (ont/ontology-manager) source (rdf/model source))
    (catch Exception e
          (throw+ {:error_code "ERR_PARSE_FAILED"
                   :ontology source}))))

(defn parse
  [user source]
  (get-ontology source)
  {:user user :source source})

(defn classes
  [user ontology]
  {:classes (mapv ont/class->map (ont/classes (get-ontology ontology)))})

(defn properties
  [user ontology class]
  (let [ont (get-ontology ontology)
        cls (ont/uri->class ont class)]
    (when-not cls
      (throw+ {:error_code "ERR_NOT_A_CLASS"
               :ontology ontology
               :class class}))
    {:properties
       (mapv ont/class->map (ont/possible-class-properties ont cls))}))

(defn triples
  [triple-doc doc-type]
  (let [m (rdf/model triple-doc doc-type)]
    (when-not m
      (throw+ {:error_code "ERR_PARSE_FAILED"
               :file triple-doc}))
    {:triples (rdf/statements m)}))

