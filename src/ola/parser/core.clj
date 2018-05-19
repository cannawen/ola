(ns ola.parser.core
  (:require
    [hickory.core :as h]
    [hickory.select :as s]
    [clojure.string :as string]
    [cheshire.core :as json]
    [clojure.java.io :as io]))


(defn speaker [el]
  (->
    (s/select (s/tag :strong) el)
    (first)
    :content
    (last)
    string/trim
    (string/replace #":" "")))

(defn text [el]
  (->>
    el
    :content
    (filter string?)
    last
    string/trim))

(defn anchor [el]
  (->>
    el
    (s/select (s/tag :a))
    (first)
    :attrs
    :name))

(defn parse-transcript [html]
  (->>
    html
    (h/parse)
    (h/as-hickory)
    (s/select (s/tag :p))
    ; remove initial <p>s before first speaker
    (drop-while (fn [el] (not= "speakerStart" (get-in el [:attrs :class]))))
    ; remove <p>s with .procedure class
    (remove (fn [el] (= "procedure" (get-in el [:attrs :class]))))
    ; remove <p>s with .timeStamp class
    (remove (fn [el] (= "timeStamp" (get-in el [:attrs :class]))))
    ; remove <p>s with single empty <a> tag
    (remove (fn [el] (and
                       (= 1 (count (el :content)))
                       (-> el :content first :content nil?))))
    ; remove <p>s with only "Interjection" content
    (remove (fn [el] (string/includes? (-> el :content last) "Interjection")))
    (reduce (fn [memo el]
              (if (= "speakerStart" (get-in el [:attrs :class]))
                (conj memo {:speaker (speaker el)
                            :text [(text el)]
                            :anchor (anchor el)})
                (update-in memo [(dec (count memo)) :text] conj (text el))))
      [])))

(defn subject-text [el]
  (last (el :content)))

(defn subject-anchor [el]
  (->>
    (el :content)
    (filter map?)
    (filter
      (fn [e]
        (string/starts-with? (get-in e [:attrs :name]) "para")))
    (map
      (fn [e]
        (get-in e [:attrs :name])))
    (first)))

(defn parse-transcript-with-subject [html]
  (->>
    html
    (h/parse)
    (h/as-hickory)
    (s/select (s/or (s/tag :h3) (s/tag :p)))
    ; remove initial <p>s before first speaker
    (drop-while (fn [el] (not= "speakerStart" (get-in el [:attrs :class]))))
    ; remove <p>s with .procedure class
    (remove (fn [el] (= "procedure" (get-in el [:attrs :class]))))
    ; remove <p>s with .timeStamp class
    (remove (fn [el] (= "timeStamp" (get-in el [:attrs :class]))))
    ; remove <p>s with single empty <a> tag
    (remove (fn [el] (and
                       (= (el :tag) :p)
                       (= 1 (count (el :content)))
                       (-> el :content first :content nil?))))
    ; remove <p>s with only "Interjection" content
    (remove (fn [el] (string/includes? (-> el :content last) "Interjection")))
    (reduce (fn [memo el]
              (cond
                (= :h3 (el :tag))
                (assoc memo :subject {:text (subject-text el)
                                      :anchor (subject-anchor el)})

                (= "speakerStart" (get-in el [:attrs :class]))
                (update memo :items conj
                  {:speaker (speaker el)
                   :text [(text el)]
                   :anchor (anchor el)
                   :subject (memo :subject)})

                :else
                (update-in memo [:items (dec (count (memo :items))) :text] conj (text el))))
      {:items []
       :subject nil})
    :items))

(defn parse-subjects [html]
  (->>
    html
    (h/parse)
    (h/as-hickory)
    (s/select (s/tag :h3))
    (reduce (fn [memo el]
              (conj memo {:text (subject-text el)
                          :anchor (subject-anchor el)}))
      [])))

(defn convert-to-json! [function folder-name]
  (doseq [file (filter (fn [file] (string/ends-with? file ".html")) (file-seq (io/file "data/html/")))]
    (->
      file
      (slurp)
      (function)
      (json/generate-string {:pretty true})
      (->> (spit (str "data/json/" folder-name "/" (string/replace (.getName file) ".html" ".json")))))))

