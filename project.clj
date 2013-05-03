(defproject parsely "0.1.0-SNAPSHOT"
  :description "REST-like API for retrieving semantic markup information"
  :url "http://github.com/iPlantCollaborativeOpenSource/parsely"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.2.6"]
                 [hoot "0.1.0-SNAPSHOT"]
                 [org.iplantc/clojure-commons "1.4.1-SNAPSHOT"]
                 [compojure "1.1.5"]
                 [com.novemberain/validateur "1.4.0"]
                 [ring/ring-jetty-adapter "1.1.8"]
                 [ring/ring-devel "1.1.8"]
                 [cheshire "5.0.2"]
                 [org.clojure/core.memoize "0.5.3"]]
  :iplant-rpm {:summary "parsely"
               :dependencies ["iplant-service-config >= 0.1.0-5"]
               :config-files ["log4j.properties"]
               :config-path "conf"}
  :profiles {:dev {:resource-paths ["conf/test"]}}
  :aot [parsely.core]
  :main parsely.core
  :ring {:handler parsely.core/app
         :init parsely.core/load-configuration-from-file
         :port 31326}
  :plugins [[lein-ring "0.8.3"]
            [org.iplantc/lein-iplant-rpm "1.4.0-SNAPSHOT"]]
  :repositories {"iplant"
                 "http://projects.iplantcollaborative.org/archiva/repository/internal/"})
