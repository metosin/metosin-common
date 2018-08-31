(ns metosin.jdbc-test
  (:require [metosin.jdbc :as jdbc :refer [query update! delete! execute! insert! insert-multi! db-spec-defaults]]
            [clojure.java.jdbc :as cjdbc]
            [clojure.test :refer :all]
            [honeysql.core :as sql]))

(def h2-spec {:classname "org.h2.Driver"
              :subprotocol "h2:mem"
              :subname "db/metosin-common-test"})

(def psql-spec {:dbtype "postgresql"
                :host "localhost"
                :port 7612
                :dbname "common"
                :user "common"
                :password "common"})

(defn h2? [spec]
  (= "org.h2.Driver" (:classname spec)))

(def db-spec (merge
               ;; Enable metosin.jdbc options
               ; db-spec-defaults
               ;; Select test DB
               (try
                 (query psql-spec ["select now()"])
                 (println "Running tests against Postgres")
                 psql-spec
                 (catch Exception e
                   (println "No Postgres available, running tests against H2. Run `docker-compose up` to use Postgres.")
                   h2-spec))))

(deftest metosin-jdbc-test
  (jdbc/with-db-connection [db db-spec]
    (jdbc/db-do-commands db
      ["DROP TABLE IF EXISTS test_table"
       (jdbc/create-table-ddl :test_table
                             [[:id (if (h2? db-spec)
                                     "bigint primary key auto_increment"
                                     "serial primary key")]
                              [:test_column1 "int"]
                              [:test_column2 "varchar"]])])

    (testing "insert! with underscores"
      ;; Postgres insert returns the row, h2 doesn't
      (if (h2? db-spec)
        (insert! db :test_table {:test_column1 100})
        (is (= [{:id 1 :test-column1 100 :test-column2 nil}]
               (insert! db :test_table {:test_column1 100})))))

    (testing "query with underscores"
      (let [q {:select [:id :test_column1 :test_column2]
               :from [:test_table]}]
        (is (= (list {:id 1 :test-column1 100 :test-column2 nil})
               (query db (sql/format q))))))

    (testing "insert! with dashes"
      (insert! db :test-table {:test-column2 "foo"}))

    (testing "query with dashes"
      (let [q {:select [:id :test-column1 :test-column2]
               :from [:test_table]
               :where [:= :test-column2 "foo"]}]
        (is (= '({:id 2 :test-column1 nil :test-column2 "foo"})
               (query db (sql/format q))))))

    (testing "update! with underscores"
      (update! db :test_table {:test_column1 200} ["id = ?" 1])
      (let [q {:select [:id :test-column1 :test-column2]
               :from [:test-table]
               :where [:= :id 1]}]
        (is (= '({:id 1 :test-column1 200 :test-column2 nil})
               (query db (sql/format q))))))

    (testing "update! with dashes"
      (update! db :test-table {:test-column2 "bar"} ["id = ?" 1])
      (let [q {:select [:id :test-column1 :test-column2]
               :from [:test-table]
               :where [:= :id 1]}]
        (is (= '({:id 1 :test-column1 200 :test-column2 "bar"})
               (query db (sql/format q))))))

    (testing "delete! with underscores"
      (delete! db :test_table ["id = 1"])
      (let [q {:select [:*]
               :from [:test-table]
               :where [:= :id 1]}]
        (is (= '() (query db (sql/format q))))))

    (testing "delete! with dashes"
      (delete! db :test-table ["id = 2"])
      (let [q {:select [:*]
               :from [:test-table]
               :where [:= :id 2]}]
        (is (= '() (query db (sql/format q))))))

    (testing "insert-multi! with dashes"
      (if (h2? db-spec)
        (insert-multi! db :test-table [{:test-column2 "bar"} {:test-column2 "bar"}])
        (is (= [{:id 3 :test-column1 nil :test-column2 "bar"} {:id 4 :test-column1 nil :test-column2 "bar"}]
               (insert-multi! db :test-table [{:test-column2 "bar"} {:test-column2 "bar"}]))))

      (is (= [{:test-column2 "bar"} {:test-column2 "bar"}]
             (query db (sql/format {:select [:test-column2] :from [:test-table]})))))

    (testing "execute!"
      (is (= '(1)
             (execute! db ["INSERT INTO test_table (test_column1) VALUES (1)"]))))))

(deftest clojure-jdbc-test
  (jdbc/with-db-connection [db (merge db-spec-defaults db-spec)]
    (jdbc/db-do-commands db
      ["DROP TABLE IF EXISTS test_table"
       (jdbc/create-table-ddl :test_table
                             [[:id (if (h2? db-spec)
                                     "bigint primary key auto_increment"
                                     "serial primary key")]
                              [:test_column1 "int"]
                              [:test_column2 "varchar"]])])

    (testing "insert! with underscores"
      ;; Postgres insert returns the row, h2 doesn't
      (if (h2? db-spec)
        (cjdbc/insert! db :test_table {:test_column1 100})
        (is (= [{:id 1 :test-column1 100 :test-column2 nil}]
               (cjdbc/insert! db :test_table {:test_column1 100})))))

    (testing "query with underscores"
      (let [q {:select [:id :test_column1 :test_column2]
               :from [:test_table]}]
        (is (= (list {:id 1 :test-column1 100 :test-column2 nil})
               (cjdbc/query db (sql/format q))))))

    (testing "insert! with dashes"
      (insert! db :test-table {:test-column2 "foo"}))

    (testing "query with dashes"
      (let [q {:select [:id :test-column1 :test-column2]
               :from [:test_table]
               :where [:= :test-column2 "foo"]}]
        (is (= '({:id 2 :test-column1 nil :test-column2 "foo"})
               (cjdbc/query db (sql/format q))))))

    (testing "update! with underscores"
      (update! db :test_table {:test_column1 200} ["id = ?" 1])
      (let [q {:select [:id :test-column1 :test-column2]
               :from [:test-table]
               :where [:= :id 1]}]
        (is (= '({:id 1 :test-column1 200 :test-column2 nil})
               (cjdbc/query db (sql/format q))))))

    (testing "update! with dashes"
      (update! db :test-table {:test-column2 "bar"} ["id = ?" 1])
      (let [q {:select [:id :test-column1 :test-column2]
               :from [:test-table]
               :where [:= :id 1]}]
        (is (= '({:id 1 :test-column1 200 :test-column2 "bar"})
               (cjdbc/query db (sql/format q))))))

    (testing "delete! with underscores"
      (delete! db :test_table ["id = 1"])
      (let [q {:select [:*]
               :from [:test-table]
               :where [:= :id 1]}]
        (is (= '() (cjdbc/query db (sql/format q))))))

    (testing "delete! with dashes"
      (delete! db :test-table ["id = 2"])
      (let [q {:select [:*]
               :from [:test-table]
               :where [:= :id 2]}]
        (is (= '() (cjdbc/query db (sql/format q))))))

    (testing "insert-multi! with dashes"
      (if (h2? db-spec)
        (cjdbc/insert-multi! db :test-table [{:test-column2 "bar"} {:test-column2 "bar"}])
        (is (= [{:id 3 :test-column1 nil :test-column2 "bar"} {:id 4 :test-column1 nil :test-column2 "bar"}]
               (cjdbc/insert-multi! db :test-table [{:test-column2 "bar"} {:test-column2 "bar"}]))))

      (is (= [{:test-column2 "bar"} {:test-column2 "bar"}]
             (cjdbc/query db (sql/format {:select [:test-column2] :from [:test-table]})))))

    (testing "execute!"
      (is (= '(1)
             (cjdbc/execute! db ["INSERT INTO test_table (test_column1) VALUES (1)"]))))))
