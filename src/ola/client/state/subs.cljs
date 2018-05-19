(ns ola.client.state.subs
  (:require
    [re-frame.core :refer [reg-sub]]))

(reg-sub
  :transcripts
  (fn [db _]
    (db :transcripts)))