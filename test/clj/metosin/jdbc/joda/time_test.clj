(ns metosin.jdbc.joda.time-test
  (:require [metosin.jdbc :refer :all]
            [clojure.test :refer :all]
            [metosin.dates :as dates]
            metosin.jdbc.joda.time)
  (:import [org.joda.time DateTime LocalDate]))

(def h2-spec {:classname "org.h2.Driver"
              :subprotocol "h2:mem"
              :subname "db/metosin-common-test"})

(deftest metosin-jdbc-test
  (with-db-connection [db h2-spec]
    (db-do-commands db
      (create-table-ddl :test_table
        [:id "bigint primary key auto_increment"]
        [:datetime "timestamp"]
        [:date "date"]))

    (insert! db :test_table {:datetime (dates/date-time 2015 2 9 13 1)
                             :date (dates/date 2015 2 9)})

    (testing "read inserted values"
      (let [{:keys [datetime datetime-default date date-default]}
            (first (query db ["SELECT * from test_table"]))]

        (is (= (dates/date-time 2015 2 9 13 1)
               datetime))

        (is (= (dates/date 2015 2 9)
               date))
        ))))
