(ns metosin.transit.java-time
  "Transit readers and writers for JodaTime and goog.date.

  Supports two types:
  - DateTime (java.time.ZonedDateTime, goog.date.UtcDateTime)
  - LocalDate (java.time.LocalDate, goog.date.Date)

  Represents DateTimes in RFC 3339 format: yyyy-mm-ddTHH:MM:SS.sssZ.
  RFC 3339 format is an specific profile of ISO 8601 DateTime format.
  DateTimes are first converted to UTC, as that is only zone JavaScript
  supports.

  Use metosin.transit.dates for Cljs."
  (:require [cognitect.transit :as transit])
  (:import [java.time LocalDate ZonedDateTime ZoneId]
           [java.time.format DateTimeFormatter]))

(def writers
  {java.time.ZonedDateTime
   (transit/write-handler (constantly "DateTime")
                          (fn [^ZonedDateTime date]
                            (.format (.withZoneSameInstant date (ZoneId/of "Z")) DateTimeFormatter/ISO_DATE_TIME)))

   java.time.LocalDate
   (transit/write-handler (constantly "Date")
                          (fn [^LocalDate date]
                            (.format date DateTimeFormatter/ISO_LOCAL_DATE)))})

(def readers
  {"DateTime" (transit/read-handler (fn [^String s]
                                      (ZonedDateTime/parse s)))
   "Date"     (transit/read-handler (fn [^String s]
                                      (LocalDate/parse s)))})
