(ns ola.client.state.subs
  (:require
    [re-frame.core :refer [reg-sub]]))

(reg-sub
  :page
  (fn [db _]
    (db :page)))

(reg-sub
  :query
  (fn [db _]
    (db :query)))

(reg-sub
  :transcripts
  (fn [db _]
    (db :transcripts)))

(reg-sub
  :speakers
  (fn [db _]
    (db :speakers)))