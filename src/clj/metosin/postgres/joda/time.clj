(ns metosin.postgres.joda.time
  (:require [clj-time.coerce :as tc]
            [clojure.java.jdbc :as jdbc]
            [clj-time.core :as t]))

(extend-protocol jdbc/ISQLValue
  org.joda.time.DateTime
  (sql-value [this]
    (tc/to-sql-time this))

  ; FIXME: Is this sane?
  org.joda.time.LocalDate
  (sql-value [this]
    (tc/to-sql-date this)))

(extend-protocol jdbc/IResultSetReadColumn
  java.sql.Timestamp
  (result-set-read-column [x _2 _3]
    (tc/from-sql-time x))

  java.sql.Date
  (result-set-read-column [x _2 _3]
    (org.joda.time.LocalDate. (.getTime x) (t/default-time-zone))))

; FIXME: Create tests
