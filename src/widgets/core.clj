(ns widgets.core
  (:require [widgets.display.window :as window])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (window/draw))
