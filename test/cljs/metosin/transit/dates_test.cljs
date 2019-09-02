(ns metosin.transit.dates-test
  (:require [metosin.dates :as d]
            [metosin.transit.dates :as t]
            [cognitect.transit :as transit]
            [clojure.test :refer-macros [deftest testing is]]))

(d/initialize-timezone! "Europe/Helsinki")

(deftest write-local-date
  (let [writer (transit/writer :json {:handlers t/writers})]
    (is (= "[\"^ \",\"~:foo\",[\"~#Date\",\"2015-05-14\"]]"
           (transit/write writer {:foo (d/date 2015 5 14)})))))

(deftest read-local-date
  (let [reader (transit/reader :json {:handlers t/readers})]
    (is (= {:foo (d/date 2015 5 14)}
           (transit/read reader "[\"^ \",\"~:foo\",[\"~#Date\",\"2015-05-14\"]]")))))

(deftest write-date-time
  (let [writer (transit/writer :json {:handlers t/writers})]
    (is (= "[\"^ \",\"~:foo\",[\"~#DateTime\",\"2015-05-14T12:13:00.000Z\"]]"
           (transit/write writer {:foo (d/date-time 2015 5 14 12 13)})))))

(deftest read-date-time
  (testing "UTC"
    (let [reader (transit/reader :json {:handlers t/readers})]
      (is (= {:foo (d/date-time 2015 5 14 12 13)}
             (transit/read reader "[\"^ \",\"~:foo\",[\"~#DateTime\",\"2015-05-14T12:13:00.000Z\"]]")))))

  ;; goog.date can only represent UTC date-times,
  ;; but can parse (initialized) offset date-times.
  (testing "Zone (offset)"
    (let [reader (transit/reader :json {:handlers t/readers})]
      (is (= {:foo (d/date-time 2015 5 14 9 13)}
             (transit/read reader "[\"^ \",\"~:foo\",[\"~#DateTime\",\"2015-05-14T12:13:00.000+03:00\"]]")))))

  ;; zone-ids not supported
  #_
  (testing "Zone (offset and id)"
    (let [reader (transit/reader :json {:handlers t/readers})]
      (is (= {:foo (d/date-time 2015 5 14 12 13)}
             (transit/read reader "[\"^ \",\"~:foo\",[\"~#DateTime\",\"2015-05-14T12:13:00.000+03:00[Europe/Helsinki]\"]]"))))))
