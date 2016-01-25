(ns metosin.dates-test
  (:require [metosin.dates :as d]
            #?(:clj [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros [deftest is testing] :as test])))

(deftest unparse-test
  (is (= "2015-04-15" (d/unparse "yyyy-MM-dd" (d/date 2015 4 15))))
  (is (= "2015-04-15 09:13" (d/unparse "yyyy-MM-dd HH:mm" (d/date-time 2015 4 15 9 13)))))

(deftest parse-test
  (is (= (d/date 2015 4 15) (d/parse-date "yyyy-MM-dd" "2015-04-15")))
  (is (= (d/date-time 2015 4 15 9 13) (d/parse "yyyy-MM-dd HH:mm" "2015-04-15 09:13"))))

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
