(ns metosin.edn
  "Side-effect-free edn-reader for both clj & cljs"
  (:require
    #?(:clj
        [clojure.edn :as edn]
       :cljs [cljs.tools.reader :as reader]))
  #?(:clj
     (:refer-clojure :exclude [read-string])))

(defn read-string
  ([s]
    (read-string {} s))
  ([opts s]
    #?(:clj (edn/read-string opts s)
       :cljs (reader/read-string opts s))))


