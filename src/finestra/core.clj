(ns finestra.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.tools.logging :as log]
            [finestra.display.window :as window]
            [finestra.display.graph :as graph]
            [finestra.weather :as weather])
  (:gen-class))

(def DEBUG false)
(def WEATHER-ZIP "21061")
(def VALID-WIDGETS [:weather :graph])

(defn- validate-widget
  "Returns true if the widget type is valid"
  [widget-type]
  (not (nil? (some #(= % widget-type) VALID-WIDGETS))))

(def cli-options
  [
   ["-h" "--help"]
   ["-r" "--refresh-rate SECONDS" "The length of time in SECONDS the widget will wait before automatically refreshing"
    :default (* 10 60 1000)
    :parse-fn #(* (Integer/parseInt %) 1000)
    :validate [#(number? %) "The refresh rate specified should be an integer."]]

   ["-o" "--openweathermap-apikey PATH" "Path to the file that contains an openweathermap api key for retrieving weather."
    :default "./openweathermap.apikey"
    :parse-fn str]

   ["-u" "--units TYPE" "Celsius or Fahrenheit. c||f"
    :default :f
    :parse-fn keyword
    :validate [#(or (= % :c) (= % :f)) "Valid units are 'f' or 'c'."]]

   ["-w" "--widget TYPE" "Widget you wish to launch"
    :default :weather
    :parse-fn keyword
    :validate [validate-widget "The specified widget type is not supported."]]

   ["-z" "--zip-code ZIPCODE" "Your current zip code."
    :default "21061"
    :parse-fn str
    :validate [#(< 10000 (Integer/parseInt %) 99999) "Must be a valid US zipcode."]]

   ])

;(defn- debug
  ;"Development and testing code only."
  ;[]
  ;(window/draw window/border))
  ;(window/draw (window/titled-border "Fun")))
  ;(window/draw (weather/generate WEATHER-ZIP)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [parsed-args (parse-opts args cli-options)
        widget-type (-> parsed-args :options :widget)
        zip-code (-> parsed-args :options :zip-code)
        errors (:errors parsed-args)]
    (when errors
      (dotimes [n (count errors)]
        (log/error (errors n)))
      (System/exit 1))
    ;(println parsed-args)))
    (window/draw (case widget-type
                   :weather (weather/generate parsed-args)
                   :graph (graph/generate parsed-args)
                   (weather/generate parsed-args))
                 parsed-args)))
