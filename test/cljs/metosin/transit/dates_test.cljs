(ns metosin.transit.dates-test
  (:require [metosin.dates :as d]
            [metosin.transit.dates :as t]
            [cognitect.transit :as transit]
            [clojure.test :as test :refer [deftest is testing]]))

(deftest write-local-date
  (let [writer (transit/writer :json {:handlers t/writers})]
    (is (= "[\"^ \",\"~:foo\",[\"~#Date\",\"2015-05-14\"]]" (transit/write writer {:foo (d/date 2015 5 14)})))))

(deftest read-local-date
  (let [reader (transit/reader :json {:handlers t/readers})]
    (is (= {:foo (d/date 2015 5 14)} (transit/read reader "[\"^ \",\"~:foo\",[\"~#Date\",\"2015-05-14\"]]")))))

(deftest write-date-time
  (let [writer (transit/writer :json {:handlers t/writers})]
    (is (= "[\"^ \",\"~:foo\",[\"~#DateTime\",\"2015-05-14T12:13:00.000Z\"]]" (transit/write writer {:foo (d/date-time 2015 5 14 12 13)})))))

(deftest read-date-time
  (let [reader (transit/reader :json {:handlers t/readers})]
    (is (= {:foo (d/date-time 2015 5 14 12 13)} (transit/read reader "[\"^ \",\"~:foo\",[\"~#DateTime\",\"2015-05-14T12:13:00.000Z\"]]")))))

