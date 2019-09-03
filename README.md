# Metosin-common

[![Clojars Project](https://img.shields.io/clojars/v/metosin/metosin-common.svg)](https://clojars.org/metosin/metosin-common)
[![Build Status](https://travis-ci.org/metosin/metosin-common.svg?branch=master)](https://travis-ci.org/metosin/metosin-common)

Random collection is various namespaces used in multiple Metosin projects.

Our intent is to package bits and pieces from this repo as proper libraries where it makes sense.

## Project statement

Unlike more general of our libraries (like
[compojure-api](https://github.com/metosin/compojure-api) and
[ring-swagger](https://github.com/metosin/ring-swagger)) this project is
primarily intended for use in Metosin's projects. Feel free to use, but
don't expect full support.

- We might remove features if we think they are not useful anymore
- We will reject PRs and issues about features we wouldn't use ourselves

## Use

Recommended way to use this will be to copy the namespaces the project uses
into the project source-paths. To help with this, one can use
`metosin.copy-namespaces` in a script that can be ran using `clj`.

Add deps.edn alias:
```
{:metosin-common {:extra-deps {metosin/metosin-common {:mvn/version "0.6.0"}}
                  :main-opts ["metosin_common.clj"]}}
```

Create `metosin_common.clj` script, check [example](./metosin_common_example.clj).

## License

Copyright © 2016-2019 [Metosin Oy](http://www.metosin.fi).

Distributed under the Eclipse Public License 1.0, the same as Clojure.

[`metosin.testing`](./src/cljc/metosin/testing.cljc) is based on code from clojure.test, which comes with the following notice:

> Copyright (c) Rich Hickey. All rights reserved.
