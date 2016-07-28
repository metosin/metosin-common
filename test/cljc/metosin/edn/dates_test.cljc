(ns metosin.edn.dates-test
  (:require [metosin.dates :as d :include-macros true]
            [metosin.edn.dates :as ed]
            [metosin.edn :as edn]
    #?(:clj [clojure.test :refer :all]
       :cljs [cljs.test :refer-macros [deftest is testing] :as test])))

(deftest dates-test
  (testing "date"
    (let [date (d/date 2016 7 28)]
      (is (= "#Date \"2016-07-28\"" (pr-str date)))
      (is (= date (edn/read-string {:readers ed/readers} (pr-str date))))))
  (testing "date-time"
    (let [date-time (d/date-time 2016 7 28 13 12 15)]
      (is (= "#DateTime \"2016-07-28T13:12:15.000Z\"" (pr-str date-time)))
      (is (= date-time (edn/read-string {:readers ed/readers} (pr-str date-time)))))))
