(ns metosin.email
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.tools.logging :as log]
            [clostache.parser :as clostache]
            [schema.core :as s]
            [postal.core :as postal]))

(def deliveries (atom []))

;;
;; Internals:
;;

(defn- log-mail! [{:keys [from to subject body]}]
  (log/infof "EMAIL: from=[%s] to=[%s] subject=[%s] body=[%s]" from to subject body))

(defn- log-mail-fail! [{:keys [from to subject body]} resp]
  (log/errorf "EMAIL FAILURE: %s from=[%s] to=[%s] subject=[%s] body=[%s]" (pr-str resp) from to subject body))

(defn- smtp-send! [email-env mail]
  (log-mail! mail)
  (let [resp (postal/send-message (:smtp (:settings email-env)) mail)]
    (if-not (= (:error resp) :SUCCESS)
      (log-mail-fail! mail resp)))
  nil)

(defn- mock-send! [_ mail]
  (log-mail! mail)
  (swap! deliveries conj mail)
  nil)

;;
;; Template to mail
;;

(defn render-template
  "Render mustache email template. The first line is presumed to be the subject."
  [template context]
  (let [url (io/resource template)
        _ (assert url (format "Template %s doesn't exist" template))
        [subject body] (-> url
                           (io/reader :encoding "UTF-8")
                           (slurp)
                           (clostache/render context)
                           (string/split #"\n" 2))]
    {:subject subject
     :body body}))

;;
;; Api
;;

(defn send-mail [email-env mail]
  (let [sender (if (:mock? email-env)
                 mock-send!
                 smtp-send!)]
    (sender email-env (merge {:from (:from (:settings email-env))}
                             mail))))

;;
;; Email component:
;;

(s/defschema Config
  (s/maybe {(s/optional-key :mock?) s/Bool
            :context s/Any
            :settings {:from s/Str
                       (s/optional-key :smtp) {(s/optional-key :ssl) s/Bool
                                               (s/optional-key :tls) s/Bool
                                               (s/optional-key :port) s/Int
                                               :host s/Str
                                               :user s/Str
                                               :pass s/Str}}}))
