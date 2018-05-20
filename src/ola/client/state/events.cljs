(ns ola.client.state.events
  (:require
    [re-frame.core :refer [reg-event-fx dispatch reg-fx]]
    [bloom.omni.fx.ajax :as ajax]
    [bloom.omni.fx.router :as router]))

(reg-fx :ajax ajax/fx)

(reg-fx :router router/fx)

(reg-event-fx
  :init!
  (fn [{db :db} _]
    {:router [:init!]
     :dispatch [:-fetch-speakers!]
     :db {:page nil
          :speakers []
          :transcripts []}}))

(reg-event-fx
  :set-page!
  (fn [{db :db} [_ page]]
    {:db (assoc db :page page)}))

(reg-event-fx
  :set-query!
  (fn [{db :db} [_ query]]
    {:db (assoc db :query query)}))

(reg-event-fx
  :search!
  (fn [{db :db} _]
    {:dispatch [:-fetch-transcripts! (db :query)]}))

(reg-event-fx
  :-fetch-speakers!
  (fn [_ _]
    {:ajax {:method :get
            :uri "/api/speakers"
            :on-success (fn [response]
                          (dispatch [:-handle-speakers! response]))}}))

(reg-event-fx
  :-handle-speakers!
  (fn [{db :db} [_ speakers]]
    {:db (assoc db :speakers speakers)}))

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
