(ns ola.client.state.events
  (:require
    [re-frame.core :refer [reg-event-fx dispatch reg-fx]]
    [bloom.omni.fx.ajax :as ajax]))

(reg-fx :ajax ajax/fx)

(reg-event-fx
  :init!
  (fn [{db :db} _]
    {:db {:transcripts []}
     :dispatch [:-fetch-transcripts! "zimmer"]}))

(reg-event-fx
  :-fetch-transcripts!
  (fn [_ [_ speaker]]
    {:ajax {:method :get
            :uri "/api/transcripts"
            :params {:speaker speaker}
            :on-success (fn [response]
                          (dispatch [:-handle-transcripts! response]))}}))

(reg-event-fx
  :-handle-transcripts!
  (fn [{db :db} [_ transcripts]]
    {:db (assoc db :transcripts transcripts)}))
