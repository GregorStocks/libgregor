(ns libgregor.db
  (:require [com.stuartsierra.component :as component]
            [libgregor.log :as log]
            [clojure.spec :as s]
            [clojure.set :as set]
            [clojure.java.jdbc :as sql]))

(defn query [database q & args]
  (apply sql/query (:conn database) q args))

(defn execute! [database args]
  (sql/execute! (:conn database) args))

(defn update! [database table set-map where-clause]
  (sql/update! (:conn database) table set-map where-clause))

(defn insert! [database table-name & xs]
  (apply sql/insert! (:conn database) table-name xs))

(defn insert-multi! [database table-name & xs]
  (apply sql/insert-multi! (:conn database) table-name xs))

(defn init-tables! [database]
  (let [conn (:conn database)]
    (log/info "Creating for" database)
    (doseq [[k v] (:table-specs database)]
      (try
        (log/info "Creating" k)
        (sql/db-do-commands conn (sql/create-table-ddl k v))
        (catch Exception e
          (log/warn e))))))

(defn destroy-tables! [database]
  (doseq [[k v] (:table-specs database)]
    (try
      (log/info "Dropping" k)
      (execute! database (sql/drop-table-ddl k))
      (catch Exception e
        (log/warn e)))))

(defn new-connection [config]
  (let [spec (merge
              (case (:db-type config)
                "sqlite" {:classname "org.sqlite.JDBC"
                          :subprotocol "sqlite"
                          :subname (:db-path config)}
                "postgres" {:dbtype "postgresql"
                            :dbname (:db-database config)
                            :host (:db-path config)
                            :port (:db-port config)}
                (throw (ex-info "Unsupported db-type" {:config config})))
              {:user (:db-username config)
               :password (:db-password config)})]
    {:connection (sql/get-connection spec)}))

(defrecord Database [config
                     table-specs]
  component/Lifecycle
  (start [this]
    (let [result (assoc this :conn (new-connection config))]
      (when (:init-db? config)
        (init-tables! result))
      result))

  (stop [this]
    (when-let [c (:conn this)]
      (.close (:connection c)))
    (assoc this :conn nil)))

