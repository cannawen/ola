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
     :db {:page {:id nil
                 :data nil}
          :speakers []
          :transcripts []
          :biases []}}))

(reg-event-fx
  :set-page!
  (fn [{db :db} [_ page data]]
    {:db (assoc db :page {:id page :data data})}))

(reg-event-fx
  :search!
  (fn [_ [_ query]]
    {:dispatch-n [[:-fetch-transcripts! query]
                  [:-fetch-biases! query]]}))

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

(reg-event-fx
  :-fetch-biases!
  (fn [_ [_ speaker]]
    {:ajax {:method :get
            :uri "/api/speakers/biases"
            :params {:speaker speaker
                     :min_word 500}
            :on-success (fn [response]
                          (dispatch [:-handle-biases! response]))}}))

(reg-event-fx
  :-handle-biases!
  (fn [{db :db} [_ biases]]
    {:db (assoc db :biases biases)}))
