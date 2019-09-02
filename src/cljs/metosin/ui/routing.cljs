(ns metosin.ui.routing
  "Please use Reitit-frontend instead."
  {:deprecated "0.6.0"}
  (:require [clojure.set :refer [rename-keys]]
            [clojure.core.async :refer [<! go]]
            clojure.core.async.impl.channels
            [domkm.silk :as silk]))

;;
;; Route data
;;

(deftype AdditionalData [data]
  silk/Pattern
  (-match [_ _]
    data)
  (-unmatch [_ _]
    nil)
  (-match-validator [_]
    (constantly true))
  (-unmatch-validators [_]
    {}))

(defn data
  "Merges additional data into route data.

  Useful for:
  - marking if the route is private or public
  - marking what menu item should be active"
  [x]
  (AdditionalData. x))

;;
;; Impl
;;

(defn- chan? [v]
  (instance? cljs.core.async.impl.channels.ManyToManyChannel v))

(defn- query-params [match]
  (assoc match :routing/query (-> match :domkm.silk/url :query)))

(defn- update-route! [routes cursor route-change]
  (let [prev @cursor
        uri (subs js/location.hash 1)
        r (or (some-> (silk/arrive routes uri)
                      (query-params)
                      (dissoc :domkm.silk/url :domkm.silk/routes :domkm.silk/pattern)
                      (rename-keys {:domkm.silk/name :routing/id}))
              {:routing/id :default})
        v (route-change r prev)]
    (if (chan? v)
      (go
        (loop []
          (let [x (<! v)]
            (when x
              (reset! cursor x)
              (recur)))))
      (if v (reset! cursor v)))))

(defn hook!
  "Adds `onhashchange` hook and calls update-route logic when hash change event happens.

  Update-route will call given `route-change` function. `route-change` should return a
  new route data or a channel which will contain new route data in future. Channel
  can return multiple route data maps. This can be used to delay route-change
  until e.g. data is loaded.

  FIXME: Using cursor/atom is stupid.
  Update-route will set the new route data to given cursor using `reset!`."
  [routes cursor route-change]
  (set! js/window.onhashchange (partial update-route! routes cursor route-change))
  (update-route! routes cursor route-change))

(defn href [routes k & [params]]
  (try
    (str "#" (silk/depart routes k params))
    (catch js/Error _
      (js/console.log "Couldn't create href for" (pr-str k) (pr-str params))
      nil)))

(defn update-uri!
  "Warning: this triggers dispatch!"
  [routes k & [params]]
  (set! js/window.location.hash (href routes k params)))
