(ns metosin.ring.util.etag-test
  (:require [metosin.ring.util.etag :refer :all]
            [clojure.java.io :as io]
            [clojure.test :refer :all]
            [metosin.dates :as d]))

(def req {:status 200, :body nil, :headers {"if-none-match" "a"}})

(deftest etag-test
  (testing "same etag"
    (is (true? (none-match? req "a"))))

  (testing "different etag"
    (is (not (none-match? req "b")))))
