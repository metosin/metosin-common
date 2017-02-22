(ns metosin.hetu-test
  (:require [metosin.hetu :as hetu]
            [clojure.test :refer [deftest testing is]]))

(deftest validity-test
  (testing "valid hetus"
    (is (hetu/valid? "010170-0205"))
    (is (hetu/valid? "311210A010K")))
  (testing "invalid hetus"
    (is (not (hetu/valid? "")))
    (is (not (hetu/valid? "test string")))
    (is (not (hetu/valid? "310270-0100")))  ; illegal day
    (is (not (hetu/valid? "011370-0250")))  ; illegal month
    (is (not (hetu/valid? "010170=0205")))  ; illegal century
    (is (not (hetu/valid? "010170-0105")))  ; illegal checksum
    (is (not (hetu/valid? nil)))))
