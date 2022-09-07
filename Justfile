help:
    @just --list

lint:
    clj-kondo --lint src test

test:
    clojure -M:test

autotest:
    clojure -M:test --watch

# Run Cljs unit tests once (shadow-cljs compile, karma run)
test-cljs:
	npx shadow-cljs compile test
	npx karma start --single-run

# Start cljs compilation for cljs unit test suite. Start Karma separately to run the tests via `make autotest-cljs`
autotest-cljs-compile:
	npx shadow-cljs watch test

# Start karma test runner for cljs unit test suite (also needs shadow-cljs process)
autotest-cljs:
	npx karma start
