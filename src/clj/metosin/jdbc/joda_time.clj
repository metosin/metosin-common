(ns metosin.jdbc.joda-time
  (:require [clojure.java.jdbc :as jdbc])
  (:import [org.joda.time DateTime LocalDate DateTimeZone]))

(defn init! []
  (extend-protocol jdbc/ISQLValue
    org.joda.time.DateTime
    (sql-value [this]
      (java.sql.Timestamp. (.getMillis this)))

    org.joda.time.LocalDate
    (sql-value [this]
      (java.sql.Date. (.getMillis (.toDateTimeAtStartOfDay this (DateTimeZone/getDefault))))))

  (extend-protocol jdbc/IResultSetReadColumn
    java.sql.Timestamp
    (result-set-read-column [x _2 _3]
      (DateTime. x))

    java.sql.Date
    (result-set-read-column [x _2 _3]
      (LocalDate/fromDateFields x))))

(init!)

