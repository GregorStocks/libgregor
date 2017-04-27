(ns libgregor.config
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer (env)]
            [libgregor.log :as log]
            [clojure.spec :as s]))

(s/def :libgregor/default (constantly true))
(s/def :libgregor/config-option (s/keys :opt [:libgregor/default]))
(s/def :libgregor/config-options (s/map-of keyword? :libgregor/config-option))

(defn base-options [app-name]
  {:app-name app-name
   :port {:default 4000}
   :run-server? {:default true}
   :init-db? {:default true}
   :db-type {:default "sqlite"}
   :db-path {:default (str "db/" app-name ".sqlite")}
   :db-port {:default 5432}
   :db-database {}
   :db-username {}
   :db-password {}})

(defn default-config [options]
  (s/assert :libgregor/config-options options)
  (into {} (for [[k v] options] [k (:libgregor/default v)])))

(defn environmental-overrides [app-name options]
  (s/assert :libgregor/config-options options)
  (into {} (for [[k v] options]
             (when-let [v (env k)]
               [k v]))))

(defn make [app-name config-options overrides]
  (s/assert :libgregor/config-options config-options)
  (let [combined (merge (base-options app-name) config-options)]
    (merge (default-config combined)
           overrides
           (environmental-overrides app-name combined))))
