(defproject gregor-stocks/libgregor "0.2.0-SNAPSHOT"
  :description "Stuff Gregor uses"
  :url "https://github.com/GregorStocks/libgregor"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [compojure "1.5.1"]
                 [ring/ring-core "1.5.0"]
                 [ring/ring-jetty-adapter "1.5.0"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-json "0.4.0"]
                 [com.taoensso/timbre "4.8.0"]
                 [ring-accept-param "0.1.1"]
                 [clj-http "3.4.1"]
                 [org.clojure/data.json "0.2.6"]
                 [com.stuartsierra/component "0.3.2"]
                 [org.clojure/java.jdbc "0.7.0-alpha3"]
                 [org.xerial/sqlite-jdbc "3.16.1"]
                 [org.postgresql/postgresql "42.0.0"]
                 [environ "1.1.0"]])
