(ns widgets.display.window
  (:require [lanterna.terminal :as t]
            [lanterna.screen :as s]))


; Unicode lines for drawing.
;  L- : line
;  T  : top
;  B  : bottom
;  L  : left
;  R  : right
(def L-HORIZ "\u2500")
(def L-VERT "\u2502")
(def L-TL "\u250c")
(def L-TR "\u2510")
(def L-BL "\u2514")
(def L-BR "\u2518")

; Size of the terminal
(def TERM-SIZE (ref [0 0]))

; Screen refresh rate
(def REFRESH-INTERVAL (* 10 60 1000)) ; minutes, seconds, miliseconds

; Helper functions.
(defn str-repeat
  "Repeats a character length number of times to create a
   string."
  [string length]
  (apply str (repeat length string)))

(defn terminal-connect
  "Returns a symbol that acts as an interaction point with
   the terminal."
  []
  (t/get-terminal :text))

;(defn refresh
  ;"Refreshes screen to display buffer."
  ;[TERM SCREEN]
  ;(t/clear TERM)
  ;(s/redraw SCREEN))

(defn screen-resize
  "This function is called when the screen changes size."
  [cols rows]
  (dosync (ref-set TERM-SIZE [cols rows]))

  (println "Screen resize occurred."))

(defn screen-connect
  "Returns a symbol that acts as a terminal buffer."
  []
  (s/get-screen :text {:resize-listener screen-resize}))


(defn generate-write
  "Creates the write function based on a given screen. The
   write function will print text onto the screen buffer."
  [screen]
  (partial s/put-string screen))

(defn generate-write-vertical
  "Creates the write vertical function. Same as write, but
   draws characters in a vertical line."
  [fn-write]
  (fn
    [x y text index]
    (if (empty? text)
      :done
      (let [f-char (first text)
            rest-string (rest text)]
        (fn-write x
                  (+ y index)
                  (str f-char))
        (recur x y rest-string (inc index))))))

(defn draw
  "Draws an item."
  [fn-draw]
  (let [TERM (terminal-connect)
        SCREEN (screen-connect)
        write (generate-write SCREEN)
        write-vertical (generate-write-vertical write)]
    (t/start TERM)
    (s/start SCREEN)
    (loop []
      (t/clear TERM)
      (s/clear SCREEN)
      (s/redraw SCREEN)
      (s/move-cursor SCREEN 0 0)
      (fn-draw TERM SCREEN write write-vertical)
      (s/redraw SCREEN)
      (case (t/get-key-blocking TERM {:timeout REFRESH-INTERVAL})
        \r (do (println :refreshing) (recur))
        nil (recur)
        :exit))))

(defn border
  "Creates a border around the entire window."
  [TERM SCREEN write write-vertical]
  (let [x 0
        y 0
        w (dec (first (deref TERM-SIZE)))
        h (dec (second (deref TERM-SIZE)))
        horizontal-border (str-repeat L-HORIZ (- w 1))
        vertical-border (str-repeat L-VERT (- w 1))]
    (write (inc x) y horizontal-border)
    (write (inc x) h horizontal-border)
    (write-vertical x (inc y) vertical-border 0)
    (write-vertical w (inc y) vertical-border 0)
    (write x y L-TL)
    (write x h L-BL)
    (write w y L-TR)
    (write w h L-BR)))

(defn titled-border
  "Creates a border around the window and inserts a title."
  [title]
  (fn [TERM SCREEN write write-vertical]
    (border TERM SCREEN write write-vertical)
    (let [x 0
          y 0]
      (write (+ 2 x) y title))))
