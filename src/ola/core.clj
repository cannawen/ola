(ns ola.core
  (:gen-class)
  (:require
    [ola.server.analysis.core :as data]
    [bloom.omni.core :as omni]
    [ola.server.core :refer [routes]]))

(def config
  {:omni/title "OLA"
   :omni/cljs {:main "ola.client.core"}
   :omni/css {:styles "ola.client.styles/styles"}
   :omni/api-routes routes
   :omni/http-port 8080})

(defn start! []
  (data/load-data!)
  (omni/start! omni/system config))

(defn stop! []
  (omni/stop!))

(defn -main []
  (start!))
