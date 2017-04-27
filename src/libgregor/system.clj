(ns libgregor.system
  (:require [com.stuartsierra.component :as component]
            [clojure.spec :as s]
            [libgregor.config :as config]
            [libgregor.db :as db]
            [libgregor.handler :as handler]))

(defn build-system [{:keys [app-name
                             config-spec
                             table-specs
                             app
                             overrides]
                      :as system-spec}]
  (component/system-map
   :config (config/make app-name config-spec overrides)
   :database (component/using (db/map->Database {:table-specs table-specs}) [:config])
   :app (component/using (handler/map->App {:app app}) [:database :config])))

(defn run [system-spec]
  (s/check-asserts true)
  (component/start (build-system system-spec)))