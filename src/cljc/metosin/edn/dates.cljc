(ns metosin.edn.dates
  "EDN tag readers and writers for JodaTime and goog.date.

  Supports two types:
  - DateTime (org.joda.time.DateTime, goog.date.UtcDateTime)
  - LocalDate (org.joda.time.LocalDate, goog.date.Date)

  Represents DateTimes in RFC 3339 format: yyyy-mm-ddTHH:MM:SS.sssZ.
  RFC 3339 format is an specific profile of ISO 8601 DateTime format.

  Some consideration has been made to provide performant read
  implemenation for ClojureScript."
  (:require [metosin.dates :as d])
  #?(:clj (:import [org.joda.time DateTime LocalDate]
                   [java.io Writer])))

(defn- date-time->reader-str ^String [d]
  (str "#DateTime \"" (d/to-string d) \"))

(defn- date->reader-str ^String [d]
  (str "#Date \"" (d/to-string d) \"))

#?(:clj
   (do
     (defmethod print-dup DateTime [^DateTime d ^Writer out]
       (.write out (date-time->reader-str d)))

     (defmethod print-method DateTime [^DateTime d ^Writer out]
       (.write out (date-time->reader-str d)))

     (defmethod print-dup LocalDate [^LocalDate d ^Writer out]
       (.write out (date->reader-str d)))

     (defmethod print-method LocalDate [^LocalDate d ^Writer out]
       (.write out (date->reader-str d))))
   :cljs
   (extend-protocol IPrintWithWriter
     goog.date.DateTime
     (-pr-writer [d out opts]
       (-write out (date-time->reader-str d)))
     goog.date.Date
     (-pr-writer [d out opts]
       (-write out (date->reader-str d)))))

(def readers
  {'Date d/date
   'DateTime d/date-time})
