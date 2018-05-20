(ns ola.client.ui.app
  (:require
    [re-frame.core :refer [subscribe dispatch]]))

(defn search-view []
  [:form
   {:on-submit (fn [e]
                 (.preventDefault e)
                 (dispatch [:search!]))}
   [:input.search {:type "search"
                   :value @(subscribe [:query])
                   :on-change (fn [e]
                                (dispatch [:set-query! (.. e -target -value)]))}]
   [:button "Go"]])

(defn hansard-view []
  [:div
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

(defn app-view []
  [:div
   [:h1 "Ontario Legislative Assembly"]
   [search-view]
   [hansard-view]])
