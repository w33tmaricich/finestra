(ns finestra.display.graph
  (:require [finestra.display.window :as window]
            [lanterna.terminal :as t]
            [lanterna.screen :as s]))

(defn generate
  "Creates a function that will be run to render a graph."
  [args]
  (fn [TERM SCREEN write write-vertical]
    ((window/titled-border "Graph Example")
     TERM SCREEN write write-vertical)))
