(ns metosin.postgres
  (:require [metosin.postgres.types :as t]))

(defn enum
  "Turns keywords to Postgres enums. Keyword namespace -> enum type,
   keyword name -> enum value.

   For example on insert '{:status (enum :oppo_status_type/active)}'"
  [kword]
  (t/->PgType (namespace kword) (name kword)))

