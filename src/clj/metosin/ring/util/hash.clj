(ns metosin.ring.util.hash
  (:require [clojure.java.io :as io])
  (:import (org.apache.commons.codec.digest DigestUtils)))

(defn resource-hash [resource-name]
  (if-let [res (io/resource resource-name)]
    (with-open [is (io/input-stream res)]
      (DigestUtils/md5Hex is))))

(def memo-resource-hash (memoize resource-hash))
