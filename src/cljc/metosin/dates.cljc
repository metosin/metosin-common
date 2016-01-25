(ns metosin.dates
  "Use this namespace to format dates and datetimes for user.

  Don't use for serializing or deserializing.

  Clojure side uses always Helsinki timezone.
  On Cljs side, uses the timezone of browser."
  (:refer-clojure :exclude [format])
  #?(:cljs (:require goog.date.UtcDateTime
                     goog.date.Date
                     goog.i18n.DateTimeFormat
                     goog.i18n.DateTimeParse))
  #?(:clj  (:import [org.joda.time DateTimeZone]
                    [org.joda.time.format DateTimeFormat])))

; Default to UTC ALWAYS!
#?(:clj (DateTimeZone/setDefault DateTimeZone/UTC))

; FIXME: No hardcoding
; Maybe we should have own formatter type which contains this?
#?(:clj (def helsinki-tz (DateTimeZone/forID "Europe/Helsinki")))

;;
;; Types
;;

(def DateTime #?(:clj  org.joda.time.DateTime,
                 :cljs goog.date.UtcDateTime))
(def LocalDate #?(:clj  org.joda.time.LocalDate,
                  :cljs goog.date.Date))

;;
;; Conversions
;;

(defprotocol ToNative
  (to-native [x] "Convers to native Date object (java.util.Date or js/Date)."))

#?(:cljs
    (extend-protocol ToNative
      goog.date.Date
      (to-native [x]
        ; FIXME: broken
        (js/Date. (.getYear x) (.getMonth x) (.getDate x) 0 0 0 0))
      goog.date.UtcDateTime
      (to-native [x]
        ; FIXME: broken
        (js/Date. (.getYear x) (.getMonth x) (.getDate x) (.getHours x) (.getMinutes x) (.getSeconds x) (.getMilliseconds x))))
   :clj
    (extend-protocol ToNative
      org.joda.time.DateTime
      (to-native [x]
        (.toDate x))
      org.joda.time.LocalDate
      (to-native [x]
        ; LocalDate toDate creates date in local timezone, that is Helsinki
        (.toDate (.toDateTimeAtStartOfDay x)))))

(defprotocol ToDateTime
  (-to-date-time [x] "Convers Date or such to DateTime."))

#?(:cljs
   (extend-protocol ToDateTime
     js/Date
     (-to-date-time [x]
       (goog.date.UtcDateTime. x))
     goog.date.Date
     (-to-date-time [x]
       (goog.date.UtcDateTime. (.getYear x) (.getMonth x) (.getDate x))))
   :clj
   (extend-protocol ToDateTime
     java.util.Date
     (-to-date-time [x]
       (org.joda.time.DateTime. x))
     org.joda.time.LocalDate
     (-to-date-time [x]
       (org.joda.time.DateTime. (.getYear x) (.getMonthOfYear x) (.getDayOfMonth x) 0 0))))

(defprotocol ToDate
  (-to-date [x] "Convers DateTime or such to Date."))

#?(:clj
   (extend-protocol ToDate
     java.util.Date
     (-to-date [x]
       (org.joda.time.LocalDate/fromDateFields x))
     org.joda.time.DateTime
     (-to-date [x]
       (.toLocalDate x))
     nil
     (-to-date [x]
       nil))
   :cljs
   (extend-protocol ToDate
     js/Date
     (-to-date [x]
       (goog.date.Date. x))
     goog.date.UtcDateTime
     (-to-date [x]
       (goog.date.Date. (.getYear x) (.getMonth x) (.getDate x)))))

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
;; Formatter and parser constructors, private.
;; these turn pattern values into low level implementations.
;;

(defn- formatter' [f]
  #?(:cljs (goog.i18n.DateTimeFormat. f)
     :clj  (DateTimeFormat/forPattern f)))

(def ^:private formatter (memoize formatter'))

(defn- parser' [f]
  #?(:cljs (goog.i18n.DateTimeParse. f)
     :clj  (DateTimeFormat/forPattern f)))

(def ^:private parser (memoize parser'))

;;
;; Constructors
;;

(defn date-time
  ([x]
   (-to-date-time x))
  ([s pattern]
   #?(:cljs (let [d (goog.date.UtcDateTime. 0 0 0 0 0 0 0)]
              (.strictParse (parser pattern) s d)
              d)
      :clj  (org.joda.time.DateTime/parse s (parser pattern))))
  ([y m d hh mm]
   #?(:clj  (org.joda.time.DateTime. y m d hh mm)
      :cljs (goog.date.UtcDateTime.  y (dec m) d hh mm)))
  ([y m d hh mm ss]
   #?(:clj  (org.joda.time.DateTime. y m d hh mm ss)
      :cljs (goog.date.UtcDateTime.  y (dec m) d hh mm ss))))

(defn date
  ([x]
   (-to-date x))
  ([s pattern]
   #?(:cljs (let [d (goog.date.Date. 0 0 0)]
              (.strictParse (parser pattern) s d)
              d)
      :clj  (org.joda.time.LocalDate/parse s (parser pattern))))
  ([y m d]
   #?(:clj  (org.joda.time.LocalDate. y m d)
      :cljs (goog.date.Date. y (dec m) d))))

(defn now []
  #?(:clj  (org.joda.time.DateTime.)
     :cljs (goog.date.UtcDateTime.)))

(defn today []
  #?(:clj  (org.joda.time.LocalDate.)
     :cljs (goog.date.Date.)))

;;
;; Format
;;

(defn format [x pattern]
  (let [f (formatter pattern)]
    #?(:cljs (.format f x)
       :clj  (.toString x f))))

;;
;; Utilities
;;

(defn start-of-week [date]
  #?(:cljs (goog.date.Date. (.getYear date) (.getMonth date) (- (.getDate date) (.getIsoWeekday date)))
     :clj  (.withMinimumValue (.dayOfWeek date))))

(defn end-of-week [date]
  #?(:cljs (goog.date.Date. (.getYear date) (.getMonth date) (+ (.getDate date) (- 6 (.getIsoWeekday date))))
     :clj  (.withMaximumValue (.dayOfWeek date))))

;; TODO:
;; start-of-month
;; end-of-month
;; start-of-year?
;; end-of-year?
;; Better API for these 6 calls?

(defn add [date x]
  {:pre [#?(:cljs (instance? goog.date.Interval x))]}
  #?(:cljs
      (let [n (.clone date)]
        (.add n x)
        n)
     :clj
     (.plus date x)))

(defn days [n]
  #?(:cljs (goog.date.Interval. goog.date.Interval.DAYS n)
     :clj  (org.joda.time.Days/days n)))

;; TODO:
;; minus
;; years
;; months
;; hours
;; minutes
;; seconds
;; milliseconds?

;;
;; "Legacy api"
;;

(def date-fmt "d.M.yyyy")
(def date-time-fmt "d.M.yyyy H:mm")

(defn date->str [d]
  (if d
    (format d date-fmt)))

(defn date-time->str [d]
  (if d
    (format
      #?(:clj  (.withZone d helsinki-tz)
         :cljs (goog.date.DateTime. d))
      date-time-fmt)))
