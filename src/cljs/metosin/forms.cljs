(ns metosin.forms
  (:require [lomakkeet.reagent :as f]
            [reagent.core :as r]
            lomakkeet.core
            [common.loc :refer [loc]]
            [schema.coerce :as sc]))

(defn explain-error [v]
  (if-let [v (f/validation-error->str v)]
    (if (set? v)
      (loc [:errors :enum-error])
      (loc [:errors v]))))

(defn create-form
  ([data] (create-form data nil))
  ([data opts]
   (f/create-form data
                  (assoc (update opts :opts assoc
                                 :explain-error explain-error
                                 :datepicker-i18n (clj->js (loc [:pikaday-i18n])))
                         :coercion-matcher sc/string-coercion-matcher))))

(defn form-status [{:keys [errors value initial-value]}]
  [:span.form-status
   (cond
     errors  (loc :forms.errors)
     (not= value initial-value) (loc :forms.unsaved))
   [:br.visible-xs-inline]])

(defn errors' [form]
  (:errors @(:data form)))

(defn form-error [form]
  (if-let [v (explain-error @(r/track errors' form))]
    [:div.alert.alert-danger v]))

(defn dirty?' [form]
  (f/dirty? @(:data form)))

(defn errors?' [form]
  (f/errors? @(:data form)))

(defn saveable' [form]
  (and @(r/track dirty?' form)
       (not @(r/track errors?' form))))

(defn cancel-btn [form]
  [:button.btn.btn-warning
   {:type "button"
    :disabled (not @(r/track dirty?' form))
    :on-click #(swap! (:data form) (comp f/validate f/reset))}
   (loc :forms.cancel)])

(defn delete-btn [& {:as opts}]
  [:button.btn.btn-danger
   (merge {:type "button"}
          opts)
   [:i.fa.fa-trash-o] " "
   (loc :forms.delete)])

(defn save-btn [form & {:as opts}]
  [:button.btn.btn-success
   (merge {:type "button"
           :disabled (not @(r/track saveable' form))}
          opts)
   [:i.fa.floppy-o] " "
   (or (:text opts) (loc :forms.save))])

(defn submit-btn [form & {:as opts}]
  [:button.btn.btn-success
   (merge {:type "submit"
           :disabled (not @(r/track saveable' form))}
          opts)
   [:i.fa.floppy-o] " "
   (loc :forms.save)])
