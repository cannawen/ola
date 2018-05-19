(ns ola.client.ui.app
  (:require
    [re-frame.core :refer [subscribe]]))

(defn app-view []
  [:div
   [:h1 "Hello World"]
   (for [transcript @(subscribe [:transcripts])]
     ^{:key (transcript :date)}
     [:div.transcript
      [:h2 (transcript :date)]
      [:table
       [:tbody
        (for [{:keys [speaker text anchor]} (transcript :data)]
          ^{:key anchor}
          [:tr
           [:td {:style {:white-space "nowrap"
                         :vertical-align "top"}} speaker]
           [:td {:style {:vertical-align "top"}}
            (into [:div.text]
                  (for [t text]
                    [:p t]))]])]]])])
