(ns widgets.weather
  (:require [lanterna.terminal :as t]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [lanterna.screen :as s]
            [widgets.display.window :as window]))

(defn k->f
  "Converts a temperature from kelvin to fahrenheight"
  [k]
  (int (- (* k (/ 9 5)) 459.67)))

(defn k->c
  "Converts a temperature from kelvin to celsius"
  [k]
  (int (- k 273.15)))

(def DEGREES "\u00b0")

(def CONFIG {:border-title "Weather"
             :units {:symbol "f"
                     :fn k->f}})

(def units-symbol (comp :symbol :units))

(defn get-weather
  "Gets the weather from online using your zip code."
  [zip]
  (let [api-key (slurp "openweathermap.apikey")
        response (client/get (str "http://api.openweathermap.org/data/2.5/weather?zip="
                                  zip
                                  "&appid="
                                  api-key))]
    (if (= 200 (:status response))
      (let [data (:body response)] ;string
        (json/read-str data :key-fn keyword))
        ;data)
      nil)))

(defn ascii-cloudy
  "Draws an ascii cloud."
  [write location]
  (let [x (:x location)
        y (:y location)]
    (write (+ 3 x) (inc y)     "___"       )
    (write (+ 2 x) (+ 2 y)    "(   )"      )
    (write x (+ 3 y)        "(___)__)"     )))

(defn ascii-misty
  "Draws ascii mist."
  [write location]
  (let [x (:x location)
        y (:y location)]
    (write (+ 2 x) (inc y)  "--")
    (write (+ 1 x) (+ 2 y) "-------___")
    (write (+ 3 x) (+ 3 y)   "____---")))

(defn ascii-stormy
  "A stormy cloud!"
  [write location]
  (let [x (:x location)
        y (:y location)]
    (ascii-cloudy write {:x x :y (dec y)})
    (write (inc x) (+ 3 y)   "` `_/`"   )
    (write (+ 2 x) (+ 4 y)          "`/ ` `"  )))

(defn ascii-sunny
  "Draws an ascii art sun!"
  [write location]
  (let [x (:x location)
        y (:y location)]
    (write (+ 4 x) y            "|"      )
    (write (+ 2 x) (+ 1 y)   "\\ _ /"    )
    (write x (+ 2 y)        "-= (_) =-"  )
    (write (+ 2 x) (+ 3 y)    "/   \\"   )
    (write (+ 4 x) (+ 4 y)      "|"      )))

(defn ascii-unknown
  "Draws an ascii art sun!"
  [write location]
  (let [x (:x location)
        y (:y location)]
    (write (+ 3 x) y         "____ "  )
    (write (+ 2 x) (+ 1 y)  "|    |"  )
    (write (+ 6 x) (+ 2 y)      "/"   )
    (write (+ 5 x) (+ 3 y)     " "    )
    (write (+ 5 x) (+ 4 y)     "o"    )))


(defn appropriate-image
  "chooses the icon drawing function appropriate to the current weather"
  [condition]
  (case condition
    :clear ascii-sunny
    :clouds ascii-cloudy
    :fog ascii-misty
    :haze ascii-misty
    :mist ascii-misty
    :rain ascii-stormy
    ascii-unknown))


(defn generate
  "Creates the function that draws the weather for your specified location."
  [zip-code]
  (fn [TERM SCREEN write write-vertical]
    (let [x 0
          y 0
          w (dec (first (deref window/TERM-SIZE)))
          h (dec (second (deref window/TERM-SIZE)))
          w-left-edge 2
          h-top-left 2
          h-bottom-left (- h 2)
          image-x (- (/ w 2) 4)
          image-y (- (/ h 2) 3)
          image-location {:x image-x :y image-y}
          forecast (get-weather zip-code)
          location (:name forecast)
          condition (-> forecast :weather first :main clojure.string/lower-case keyword)
          temp (str :current " " (k->f (-> forecast :main :temp)) DEGREES "f")
          temp-min (str :low " " (k->f (-> forecast :main :temp_min)) DEGREES "f")
          temp-max (str :high " " (k->f (-> forecast :main :temp_max)) DEGREES "f")
          ]
      ;generate the border
      ((window/titled-border (str (:border-title CONFIG) "-" location))
       TERM SCREEN write write-vertical)
      ;draw widget
      ((appropriate-image condition) write {:x (- (/ w 2) 4)
                                            :y (- (/ h 2) 3)})
      (write w-left-edge h-top-left (str condition))
      (write w-left-edge (- h-bottom-left 2) (str temp))
      (write w-left-edge (dec h-bottom-left) (str temp-min))
      (write w-left-edge h-bottom-left (str temp-max)))))
