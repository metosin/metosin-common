(ns metosin.ping
  (:require [reagent.core :as r]
            [chord.client :refer [ws-ch]]
            [clojure.core.async :refer [go alt! chan <! >! close! timeout]]
            [common.loc :refer [loc]]))

(defonce prev-channel (atom nil))
(defonce offline? (r/atom false))

(def +ping-every+ 1500)
(def +timeout+ 2000)
(def +fails+ 3)

(defn reconnect []
  (js/window.location.reload))

(defn create-url [path]
  (let [proto (.. js/window -location -protocol)
        host  (.. js/window -location -host)]
    (str (if (= "https:" proto) "wss" "ws") ":" host path)))

(defn start-ping []
  (let [ctrl-ch (chan)]
    (go
     (if-let [x @prev-channel] (>! x true))
     (reset! prev-channel ctrl-ch)
     (loop [conn nil
            timeout-ch (timeout +timeout+)
            fail 0]
       (reset! offline? (>= fail +fails+))
       (if-not conn
         (let [y (ws-ch (create-url "/ping"))
               {:keys [ws-channel error] :as x} (alt! ctrl-ch :stop
                                                      y ([v] v))]
           (if (= :stop x)
             (do
              (close! conn)
              nil)
             (if error
               (do
                (<! (timeout +ping-every+))
                (recur nil (timeout +timeout+) (inc fail)))
               (do
                (if (>= fail +fails+) (reconnect))
                (>! ws-channel {:type :ping})
                (recur ws-channel (timeout +timeout+) 0)))))
         (let [{:keys [type error]} (alt! timeout-ch {:type :timeout}
                                          conn ([v] (:message v))
                                          ctrl-ch {:type :stop})]
           (if (and type (not error))
             (case type
               :stop nil
               :timeout (recur conn (timeout +timeout+) (inc fail))
               :pong (do (if (pos? fail) (reconnect))
                         (<! (timeout +ping-every+))
                         (>! conn {:type :ping})
                         (recur conn (timeout +timeout+) 0))
               nil)
             (recur nil (timeout +timeout+) (inc fail)))))))
    ctrl-ch))

(start-ping)

(defn ping-view []
  (when @offline?
    [:div.ping-view
     {:style {:opacity 1
              :display "block"
              :tab-index "-1"}}
     [:div
      {:style {:position "fixed"
               :top 0
               :bottom 0
               :left 0
               :right 0
               :z-index 1040
               :background "#000"
               :opacity 0.5}}]
     [:div
      {:style {:position "fixed"
               :top 0
               :bottom 0
               :left 0
               :right 0
               :display "flex"
               :align-items "center"
               :justify-content "center"
               :z-index 1050}}
      [:div.ping-view-text
       {:style {:margin "0 15%"}}
       [:h1
        {:style {:text-align "center"
                 :color "#fff"
                 :text-shadow "0 0 10px rgba(0,0,0,1)"}}
        [:i.fa.fa-spinner.fa-pulse] " " (loc :app.offline)]]]]))
