(def +version+ "0.6.0-SNAPSHOT")

(set-env!
  ; Test path can be included here as source-files are not included in JAR
  ; Just be careful to not AOT them
  :source-paths #{"test/clj" "test/cljc" "test/cljs" "dev-resources"}
  :resource-paths #{"src/clj" "src/cljc" "src/cljs" "resources" "src/scss"}
  :dependencies '[[org.clojure/clojure "1.10.1" :scope "provided"]
                  [org.clojure/clojurescript "1.10.520" :scope "test"]

                  [adzerk/boot-cljs "2.1.5" :scope "test"]
                  [crisptrutski/boot-cljs-test "0.3.4" :scope "test"]
                  [doo "0.1.8" :scope "test"]
                  [metosin/bat-test "0.4.3" :scope "test"]

                  ;; for testing metosin.jdbc
                  [com.h2database/h2 "1.4.199" :scope "test"]

                  ;; metosin.ring.util.*
                  [ring/ring-core "1.7.1" :scope "test"]
                  ;; metosin.dates
                  [joda-time/joda-time "2.10.3" :scope "test"]
                  ;; metosin.core.async.debounce
                  [org.clojure/core.async "0.4.500" :scope "test"]
                  ;; metosin.email
                  [org.clojure/tools.logging "0.5.0" :scope "test"]
                  ;; metosin.dates.generators
                  [org.clojure/test.check "0.10.0" :scope "test"]
                  ;; metosin.ui.routing.schema
                  [metosin/schema-tools "0.12.0" :scope "test"]
                  ;; metosin.email
                  [de.ubercode.clostache/clostache "1.4.0" :scope "test"]
                  ;; metosin.email
                  [com.draines/postal "2.0.3" :scope "test"]
                  ;; metosin.jdbc, metosin.postgres.joda.time, metosin.postgres.types
                  [org.clojure/java.jdbc "0.7.10" :scope "test"]
                  ;; metosin.postgres.types
                  [org.postgresql/postgresql "42.2.6" :scope "test"]
                  ;; metosin.sql
                  [honeysql "0.9.6" :scope "test"]
                  ;; metosin.ping
                  [aleph "0.4.6" :scope "test"]
                  ;; metosin.postgres.types
                  [cheshire "5.9.0" :scope "test"]
                  ;; metosin.transit.dates
                  [com.cognitect/transit-clj "0.8.313" :scope "test"]
                  [com.cognitect/transit-cljs "0.8.256" :scope "test"]
                  ;; metosin.ping
                  [reagent "0.8.1" :scope "test"]
                  ;; metosin.email, metosin
                  [prismatic/schema "1.1.12" :scope "test"]
                  ;; metosin.ping
                  [jarohen/chord "0.8.1" :scope "test"]
                  ;; metosin.ui.routing.schema
                  [com.domkm/silk "0.1.2" :scope "test"]
                  ;; metosin.xml
                  [frankiesardo/linked "1.3.0" :scope "test"]
                  ;; metosin.mime
                  [org.apache.tika/tika-core "1.23"]
                  ])

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
