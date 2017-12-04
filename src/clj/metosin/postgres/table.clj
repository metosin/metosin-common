(ns metosin.postgres.table
  (:require [clojure.java.jdbc :as jdbc]))

(defn table-details
  "Extracts table details as a map PostgreSQL Database table."
  [db schema table]
  (with-open [conn (jdbc/get-connection db)]
    (->> (.getColumns (.getMetaData conn) nil schema table nil)
         resultset-seq
         (map (fn [{:keys [column_name type_name column_size is_nullable is_autoincrement]}]
                {:size column_size
                 :name column_name
                 :type type_name
                 :nillable? (= "YES" is_nullable)
                 :auto-increment? (= "YES" is_autoincrement)})))))

(defn table-names
  "Extracts all table names as keywords from a PostgreSQL Database Schema."
  [db schema]
  (->> ["select table_name from information_schema.tables where table_schema = ?"]
       (jdbc/query db)
       (map (comp keyword :table_name))))
