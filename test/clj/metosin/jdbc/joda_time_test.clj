(ns metosin.jdbc.joda-time-test
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.test :refer [deftest testing is]]
            [metosin.dates :as dates]
            [metosin.jdbc.joda-time :as x]))

(def h2-spec {:classname "org.h2.Driver"
              :subprotocol "h2:mem"
              :subname "db/metosin-common-test"})

(deftest metosin-jdbc-test
  (jdbc/with-db-connection [db h2-spec]
    (jdbc/db-do-commands db
                         (jdbc/create-table-ddl :test_table
                                                [[:id "bigint primary key auto_increment"]
                                                 [:datetime "timestamp"]
                                                 [:date "date"]]))

    (x/init!)

    (jdbc/insert! db :test_table {:datetime (dates/date-time 2015 2 9 13 1)
                                  :date (dates/date 2015 2 9)})

    (testing "read inserted values"
      (let [{:keys [datetime date]}
            (first (jdbc/query db ["SELECT * from test_table"]))]

        (is (= (dates/date-time 2015 2 9 13 1)
               datetime))

        (is (= (dates/date 2015 2 9)
               date))))))
