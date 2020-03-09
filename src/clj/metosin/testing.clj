(ns metosin.testing
  (:require [clojure.test :refer [assert-any assert-expr do-report function?]]))

;; assert-not and assert-expr 'not are based from assert-predicate and assert-expr :default
;; <https://github.com/clojure/clojure/blob/c0326d2386dd1227f35f46f1c75a8f87e2e93076/src/clj/clojure/test.clj#L435>

;; Clojure only. Cljs.test/assert-expr has different arity.
;; Check https://seespotcode.net/2018/01/13/portable-clojure-test-assert-expr/ if cljs
;; version is needed.

(defn- assert-not [msg form]
  (let [subform (second form)
        args (rest subform)
        pred (first subform)]
    `(let [values# (list ~@args)
           result# (apply ~pred values#)]
       (if-not result#
         (do-report
           {:type :pass, :message ~msg,
            :expected '~form, :actual (list '~'not (cons '~pred values#))})
         (do-report
           {:type :fail, :message ~msg,
            :expected '~form, :actual (cons '~pred values#)}))
       result#)))

(defmethod assert-expr 'not [msg form]
  (let [subform (second form)]
    (if (and (sequential? subform) (function? (first subform)))
      (assert-not msg form)
      (assert-any msg form))))
