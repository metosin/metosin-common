(ns metosin.ui.core)

(defn get-or-deref [x]
  (if (satisfies? IDeref x) @x x))

(defn prevent-default [e]
  (doto e
    (.preventDefault)
    (.stopPropagation)))
