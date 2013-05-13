(ns parsely.controllers
  (:use [slingshot.slingshot :only [throw+]]
        [clojure-commons.error-codes])
  (:require [cheshire.core :as json]
            [hoot.rdf :as rdf]
            [hoot.csv :as csv]
            [clojure.core.memoize :as memo]
            [parsely.actions :as actions]
            [parsely.irods :as prods]))

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
               :fields     (mapv first (filter not-valid? field-seq))}))))

(defn validate-params
  [params func-map]
  (check-missing-params params (keys func-map))
  (check-params-valid params func-map))

(defn parse
  [body params]
  (validate-params body {:url string? :user params})
  (json/generate-string (actions/parse (:user params) (:url body))))

(defn classes-base
  [params]
  (validate-params params {:url string? :user params})
  (json/generate-string (actions/classes (:user params) (:url params))))

(def classes
  (memo/memo-lru classes-base 10))

(defn properties-base
  [params]
  (validate-params params {:url string? :user string? :class string?})
  (json/generate-string 
    (actions/properties (:user params) (:url params) (:class params))))

(def properties
  (memo/memo-lru properties-base 10))

(defn accepted-types
  []
  (set (concat rdf/accepted-languages csv/csv-types)))

(defn triples-base
  [params]
  (validate-params params {:url string? :user string? :type #(contains? (accepted-types) %)})
  (json/generate-string
    (actions/triples (:user params) (:url params) (:type params))))

(def triples
  (memo/memo-lru triples-base 10))

(defn add-type
  [body params]
  (validate-params params {:user string?})
  (validate-params body {:path string? :type #(contains? (accepted-types) %)})
  (json/generate-string
    (prods/add-type (:user params) (:path body) (:type body))))

(defn delete-type
  [params]
  (validate-params params {:user string? :type #(contains? (accepted-types) %) :path string?})
  (json/generate-string
    (prods/delete-type (:user params) (:path params) (:type params))))

(defn get-types
  [params]
  (validate-params params {:path string? :user string?})
  (json/generate-string
    {:types (prods/get-types (:user params) (:path params))}))

(defn find-typed-paths
  [params]
  (validate-params params {:user string? :type string?})
  (json/generate-string
    {:paths (prods/find-paths-with-type (:user params) (:path params))}))

(defn get-type-list [] (json/generate-string {:types csv/csv-types}))
