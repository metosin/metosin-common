(ns metosin.ring.util.last-modified-test
  (:require [metosin.ring.util.last-modified :as last-modified]
            [ring.util.time :as ring-time]
            [clojure.test :refer [deftest testing is]]
            [metosin.dates :as d]))

(def req {:status 200, :body nil, :headers {"if-modified-since" (ring-time/format-date (d/to-native (d/date-time 2017 1 11 16 43)))}})

(deftest last-modified-test
  (testing "browser cache entry is up-to-date"
    (is (true? (last-modified/not-modified-since? req (d/date-time 2017 1 11 16 43)))))

  (testing "document is updated 1min after browsers cache entry"
    (is (not (last-modified/not-modified-since? req (d/date-time 2017 1 11 16 44))))))
