(def +version+ "0.4.2-SNAPSHOT")

(set-env!
  ; Test path can be included here as source-files are not included in JAR
  ; Just be careful to not AOT them
  :source-paths #{"test/clj" "test/cljc" "test/cljs" "dev-resources"}
  :resource-paths #{"src/clj" "src/cljc" "src/cljs"}
  :dependencies '[[org.clojure/clojure "1.9.0" :scope "provided"]
                  [org.clojure/clojurescript "1.10.339" :scope "test"]

                  [boot/core "2.7.2" :scope "test"]
                  [adzerk/boot-cljs "2.1.4" :scope "test"]
                  [crisptrutski/boot-cljs-test "0.3.4" :scope "test"]
                  [doo "0.1.8" :scope "test"]
                  [metosin/bat-test "0.4.0" :scope "test"]

                  ;; for testing metosin.jdbc
                  [com.h2database/h2 "1.4.197" :scope "test"]

                  ;; metosin.jdbc
                  [potemkin "0.4.5"]
                  ;; metosin.dates
                  [joda-time/joda-time "2.10"]
                  ;; metosin.core.async.debounce
                  [org.clojure/core.async "0.4.474"]
                  ;; metosin.email
                  [org.clojure/tools.logging "0.4.0"]
                  ;; metosin.dates.generators
                  [org.clojure/test.check "0.9.0"]
                  ;; metosin.email
                  [metosin/palikka "0.5.4"]
                  ;; metosin.ping
                  [metosin/ring-http-response "0.9.0"]
                  ;; metosin.ui.routing.schema
                  [metosin/schema-tools "0.10.3"]
                  ;; metosin.ui.routing.schema
                  [metosin/potpuri "0.5.1"]
                  ;; metosin.email
                  [de.ubercode.clostache/clostache "1.4.0"]
                  ;; metosin.email
                  [com.draines/postal "2.0.2"]
                  ;; metosin.jdbc, metosin.postgres.joda.time, metosin.postgres.types
                  [org.clojure/java.jdbc "0.7.7"]
                  ;; metosin.jdbc
                  [camel-snake-kebab "0.4.0"]
                  ;; metosin.postgres.types
                  [org.postgresql/postgresql "42.2.2"]
                  ;; metosin.sql
                  [honeysql "0.9.3"]
                  ;; metosin.ping
                  [aleph "0.4.6"]
                  ;; metosin.postgres.types
                  [cheshire "5.8.0"]
                  ;; metosin.transit.dates
                  [com.cognitect/transit-clj "0.8.309"]
                  [com.cognitect/transit-cljs "0.8.256"]
                  ;; metosin.ping
                  [reagent "0.8.1"]
                  ;; metosin.email, metosin
                  [prismatic/schema "1.1.9"]
                  ;; metosin.ping
                  [jarohen/chord "0.8.1"]
                  ;; metosin.ui.routing.schema
                  [com.domkm/silk "0.1.2"]])

(require
  '[adzerk.boot-cljs :refer [cljs]]
  '[metosin.bat-test :refer [bat-test]]
  '[crisptrutski.boot-cljs-test :refer [test-cljs]])

(task-options!
  pom {:project 'metosin/metosin-common
       :version +version+
       :description "Random collection of various namespaces used in multiple Metosin projects."
       :license {"Eclipse Public License" "http://opensource.org/licenses/mit-license.php"}
       :scm {:url "https://github.com/metosin/metosin-common"}}
  cljs {:source-map true})

(deftask build []
  (comp
    (pom)
    (jar)
    (install)))

(ns-unmap *ns* 'test)

(deftask test []
  (comp
    (bat-test)
    (test-cljs :exit? true)))

(deftask dev []
  (comp
    (watch)
    (repl :server true)
    (pom)
    (jar)
    (install)
    (bat-test)
    (test-cljs :exit? false)))

(deftask deploy []
  (comp
    (build)
    (push :repo "clojars" :gpg-sign (not (.endsWith +version+ "-SNAPSHOT")))))
