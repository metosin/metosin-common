(ns metosin.transit.java-time-test
  (:require [metosin.transit.java-time :as t]
            [cognitect.transit :as transit]
            [clojure.test :refer [deftest testing is]])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]
           [java.time ZonedDateTime LocalDate ZoneId]))

(deftest write-local-date
  (with-open [out (ByteArrayOutputStream. 4096)]
    (let [writer (transit/writer out :json {:handlers t/writers})]
      (transit/write writer {:foo (LocalDate/of 2015 5 14)})
      (is (= "[\"^ \",\"~:foo\",[\"~#Date\",\"2015-05-14\"]]"
             (.toString out))))))

(deftest read-local-date
  (with-open [in (ByteArrayInputStream. (.getBytes "[\"^ \",\"~:foo\",[\"~#Date\",\"2015-05-14\"]]"))]
    (let [reader (transit/reader in :json {:handlers t/readers})]
      (is (= {:foo (LocalDate/of 2015 5 14)}
             (transit/read reader))))))

(deftest write-date-time
  (testing "UTC"
    (with-open [out (ByteArrayOutputStream. 4096)]
      (let [writer (transit/writer out :json {:handlers t/writers})]
        (transit/write writer {:foo (ZonedDateTime/of 2015 5 14 12 13 0 0 (ZoneId/of "Z"))})
        (is (= "[\"^ \",\"~:foo\",[\"~#DateTime\",\"2015-05-14T12:13:00Z\"]]"
               (.toString out))))))

  (testing "With zone (offset) - converted to UTC"
    (with-open [out (ByteArrayOutputStream. 4096)]
      (let [writer (transit/writer out :json {:handlers t/writers})]
        (transit/write writer {:foo (ZonedDateTime/of 2015 5 14 12 13 0 0 (ZoneId/of "+03:00"))})
        (is (= "[\"^ \",\"~:foo\",[\"~#DateTime\",\"2015-05-14T09:13:00Z\"]]"
               (.toString out))))))

  (testing "With zone (id) - converted to UTC"
    (with-open [out (ByteArrayOutputStream. 4096)]
      (let [writer (transit/writer out :json {:handlers t/writers})]
        (transit/write writer {:foo (ZonedDateTime/of 2015 5 14 12 13 0 0 (ZoneId/of "Europe/Helsinki"))})
        (is (= "[\"^ \",\"~:foo\",[\"~#DateTime\",\"2015-05-14T09:13:00Z\"]]"
               (.toString out)))))))

(deftest read-date-time
  (testing "UTC"
    (with-open [in (ByteArrayInputStream. (.getBytes "[\"^ \",\"~:foo\",[\"~#DateTime\",\"2015-05-14T12:13:00.000Z\"]]"))]
      (let [reader (transit/reader in :json {:handlers t/readers})]
        (is (= {:foo (ZonedDateTime/of 2015 5 14 12 13 0 0 (ZoneId/of "Z"))}
               (transit/read reader))))))

  (testing "With zone (offset)"
    (with-open [in (ByteArrayInputStream. (.getBytes "[\"^ \",\"~:foo\",[\"~#DateTime\",\"2015-05-14T12:13:00.000+03:00\"]]"))]
      (let [reader (transit/reader in :json {:handlers t/readers})]
        (is (= {:foo (ZonedDateTime/of 2015 5 14 12 13 0 0 (ZoneId/of "+03:00"))}
               (transit/read reader))))))

  (testing "With zone (id)"
    (with-open [in (ByteArrayInputStream. (.getBytes "[\"^ \",\"~:foo\",[\"~#DateTime\",\"2015-05-14T12:13:00.000+03:00[Europe/Helsinki]\"]]"))]
      (let [reader (transit/reader in :json {:handlers t/readers})]
        (is (= {:foo (ZonedDateTime/of 2015 5 14 12 13 0 0 (ZoneId/of "Europe/Helsinki"))}
               (transit/read reader)))))))
