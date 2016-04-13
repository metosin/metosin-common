## Unreleased

- **BREAKING**: Update java.jdbc to new version and removed all deprecated versions arities from metosin.jdbc namespace

## 0.1.4 (13.4.2016)

- Disable warn of reflection

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

## 0.1.2 (9.2.2016)

**[compare](https://github.com/metosin/metosin-common/compare/0.1.1...master)**

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
