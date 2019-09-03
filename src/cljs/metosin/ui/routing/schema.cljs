(ns metosin.ui.routing.schema
  "Please use Reitit-frontend instead."
  {:deprecated "0.6.0"}
  (:require [domkm.silk :as silk]
            [schema.coerce :as sc]
            [schema-tools.core :as st]
            [clojure.string :as string]))

(defprotocol ToUrlParam
  (-to-url-param [this]))

(defn to-url-param [x]
  (if (satisfies? ToUrlParam x)
    (-to-url-param x)
    x))

(extend-protocol ToUrlParam
  PersistentHashSet
  (-to-url-param [this]
    ; FIXME: Values must not contains commas
    (string/join "," (map to-url-param this)))
  ISequential
  (-to-url-param [this]
    ; FIXME: Values must not contains commas
    (string/join "," (map to-url-param this)))
  Keyword
  (-to-url-param [this] (name this)))

(defrecord SchemaQueryParams [schema coercer]
  silk/Pattern
  (-match [this that]
    (coercer
      (persistent!
        (reduce-kv (fn [acc k v]
                     (assoc! acc (keyword k) v))
                   (transient (empty that))
                   that))))
  (-unmatch [this that]
    (persistent!
      (reduce-kv (fn [acc k v]
                   (assoc! acc (name k) (to-url-param v)))
                 (transient {})
                 (st/select-schema that schema))))
  (-match-validator [_]
    some?)
  (-unmatch-validators [_]
    {}))

(defn collection-matcher [schema]
  (if (or (sequential? schema) (set? schema))
    (fn [value]
      (if (string? value)
        (into (empty schema) (string/split value #","))
        value))))

(defn query-string-coercion-matcher [schema]
  (or (collection-matcher schema)
      (sc/string-coercion-matcher schema)))

(defn schema-query
  ([schema] (schema-query schema nil))
  ([schema {:keys [coercion-matcher]
            :or {coercion-matcher query-string-coercion-matcher}}]
   (map->SchemaQueryParams {:schema schema
                            :coercer (sc/coercer schema coercion-matcher)})))
