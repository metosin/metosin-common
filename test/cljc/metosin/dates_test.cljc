(ns metosin.dates-test
  (:require [metosin.dates :as d]
            #?(:clj [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros [deftest is testing] :as test])))

(deftest date-test
  (testing "date-time to date"
    (is (= (d/date 2015 5 14) (d/date (d/date-time 2015 5 14 9 13)))))
  (testing "native to date"
    (is (= (d/date 2015 5 14) (d/date #inst "2015-05-14T12:00"))))
  (testing "to native"
    (is (= #inst "2015-05-14T00:00" (d/to-native (d/date 2015 5 14))))))

(deftest date-time-test
  (testing "date to date-time"
    (is (= (d/date-time 2015 5 14 0 0) (d/date-time (d/date 2015 5 14)))))
  (testing "native to date-time"
    (is (= (d/date-time 2015 5 14 9 13) (d/date-time #inst "2015-05-14T09:13"))))
  (testing "to native"
    (is (= #inst "2015-05-14T09:13" (d/to-native (d/date-time 2015 5 14 9 13))))))

(deftest format-test
  (is (= "2015-04-15" (d/format (d/date 2015 4 15) "yyyy-MM-dd")))
  (is (= "2015-04-15 09:13" (d/format (d/date-time 2015 4 15 9 13) "yyyy-MM-dd HH:mm"))))

(deftest parse-test
  (is (= (d/date 2015 4 15) (d/date "2015-04-15" "yyyy-MM-dd")))
  (is (= (d/date-time 2015 4 15 9 13) (d/date-time "2015-04-15 09:13" "yyyy-MM-dd HH:mm"))))

(deftest start-of-week-test
  (is (= (d/date 2016 1 25) (d/start-of-week (d/date 2016 1 27)))))

(deftest end-of-week-test
  (is (= (d/date 2016 1 31) (d/end-of-week (d/date 2016 1 27)))))

(deftest add-test
  (is (= (d/date 2016 1 28) (d/add (d/date 2016 1 27) (d/days 1))))
  (is (= (d/date-time 2016 1 28 12 13) (d/add (d/date-time 2016 1 27 12 13) (d/days 1)))))

(deftest legacy-api-test
  (is (= "14.5.2015" (d/date->str (d/date 2015 5 14))))
  ;; Helsinki tz
  (is (= "14.5.2015 12:13" (d/date-time->str (d/date-time 2015 5 14 9 13)))))
