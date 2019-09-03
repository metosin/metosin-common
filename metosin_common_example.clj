;; Example file, use similar file in your project with e.g. deps.edn:

;; This file copies namespaces from metosin-common in classpath
;; to source paths in the project. Select the version using deps.edn.

(require '[metosin.copy-namespaces :refer :all])

;; Reset folders to ensure only following namespaces are present
(delete-folders ["src/clj/metosin" "src/cljc/metosin"])

(copy-namespaces '[metosin.ring.util.etag
                   metosin.ring.util.last-modified
                   metosin.ring.util.cache
                   metosin.postgres.types]
                 "src/clj" [".clj"])

(copy-namespaces '[metosin.dates
                   metosin.transit.dates]
                 "src/cljc" [".cljc"])

