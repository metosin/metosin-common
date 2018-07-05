(ns metosin.postgres.types-test
  (:require [metosin.postgres.types :refer :all]
            [clojure.java.jdbc :as jdbc]
            [clojure.test :refer :all]
            [metosin.dates :as md]))


(deftest pgobject->clj-test
  (testing "json"
    (is (= {:foo "a"} (pgobject->clj (->PGobject "json" "{\"foo\":\"a\"}"))))
    (is (= {:foo "a"} (pgobject->clj (write-json {:foo "a"}))))
    ; (is (= {:foo "2018-07-05T11:01:000Z"} (pgobject->clj (write-json {:foo (md/date-time 2018 7 5 11 1)}))))
    (is (= ["a" "b"] (pgobject->clj (->PGobject "json" "[\"a\",\"b\"]"))))
    (is (= ["a" "b"] (pgobject->clj (write-json ["a" "b"]))))
    ))
