(ns metosin.ring.util.hash-test
  (:require [metosin.ring.util.hash :refer :all]
            [clojure.java.io :as io]
            [clojure.test :refer :all]))

(deftest resource-hash-test
  (is (= "hello world\n" (slurp (io/resource "foobar.txt"))))
  (is (= "6f5902ac237024bdd0c176cb93063dc4" (resource-hash "foobar.txt")))
  (is (= nil (resource-hash "non-existing.txt"))))
