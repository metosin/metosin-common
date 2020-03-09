(ns metosin.dates.generators
  "Based on https://github.com/dm3/clojure.joda-time/blob/master/test/joda_time/generators.clj"
  (:require [clojure.test.check.generators :as g])
  (:import [org.joda.time.chrono ISOChronology]
           [org.joda.time LocalDate DateTime]))

(def default-chronology (ISOChronology/getInstanceUTC))

(def year-of-century
  (g/choose 1 100))

(def year-of-era
  (g/choose 1 10000))

(def month-of-year
  (g/choose 1 12))

(def day-of-week
  (g/choose 1 7))

(def day-of-month
  (g/choose 1 28))

(def hour-of-day
  (g/choose 0 23))

(def minute-of-hour
  (g/choose 0 59))

(def second-of-minute
  (g/choose 0 59))

(def millis-of-second
  (g/choose 0 999))

;;;;;;;;;; Date-Times

(defn- date-time-tuple [& {:keys [chrono] :or {chrono default-chronology}}]
  (g/tuple year-of-era month-of-year day-of-month
           hour-of-day minute-of-hour second-of-minute
           millis-of-second (g/return chrono)))

(defn date-time [& {:keys [chrono] :or {chrono default-chronology}}]
  (g/fmap (partial apply #(DateTime. %1 %2 %3 %4 %5 %6 %7 %8))
          (date-time-tuple :chrono chrono)))

(defn- date-tuple [& _]
  (g/tuple year-of-era month-of-year day-of-month))

(def local-date
  (g/fmap (partial apply #(LocalDate. %1 %2 %3)) (date-tuple)))
