(ns parsely.irods
  (:use [parsely.config]
        [clj-jargon.jargon]
        [clojure-commons.error-codes]
        [slingshot.slingshot :only [throw+]])
  (:require [hoot.rdf :as rdf]
            [hoot.csv :as csv]))

(def all-types (set (concat rdf/accepted-languages csv/csv-types)))

(defn add-type
  [user path type]
  (with-jargon (jargon-cfg) [cm]
    (when-not (contains? all-types type)
      (throw+ {:error_code ERR_BAD_OR_MISSING_FIELD
               :type type}))
    
    (when-not (exists? cm path)
      (throw+ {:error_code ERR_DOES_NOT_EXIST
               :path path}))
    
    (when-not (user-exists? cm user)
      (throw+ {:error_code ERR_NOT_A_USER
               :user user}))
    
    (when-not (owns? cm user path)
      (throw+ {:error_code ERR_NOT_OWNER
               :user user
               :path path}))
    
    (add-metadata cm path (type-attribute) type "")
    {:path path
     :type type}))

(defn get-types
  [user path]
  (with-jargon (jargon-cfg) [cm]
    (when-not (exists? cm path)
      (throw+ {:error_code ERR_DOES_NOT_EXIST
               :path path}))
    
    (when-not (user-exists? cm user)
      (throw+ {:error_code ERR_NOT_A_USER
               :user user}))
    
    (when-not (is-readable? cm user path)
      (throw+ {:error_code ERR_NOT_READABLE
               :user user
               :path path}))
    (mapv :value (get-attribute cm path (type-attribute)))))

