(ns metosin.ui.mouse
  (:require [clojure.core.async :as a]))

;;
;; Window clicks singleton channel
;;

(def ^:private window-clicks (a/chan))
(def ^:private window-clicks-mult (a/mult window-clicks))

(defn listen-window-clicks [chan]
  (a/tap window-clicks-mult chan))

(defn unlisten-window-clicks [chan]
  (a/untap window-clicks-mult chan))

(-> js/window .-onclick (set! (fn [e] (a/put! window-clicks {:target (.-target e)}))))
