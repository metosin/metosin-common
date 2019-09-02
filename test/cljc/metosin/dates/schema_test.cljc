(ns metosin.dates.schema-test
  (:require [metosin.dates :as d]
            [metosin.dates.schema :as ds]
            [schema.coerce :as sc]
            [clojure.test :as t :refer [deftest is]]))

(def schema {:local-date d/LocalDate
             :date-time d/DateTime})

(def coercer (sc/coercer schema ds/date-coercion-matcher))

(deftest date-coercion-matcher
  (is (= {:local-date (d/date 2015 5 14)
          :date-time (d/date-time 2015 5 14 9 13)}
         (coercer {:local-date "2015-05-14"
                   :date-time "2015-05-14T09:13:00.000Z"}))))
