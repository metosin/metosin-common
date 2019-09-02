(ns metosin.ring.util.cache-test
  (:require [metosin.ring.util.cache :as cache]
            [clojure.test :refer [deftest is]]))

(def handler (-> (fn [req]
                   (cond-> {:status 200 :body nil}
                     (:no-cache req) (cache/cache-control cache/no-cache)))
                 (cache/wrap-cache {:value cache/cache-30d
                                    :default? true})))

(deftest wrap-cache-test
  (is (= {:status 200, :body nil, :headers {"Cache-Control" cache/cache-30d}}
         (handler {})))
  (is (= {:status 200, :body nil, :headers {"Cache-Control" cache/no-cache}}
         (handler {:no-cache true}))))
