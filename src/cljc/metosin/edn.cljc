(ns metosin.edn
  "Side-effect-free edn-reader for both clj & cljs"
  (:require #?(:clj [clojure.edn :as edn]
               :cljs [cljs.tools.reader.edn :as edn]))
  #?(:clj (:refer-clojure :exclude [read-string])))

(defn read-string
  ([s]
   (read-string {} s))
  ([opts s]
   (edn/read-string opts s)))
