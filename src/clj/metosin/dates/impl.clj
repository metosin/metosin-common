(ns metosin.dates.impl
  (:refer-clojure :exclude [format])
  (:require [metosin.dates.protocols :as p])
  (:import [org.joda.time DateTime LocalDate DateTimeZone]
           [org.joda.time.format DateTimeFormat]))

(defn formatter' [f]
  (DateTimeFormat/forPattern f))

(def formatter (memoize formatter'))

(defn parser' [f]
  (DateTimeFormat/forPattern f))

(def parser (memoize parser'))

(defn format [this {:keys [pattern timezone]}]
  (let [date (p/-with-zone this timezone)
        formatter (DateTimeFormat/forPattern pattern)]
    (.toString date formatter)))

(defn parse-date-time [this {:keys [pattern]}]
  (DateTime/parse this (parser pattern)))

(defn parse-local-date [this {:keys [pattern]}]
  (LocalDate/parse this (parser pattern)))

(extend-type DateTime
  p/IDateLike
  (-add [this interval]
    (.plus this interval))
  (-minus [this interval]
    (.minus this interval))
  (-with-zone [this timezone-id]
    (.withZone this (DateTimeZone/forID timezone-id)))
  (-format [this options]
    (format this options))

  p/ToNative
  (-to-native [this]
    (.toDate this))

  p/ToString
  (-to-string [this]
    (.toString (.withZone this (org.joda.time.DateTimeZone/forID "UTC")))))

(extend-type LocalDate
  p/IDateLike
  (-add [this interval]
    (.plus this interval))
  (-minus [this interval]
    (.minus this interval))
  (-with-zone [this _]
    this)
  (-format [this options]
    (format this options))

  p/ToNative
  (-to-native [this]
    (.toDate (.toDateTimeAtStartOfDay this)))

  p/ToString
  (-to-string [this]
    (.toString this)))

(extend-protocol p/ToDateTime
  java.util.Date
  (-to-date-time [this]
    (org.joda.time.DateTime. this))

  org.joda.time.LocalDate
  (-to-date-time [this]
    (org.joda.time.DateTime. (.getYear this) (.getMonthOfYear this) (.getDayOfMonth this) 0 0))

  String
  (-to-date-time [this]
    (org.joda.time.DateTime/parse this)))

(extend-protocol p/ToDate
  java.util.Date
  (-to-date [this]
    (org.joda.time.LocalDate/fromDateFields this))
  org.joda.time.DateTime
  (-to-date [this]
    (.toLocalDate this))
  String
  (-to-date [this]
    (org.joda.time.LocalDate/parse this)))
