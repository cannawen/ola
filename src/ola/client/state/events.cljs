(ns ola.client.state.events
  (:require
    [re-frame.core :refer [reg-event-fx dispatch reg-fx]]
    [bloom.omni.fx.ajax :as ajax]))

(reg-fx :ajax ajax/fx)

(reg-event-fx
  :init!
  (fn [{db :db} _]
    {:db {:transcripts []}}))

(reg-event-fx
  :set-query!
  (fn [{db :db} [_ query]]
    {:db (assoc db :query query)}))

(reg-event-fx
  :search!
  (fn [{db :db} _]
    {:dispatch [:-fetch-transcripts! (db :query)]}))

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
