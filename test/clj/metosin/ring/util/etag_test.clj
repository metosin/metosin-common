(ns metosin.ring.util.etag-test
  (:require [metosin.ring.util.etag :as etag]
            [clojure.test :refer [deftest is testing]]))

(def req {:status 200, :body nil, :headers {"if-none-match" "a"}})

(deftest etag-test
  (testing "same etag"
    (is (true? (etag/none-match? req "a"))))

  (testing "different etag"
    (is (not (etag/none-match? req "b")))))
