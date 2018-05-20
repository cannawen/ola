(ns ola.server.core
  (:require
    [clojure.java.io :as io]
    [ola.parser.core :as parser]
    [clojure.data.json :as json]
    [clojure.string :as string]
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.keyword-params :refer [wrap-keyword-params]])
  (:import (java.io File)))

(defn get-speaker-lines [transcript speaker]
  (filter
    (fn [line]
      (string/includes? (string/lower-case (line :speaker))
                        (string/lower-case speaker)))
    transcript))

(defn get-transcripts [speaker]
  (->> (io/file "data/json/detailed")
       (file-seq)
       (filter (fn [f] (.isFile f)))
       (sort-by (fn [f] (.getName f)))
       (reverse)
       (map
         (fn [f]
           {:date (.getPath f)
            :data (get-speaker-lines
                    (json/read-str (slurp (.getPath f)) :key-fn keyword)
                    speaker)}))
       (filter
         (fn [f]
           (> (count (f :data)) 0)))))

(def routes
  [[[:get "/api/transcripts"]
    (fn [request]
      (let [{:keys [speaker]} (request :params)]
        (println request)
        {:status 200
         :body (get-transcripts speaker)}))
    [wrap-keyword-params wrap-params]]])
