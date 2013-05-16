(ns parsely.core
  (:gen-class)
  (:use [compojure.core]
        [clojure-commons.lcase-params :only [wrap-lcase-params]]
        [clojure-commons.query-params :only [wrap-query-params]]
        [clojure-commons.error-codes]
        [parsely.json-body :only [parse-json-body]]
        [ring.middleware
         params
         keyword-params
         nested-params
         multipart-params
         cookies
         session
         stacktrace]
        [slingshot.slingshot :only [try+ throw+]])
  (:require [clojure.tools.cli :as cli]
            [clojure.tools.logging :as log]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.adapter.jetty :as jetty]
            [parsely.config :as config]
            [parsely.controllers :as controllers]))

(defroutes parsely-routes
  (GET "/" [] "Welcome to parsely.")
  
  #_((POST "/parse" [:as {params :params body :body}]
        (trap "parse" controllers/parse body params))
  
  (GET "/classes" [:as {params :params}]
       (trap "classes" controllers/classes params))
  
  (GET "/properties" [:as {params :params}]
       (trap "properties" controllers/properties params)))
  
  (GET "/triples" [:as {params :params}]
       (trap "triples" controllers/triples params))
  
  (POST "/type" [:as {body :body params :params}]
        (trap "add-type" controllers/add-type body params))
  
  (GET "/type" [:as {params :params}]
       (trap "get-types" controllers/get-types params))
  
  (POST "/auto-type" [:as {body :body params :params}]
        (trap "set-auto-type" controllers/set-auto-type body params))
  
  (GET "/auto-type" [:as {params :params}]
       (trap "get-auto-type" controllers/preview-auto-type params))
  
  (DELETE "/type" [:as {params :params}]
          (trap "delete-types" controllers/delete-type params))
  
  (GET "/type-list" []
       (trap "get-type-list" controllers/get-type-list))
  
  (GET "/type/paths" [:as {params :params}]
       (trap "find-paths-by-type" controllers/find-typed-paths params)))

(defn site-handler [routes]
  (-> routes
    parse-json-body
    wrap-keyword-params
    wrap-lcase-params
    wrap-query-params))

(def app
  (site-handler parsely-routes))

(defn load-configuration-from-file
  "Loads the configuration properties from a file."
  []
  (config/load-config-from-file))

(defn load-configuration-from-zookeeper
  "Loads the configuration properties from Zookeeper."
  []
  (config/load-config-from-zookeeper))

(defn -main
  [& _]
  (load-configuration-from-zookeeper)
  (log/warn "Listening on" (config/listen-port))
  (jetty/run-jetty app {:port (config/listen-port)}))
