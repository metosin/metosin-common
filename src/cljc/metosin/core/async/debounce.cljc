(ns metosin.core.async.debounce
  (:require [clojure.core.async :as a]))

(defn debounce
  "Creates a channel which will change put a new value to the output channel
   after timeout has passed. Each value change resets the timeout. If value
   changes more frequently only the latest value is put out.
   When input channel closes, the output channel is closed."
  [in ms]
  (let [out (a/chan)]
    (a/go-loop [last-val nil]
      (let [val (if (nil? last-val) (a/<! in) last-val)
            timer (a/timeout ms)]
        (a/alt!
          in ([v] (if v
                    (recur v)
                    (a/close! out)))
          timer ([_] (do (a/>! out val) (recur nil))))))
    out))

