(ns metosin.ping-test
  (:require [metosin.ping :as ping]
            [clojure.test :refer [deftest is]]
            [aleph.http :as http]
            [aleph.netty :as netty]
            [manifold.stream :as s]))

(def handler (ping/create-handler))

(deftest ping-test
  (with-open [server (http/start-server handler {:port 0})]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"status: 400" @(http/get (str "http://localhost:" (netty/port server) "/ping"))))

    (let [client @(http/websocket-client (str "ws://localhost:" (netty/port server) "/ping"))]
      (s/put! client (pr-str {:type :ping}))
      (is (= (pr-str {:type :pong}) @(s/take! client)))
      (s/put! client (pr-str {:type :ping}))
      (is (= (pr-str {:type :pong}) @(s/take! client))))))
