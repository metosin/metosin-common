(ns metosin.loc
  (:require [clojure.string :as s]
            #?(:cljs [goog.string :as gs])))

(defn- missing [ks]
  (#?(:clj println, :cljs js/console.log) "LOC: missing term: [" (pr-str ks) "]")
  (str "**LOC:" (pr-str ks) "**"))

(defn- korks->ks
  "If given keyword, turn it to keyword vector, else return the original value."
  [korks]
  (if (keyword? korks)
    (-> korks
        name
        (s/split #"\.")
        (->> (map keyword)))
    korks))

(defn loc* [terms ks & args]
  (if-let [text (get-in terms (korks->ks ks))]
    (if (seq args) (apply #?(:cljs gs/format, :clj format) text args) text)
    (missing ks)))

; Schema error -> loc ks
; (defn error-message [v]
;   (if (instance? schema.utils.ValidationError v)
;     (let [[b] @(.-expectation-delay v)]
;       [:errors (keyword b)])))
