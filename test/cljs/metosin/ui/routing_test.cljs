(ns metosin.ui.routing-test
  (:require [metosin.ui.routing :refer [data]]
            [domkm.silk :as silk]
            [clojure.test :refer-macros [deftest testing is] :as test]))

;;
;; Search query params
;;

(deftest data-test
  (testing "basic"
    (let [routes (silk/routes [[:login [["login"] (data {:public? true})]]])]
      (is (= "/login"
             (silk/depart routes :login)))
      (is (= {:domkm.silk/name :login :public? true}
             (select-keys (silk/arrive routes "/login") [:domkm.silk/name :public?])))))

  ;; FIXME: Broken
  #_
  (testing "with query parameters"
    (let [routes (silk/routes [[:ticket [["tickets" :id] {"foo" :foo} (data {:routing/nav :tickets})]]])]
      (is (= "/tickets/1337?foo=bar"
             (silk/depart routes :ticket {:id 1337 :foo "bar"})))
      (is (= {:domkm.silk/name :ticket :id 1337 :foo "bar" :routing/nav :tickets}
             (select-keys (silk/arrive routes "/tickets/1337?foo=bar") [:domkm.silk/name :id :foo :routing/nav])))))
  )
