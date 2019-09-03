(ns metosin.xml-test
  (:require [clojure.test :refer [deftest testing is]]
            [metosin.xml :as p]
            [linked.core :as linked])
  (:import [linked.map LinkedMap]))

(deftest parse-test
  (testing "String"
    (is (= {:tag :foo, :attrs nil, :content ["bar"]}
           (p/parse-string "<foo>bar</foo>")))

    (is (= {:tag :items, :attrs nil
            :content [{:tag :item, :attrs nil, :content ["1"]}
                      {:tag :item, :attrs nil, :content ["2"]}]}
           (p/parse-string "<items><item>1</item><item>2</item></items>")))

    (is (= {:tag :items, :attrs nil
            :content [{:tag :item, :attrs nil
                       :content [{:tag :foo, :attrs nil, :content ["1"]}
                                 {:tag :bar, :attrs nil, :content ["x"]}]}
                      {:tag :item, :attrs nil
                       :content [{:tag :foo, :attrs nil, :content ["2"]}
                                 {:tag :bar, :attrs nil, :content ["y"]}]}]}
           (p/parse-string "<items><item><foo>1</foo><bar>x</bar></item><item><foo>2</foo><bar>y</bar></item></items>"))) )

  #_
  (testing "InputStream"
    (is (some? (with-open [is (.openStream (io/resource "noxml/test1.xml"))]
                 (p/parse is)))))

  #_
  (testing "java.net.URL"
    (is (some? (p/parse (io/resource "noxml/test1.xml"))))))

(deftest xml->clj-test
  (is (= {:foo "bar"}
         (p/xml->clj {:tag :foo, :attrs nil, :content ["bar"]})))

  (is (= {:items {:item ["1" "2"]}}
         (p/xml->clj {:tag :items, :attrs nil
                      :content [{:tag :item, :attrs nil, :content ["1"]}
                                {:tag :item, :attrs nil, :content ["2"]}]})))

  (is (= {:items {:item [{:bar "x", :foo "1"}
                         {:bar "y", :foo "2"}]}}
         (p/xml->clj {:tag :items, :attrs nil
                      :content [{:tag :item, :attrs nil
                                 :content [{:tag :foo, :attrs nil, :content ["1"]}
                                           {:tag :bar, :attrs nil, :content ["x"]}]}
                                {:tag :item, :attrs nil
                                 :content [{:tag :foo, :attrs nil, :content ["2"]}
                                           {:tag :bar, :attrs nil, :content ["y"]}]}]})))

  (testing "Alternative map implementation"
    (is (instance? LinkedMap
                   (p/xml->clj {:tag :foo, :attrs nil, :content ["bar"]}
                               {:create-map linked/map})))))
