(ns ola.client.ui.app
  (:require
    [re-frame.core :refer [subscribe dispatch]]
    [ola.client.state.routes :as routes]))

(defn index-page []
  [:div
   (for [speaker @(subscribe [:speakers])]
     [:div.speaker speaker])])

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

(defn search-page []
  [:div
   [search-view]
   [hansard-view]])

(defn app-view []
  [:div
   [:h1 "Ontario Legislative Assembly"]
   [:a {:href (routes/index)} "index"]
   [:a {:href (routes/search)} "search"]
   (case @(subscribe [:page])
     :index [index-page]
     :search [search-page]
     nil)])
