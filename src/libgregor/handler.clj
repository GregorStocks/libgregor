(ns libgregor.handler
  (:require [compojure.core :refer [defroutes GET POST ANY context]]
            [compojure.route :as route]
            [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.spec :as s]
            [libgregor.log :as log]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn wrap-logging [app]
  (fn [request]
    (try
      (app request)
      (catch Exception e
        (log/error e)
        {:status 500}))))

(defn wrap-config [app config]
  (fn [request] (app (assoc request :config config))))

(defn wrap-database [app database]
  (fn [request] (app (assoc request :database database))))

(defn make-handler [app config database]
  (-> app
      (wrap-json-response {:pretty true})
      (wrap-json-body {:keywords? true :bigdecimals? true})
      (wrap-content-type)
      (wrap-defaults (dissoc site-defaults :security)) ;; uhhhh todo probably
      wrap-logging
      (wrap-config config)
      (wrap-database database)))

(defrecord App [app config database]
  component/Lifecycle
  (start [this]
    (let [handler (make-handler app config database)
          server (when (:run-server? config)
                   (jetty/run-jetty handler {:port (:port config)
                                             :join? false}))]
      (assoc this :handler handler :server server)))
  (stop [this]
    (when-let [s (:server this)]
      (.stop s)
      (.join s))
    (assoc this :server nil)))
