(ns metosin.ring.util.cache-test
  (:require [metosin.ring.util.cache :refer :all]
            [clojure.test :refer :all]))

(def handler (-> (fn [req]
                   (cond-> {:status 200 :body nil}
                     (:no-cache req) (cache-control no-cache)))
                 (wrap-cache {:value cache-30d
                              :default? true})))

(deftest wrap-cache-test
  (is (= {:status 200, :body nil, :headers {"cache-control" cache-30d}}
         (handler {})))
  (is (= {:status 200, :body nil, :headers {"cache-control" no-cache}}
         (handler {:no-cache true}))))
