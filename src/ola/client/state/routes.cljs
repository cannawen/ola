(ns ola.client.state.routes
  (:require
    [re-frame.core :refer [dispatch]]
    [bloom.omni.fx.router :as router]))

(router/defroute index "/" []
  (dispatch [:set-page! :index]))

(router/defroute search "/search" []
  (dispatch [:set-page! :search]))

(router/defroute search-results "/search/:query" [query]
  (dispatch [:search! query])
  (dispatch [:set-page! :search]))
