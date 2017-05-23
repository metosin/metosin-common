(ns metosin.jdbc-test
  (:require [metosin.jdbc :refer :all]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]))

(def h2-spec (merge
               db-spec-defaults
               {:classname "org.h2.Driver"
                :subprotocol "h2:mem"
                :subname "db/metosin-common-test"}))

;; Run docker-compose up
(def psql-spec (merge
                 db-spec-defaults
                 {:dbtype "postgresql"
                  :host "localhost"
                  :port 7612
                  :dbname "common"
                  :user "common"
                  :password "common"}))

(defn h2? [spec]
  (= "org.h2.Driver" (:classname spec)))

(deftest metosin-jdbc-test
  (doseq [db-spec [h2-spec psql-spec]
          :when (try
                  (query db-spec ["select now()"])
                  (println "\nRunning tests against" (or (:classname db-spec) (:dbtype db-spec)))
                  true
                  (catch Exception e
                    (println (str "\nNot running tests against " (or (:classname db-spec) (:dbtype db-spec)) ", start the DB to run tests."))
                    false))]
  (jdbc/with-db-connection [db db-spec]
    (jdbc/db-do-commands db
      (jdbc/create-table-ddl :test_table
                             [[:id "bigint primary key auto_increment"]
                              [:test_column1 "int"]
                              [:test_column2 "varchar"]]))

    (testing "insert! with underscores"
      (insert! db :test_table {:test_column1 100}))

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
      (insert-multi! db :test-table [{:test-column2 "bar"} {:test-column2 "bar"}])
      (is (= [{:test-column2 "bar"} {:test-column2 "bar"}]
             (query db (sql/format {:select [:test-column2] :from [:test-table]})))))

    (testing "execute!"
      (is (= '(1)
             (execute! db ["INSERT INTO test_table (test_column1) VALUES (1)"])))))))

(def kebab-keywords #'metosin.jdbc/kebab-keywords)

(deftest kebab-keywords-test
  (testing "kebab-keywords"
    (is (= nil (kebab-keywords nil)))
    (is (= 1 (kebab-keywords 1)))
    (is (= '() (kebab-keywords '())))
    (is (= '({:id 1 :column "foo"}) (kebab-keywords '({:id 1 :column "foo"}))))
    (is (= '({:id 1 :column-name "foo"}) (kebab-keywords '({:id 1 :column_name "foo"}))))
    (is (= '({:id 1 :column-name "bar"}) (kebab-keywords '({:id 1 :column-name "bar"}))))))
