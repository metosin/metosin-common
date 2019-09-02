(ns metosin.transit.dates-test
  (:require [metosin.dates :as d]
            [metosin.transit.dates :as t]
            [cognitect.transit :as transit]
            [clojure.test :refer [deftest is]])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]))

(deftest write-local-date
  (let [out (ByteArrayOutputStream. 4096)
        writer (transit/writer out :json {:handlers t/writers})]
    (transit/write writer {:foo (d/date 2015 5 14)})
    (is (= "[\"^ \",\"~:foo\",[\"~#Date\",\"2015-05-14\"]]" (.toString out)))))

(deftest read-local-date
  (let [in (ByteArrayInputStream. (.getBytes "[\"^ \",\"~:foo\",[\"~#Date\",\"2015-05-14\"]]"))
        reader (transit/reader in :json {:handlers t/readers})]
    (is (= {:foo (d/date 2015 5 14)} (transit/read reader)))))

(deftest write-date-time
  (let [out (ByteArrayOutputStream. 4096)
        writer (transit/writer out :json {:handlers t/writers})]
    (transit/write writer {:foo (d/date-time 2015 5 14 12 13)})
    (is (= "[\"^ \",\"~:foo\",[\"~#DateTime\",\"2015-05-14T12:13:00.000Z\"]]" (.toString out)))))

(deftest read-date-time
  (let [in (ByteArrayInputStream. (.getBytes "[\"^ \",\"~:foo\",[\"~#DateTime\",\"2015-05-14T12:13:00.000Z\"]]"))
        reader (transit/reader in :json {:handlers t/readers})]
    (is (= {:foo (d/date-time 2015 5 14 12 13)} (transit/read reader)))))
