(ns parsely.config
  (:use [slingshot.slingshot :only [throw+]])
  (:require [clojure-commons.config :as cc]
            [clojure-commons.error-codes :as ce]))

(def ^:private props
  "A ref for storing the configuration properties."
  (ref nil))

(def ^:private config-valid
  "A ref for storing a configuration validity flag."
  (ref true))

(def ^:private configs
  "A ref for storing the symbols used to get configuration settings."
  (ref []))

(cc/defprop-int listen-port
  "The port that parsely listens on."
  [props config-valid configs]
  "parsely.app.listen-port")

(cc/defprop-int cache-threshold
  "The number of models that the cache can contain."
  [props config-valid configs]
  "parsely.app.cache-threshold")

#_((cc/defprop-str irods-base
  "Returns the path to the home directory in iRODS. Usually /iplant/home"
  [props config-valid configs]
  "parsely.app.irods-base")

(cc/defprop-str irods-user
  "Returns the user that porklock should connect as."
  [props config-valid configs]
  "parsely.app.irods-user")

(cc/defprop-str irods-pass
  "Returns the iRODS user's password."
  [props config-valid configs]
  "parsely.app.irods-pass")

(cc/defprop-str irods-host
  "Returns the iRODS hostname/IP address."
  [props config-valid configs]
  "parsely.app.irods-host")

(cc/defprop-str irods-port
  "Returns the iRODS port."
  [props config-valid configs]
  "parsely.app.irods-port")

(cc/defprop-str irods-zone
  "Returns the iRODS zone."
  [props config-valid configs]
  "parsely.app.irods-zone")

(cc/defprop-optstr irods-resc
  "Returns the iRODS resource."
  [props config-valid configs]
  "parsely.app.irods-resc"))

(defn- validate-config
  "Validates the configuration settings after they've been loaded."
  []
  (when-not (cc/validate-config configs config-valid)
    (throw+ {:error_code ce/ERR_CONFIG_INVALID})))

(defn load-config-from-file
  "Loads the configuration settings from a file."
  []
  (cc/load-config-from-file (System/getenv "IPLANT_CONF_DIR") "parsely.properties" props)
  (cc/log-config props)
  (validate-config))

(defn load-config-from-zookeeper
  "Loads the configuration settings from Zookeeper."
  []
  (cc/load-config-from-zookeeper props "parsely")
  (cc/log-config props)
  (validate-config))

