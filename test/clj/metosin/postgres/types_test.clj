(ns metosin.postgres.types-test
  (:require [metosin.postgres.types :refer :all]
            [clojure.java.jdbc :as jdbc]
            [clojure.test :refer :all]))


(deftest pgobject->clj-test
  (testing "keyword"
    (is (= :foo/bar (pgobject->clj (->PGobject keyword-type-name "(foo/bar)"))))
    (is (= :foo/bar (pgobject->clj (jdbc/sql-value :foo/bar))))
    (is (= :bar (pgobject->clj (->PGobject keyword-type-name "(bar)"))))
    (is (= :bar (pgobject->clj (jdbc/sql-value :bar)))))

  (testing "json"
    (is (= {:foo "a"} (pgobject->clj (->PGobject "json" "{\"foo\":\"a\"}"))))
    (is (= {:foo "a"} (pgobject->clj (jdbc/sql-value {:foo "a"}))))
    (is (= ["a" "b"] (pgobject->clj (->PGobject "json" "[\"a\",\"b\"]"))))
    (is (= ["a" "b"] (pgobject->clj (jdbc/sql-value ["a" "b"]))))
    ))
