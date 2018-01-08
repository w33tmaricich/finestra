(ns widgets.core
  (:require [widgets.display.window :as window]
            [widgets.weather :as weather])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;(window/draw window/border))
  ;(window/draw (window/titled-border "Fun")))
  (window/draw (weather/generate "90806")))
