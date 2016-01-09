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

; This is required, because the sql-value dispatcher gets the value "keyword"
; only when using the same connection that was used to create the type. After
; resetting the connection, the dispatcher sometimes gets "app.keyword"
; and sometimes it gets "keyword"
(def keyword-type-name "keyword")
(def keyword-type-long-name "\"app\".\"keyword\"")

(extend-protocol jdbc/ISQLValue
  clojure.lang.Keyword
  (sql-value [this]
    (let [k (if (namespace this)
              (str  (namespace this) "/" (name this))
              (name this))]
      (->PGobject keyword-type-long-name (str "(" k ")"))))

  clojure.lang.IPersistentMap
  (sql-value [this]
    (->PGobject "json" (json/generate-string this)))

  clojure.lang.IPersistentVector
  (sql-value [this]
    (->PGobject "json" (json/generate-string this))))

;;
;; From Postgres to Clojure
;;

(defn handle-keyword [x]
  (let [s (.getValue x)]
    (keyword (.substring s 1 (dec (count s))))))

(defmulti pgobject->clj (fn [x] (.getType x)))

(defmethod pgobject->clj "json" [x]
  (json/parse-string (.getValue x) true))

(defmethod pgobject->clj keyword-type-name [x]
  (handle-keyword x))

(defmethod pgobject->clj keyword-type-long-name [x]
  (handle-keyword x))

(defmethod pgobject->clj :default [x] x)

(extend-protocol jdbc/IResultSetReadColumn
  PGobject
  (result-set-read-column [x rsmeta idx]
    (pgobject->clj x)))

; FIXME: Move to test file
(deftest pgobject->clj-test
  (testing "keyword"
    (is (= :foo/bar (pgobject->clj (->PGobject keyword-type-name "(foo/bar)"))))
    (is (= :foo/bar (pgobject->clj (jdbc/sql-value :foo/bar))))
    (is (= :bar (pgobject->clj (->PGobject keyword-type-name "(bar)"))))
    (is (= :bar (pgobject->clj (jdbc/sql-value :bar)))))

  (testing "json"
    (is (= {:foo "a"} (pgobject->clj (->PGobject "json" "{\"foo\":\"a\"}"))))
    (is (= {:foo "a"} (pgobject->clj (jdbc/sql-value {:foo "a"}))))
    (is (= ["a" "b"] (pgobject->clj (->PGobject "json" "[\"a\",\"b\"]"))))
    (is (= ["a" "b"] (pgobject->clj (jdbc/sql-value ["a" "b"]))))
    ))
