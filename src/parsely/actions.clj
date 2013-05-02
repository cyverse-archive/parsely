(ns parsely.actions
  (:require [hoot.ontology :as ont]
            [hoot.rdf :as rdf])
  (:use [slingshot.slingshot :only [throw+]]))

(defn get-ontology
  [source]
  (ont/ontology (ont/ontology-manager) source (rdf/model source)))

(defn parse
  [user source]
  (try
    (get-ontology source)
    {:user user :source source}
    (catch Exception e
            (throw+ {:error_code "ERR_PARSE_FAILED"
                     :user user
                     :source source}))))

(defn classes
  [user ontology]
  {:classes 
   (mapv ont/class->map (ont/classes (get-ontology ontology)))})

(defn properties
  [user ontology class]
  (let [ont (get-ontology ontology)]
    (println class)
    (println (ont/uri->class ont class))
    {:properties
     (mapv ont/class->map (ont/possible-class-properties ont (ont/uri->class ont class)))}))

