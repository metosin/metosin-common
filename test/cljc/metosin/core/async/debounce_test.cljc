(ns metosin.core.async.debounce-test
  (:require [metosin.core.async.debounce :as d]
            #?(:clj [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros [deftest is testing] :as test])))
