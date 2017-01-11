(ns metosin.ring.util.last-modified-test
  (:require [metosin.ring.util.last-modified :refer :all]
            [ring.util.time :as ring-time]
            [clojure.java.io :as io]
            [clojure.test :refer :all]
            [metosin.dates :as d]))

(def req {:status 200, :body nil, :headers {"if-modified-since" (ring-time/format-date (d/to-native (d/date-time 2017 1 11 16 43)))}})

(deftest last-modified-test
  (testing "browser cache entry is up-to-date"
    (is (true? (not-modified-since? req (d/date-time 2017 1 11 16 43)))))

  (testing "document is updated 1min after browsers cache entry"
    (is (not (not-modified-since? req (d/date-time 2017 1 11 16 44))))))
