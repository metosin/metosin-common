(ns metosin.jdbc
  "Wraps clojure.jdbc with optionated entities and identifiers fn."
  (:require [clojure.string :as string]
            [clojure.java.jdbc :as jdbc]
            [potemkin :refer [import-vars]]
            [clojure.walk :refer [postwalk]]
            [camel-snake-kebab.core :refer [->kebab-case]]))


(defn entities [x]
  (string/replace x #"-" "_"))

(defn identifiers [x]
  (-> x
      (string/lower-case)
      (string/replace #"_" "-")))

(def db-spec-defaults
  {:entities entities
   :identifiers identifiers})

;; Deprecated?

(defn
  ^{:doc (:doc (meta #'jdbc/query))
    :deprecated true}
  query
  ([db sql-params](query db sql-params {}))
  ([db sql-params options]
   (jdbc/query db sql-params (assoc options :identifiers identifiers :entities entities))))

(defn
  ^{:doc (:doc (meta #'jdbc/find-by-keys))
    :deprecated true}
  find-by-keys
  ([db table columns] (find-by-keys db table columns {}))
  ([db table columns opts]
   (jdbc/find-by-keys (assoc opts :identifiers identifiers :entities entities))))

(defn
  ^{:doc (:doc (meta #'jdbc/get-by-id))
    :deprecated true}
  get-by-id
  ([db table columns] (find-by-keys db table columns {}))
  ([db table columns opts]
   (jdbc/find-by-keys (assoc opts :identifiers identifiers :entities entities))))

(defn
  ^{:doc (:doc (meta #'jdbc/insert!))
    :deprecated true}
  insert!
  ([db table row] (insert! db table row {}))
  ([db table cols-or-row values-or-opts]
   (if (map? values-or-opts)
     (jdbc/insert! db table cols-or-row (assoc values-or-opts :entities entities :identifiers identifiers))
     (jdbc/insert! db table cols-or-row values-or-opts {:entities entities :identifiers identifiers})))
  ([db table cols values opts]
   (jdbc/insert! db table cols values (assoc opts :entities entities :identifiers identifiers))))

(defn
  ^{:doc (:doc (meta #'jdbc/insert-multi!))
    :deprecated true}
  insert-multi!
  ([db table rows] (insert-multi! db table rows {}))
  ([db table cols-or-rows values-or-opts]
   (if (map? values-or-opts)
     (jdbc/insert-multi! db table cols-or-rows (assoc values-or-opts :entities entities :identifiers identifiers))
     (jdbc/insert-multi! db table cols-or-rows values-or-opts {:entities entities :identifiers identifiers})))
  ([db table cols values opts]
   (jdbc/insert-multi! db table cols values (assoc opts :entities entities :identifiers identifiers))))

(defn
  ^{:doc (:doc (meta #'jdbc/update!))
    :deprecated true}
  update!
  ([db table set-map where-clause] (update! db table set-map where-clause {}))
  ([db table set-map where-clause options]
   (jdbc/update! db table set-map where-clause (assoc options :entities entities :identifiers identifiers))))

(defn
  ^{:doc (:doc (meta #'jdbc/delete!))
    :deprecated true}
  delete!
  ([db table where-clause] (delete! db table where-clause {}))
  ([db table where-clause options]
   (jdbc/delete! db table where-clause (assoc options :entities entities :identifiers identifiers))))

(import-vars
  [clojure.java.jdbc

   execute!

   with-db-transaction
   with-db-metadata
   with-db-connection

   db-do-commands
   db-do-prepared

   create-table-ddl])
