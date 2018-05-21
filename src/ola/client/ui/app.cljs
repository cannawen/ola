(ns ola.client.ui.app
  (:require
    [reagent.core :as r]
    [re-frame.core :refer [subscribe dispatch]]
    [ola.client.state.routes :as routes]))

(defn index-page []
  [:div
   (for [speaker @(subscribe [:speakers])]
     [:div.speaker
      {:key speaker}
      [:a
       {:href (routes/search-results {:query speaker})}
       speaker]])])

(defn search-view []
  (let [query (r/atom "")]
    (fn []
      [:form
       {:on-submit (fn [e]
                     (.preventDefault e)
                     (dispatch [:search! @query]))}
       [:input.search {:type "search"
                       :value @query
                       :on-change (fn [e]
                                    (reset! query (.. e -target -value)))}]
       [:button "Go"]])))

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
            [:a {:href (str "http://www.ontla.on.ca/web/house-proceedings/house_detail.do?Date=" date "#" anchor)} "[src]"]]])]]])])

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
   [search-view]
   [biases-view]
   [hansard-view]])

(defn app-view []
  [:div
   [:h1 "Ontario Legislative Assembly"]
   [:a {:href (routes/index)} "index"]
   [:a {:href (routes/search)} "search"]
   (case (:id @(subscribe [:page]))
     :index [index-page]
     :search [search-page]
     nil)])
