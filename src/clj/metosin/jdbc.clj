(ns metosin.jdbc
  "Wraps clojure.jdbc with optionated entities and identifiers fn."
  (:require [clojure.string :as string]
            [clojure.java.jdbc :as jdbc]
            [potemkin :refer [import-vars]]))

(defn entities [x]
  (string/replace x #"-" "_"))

(defn identifiers [x]
  (-> x
      (string/lower-case)
      (string/replace #"_" "-")))

(defn query
  {:arglists '([db-spec sql-and-params
                :as-arrays? false
                :result-set-fn doall :row-fn identity]
               [db-spec sql-and-params
                :as-arrays? true
                :result-set-fn vec :row-fn identity]
               [db-spec [sql-string & params]]
               [db-spec [stmt & params]]
               [db-spec [option-map sql-string & params]])}
  [db sql-params & {:as m}]
  (let [options (assoc m :identifiers identifiers)]
    (apply jdbc/query db sql-params (mapcat identity options))))

(defn insert!
  {:arglists '([db-spec table row-map :transaction? true]
               [db-spec table row-map & row-maps :transaction? true]
               [db-spec table col-name-vec col-val-vec & col-val-vecs :transaction? true])}
  [db table & options]
  (apply jdbc/insert! db table (concat options [:entities entities])))

(defn update!
  [db table set-map where-clause & {:as m}]
  (let [options (assoc m :entities entities)]
    (apply jdbc/update! db table set-map where-clause (mapcat identity options))))

(defn delete!
  [db table where-clause & {:as m}]
  (let [options (assoc m :entities entities)]
    (apply jdbc/delete! db table where-clause (mapcat identity options))))

(import-vars
  [clojure.java.jdbc

   execute!

   with-db-transaction
   with-db-metadata
   with-db-connection

   db-do-commands
   db-do-prepared
   db-transaction

   create-table-ddl])
