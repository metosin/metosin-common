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

## TODO

- [ ] Test everything
- [ ] Start using

## Ideas

- [ ] Static resources handler and middleware
- [ ] Common users, groups and logic backend and UI
- [ ] Changelog UI and generation mechanism...?

## Separate useful stuff

1. Cljc Date library
2. Postgres library
3. Reagent components library?

## License

Copyright © 2016 [Metosin Oy](http://www.metosin.fi)

Distributed under the Eclipse Public License, the same as Clojure.
