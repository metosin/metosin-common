(ns metosin.string.linky)

(def linky-re (js/RegExp. "((ftp|https?)://|(www\\.)|(mailto:)?[A-Za-z0-9._%+-]+@)\\S*[^\\s.;,(){}<>\"\u201d\u2019]" "i"))

(defn linky [text]
  (loop [r [:span]
         text text]
    (if-let [m (.match text linky-re)]
      (let [url (aget m 0)
            i   (.-index m)]
        (recur (conj r (.substring text 0 i)
                     [:a {:href url :target "new"} url])
               (.substring text (+ i (count url)))))
      (conj r text))))
