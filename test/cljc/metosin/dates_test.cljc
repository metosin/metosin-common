(ns metosin.dates-test
  (:require [metosin.dates :as d :include-macros true]
            #?(:clj [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros [deftest is testing] :as test])))

#?(:cljs (d/initialize-timezone! "Europe/Helsinki"))
#?(:cljs (d/initialize-timezone! "America/Los_Angeles"))

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
  (is (= "2015-04-15" (d/format (d/date 2015 4 15) {:pattern "yyyy-MM-dd"})))
  (is (= "2015-04-15 09:13" (d/format (d/date-time 2015 4 15 9 13) {:pattern "yyyy-MM-dd HH:mm"})))

  (testing "standard time"
    (is (= "2015-03-01 11:13" (d/format (d/date-time 2015 3 1 9 13) {:pattern "yyyy-MM-dd HH:mm" :timezone "Europe/Helsinki"})))
    (is (= "2015-03-01 01:13" (d/format (d/date-time 2015 3 1 9 13) {:pattern "yyyy-MM-dd HH:mm" :timezone "America/Los_Angeles"}))) )
  (testing "summer time"
    (is (= "2015-07-15 12:13" (d/format (d/date-time 2015 7 15 9 13) {:pattern "yyyy-MM-dd HH:mm" :timezone "Europe/Helsinki"})))
    (is (= "2015-07-15 02:13" (d/format (d/date-time 2015 7 15 9 13) {:pattern "yyyy-MM-dd HH:mm" :timezone "America/Los_Angeles"}))) ))

(deftest parse-test
  (is (= (d/date 2015 4 15) (d/date "2015-04-15" {:pattern "yyyy-MM-dd"})))
  (is (= (d/date-time 2015 4 15 9 13) (d/date-time "2015-04-15 09:13" {:pattern "yyyy-MM-dd HH:mm"}))))

(deftest start-of-week-test
  (is (= (d/date 2016 1 25) (d/start-of-week (d/date 2016 1 27))))
  (is (= (d/date-time 2016 1 25 12 0) (d/start-of-week (d/date-time 2016 1 27 12 0)))))

(deftest end-of-week-test
  (is (= (d/date 2016 1 31) (d/end-of-week (d/date 2016 1 27))))
  (is (= (d/date-time 2016 1 31 12 0) (d/end-of-week (d/date-time 2016 1 27 12 0)))))

(deftest add-test
  (is (= (d/date 2016 1 28) (d/add (d/date 2016 1 27) (d/days 1))))
  (is (= (d/date-time 2016 1 28 12 13) (d/add (d/date-time 2016 1 27 12 13) (d/days 1)))))

(deftest legacy-api-test
  (is (= "14.5.2015" (d/date->str (d/date 2015 5 14))))
  ;; Helsinki tz
  (is (= "14.5.2015 12:13" (d/date-time->str (d/date-time 2015 5 14 9 13)))))

#?(:clj (deftest closure-timezone-test
          (is (= {:id "America/Los_Angeles"
                  :names ["PST" "Pacific Standard Time" "PDT" "Pacific Daylight Time"]
                  :std_offset -480
                  :transitions [2770,   60, 7137,   0, 11506,  60, 16041,  0, 20410,  60, 24777,  0,
                                29146,  60, 33513,  0, 35194,  60, 42249,  0, 45106,  60, 50985,  0,
                                55354,  60, 59889,  0, 64090,  60, 68625,  0, 72994,  60, 77361,  0,
                                81730,  60, 86097,  0, 90466,  60, 94833,  0, 99202,  60, 103569, 0,
                                107938, 60, 112473, 0, 116674, 60, 121209, 0, 125578, 60, 129945, 0,
                                134314, 60, 138681, 0, 143050, 60, 147417, 0, 151282, 60, 156153, 0,
                                160018, 60, 165057, 0, 168754, 60, 173793, 0, 177490, 60, 182529, 0,
                                186394, 60, 191265, 0, 195130, 60, 200001, 0, 203866, 60, 208905, 0,
                                212602, 60, 217641, 0, 221338, 60, 226377, 0, 230242, 60, 235113, 0,
                                238978, 60, 243849, 0, 247714, 60, 252585, 0, 256450, 60, 261489, 0,
                                265186, 60, 270225, 0, 273922, 60, 278961, 0, 282826, 60, 287697, 0,
                                291562, 60, 296433, 0, 300298, 60, 305337, 0, 309034, 60, 314073, 0,
                                317770, 60, 322809, 0, 326002, 60, 331713, 0, 334738, 60, 340449, 0,
                                343474, 60, 349185, 0, 352378, 60, 358089, 0, 361114, 60, 366825, 0,
                                369850, 60, 375561, 0, 378586, 60, 384297, 0, 387322, 60, 393033, 0,
                                396058, 60, 401769, 0, 404962, 60, 410673, 0, 413698, 60, 419409, 0,
                                422434, 60, 428145, 0, 431170, 60, 436881, 0, 439906, 60, 445617, 0,
                                448810, 60, 454521, 0, 457546, 60, 463257, 0, 466282, 60, 471993, 0,
                                475018, 60, 480729, 0, 483754, 60, 489465, 0, 492490, 60, 498201, 0,
                                501394, 60, 507105, 0, 510130, 60, 515841, 0, 518866, 60, 524577, 0,
                                527602, 60, 533313, 0, 536338, 60, 542049, 0, 545242, 60, 550953, 0,
                                553978, 60, 559689, 0, 562714, 60, 568425, 0, 571450, 60, 577161, 0,
                                580186, 60, 585897, 0, 588922, 60, 594633, 0]}
                 (d/closure-timezone "America/Los_Angeles")))))
