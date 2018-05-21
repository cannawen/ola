(ns ola.server.core
  (:require
    [ola.parser.core :as parser]
    [clojure.string :as string]
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.keyword-params :refer [wrap-keyword-params]]))

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
