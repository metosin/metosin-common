(ns metosin.copy-namespaces
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn copy-namespaces
  [namespaces target exts]
  (doseq [namespace namespaces
          ext exts
          :let [url-path (str (str/replace (munge namespace) "." "/") ext)
                res (io/resource url-path)]
          :when res
          :let [trg (apply io/file target (str/split url-path #"/"))]]
    (io/make-parents trg)
    (println "Copy" url-path "to" (.getPath trg))
    (spit trg (slurp res))))

(defn delete-folders [folders]
  (doseq [d folders
          f (file-seq (io/file d))]
    (.delete f)))

(comment
  (copy-namespaces '(metosin.dates) "foobar" [".cljc"]))
