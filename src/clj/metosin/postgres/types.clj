(ns metosin.postgres.types
  (:require [cheshire.core :as json]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as cs]
            [clojure.test :refer :all])
  (:import [org.postgresql.util PGobject]))

(defn ->PGobject [type value]
  (doto (PGobject.)
    (.setType type)
    (.setValue value)))

;;
;; From Clojure to Postgres
;;

; ISQLValue is used when data is being inserted into JDBC
; ISQLValue is also used as default if there is no ISQLParameter
; implementation.

;; Marker for wrapping inserted values to wanted Postgres type
(defrecord PgType [type value])

(extend-protocol jdbc/ISQLValue
  PgType
  (sql-value [this]
    (->PGobject (:type this) (:value this)))

  clojure.lang.IPersistentMap
  (sql-value [this]
    (->PGobject "json" (json/generate-string this)))

  clojure.lang.IPersistentVector
  (sql-value [this]
    (->PGobject "json" (json/generate-string this))))

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
