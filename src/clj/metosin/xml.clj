(ns metosin.xml
  "Simple XML parsing utils.

  Ensures safe clojure.xml features.

  Some older versions in some projects include functions to emit XML from
  Clojure data, but those are quite project specific so they won't be
  included here.

  (C) Metosin 2014-2019"
  (:require [clojure.xml :as xml])
  (:import [java.io ByteArrayInputStream]
           [javax.xml.parsers SAXParserFactory]
           [javax.xml XMLConstants]))

(defn startparse-sax-safe
  "Disallow dangerous parser features"
  [s ch]
  (..
    (doto (SAXParserFactory/newInstance)
      (.setFeature XMLConstants/FEATURE_SECURE_PROCESSING true)
      (.setFeature "http://apache.org/xml/features/disallow-doctype-decl" true))
    (newSAXParser)
    (parse s ch)))

;; TODO: InputStream version?

(defn parse-string
  "Reads string of XML to clojure.xml XML representation."
  ([s]
   (parse-string s nil))
  ([s {:keys [encoding] :or {encoding "UTF-8"}}]
   (with-open [is (ByteArrayInputStream. (.getBytes s encoding))]
     (-> (xml/parse is startparse-sax-safe)
         ;; TODO: Why?
         (update :attrs dissoc :xmlns:xsi :xmlns:xsd)
         (update :attrs (fn [attrs] (and (seq attrs) attrs)))))))

;;
;; Clojure.xml representation to "Simple Clojure data"
;;

(defn- attr-name [k]
  (keyword (str "#" (name k))))

(defn- decorate-attrs [m]
  (zipmap (map attr-name (keys m)) (vals m)))

(defn- vectorize [x y]
  ((if (vector? x) conj vector) x y))

(defn- childs? [v]
  (map? (first v)))

(defn- lift-text-nodes [m]
  (if (= (keys m) [:##text])
    (val (first m))
    m))

(defn xml->clj
  "Turns clojure.xml XML representation to *simple* Clojure structure.

  Provide `create-map` option to use alternative map implementation,
  like linked map, if you need to keep the order of properties."
  ([xml]
   (xml->clj xml nil))
  ([xml {:keys [create-map]
         :or {create-map hash-map}}]
   (letfn [(parts [{:keys [attrs content]}]
             (merge (create-map)
                    (decorate-attrs attrs)
                    (if (childs? content)
                      (reduce (partial merge-with vectorize) (map xml->clj content))
                      (hash-map :##text (first content)))))]
     (create-map (:tag xml) (-> xml parts lift-text-nodes)))))
