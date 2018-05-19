(ns ola.client.core
  (:require
    [reagent.core :as r]
    [re-frame.core :refer [dispatch-sync]]
    [ola.client.state.events] ; to register events
    [ola.client.state.subs] ; to register subs
    [ola.client.ui.app :refer [app-view]]))

(enable-console-print!)

(defn render []
  (r/render-component [app-view]
    (.. js/document (getElementById "app"))))

(defn ^:export init []
  (dispatch-sync [:init!])
  (render))

(defn ^:export reload []
  (render))