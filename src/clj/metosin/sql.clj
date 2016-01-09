(ns metosin.sql
  (:require [honeysql.format :as fmt]))

;; Expand honeysql to support PSQL ilike (case-insensitive patterns)
(defmethod fmt/fn-handler "ilike" [_ col qstr]
  (str (fmt/to-sql col) " ilike " (fmt/to-sql qstr)))

(defmethod fmt/fn-handler "not-ilike" [_ col qstr]
  (str (fmt/to-sql col) " not ilike " (fmt/to-sql qstr)))

(defn merge-and [& args]
  (let [args (filter identity args)]
    (if (seq (rest args))
      (into [:and] args)
      (if (first args)
        (first args)))))

(defn merge-or [& args]
  (let [args (filter identity args)]
    (if (seq (rest args))
      (into [:or] args)
      (if (first args)
        (first args)))))
