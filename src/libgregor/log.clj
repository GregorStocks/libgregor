(ns libgregor.log
  (:require [taoensso.timbre :as log]))

(defmacro info [& xs]
  `(log/info ~@xs))

(defmacro warn [& xs]
  `(log/warn ~@xs))

(defmacro error [& xs]
  `(log/error ~@xs))

(defn init []
  (log/info "Initialized logging."))