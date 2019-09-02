(ns metosin.postgres.types
  (:require [cheshire.core :as json]
            [clojure.java.jdbc :as jdbc])
  (:import [org.postgresql.util PGobject]))

(defn ->PGobject [type value]
  (doto (PGobject.)
    (.setType type)
    (.setValue value)))

;;
;; From Clojure to Postgres
;;

(defn write-json
  "Write a value to Postgres JSON field."
  [x]
  (->PGobject "json" (json/generate-string x)))

;;
;; From Postgres to Clojure
;;

(defn handle-keyword [^PGobject x]
  (let [s (.getValue x)]
    (keyword (.substring s 1 (dec (count s))))))

(defmulti pgobject->clj (fn [^PGobject x] (.getType x)))

(defmethod pgobject->clj "json" [^PGobject x]
  (json/parse-string (.getValue x) true))

(defmethod pgobject->clj "jsonb" [^PGobject x]
  (json/parse-string (.getValue x) true))

(defmethod pgobject->clj :default [x] x)

(extend-protocol jdbc/IResultSetReadColumn
  PGobject
  (result-set-read-column [x rsmeta idx]
    (pgobject->clj x)))
