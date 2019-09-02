(ns metosin.transit.dates
  "Transit readers and writers for JodaTime and goog.date.

  Supports two types:
  - DateTime (org.joda.time.DateTime, goog.date.UtcDateTime)
  - LocalDate (org.joda.time.LocalDate, goog.date.Date)

  Represents DateTimes in RFC 3339 format: yyyy-mm-ddTHH:MM:SS.sssZ.
  RFC 3339 format is an specific profile of ISO 8601 DateTime format.
  DateTimes are first converted to UTC, as that is only zone JavaScript
  supports.

  Some consideration has been made to provide performant read
  implemenation for ClojureScript."
  (:require [cognitect.transit :as transit]
            [metosin.dates :as d]))

(def writers
  {#?(:clj org.joda.time.DateTime, :cljs goog.date.UtcDateTime)
   (transit/write-handler (constantly "DateTime") d/to-string)

   #?(:clj org.joda.time.LocalDate, :cljs goog.date.Date)
   (transit/write-handler (constantly "Date") d/to-string)})

(def readers
  ; 1 argument arity version must be explicitly used for clojurescript
  {"DateTime" (transit/read-handler #(d/date-time %))
   "Date"     (transit/read-handler #(d/date %))})
