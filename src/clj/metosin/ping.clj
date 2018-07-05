(ns metosin.ping
  (:require [manifold.deferred :as d]
            [manifold.stream :as s]
            [aleph.http :as http]))

(defn ping-handler [req]
  (d/let-flow [conn (d/catch
                      (http/websocket-connection req)
                      (fn [_] nil))]
    (if-not conn
      {:status 400}
      (s/consume (fn [_]
                   (s/put! conn (pr-str {:type :pong})))
                 conn))))

(defn create-handler []
  (fn [{:keys [request-method uri] :as req}]
    (if (= request-method :get)
      (case uri
        "/ping" (ping-handler req)
        nil))))
