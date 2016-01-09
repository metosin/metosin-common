(ns metosin.font-awesome.content-type)

(def icons
  {:default [:i.fa.fa-file-o]
   "image" [:i.fa.fa-file-image-o]
   "text"  [:i.fa.fa-file-text-o]
   "audio" [:i.fa.fa-audio-o]
   "video" [:i.fa.fa-video-o]
   "application" {"zip" [:i.fa.fa-file-archive-o]
                  "pdf" [:i.fa.fa-file-pdf-o]
                  "vnd.openxmlformats-officedocument.spreadsheetml.sheet" [:i.fa.fa-file-excel-o]
                  "vnd.openxmlformats-officedocument.wordprocessingml.document" [:i.fa.fa-file-word-o]
                  "vnd.openxmlformats-officedocument.presentationml.presentation" [:i.fa.fa-file-powerpoint-o]}})

(defn icon [content-type]
  (if-let [[_ a b _] (re-matches #"(.*)/(.*)+(.*)" content-type)]
    (let [icon-or-sub (or (get icons a) (:default icons))]
      (if (map? icon-or-sub)
        (or (get icon-or-sub b) (:default icons))
        icon-or-sub))))
