(ns metosin.ui.routing.schema-test
  (:require [metosin.ui.routing.schema :refer [schema-query]]
            [domkm.silk :as silk]
            [schema.core :as s]
            [clojure.test :refer-macros [deftest testing is] :as test]))

;;
;; Search query params
;;

(deftest schema-query-test
  (testing "single required option"
    (let [routes (silk/routes [[:tickets [["tickets"] (schema-query {:q s/Str})]]])]
      (is (= "/tickets?q=a"
             (silk/depart routes :tickets {:q "a"})))
      (is (thrown? js/Error (silk/depart routes :tickets)))

      (is (= {:domkm.silk/name :tickets :q "a"}
             (select-keys (silk/arrive routes "/tickets?q=a") [:domkm.silk/name :q])))
      ;; FIXME: Not sure if this should work...
      #_
      (is (thrown? js/Error (silk/arrive routes "/tickets"))) ))

  (testing "single optional option"
    (let [routes (silk/routes [[:tickets [["tickets"] (schema-query {(s/optional-key :q) s/Str})]]])]
      (is (= "/tickets?q=a"
             (silk/depart routes :tickets {:q "a"})))
      (is (= "/tickets"
             (silk/depart routes :tickets)))
      (is (= {:domkm.silk/name :tickets :q "a"}
             (select-keys (silk/arrive routes "/tickets?q=a") [:domkm.silk/name :q])))
      (is (= {:domkm.silk/name :tickets}
             (select-keys (silk/arrive routes "/tickets") [:domkm.silk/name :q])))))

  (testing "optional set option"
    (let [routes (silk/routes [[:tickets [["tickets"] (schema-query {(s/optional-key :baskets) #{s/Str}})]]])]
      (is (= (str "/tickets?baskets=" (js/encodeURIComponent "a,b"))
             (silk/depart routes :tickets {:baskets #{"a" "b"}})))
      (is (= {:domkm.silk/name :tickets :baskets #{"a" "b"}}
             (select-keys (silk/arrive routes (str "/tickets?baskets=" (js/encodeURIComponent "a,b"))) [:domkm.silk/name :baskets]))))))
