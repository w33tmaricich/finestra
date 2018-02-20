(ns widgets.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.tools.logging :as log]
            [widgets.display.window :as window]
            [widgets.weather :as weather])
  (:gen-class))

(def DEBUG false)
(def VALID-WIDGETS [:weather])
(def WEATHER-ZIP "90806")

(defn- debug
  "Development and testing code only."
  []
  ;(window/draw window/border))
  ;(window/draw (window/titled-border "Fun")))
  (window/draw (weather/generate WEATHER-ZIP)))

(defn validate-widget
  "Returns true if the widget type is valid"
  [widget-type]
  (not (nil? (some #(= % widget-type) VALID-WIDGETS))))

(def cli-options
  [["-z" "--zip-code ZIPCODE" "Your current zip code."
    :default WEATHER-ZIP
    :parse-fn str
    :validate [#(< 10000 (Integer/parseInt %) 99999) "Must be a valid US zipcode."]]
   ["-w" "--widget TYPE" "Widget you wish to launch"
    :default :weather
    :parse-fn keyword
    :validate [validate-widget "The inserted widget type does not exist."]]
   ["-h" "--help"]])
  

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [parsed-args (parse-opts args cli-options)
        widget-type (-> parsed-args :options :widget)
        zip-code (-> parsed-args :options :zip-code)
        errors (:errors parsed-args)]
    (when errors
      (log/error errors)
      (System/exit 1))
    (window/draw (case widget-type
                   :weather (weather/generate zip-code)
                   (weather/generate zip-code)))))
