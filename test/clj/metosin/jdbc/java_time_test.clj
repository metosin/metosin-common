(ns metosin.jdbc.java-time-test
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.test :refer [deftest testing is]]
            [metosin.jdbc.java-time :as x])
  (:import [java.time ZonedDateTime LocalDate ZoneId]))

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

    (jdbc/insert! db :test_table {:datetime (ZonedDateTime/of 2015 2 9 13 1 0 0 (ZoneId/of "Z"))
                                  :date (LocalDate/of 2015 2 9)})

    (testing "read inserted values"
      (let [{:keys [datetime date]}
            (first (jdbc/query db ["SELECT * from test_table"]))]

        (is (= (ZonedDateTime/of 2015 2 9 13 1 0 0 (ZoneId/of "Z"))
               datetime))

        (is (= (LocalDate/of 2015 2 9)
               date))))))

