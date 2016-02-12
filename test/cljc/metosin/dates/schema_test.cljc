(ns metosin.dates.schema-test
  (:require [metosin.dates :as d]
            [metosin.dates.schema :as ds]
            [schema.coerce :as sc]
            #?(:clj [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros [deftest is testing] :as test])))

(def schema {:local-date d/LocalDate
             :date-time d/DateTime})

(def coercer (sc/coercer schema ds/date-coercion-matcher))

(deftest date-coercion-matcher
  (is (= {:local-date (d/date 2015 5 14)
          :date-time (d/date-time 2015 5 14 9 13)}
         (coercer {:local-date "2015-05-14"
                   :date-time "2015-05-14T9:13:00.000Z"}))))
