(ns metosin.dates
  "Use this namespace to format dates and datetimes for user.

   Don't use for serializing or deserializing.

   Clojure side uses always Helsinki timezone.
   On Cljs side, uses the timezone of browser."
  #?(:clj  (:require [clj-time.format :as f]
                     [clj-time.core :as t])
     :cljs (:require [cljs-time.format :as f]
                     [cljs-time.core :as t]))
  #?(:clj  (:import [org.joda.time DateTime DateTimeZone LocalDate])
     :cljs (:import [goog.date Date])))

#?(:clj (def helsinki-tz (DateTimeZone/forID "Europe/Helsinki")))
(def date-fmt       (f/formatter-local "d.M.yyyy"))
(def date-time-fmt  (f/formatter-local "d.M.yyyy H:mm"))

(defn date [y m d]
  #?(:clj  (LocalDate. y m d)
     :cljs (Date. y m d)))

(defn now []
  (t/now))

(defn date->str [d]
  (if d
    (f/unparse-local-date date-fmt d)))

(defn date-time->str [d]
  (if d
    (f/unparse date-time-fmt #?(:clj (t/to-time-zone d helsinki-tz)
                                :cljs (t/to-default-time-zone d)))))
