(ns ola.server.core
  (:require
    [ola.server.analysis.core :as data]
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.keyword-params :refer [wrap-keyword-params]]))

(def routes
  [[[:get "/api/transcripts"]
    (fn [request]
      (let [{:keys [speaker]} (request :params)]
        {:status 200
         :body (data/transcripts-with-speaker speaker)}))
    [wrap-keyword-params wrap-params]]

   [[:get "/api/speakers"]
    (fn [_]
      {:status 200
       :body (data/speakers)})]

   [[:get "/api/speakers/biases"]
    (fn [r]
      {:status 200
       :body (data/speaker-word-biases (get-in r [:params :speaker]) (int (read-string (get-in r [:params :min_word]))))})
    [wrap-keyword-params wrap-params]]])
