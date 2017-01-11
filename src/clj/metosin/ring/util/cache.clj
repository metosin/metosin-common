(ns metosin.ring.util.cache)

(def no-cache     "max-age=0,no-cache,no-store")
(def cache-30d    "public,max-age=2592000,s-maxage=2592000")

(defn cache-control
  "Returns an updated Ring response with the given or default cache-control header.

   Third parameter can be set to truthy value to only set cache-control if it is not
   already set."
  ([response] (cache-control response cache-30d))
  ([response value]
   (if (map? response)
     (assoc-in response [:headers "Cache-Control"] value)))
  ([response value default?]
   (if (and default? (get-in response [:headers "Cache-Control"]))
     response
     (cache-control response value))))

(defn wrap-cache
  "Options:
   - :value    - Value for the cache-control header.
   - :default? - If true, set the value only no cache-control is already set."
  [handler opts]
  (fn [req]
    (cache-control (handler req) (:value opts) (:default? opts))))
