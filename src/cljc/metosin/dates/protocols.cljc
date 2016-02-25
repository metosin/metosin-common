(ns metosin.dates.protocols)

(defprotocol IDateLike
  "Protocol for Date like objects like DateTimes and LocalDates."
  (-add [this interval])
  (-minus [this interval])
  (-with-zone [this timezone-id])
  (-format [this options]))

(defprotocol ToDateTime
  (-to-date-time [x] "Convers Date or such to DateTime."))

(defprotocol ToDate
  (-to-date [x] "Convers DateTime or such to Date."))

(defprotocol ToNative
  (-to-native [x] "Convers to native Date object (java.util.Date or js/Date)."))

(defprotocol ToString
  (-to-string [x] "Converts object to good date string representation"))
