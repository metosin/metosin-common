(ns metosin.postgres.types
  (:require [jsonista.core :as json]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as cs]
            [clojure.test :refer :all])
  (:import [org.postgresql.util PGobject]))

(def keyword-mapper (json/object-mapper {:encode-key-fn true
                                         :decode-key-fn true}))

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
  (->PGobject "json" (json/write-value-as-string x keyword-mapper)))

;;
;; From Postgres to Clojure
;;

(defn handle-keyword [^PGobject x]
  (let [s (.getValue x)]
    (keyword (.substring s 1 (dec (count s))))))

(defmulti pgobject->clj (fn [^PGobject x] (.getType x)))

(defmethod pgobject->clj "json" [^PGobject x]
  (json/read-value (.getValue x) keyword-mapper))

(defmethod pgobject->clj "jsonb" [^PGobject x]
  (json/read-value (.getValue x) keyword-mapper))

(defmethod pgobject->clj :default [x] x)

(extend-protocol jdbc/IResultSetReadColumn
  PGobject
  (result-set-read-column [x rsmeta idx]
    (pgobject->clj x)))
