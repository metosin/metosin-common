(ns metosin.dates
  "Use this namespace to format dates and datetimes for user."
  (:refer-clojure :exclude [format])
  #?(:cljs (:require-macros metosin.dates))
  #?(:cljs (:require [goog.string :as gs]))
  #?(:clj  (:import [org.joda.time DateTimeZone]
                    [org.joda.time.format DateTimeFormat]
                    [java.util Locale])
     :cljs (:import [goog.date Date UtcDateTime Interval]
                    [goog.i18n DateTimeFormat DateTimeParse TimeZone])))

; Default to UTC ALWAYS!
#?(:clj (DateTimeZone/setDefault DateTimeZone/UTC))

;;
;; Types
;;

(def DateTime #?(:clj  org.joda.time.DateTime,
                 :cljs goog.date.UtcDateTime))

(def LocalDate #?(:clj  org.joda.time.LocalDate,
                  :cljs goog.date.Date))

;;
;; RFC3339 conversions
;;

(defn- write-date-time
  "Represent DateTime in RFC3339 format string."
  [d]
  #?(:clj  (.toString (.withZone ^org.joda.time.DateTime d (org.joda.time.DateTimeZone/forID "UTC")))
     :cljs (str (.getUTCFullYear d)
                "-" (gs/padNumber (inc (.getUTCMonth d)) 2)
                "-" (gs/padNumber (.getUTCDate d) 2)
                "T" (gs/padNumber (.getUTCHours d) 2)
                ":" (gs/padNumber (.getUTCMinutes d) 2)
                ":" (gs/padNumber (.getUTCSeconds d) 2)
                "." (gs/padNumber (.getUTCMilliseconds d) 3)
                "Z")))

(defn- read-date-time
  "Read RFC3339 string to DateTime."
  [s]
  #?(:clj  (org.joda.time.DateTime/parse s)
     :cljs (goog.date.UtcDateTime/fromIsoString s)))

(defn- write-local-date
  "Represent Date in YYYY-MM-DD format."
  [x]
  #?(:clj  (.toString ^org.joda.time.LocalDate x)
     :cljs (.toIsoString ^Date x true false)))

(defn- read-local-date
  "Read Date in YYYY-MM-DD format."
  [x]
  #?(:clj  (org.joda.time.LocalDate/parse x)
     :cljs (let [[_ y m d] (re-find #"(\d{4})-(\d{2})-(\d{2})" x)]
             (goog.date.Date. (long y) (dec (long m)) (long d)))))

;;
;; Conversions
;;

(defprotocol ToNative
  (to-native [x] "Convers to native Date object (java.util.Date or js/Date)."))

#?(:cljs
   (extend-protocol ToNative
     goog.date.Date
     (to-native [x]
       (let [d (js/Date. (.getYear x) (.getMonth x) (.getDate x) 0 0 0 0)]
         (.setMinutes d (- (.getMinutes d) (.getTimezoneOffset d)))
         d))
     goog.date.UtcDateTime
     (to-native [x]
       ; Will create js/Date in local time zone.
       ; Manually convert to UTC. x.getTimezoneOffset can't be used because it's zero for UtcDateTime.
       (let [d (js/Date. (.getYear x) (.getMonth x) (.getDate x) (.getHours x) (.getMinutes x) (.getSeconds x) (.getMilliseconds x))]
         (.setMinutes d (- (.getMinutes d) (.getTimezoneOffset d)))
         d))
     js/Date
     (to-native [x]
       x))
   :clj
   (extend-protocol ToNative
     org.joda.time.DateTime
     (to-native [x]
       (.toDate x))
     org.joda.time.LocalDate
     (to-native [x]
       ; LocalDate toDate creates date in local timezone, that is Helsinki
       (.toDate (.toDateTimeAtStartOfDay x)))
     java.util.Date
     (to-native [x]
       x)))

(defprotocol ToDateTime
  (-to-date-time [x] "Convers Date or such to DateTime."))

#?(:cljs
   (extend-protocol ToDateTime
     js/Date
     (-to-date-time [x]
       (goog.date.UtcDateTime. x))
     goog.date.Date
     (-to-date-time [x]
       (goog.date.UtcDateTime. (.getYear x) (.getMonth x) (.getDate x)))
     string
     (-to-date-time [x]
       (read-date-time x)))
   :clj
   (extend-protocol ToDateTime
     java.util.Date
     (-to-date-time [x]
       (org.joda.time.DateTime. x))
     org.joda.time.LocalDate
     (-to-date-time [x]
       (org.joda.time.DateTime. (.getYear x) (.getMonthOfYear x) (.getDayOfMonth x) 0 0))
     String
     (-to-date-time [x]
       (read-date-time x))))

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
     String
     (-to-date [x]
       (read-local-date x)))
   :cljs
   (extend-protocol ToDate
     js/Date
     (-to-date [x]
       (goog.date.Date. x))
     goog.date.UtcDateTime
     (-to-date [x]
       (goog.date.Date. (.getYear x) (.getMonth x) (.getDate x)))
     string
     (-to-date [x]
       (read-local-date x))))

(defprotocol ToString
  (-to-string [x] "Converts object to good date string representation"))

#?(:clj
   (extend-protocol ToString
     org.joda.time.DateTime
     (-to-string [x]
       (write-date-time x))
     org.joda.time.LocalDate
     (-to-string [x]
       (write-local-date x)))
   :cljs
   (extend-protocol ToString
     goog.date.UtcDateTime
     (-to-string [x]
       (write-date-time x))
     goog.date.Date
     (-to-string [x]
       (write-local-date x))))

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

(declare get-locale)

(defn- formatter'
  [pattern locale]
  #?(:cljs (goog.i18n.DateTimeFormat. pattern (get-locale locale))
     :clj  (cond-> (DateTimeFormat/forPattern pattern)
             locale (.withLocale (get-locale locale)))))

;; TODO: fix place
"Creates formatter object for underlying implementation.

  Optional locale can be given as second argument.
  If locale is keyword, it will be used to retrieve relevant
  locale value. Or implementation value can be used directly:
  For clj this should be `java.util.Locale` instance.
  For cljs this should be `goog.i18n.DateTimeSymbols`,
  e.g. `goog.i18n.DateTimeSymbols_fi`."

(def ^:private formatter
  (memoize formatter'))

(defn- parser' [pattern locale]
  #?(:cljs (goog.i18n.DateTimeParse. pattern (get-locale locale))
     :clj  (cond-> (DateTimeFormat/forPattern pattern)
             locale (.withLocale (get-locale locale)))))

(def ^:private parser (memoize parser'))

;;
;; Timezone stuff
;; Cljs needs magic
;;

#?(:cljs (def timezones (atom {})))

#?(:clj (defn closure-timezone
          "Builds Closure timeZoneData map from JodaTime timezone."
          [timezone-id]
          ; Note: Java8 doesn't have nextTransitions so would be harder to implement with it?
          (let [tz (DateTimeZone/forID timezone-id)
                ms->m #(/ % (* 1000 60))
                ms->h #(/ % (* 1000 60 60))
                std-offset (.getStandardOffset tz 0)
                transitions (mapcat (fn [^long ms]
                                      [(ms->h ms) (ms->m (- (.getOffset tz ms) std-offset))])
                                    ; FIXME: Magic number
                                    (take 137 (iterate #(.nextTransition tz %) 0)))
                ; Skip first two items if they are both zero
                transitions (if (and (zero? (first transitions))
                                     (zero? (second transitions)))
                              (drop 2 transitions)
                              transitions)
                first-transition (.nextTransition tz 0)]
            {:id timezone-id
             :std_offset (ms->m std-offset)
             :names [(.getShortName tz 0) (.getName tz 0)
                     ; First transition should be summer time?
                     (.getShortName tz first-transition) (.getName tz first-transition)]
             :transitions (vec transitions)})))

#?(:clj (defmacro initialize-timezone!
          "Initializes given timezone for ClojureScript use."
          [timezone-id]
          `(swap! timezones assoc ~timezone-id (goog.i18n.TimeZone.createTimeZone (~'#'clj->js ~(closure-timezone timezone-id))))))

(defn- timezone' [^String timezone-id]
  #?(:clj (DateTimeZone/forID timezone-id)
     :cljs (or (get @timezones timezone-id)
               (throw (js/Error. (str "Can't find timezone \"" timezone-id "\". Did you remember to initialize it?"))))))

(def ^:private timezone (memoize timezone'))

#?(:cljs (def locales (atom {})))

(defn ^:private get-locale [locale-name]
  #?(:clj (Locale. (name locale-name))
     :cljs (get @locales locale-name)))

#?(:cljs (defn initialize-locale! [locale-name symbols]
           (swap! locales assoc locale-name symbols)))

;;
;; Constructors
;;

(defn #?(:clj ^org.joda.time.DateTime date-time :cljs date-time)
  "For pattern and locale options, check `format` docstring."
  ([]
   #?(:clj  (org.joda.time.DateTime.)
      :cljs (goog.date.UtcDateTime.)))
  ([x]
   (-to-date-time x))
  ([s {:keys [pattern locale]}]
   #?(:cljs (let [date (goog.date.UtcDateTime. 0 0 0 0 0 0 0)]
              (.strictParse ^DateTimeParse (parser pattern locale) s date)
              date)
      :clj  (org.joda.time.DateTime/parse s (parser pattern locale))))
  ([y m d hh mm]
   #?(:clj  (org.joda.time.DateTime. y m d hh mm)
      :cljs (goog.date.UtcDateTime.  y (dec m) d hh mm)))
  ([y m d hh mm ss]
   #?(:clj  (org.joda.time.DateTime. y m d hh mm ss)
      :cljs (goog.date.UtcDateTime.  y (dec m) d hh mm ss)))
  ([y m d hh mm ss millis]
   #?(:clj  (org.joda.time.DateTime. y m d hh mm ss millis)
      :cljs (goog.date.UtcDateTime.  y (dec m) d hh mm ss millis))))

(defn #?(:clj ^org.joda.time.LocalDate date :cljs date)
  "For pattern and locale options, check `format` docstring."
  ([]
   #?(:clj  (org.joda.time.LocalDate.)
      :cljs (goog.date.Date.)))
  ([x]
   (-to-date x))
  ([s {:keys [pattern locale]}]
   #?(:cljs (let [date (goog.date.Date. 0 0 0)]
              (.strictParse ^DateTimeParse (parser pattern locale) s date)
              date)
      :clj  (org.joda.time.LocalDate/parse s (parser pattern locale))))
  ([y m d]
   #?(:clj  (org.joda.time.LocalDate. y m d)
      :cljs (goog.date.Date. y (dec m) d))))

(defn to-string
  "Returns good (e.g. RFC3339 or YYYY-MM-DD) representation for given object."
  [x]
  (-to-string x))

(defn with-zone [d timezone-id]
  (if timezone-id
    #?(:clj  (.withZone d (timezone timezone-id))
       :cljs (let [tz (timezone timezone-id)
                   offset (- (.getOffset tz d))]
               ; Doesn't change date timeZone, as it's not possible in JS
               (doto (goog.date.UtcDateTime. d)
                 (.add (goog.date.Interval. goog.date.Interval.MINUTES offset)))))
    d))

;;
;; Format
;;

(defn format
  "Pattern is required.

  Locale and timezone are optional options.

  Locale can be given as keyword, and it will be used to retrieve implementation
  specific Locale.

  For cljs use, locales have to be register first using
  `(initalize-locale! name symbols)` call, where symbols is e.g.
  `goog.i18n.DateTimeSymbols_fi`.

  For cljs use, timezones have to be registered using
  `(initalize-timezone! name)` call, where name is e.g. \"Europe/Helsinki\""
  [x {:keys [pattern locale timezone]}]
  (if x
    (let [x (with-zone x timezone)
          f (formatter pattern locale)]
      #?(:cljs (.format f x)
         :clj  (.toString x f)))))

;;
;; Utilities
;;

(defn start-of-day [date-time]
  ;; FIXME: Should handle DST change, when first hour is 01
  #?(:cljs (doto (.clone date-time)
             (.setHours 0)
             (.setMinutes 0)
             (.setSeconds 0)
             (.setMilliseconds 0))
     :clj  (.withTimeAtStartOfDay date-time)))

(defn end-of-day [date-time]
  #?(:cljs (doto (.clone date-time)
             (.setHours 23)
             (.setMinutes 59)
             (.setSeconds 59)
             (.setMilliseconds 999))
     :clj  (.withMaximumValue (.millisOfDay date-time))))

(defn start-of-week [date]
  #?(:cljs (doto (.clone date)
             (.setDate (- (.getDate date) (.getIsoWeekday ^Date date))))
     :clj  (.withMinimumValue (.dayOfWeek date))))

(defn end-of-week [date]
  #?(:cljs (doto (.clone date)
             (.setDate (+ (.getDate date) (- 6 (.getIsoWeekday ^Date date)))))
     :clj  (.withMaximumValue (.dayOfWeek date))))

(defn start-of-month [date]
  #?(:cljs (doto (.clone date)
             (.setDate 1))
     :clj  (.withMinimumValue (.dayOfMonth date))))

(defn end-of-month [date]
  #?(:cljs (doto (.clone date)
             (.setDate (.getNumberOfDaysInMonth ^Date date)))
     :clj  (.withMaximumValue (.dayOfMonth date))))

(defn start-of-year [date]
  #?(:cljs (doto (.clone date)
             (.setMonth 0)
             (.setDate 1))
     :clj  (.withMinimumValue (.dayOfYear date))))

(defn end-of-year [date]
  #?(:cljs (let [decemeber (doto (.clone date)
                              (.setMonth 11))]
             (.setDate decemeber (.getNumberOfDaysInMonth ^Date decemeber))
             decemeber)
     :clj  (.withMaximumValue (.dayOfYear date))))


;; missing min/max properties:
;; - century of era
;; - day of year
;; - hour of day (keep minutes etc.)
;; - millis of second
;; - minute of day
;; - minute of hour
;; - month of year
;; - second of day (keep millis)
;; - second of minute
;; - week of weekyear
;; - weekyear
;; - year
;; - year of century
;; - year of era

;; Better API for these 6 calls?

(defn plus [date x]
  #?(:cljs
     (doto (.clone date)
       (.add x))
     :clj
     (.plus date x)))

(defn minus [date x]
  #?(:cljs
     (doto (.clone date)
       (.add (.getInverse ^Interval x)))
     :clj
     (.minus date x)))

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
  ([] (minutes 1))
  ([n]
   #?(:cljs (goog.date.Interval. goog.date.Interval.MINUTES n)
      :clj  (org.joda.time.Minutes/minutes n))))

(defn seconds
  ([] (seconds 1))
  ([n]
   #?(:cljs (goog.date.Interval. goog.date.Interval.SECONDS n)
      :clj  (org.joda.time.Seconds/seconds n))))

;;
;; Predicates
;;

(defn date?
  "Test if x is date or date-time (as they extend date) as defined by this namespace.

  Platform dependent libraries might have other date objects, but only the
  exact classes used by this namespace are accepted."
  [x]
  #?(:cljs (or (instance? goog.date.Date x) (instance? goog.date.UtcDateTime x))
     :clj (or (instance? org.joda.time.LocalDate x) (instance? org.joda.time.DateTime x))))

(defn date-time?
  "Test if x is date-time as defined by this namespace.

  Platform dependent libraries might have other date-time objects, but only the
  exact classes used by this namespace are accepted."
  [x]
  #?(:cljs (instance? goog.date.UtcDateTime x)
     :clj (instance? org.joda.time.DateTime x)))

(defn before?
  "Defined only for Date objects. Does nil check."
  [a b]
  (and a b #?(:clj (.isBefore a b)
              :cljs (neg? (goog.date.Date/compare a b)))))

(defn after?
  "Defined only for Date objects. Does nil check."
  [a b]
  (and a b #?(:clj (.isAfter a b)
              :cljs (pos? (goog.date.Date/compare a b)))))

(defn equal?
  "Defined only for Date objects. Does nil check."
  [a b]
  (and a b #?(:clj (.isEqual a b)
              :cljs (zero? (goog.date.Date/compare a b)))))
