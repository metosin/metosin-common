(ns metosin.ring.util.etag
  (:require [ring.util.response :as resp])
  (:import [org.apache.commons.codec.digest DigestUtils]))

(defn etag-hash' [v]
  (if v (DigestUtils/md5Hex v)))

(def etag-hash (memoize etag-hash'))

(defn etag [req v]
  (resp/header req "ETag" v))

(defn none-match-hash? [if-none-match hash]
  (and if-none-match hash (= if-none-match hash)))

;; TODO: there is also if-match header

(defn none-match? [req hash]
  (none-match-hash? (get (:headers req) "if-none-match") hash))
