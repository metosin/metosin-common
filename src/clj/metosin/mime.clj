(ns metosin.mime
  (:import [java.io File InputStream]
           [java.net URL]
           (org.apache.tika Tika)
           (org.apache.tika.mime MimeTypes MimeTypeException)) )

(set! *warn-on-reflection* true)

;; Ignore warnings about JPEG2000 and sqlite dependencies, we
;; don't need them here.
(System/setProperty "tika.config" "metosin-mime-tika.xml")

(def ^Tika detector (Tika.))
(def ^MimeTypes registry (MimeTypes/getDefaultMimeTypes))

;; To prevent reflection, use protocol dispatch to call correct detect
;; method. Could also just use reflection and save few lines...
;; TODO: byte-array, InputStream versions.
(defprotocol IDetect
  (-detect [this]))

(extend-protocol IDetect
  File
  (-detect [file]
    (.detect detector file))
  URL
  (-detect [url]
    (.detect detector url))
  InputStream
  (-detect [is]
    (.detect detector is)))

(defn mime-type-of
  [x]
  (-detect x))

(defn ^String extension-for-name
  [^String s]
  (try
    (or (some-> (.forName registry s)
                (.getExtension))
        "")
    (catch MimeTypeException _
      "")))
