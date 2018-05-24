(ns ola.server.analysis.core
  (:require
    [ola.server.parser.core :as parser]
    [clojure.string :as string]))

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
    (mapcat (fn [sentence] (re-seq #"\w+" sentence)))
    (map string/lower-case)
    (map keyword)
    (frequencies)
    (sort-by second)
    (reverse)))

(defn speaker-word-biases [speaker]
  (let [speaker-frequencies (into {} (word-frequencies (transcripts-with-speaker speaker)))
        speaker-total (->> speaker-frequencies
                           (map second)
                           (reduce +))
        all-frequencies (into {} (word-frequencies @transcripts))
        others-frequencies (->> all-frequencies
                                (map (fn [[word count]]
                                       [word (- count (or (speaker-frequencies word) 0))]))
                                (into {}))
        others-total (->> others-frequencies
                          (map second)
                          (reduce +))]
    (->> speaker-frequencies
         (filter (fn [[word _]]
                   (<= 10 (all-frequencies word))))
         (map (fn [[word speaker-count]]
                [(name word)
                 (float  (/ (/ speaker-count speaker-total)
                            (/ (others-frequencies word) others-total)))
                 speaker-count
                 (or (others-frequencies word) 0)]))
         (sort-by second))))
