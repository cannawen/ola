(ns ola.client.ui.app
  (:require
    [reagent.core :as r]
    [re-frame.core :refer [subscribe dispatch]]
    [ola.client.state.routes :as routes]
    [cljs-time.core :as t]
    [cljs-time.format :as f]))

(defn index-page []
  [:div
   (for [speaker @(subscribe [:speakers])]
     [:div.speaker
      {:key speaker}
      [:a
       {:href (routes/search-results {:query speaker})}
       speaker]])])

(defn between-time [target-date startY startM startD endY endM endD]
  (t/within?
    (t/interval (t/date-time startY startM startD) (t/date-time endY endM endD))
    (f/parse (f/formatter "YYYY-MM-dd") target-date)))

(defn src-url [date anchor]
  (let [session (cond
                  (between-time date 2014 7 1 2016 9 9) "1"
                  (between-time date 2016 9 11 2018 3 16) "2"
                  (between-time date 2018 3 18 2018 5 9) "3")
        parliment "41"]
    (str "https://www.ola.org/en/legislative-business/house-documents/parliament-" parliment "/session-" session "/" date "/hansard#" anchor)))

(defn hansard-view []
  [:div
   (for [[date transcripts] (reverse (sort-by first (group-by :date @(subscribe [:transcripts]))))]
     [:div.transcript
      {:key date}
      [:h2 date]
      [:table
       [:tbody
        (for [{:keys [speaker text anchor date subject]} transcripts]
          [:tr
           {:key anchor}
           [:td {:style {:white-space "nowrap"
                         :vertical-align "top"}} speaker]
           [:td {:style {:vertical-align "top"}}
            [:a {:href (str "http://www.ontla.on.ca/web/house-proceedings/house_detail.do?Date=" date "#" (:anchor subject))} (:text subject)]]
           [:td {:style {:vertical-align "top"}}
            (into [:div.text]
              (for [t text]
                [:p t]))]
           [:td {:style {:vertical-align "top"}}
            [:a {:href (src-url date anchor)} "[src]"]]])]]])])

(defn biases-view []
  [:div
   [:table
    [:thead
     [:tr
      [:th "Word"]
      [:th "Ratio vs Avg"]
      [:th "Count"]
      [:th "Others Count"]]]
    [:tbody
     (for [[word ratio speaker-count others-count] @(subscribe [:biases])]
       [:tr {:key word}
        [:td word]
        [:td ratio]
        [:td speaker-count]
        [:td others-count]])]]])

(defn search-page []
  [:div
   [biases-view]
   [hansard-view]])

(defn app-view []
  [:div
   [:a {:href "/"}
    [:h1 "Ontario Legislative Assembly"]]
   (case (:id @(subscribe [:page]))
     :index [index-page]
     :search [search-page]
     nil)])
