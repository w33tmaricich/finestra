(ns widgets.core
  (:require [widgets.display.window :as window]
            [widgets.weather :as weather])
  (:gen-class))

(def DEBUG true)

(defn- debug
  "Development and testing code only."
  []
  ;(window/draw window/border))
  ;(window/draw (window/titled-border "Fun")))
  (window/draw (weather/generate "90806")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (if (not DEBUG)
    :production-code
    (debug)))
