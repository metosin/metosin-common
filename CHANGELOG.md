## Unreleased

- Update [java.jdbc](https://github.com/clojure/java.jdbc) to version 0.7-alpha3
    - `metosin.jdbc` functions have been removed, instead provides
    `db-spec-defaults` map with entities/identifiers functions. Merge this
    to your db spec to use these options.

## 0.4.0 (21.2.2017)

- Add some type hints to `metosin.dates`.
- Remove dangerous keyword -> PGobject `ISQLValue` implementation from `metosin.postgres.types`
- Removed `ISQLValue` implementation to write maps and vectors as JSON, instead provide new
`write-json` function to create JSON PGobject.
- Add `metosin.testing` which provides `assert-expr` method for handling `(is (not ...))`
- Added `metosin.dates/ToNative` implementation for native date objects, so `to-native`
returns the same object if the object is already native date object.

**[compare](https://github.com/metosin/metosin-common/compare/0.3.0...0.4.0)**

## 0.3.0 (11.1.2016)

- Deprecate `dates/date->str`, `dates/date-time->str`, use `format` instead
- Replace `dates/add` with `dates/plus`, similar to JodaTime API instead of Closure (`add` is still available, but deprecated).
- Add `dates/before?`, `dates/after?` and `dates/equal?` predicates ([#6](https://github.com/metosin/metosin-common/issues/6))
- Add `dates/date?` and `dates/date-time?` predicates
- Add `metosin.ring.utils.etag` and `metosin.ring.utils.last-modified` to work with conditional HTTP requests
- Fix Cache-Control header letter case ([#9](https://github.com/metosin/metosin-common/issues/9))

**[compare](https://github.com/metosin/metosin-common/compare/0.2.3...0.3.0)**

## 0.2.3 (2.8.2016)

- Add TLS support to `metosin.email` - pass in `:tls true` in SMTP settings.

**[compare](https://github.com/metosin/metosin-common/compare/0.2.2...0.2.3)**

## 0.2.2 (20.5.2016)

- New namespace: `metosin.edn.dates`
    - Implements EDN tag readers/writers for Joda-Time and goog.date.

**[compare](https://github.com/metosin/metosin-common/compare/0.2.1...0.2.2)**

## 0.2.1 (13.5.2016)

- Update java.jdbc to 0.6.1 to fix Postgres problems

**[compare](https://github.com/metosin/metosin-common/compare/0.2.0...0.1)**

## 0.2.0 (12.5.2016)

- **BREAKING**: Update java.jdbc to new version and removed all deprecated versions arities from metosin.jdbc namespace
- Update deps
- Move `metosin.bootstrap.*` to [Komponentit](https://github.com/metosin/komponentit)
- Drop `metosin.forms`

**[compare](https://github.com/metosin/metosin-common/compare/0.1.4...0.2.0)**

## 0.1.4 (13.4.2016)

- Disable warn of reflection

**[compare](https://github.com/metosin/metosin-common/compare/0.1.3...0.1.4)**

## 0.1.3 (12.2.2016)

- Allow custom coercion-matcher with `metosin.ui.routing.schema/schema-query`
- `metosin.dates`
    - Replaced `today` and `now` with `date` and `date-time` zero-arity versions.
    - Support creating dates from good string representation
    - Add `to-string` to create good string representation for a object
    - Add `minus`
    - Add all durations
- New namespace: `metosin.dates.schema`
    - Implements Schema coercion-matchers for well-formatted date strings

**[compare](https://github.com/metosin/metosin-common/compare/0.1.2...0.1.3)**

## 0.1.2 (9.2.2016)

**[compare](https://github.com/metosin/metosin-common/compare/0.1.1...0.1.2)**

- `metosin.jdbc`
    - maps in `insert!` result are converted to use kebab-case

## 0.1.1 (9.2.2016)

**[compare](https://github.com/metosin/metosin-common/compare/0.1.0...0.1.1)**

- `metosin.dates`
    - Fix start/end-of-week with date-times on cljs
- Imported more `java.jdbc` function to `metosin.jdbc`
- Renamed namespace `metosin.postgres.joda.time` to `metosin.jdbc.joda.time`
- Added tests for Joda Time <-> JDBC conversions
- Fixed `LocalDate` <-> `java.sql.Date` conversion

## 0.1.0 (8.2.2016)

- Initial version
