(ns ola.server.parser.core
  (:require
    [hickory.core :as h]
    [hickory.select :as s]
    [clojure.string :as string]
    [cheshire.core :as json]
    [clojure.java.io :as io]))

(defn scrub-name [name]
  (cond
    (boolean (re-find #"Speaker|Président|Deputy" name)) "The Speaker"
    (boolean (re-find #"Clerk" name)) "The Clerk"
    (boolean (re-find #"Chair" name)) "The Chair"
    (boolean (re-find #"voix|Interjection" name)) "Interjection"
    :else
      (->
        name
        (string/trim)
        (string/replace #":" "")
        (string/replace #"(\().+| " "")
        (string/replace #"^(Mr\.|Mr\.|Ms\.|Mrs\.|Hon\.|L’hon\.|M\.|Miss|Mme|Dr\.|The Hon\.|Rev\.|Chief)" "")
        (string/trim))))

(defn speaker [el]
  (->
    (s/select (s/tag :strong) el)
    (first)
    :content
    (last)
    (scrub-name)))

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
    ;(remove (fn [el] (string/includes? (-> el :content last) "Interjection")))
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
    (s/select (s/or (s/tag :h3) (s/tag :h2) (s/tag :p)))
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
    ;(remove (fn [el] (string/includes? (-> el :content last) "Interjection")))
    (reduce (fn [memo el]
              (cond
                (or (= :h3 (el :tag))
                    (= :h2 (el :tag)))
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

(defn parse-all []
  (->> (io/file "data/html/")
       file-seq
       (filter (fn [file] (.isFile file)))
       (filter (fn [file] (string/ends-with? file ".html")))
       (mapcat (fn [file]
                 (let [[_ date] (re-matches #"(\d{4}-\d{2}-\d{2}).html" (.getName file))]
                   (->> (slurp (.getPath file))
                       parse-transcript-with-subject
                       (map (fn [transcript]
                              (assoc transcript :date date)))))))))

(defn convert-to-json! [function folder-name]
  (doseq [file (filter (fn [file] (string/ends-with? file ".html")) (file-seq (io/file "data/html/")))]
    (->
      file
      (slurp)
      (function)
      (json/generate-string {:pretty true})
      (->> (spit (str "data/json/" folder-name "/" (string/replace (.getName file) ".html" ".json")))))))
