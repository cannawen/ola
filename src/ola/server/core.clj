(ns ola.server.core
  (:require
    [clojure.java.io :as io]
    [ola.parser.core :as parser]
    [clojure.data.json :as json]
    [clojure.string :as string]
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.keyword-params :refer [wrap-keyword-params]])
  (:import (java.io File)))

(defonce transcripts (atom []))

(defn load-data! []
  (reset! transcripts (parser/parse-all)))

(defn transcripts-with-speaker [speaker]
  (->> @transcripts
       (filter
         (fn [t]
           (string/includes?
             (string/lower-case (t :speaker))
             (string/lower-case speaker))))))

(defn speakers []
  (->> @transcripts
       (map :speaker)
       set))

(def routes
  [[[:get "/api/transcripts"]
    (fn [request]
      (let [{:keys [speaker]} (request :params)]
        {:status 200
         :body (transcripts-with-speaker speaker)}))
    [wrap-keyword-params wrap-params]]

   [[:get "/api/speakers"]
    (fn [_]
      {:status 200
       :body (speakers)})]])
