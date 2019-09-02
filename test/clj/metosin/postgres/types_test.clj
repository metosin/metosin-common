(ns metosin.postgres.types-test
  (:require [metosin.postgres.types :as t]
            [clojure.test :refer [deftest testing is]]))

(deftest pgobject->clj-test
  (testing "json"
    (is (= {:foo "a"} (t/pgobject->clj (t/->PGobject "json" "{\"foo\":\"a\"}"))))
    (is (= {:foo "a"} (t/pgobject->clj (t/write-json {:foo "a"}))))
    (is (= ["a" "b"] (t/pgobject->clj (t/->PGobject "json" "[\"a\",\"b\"]"))))
    (is (= ["a" "b"] (t/pgobject->clj (t/write-json ["a" "b"]))))))
