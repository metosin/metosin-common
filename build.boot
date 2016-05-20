(def +version+ "0.2.2")

(set-env!
  ; Test path can be included here as source-files are not included in JAR
  ; Just be careful to not AOT them
  :source-paths #{"test/clj" "test/cljc" "test/cljs"}
  :resource-paths #{"src/clj" "src/cljc" "src/cljs"}
  :dependencies '[[org.clojure/clojure "1.8.0" :scope "test"]
                  [org.clojure/clojurescript "1.8.51" :scope "test"]

                  [boot/core "2.5.5" :scope "test"]
                  [adzerk/boot-cljs "1.7.228-1" :scope "test"]
                  [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT" :scope "test"]
                  [adzerk/boot-test "1.1.1" :scope "test"]

                  ;; for testing metosin.jdbc
                  [com.h2database/h2 "1.4.191" :scope "test"]

                  ;; metosin.jdbc
                  [potemkin "0.4.3"]
                  ;; metosin.dates
                  [joda-time/joda-time "2.9.3"]
                  ;; metosin.core.async.debounce
                  [org.clojure/core.async "0.2.374"]
                  ;; metosin.email
                  [org.clojure/tools.logging "0.3.1"]
                  ;; metosin.dates.generators
                  [org.clojure/test.check "0.9.0"]
                  ;; metosin.email
                  [metosin/palikka "0.5.0"]
                  ;; metosin.ping
                  [metosin/ring-http-response "0.6.5"]
                  ;; metosin.ui.routing.schema
                  [metosin/schema-tools "0.9.0"]
                  ;; metosin.ui.routing.schema
                  [metosin/potpuri "0.3.0"]
                  ;; metosin.email
                  [de.ubercode.clostache/clostache "1.4.0"]
                  ;; metosin.email
                  [com.draines/postal "1.11.4"]
                  ;; metosin.jdbc, metosin.postgres.joda.time, metosin.postgres.types
                  [org.clojure/java.jdbc "0.6.1"]
                  ;; metosin.jdbc
                  [camel-snake-kebab "0.3.2"]
                  ;; metosin.postgres.types
                  [org.postgresql/postgresql "9.4.1208"]
                  ;; metosin.sql
                  [honeysql "0.6.3"]
                  ;; metosin.ping
                  [aleph "0.4.1"]
                  ;; metosin.postgres.types
                  [cheshire "5.6.1"]
                  ;; metosin.transit.dates
                  [com.cognitect/transit-clj "0.8.285"]
                  [com.cognitect/transit-cljs "0.8.237"]
                  ;; metosin.ping
                  [reagent "0.6.0-alpha2"]
                  ;; metosin.forms, metosin.email, metosin
                  [prismatic/schema "1.1.1"]
                  ;; metosin.ping
                  [jarohen/chord "0.7.0"]
                  ;; metosin.ui.routing.schema
                  [com.domkm/silk "0.1.2"]])

(require
  '[adzerk.boot-cljs :refer [cljs]]
  '[adzerk.boot-test :refer [test]]
  '[crisptrutski.boot-cljs-test :refer [test-cljs]])

(task-options!
  pom {:project 'metosin/metosin-common
       :version +version+
       :description "Random collection of various namespaces used in multiple Metosin projects."
       :license {"Eclipse Public License" "http://opensource.org/licenses/mit-license.php"}}
  cljs {:source-map true})

(deftask build []
  (comp
    (pom)
    (jar)
    (install)))

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

(deftask deploy []
  (comp
    (build)
    (push :repo "clojars" :gpg-sign (not (.endsWith +version+ "-SNAPSHOT")))))
