(ns metosin.dates
  "Use this namespace to format dates and datetimes for user.

  Don't use for serializing or deserializing."
  (:refer-clojure :exclude [format])
  #?(:clj  (:require [metosin.dates.protocols :as p]
                     [metosin.dates.impl :as impl])
     :cljs (:require goog.date.UtcDateTime
                     goog.date.Date
                     goog.i18n.DateTimeFormat
                     goog.i18n.DateTimeParse
                     goog.i18n.TimeZone
                     [goog.string :as gs]
                     [metosin.dates.protocols :as p]
                     [metosin.dates.impl :as impl]))
  #?(:clj  (:import [org.joda.time DateTimeZone]
                    [org.joda.time.format DateTimeFormat])))

#?(:clj (set! *warn-on-reflection* true))

; Default to UTC ALWAYS!
#?(:clj (DateTimeZone/setDefault DateTimeZone/UTC))

;;
;; Types
;;

(def DateTime #?(:clj  org.joda.time.DateTime,
                 :cljs goog.date.UtcDateTime))

(def LocalDate #?(:clj  org.joda.time.LocalDate,
                  :cljs goog.date.Date))

; FIXME: Is this a good idea?
; Required for using dates as keys etc.
#?(:cljs
    (extend-type goog.date.Date
      IEquiv
      (-equiv [o other]
        (and (instance? goog.date.Date other)
             (identical? (.getTime o) (.getTime other))
             (identical? (.getTimezoneOffset o) (.getTimezoneOffset other))))
      IComparable
      (-compare [o other]
        (- (.getTime o) (.getTime other)))))

;;
;; Constructors
;;

(defn date-time
  ([]
   #?(:clj  (org.joda.time.DateTime.)
      :cljs (goog.date.UtcDateTime.)))
  ([x]
   (p/-to-date-time x))
  ([s {:keys [pattern] :as options}]
   (impl/parse-date-time s options))
  ([y m d hh mm]
   #?(:clj  (org.joda.time.DateTime. y m d hh mm)
      :cljs (goog.date.UtcDateTime.  y (dec m) d hh mm)))
  ([y m d hh mm ss]
   #?(:clj  (org.joda.time.DateTime. y m d hh mm ss)
      :cljs (goog.date.UtcDateTime.  y (dec m) d hh mm ss))))

(defn date
  ([]
   #?(:clj  (org.joda.time.LocalDate.)
      :cljs (goog.date.Date.)))
  ([x]
   (p/-to-date x))
  ([s {:keys [pattern] :as options}]
   (impl/parse-local-date s options))
  ([y m d]
   #?(:clj  (org.joda.time.LocalDate. y m d)
      :cljs (goog.date.Date. y (dec m) d))))

(defn to-string
  "Returns good (e.g. RFC3339 or YYYY-MM-DD) representation for given object."
  [this]
  (p/-to-string this))

(defn to-native [this]
  (p/-to-native this))

(defn with-zone [date timezone-id]
  (p/-with-zone date timezone-id))

;;
;; Format
;;

(defn format [date {:keys [pattern timezone] :as options}]
  (p/-format date options))

;;
;; Utilities
;;

(defn start-of-week [date]
  #?(:cljs (doto (.clone date)
             (.setDate (- (.getDate date) (.getIsoWeekday date))))
     :clj  (.withMinimumValue (.dayOfWeek date))))

(defn end-of-week [date]
  #?(:cljs (doto (.clone date)
             (.setDate (+ (.getDate date) (- 6 (.getIsoWeekday date)))))
     :clj  (.withMaximumValue (.dayOfWeek date))))

;; TODO:
;; start-of-month
;; end-of-month
;; start-of-year?
;; end-of-year?
;; Better API for these 6 calls?

(defn add [date interval]
  (p/-add date interval))

(defn minus [date interval]
  (p/-minus date interval))

(defn years
  ([] (years 1))
  ([n]
   #?(:cljs (goog.date.Interval. goog.date.Interval.YEARS n)
      :clj  (org.joda.time.Years/years n))))

(defn months
  ([] (months 1))
  ([n]
   #?(:cljs (goog.date.Interval. goog.date.Interval.MONTHS n)
      :clj  (org.joda.time.Months/months n))))

(defn weeks
  ([] (weeks 1))
  ([n]
   #?(:cljs (goog.date.Interval. goog.date.Interval.DAYS (* 7 n))
      :clj  (org.joda.time.Weeks/weeks n))))

(defn days
  ([] (days 1))
  ([n]
   #?(:cljs (goog.date.Interval. goog.date.Interval.DAYS n)
      :clj  (org.joda.time.Days/days n))))

(defn hours
  ([] (hours 1))
  ([n]
   #?(:cljs (goog.date.Interval. goog.date.Interval.HOURS n)
      :clj  (org.joda.time.Hours/hours n))))

(defn minutes
  ([] (days 1))
  ([n]
   #?(:cljs (goog.date.Interval. goog.date.Interval.MINUTES n)
      :clj  (org.joda.time.Minutes/minutes n))))

(defn seconds
  ([] (days 1))
  ([n]
   #?(:cljs (goog.date.Interval. goog.date.Interval.SECONDS n)
      :clj  (org.joda.time.Seconds/seconds n))))

;;
;; "Legacy api"
;;

(def date-fmt {:pattern "d.M.yyyy"})
(def date-time-fmt {:pattern "d.M.yyyy H:mm"
                    :timezone "Europe/Helsinki"})

(defn date->str [d]
  (if d
    (format d date-fmt)))

(defn date-time->str [d]
  (if d
    (format d date-time-fmt)))
