(ns metosin.jdbc.java-time
  (:require [clojure.java.jdbc :as jdbc])
  (:import [java.time ZonedDateTime LocalDate ZoneId]))

(defn init! []
  (extend-protocol jdbc/ISQLValue
    ZonedDateTime
    (sql-value [this]
      (java.sql.Timestamp/valueOf (.toLocalDateTime this)))

    LocalDate
    (sql-value [this]
      (java.sql.Date/valueOf this)))

  (extend-protocol jdbc/IResultSetReadColumn
    java.sql.Timestamp
    (result-set-read-column [x _2 _3]
      (ZonedDateTime/of (.toLocalDateTime x) (ZoneId/of "Z")))

    java.sql.Date
    (result-set-read-column [x _2 _3]
      (.toLocalDate x))))

(init!)
