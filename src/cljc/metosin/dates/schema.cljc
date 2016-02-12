(ns metosin.dates.schema
  (:require [metosin.dates :as dates]))

(defn local-date-coercion-matcher
  "Coerces well formatted date to string to LocalDate"
  [schema]
  (if (= dates/LocalDate schema)
    (fn [x]
      (if (string? x)
        (dates/date x)
        x))))

(defn date-time-coercion-matcher
  "Coerces well formatted date-time string to DateTime"
  [schema]
  (if (= dates/DateTime schema)
    (fn [x]
      (if (string? x)
        (dates/date-time x)
        x))))

(defn date-coercion-matcher [schema]
  (or (local-date-coercion-matcher schema)
      (date-time-coercion-matcher schema)))
