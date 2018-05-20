(ns ola.client.state.subs
  (:require
    [re-frame.core :refer [reg-sub]]))

(reg-sub
  :query
  (fn [db _]
    (db :query)))

(reg-sub
  :transcripts
  (fn [db _]
    (take 5 (db :transcripts))))