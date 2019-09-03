(ns metosin.edn
  "Side-effect-free edn-reader for both clj & cljs

  Use clojure.tools.reader.edn, it is available in both environments."
  {:deprecated "0.6.0"}
  (:require #?(:clj [clojure.tools.reader.edn :as edn]
               :cljs [cljs.tools.reader.edn :as edn]))
  #?(:clj (:refer-clojure :exclude [read-string])))

(defn read-string
  ([s]
   (read-string {} s))
  ([opts s]
   (edn/read-string opts s)))
