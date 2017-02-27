(ns metosin.hetu
  "Validator for Finnish personal identifaction codes (henkilötunnus/hetu).

  > (require '[metosin.hetu :as hetu])
  > (hetu/valid? \"010170-0205\")
  true"
  (:require [metosin.dates :as dates]))

(defn- valid-date? [d m y]
  (try
    (let [date (dates/date y m d)]
      (and (= d (dates/day date)) (= m (dates/month date)) (= y (dates/year date))))
    (catch #?(:clj Exception :cljs js/Error) _
        false)))

(defn- parse-int [x] #?(:cljs (js/parseInt x) :clj (Integer/valueOf x)))

(let [->cent-number {"+" 1800, "-" 1900, "A" 2000}
      checkchars "0123456789ABCDEFHJKLMNPRSTUVWXY"
      checkchars-map (zipmap (range) (map str checkchars))
      hetu-pattern (re-pattern (str "^((\\d{2})(\\d{2})(\\d{2}))([\\-\\+A])(\\d{3})([" checkchars "])$"))]

  (defn- get-year [year-in-cent cent]
    (let [cent-number (->cent-number cent)]
      (+ cent-number (parse-int year-in-cent))))

  (defn valid?
    "Check that s is a valid hetu string."
    [s]
    (boolean
      (when-let [parts (and s (re-matches hetu-pattern s))]
        (let [[_ date day month year-in-cent cent identifier checkchar] parts
              year (get-year year-in-cent cent)
              check-number (parse-int (str date identifier))
              check-reminder (rem check-number 31)]
          (and
            (valid-date? (parse-int day) (parse-int month) year)
            (= (checkchars-map check-reminder) checkchar)))))))
