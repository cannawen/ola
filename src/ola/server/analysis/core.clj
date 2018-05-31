(ns ola.server.analysis.core
  (:require
    [ola.server.parser.core :as parser]
    [clojure.string :as string]
    [cheshire.core :as json]))

(defn speaker-word-biases [speaker min-word]
  (->>
    (str "data/json/frequencies/" speaker ".json")
    (slurp)
    (json/parse-string)
    (filter
      (fn [row]
        (> (last row) min-word)))
    (reverse)
    (take 10)))

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
