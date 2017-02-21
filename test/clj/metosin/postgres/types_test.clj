(ns metosin.postgres.types-test
  (:require [metosin.postgres.types :refer :all]
            [clojure.java.jdbc :as jdbc]
            [clojure.test :refer :all]))


(deftest pgobject->clj-test
  (testing "json"
    (is (= {:foo "a"} (pgobject->clj (->PGobject "json" "{\"foo\":\"a\"}"))))
    (is (= {:foo "a"} (pgobject->clj (write-json {:foo "a"}))))
    (is (= ["a" "b"] (pgobject->clj (->PGobject "json" "[\"a\",\"b\"]"))))
    (is (= ["a" "b"] (pgobject->clj (write-json ["a" "b"]))))
    ))
