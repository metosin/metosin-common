(ns metosin.jdbc
  "Wraps clojure.jdbc with optionated entities and identifiers fn."
  (:require [clojure.string :as string]))

(defn entities [x]
  (string/replace x #"-" "_"))

(defn identifiers [x]
  (-> x
      (string/lower-case)
      (string/replace #"_" "-")))

(def db-spec-defaults
  {:entities entities
   :identifiers identifiers})
