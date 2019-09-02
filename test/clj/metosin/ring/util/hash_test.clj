(ns metosin.ring.util.hash-test
  (:require [metosin.ring.util.hash :as hash]
            [clojure.java.io :as io]
            [clojure.test :refer [deftest is]]))

(deftest resource-hash-test
  (is (= "hello world\n" (slurp (io/resource "foobar.txt"))))
  (is (= "6f5902ac237024bdd0c176cb93063dc4" (hash/resource-hash "foobar.txt")))
  (is (= nil (hash/resource-hash "non-existing.txt"))))
