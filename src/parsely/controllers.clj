(ns parsely.controllers
  (:use [slingshot.slingshot :only [throw+]]
        [clojure-commons.error-codes])
  (:require [cheshire.core :as json]
            [parsely.actions :as actions]))

(defn check-missing-params
  [params required-keys]
  (let [not-valid? #(not (contains? params %))]
    (if (some not-valid? required-keys)
    (throw+ {:error_code ERR_BAD_OR_MISSING_FIELD
             :fields (filter not-valid? required-keys)}))))

(defn check-params-valid
  [params func-map]
  (let [not-valid? #(not ((last %1) (get params (first %1))))
        field-seq  (seq func-map)]
    (when (some not-valid? field-seq)
      (throw+ {:error_code ERR_BAD_OR_MISSING_FIELD
               :fields (mapv first (filter not-valid? field-seq))}))))

(defn validate-params
  [params func-map]
  (check-missing-params params (keys func-map))
  (check-params-valid params func-map))

(defn parse
  [body params]
  (validate-params params {:user string?})
  (validate-params body {:source string?})
  (json/generate-string (actions/parse (:user params) (:source body))))

(defn classes
  [params]
  (validate-params params {:user string? :ontology string?})
  (json/generate-string (actions/classes (:user params) (:ontology params))))

(defn properties
  [params]
  (validate-params params {:user string? :ontology string? :class string?})
  (json/generate-string 
    (actions/properties (:user params) (:ontology params) (:class params))))