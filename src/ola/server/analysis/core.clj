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

(defn word-frequencies [transcripts]
  (->>
    (map :text transcripts)
    (flatten)
    (mapcat (fn [sentence] (re-seq #"[\wÀ-ÿ'’]+" sentence)))
    (map string/lower-case)
    (map keyword)
    (frequencies)
    (sort-by second)
    (reverse)))

(defn compute-word-frequencies [min-word]
  (map
    (fn [speaker]
      {:speaker speaker
       :data (speaker-word-biases speaker min-word)})
    (speakers)))

(defn create-speaker-json []
  (doseq [data (compute-word-frequencies 0)]
    (spit
      (str "data/json/frequencies/" (data :speaker) ".json")
      (json/generate-string (data :data) {:pretty true}))))

