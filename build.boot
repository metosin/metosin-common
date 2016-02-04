(set-env!
  ; Test path can be included here as source-files are not included in JAR
  ; Just be careful to not AOT them
  :source-paths #{"test/clj" "test/cljc" "test/cljs"}
  :resource-paths #{"src/clj" "src/cljc" "src/cljs"}
  :dependencies '[[org.clojure/clojure "1.7.0" :scope "test"]
                  [org.clojure/clojurescript "1.7.228" :scope "test"]

                  [boot/core "2.5.2" :scope "test"]
                  [adzerk/boot-cljs "1.7.170-3" :scope "test"]
                  [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT" :scope "test"]
                  [adzerk/boot-test "1.1.0" :scope "test"]

                  ;; for testing metosin.jdbc
                  [com.h2database/h2 "1.4.191" :scope "test"]

                  ;; metosin.core.async.debounce
                  [org.clojure/core.async "0.2.374"]
                  ;; metosin.email
                  [org.clojure/tools.logging "0.3.1"]
                  ;; metosin.dates.generators
                  [org.clojure/test.check "0.9.0"]
                  ;; metosin.email
                  [metosin/palikka "0.3.0"]
                  ;; metosin.ping
                  [metosin/ring-http-response "0.6.5"]
                  ;; metosin.forms
                  [metosin/lomakkeet "0.2.7"]
                  ;; metosin.ui.routing.schema
                  [metosin/schema-tools "0.7.0"]
                  ;; metosin.ui.routing.schema
                  [metosin/potpuri "0.2.3"]
                  ;; metosin.email
                  [de.ubercode.clostache/clostache "1.4.0"]
                  ;; metosin.email
                  [com.draines/postal "1.11.4"]
                  ;; metosin.postgres.joda.time, metosin.dates
                  [clj-time "0.11.0"]
                  ;; metosin.jdbc, metosin.postgres.joda.time, metosin.postgres.types
                  [org.clojure/java.jdbc "0.4.2"]
                  ;; metosin.postgres.types
                  [org.postgresql/postgresql "9.4.1207.jre7"]
                  ;; metosin.sql
                  [honeysql "0.6.2"]
                  ;; metosin.ping
                  [aleph "0.4.1-beta3"]
                  ;; metosin.postgres.types
                  [cheshire "5.5.0"]
                  ;; metosin.transit.dates
                  [com.cognitect/transit-clj "0.8.269"]
                  ;; metosin.bootstrap.modal, metosin.bootstrap.tabs
                  [reagent "0.6.0-alpha"]
                  ;; metosin.forms, metosin.email, metosin
                  [prismatic/schema "1.0.4"]
                  ;; metosin.ping
                  [jarohen/chord "0.7.0"]
                  ;; metosin.ui.routing.schema
                  [com.domkm/silk "0.1.1"]])

(require
  '[adzerk.boot-cljs :refer [cljs]]
  '[adzerk.boot-test :refer [test]]
  '[crisptrutski.boot-cljs-test :refer [test-cljs]])

(task-options!
  pom {:project 'metosin/metosin-common
       :version "0.1.0-SNAPSHOT"
       :description "Random collection is various namespaces used in multiple Metosin projects."
       :license {"Eclipse Public License" "http://opensource.org/licenses/mit-license.php"}}
  cljs {:source-map true})

(deftask run-tests []
  (comp
    (test)
    (test-cljs)))

(deftask dev []
  (comp
    (watch)
    (repl :server true)
    (pom)
    (jar)
    (install)
    (run-tests)))
