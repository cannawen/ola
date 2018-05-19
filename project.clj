(defproject ola "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]

                 ; parser
                 [hickory "0.7.1"]
                 [cheshire "5.8.0"]
                 [io.bloomventures/omni "0.14.6"]

                 ; web app - client
                 [org.clojure/clojurescript "1.9.946"]
                 [reagent "0.8.1"]
                 [re-frame "0.10.5"]
                 [org.clojure/spec.alpha "0.1.143"]]

  :main ola.core

  :min-lein-version "2.5.0"

  :omni-config ola.core/config

  :plugins [[io.bloomventures/omni "0.14.6"]]

  :uberjar-name "ola.jar"

  :profiles {:uberjar {:aot :all
                       :prep-tasks [["omni" "compile"]
                                    "compile"]}})
