(ns finestra.display.graph
  (:require [finestra.display.window :as window]
            [lanterna.terminal :as t]
            [lanterna.screen :as s]))

(defn fill-color
  "Fills an area with a given color.
  ---
  location is a map of:
  :x x coordinate
  :y y coordinate
  :w width of the figure
  :h height of the figure"
  [write {:keys [x y w h]} color]
  (let [row (window/str-repeat " " w)]
    (dotimes [n h]
      (write x (+ n y) row {:bg color}))))

(defn border-fill-color
  "Fill-color with a border surounding."
  [write write-vertical location color]
  (fill-color write location color)
  (window/border write write-vertical location))

(defn horiz-bar
  "A bar graph horizontally."
  [write write-vertical {:keys [x y w h]} color label]
  (let [label-size (count label)]
    ;label
    (write (- (dec x) label-size) y label)
    ;seperator
    (dotimes [n h]
      (write (dec x) (+ n y) (str " " window/L-VERT " ")))
    ;bar
    ;TODO: fix the coord location of border fill
    (border-fill-color write write-vertical {:x x :y y :w w :h h} :yellow)))
  

(defn generate
  "Creates a function that will be run to render a graph."
  [args]
  (fn [TERM SCREEN write write-vertical]
    ; draw the outline of the window
    ((window/titled-window-border "Graph Example")
     TERM SCREEN write write-vertical)

    ; draw a box!
    (fill-color write {:x 10 :y 5 :w 15 :h 3} :yellow)

    (horiz-bar write write-vertical {:x 21 :y 10 :w 15 :h 3} :red "horiz-bar example")))
