(ns metosin.ring.util.last-modified
  (:require [ring.util.response :as resp]
            [ring.util.time :as ring-time])
  (:import [org.joda.time DateTime]))

(defn last-modified [req ^DateTime date]
  (if date
    (resp/header req "Last-Modified" (ring-time/format-date (.toDate date)))
    req))

(defn parse-date [s]
  (some-> s ring-time/parse-date (DateTime.)))

(defn not-modified-since-time? [^DateTime if-modified-since ^DateTime last-modified]
  (and last-modified if-modified-since (not (.isBefore if-modified-since (.withMillisOfSecond last-modified 0)))))

;; TODO: There is also if-unmodified-since

(defn not-modified-since? [req last-modified]
  (not-modified-since-time? (parse-date (get (:headers req) "if-modified-since"))
                            last-modified))
