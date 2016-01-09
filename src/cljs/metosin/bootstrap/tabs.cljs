(ns metosin.bootstrap.tabs
  (:require [goog.dom :as dom]
            [reagent.core :as reagent]
            [reagent.ratom :refer [atom]]
            [metosin.ui.core :refer [prevent-default]]))

(enable-console-print!)

(defn tabs
  "- :active     (Optional) IReactiveAtom used for storing current tab
   - :on-click   (Optional) function to be called with key of tab when new tab is selected
   - :content    Collection of key value pairs or a map; value is the label"
  [& {:keys [active on-click content]}]
  (let [active (or active (atom nil))]
    (fn [& {:keys [content]}]
      (let [a @active]
        [:ul.nav.nav-tabs
         (for [[k label] content
               :when k]
           [:li {:key k
                 :class (if (= k a) "active")}
            [:a {:href "#" ; Href is useful styling
                 :on-click (fn [e]
                             (prevent-default e)
                             (reset! active k)
                             (when on-click (on-click k)))}
             label]])]))))
