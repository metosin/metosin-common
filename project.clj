(defproject com.metosin/metosin-common "0.1.0-SNAPSHOT"
  :description "Random collection is various namespaces used in multiple Metosin projects."
  :license {:name "Proprietary"}
  :url "https://github.com/metosin/metosin-common"
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :source-paths ["src/clj" "src/cljc" "src/cljs"]
  :plugins [[circle/s3-wagon-private "1.2.2"]]
  :repositories [["private" {:url "s3p://metosin-maven/releases/" :creds :gpg}]])
