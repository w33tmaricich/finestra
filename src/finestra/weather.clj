(ns finestra.weather
  (:require [lanterna.terminal :as t]
            [clojure.tools.logging :as log]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [lanterna.screen :as s]
            [finestra.display.window :as window]))

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
  [args]
  (let [api-key-path (-> args :options :openweathermap-apikey)
        zip (-> args :options :zip-code)
        api-key (try
                  (slurp api-key-path)
                  (catch Exception e (.getMessage e)))
        response (client/get (str "http://api.openweathermap.org/data/2.5/weather?zip="
                                  zip
                                  "&appid="
                                  api-key))]
    (if (= 200 (:status response))
      (let [data (:body response)] ;string
        (json/read-str data :key-fn keyword)))))

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
    (write (+ 2 x) (+ 4 y)    "`/ ` `"  )))

(defn ascii-snowy
  "A stormy cloud!"
  [write location]
  (let [x (:x location)
        y (:y location)]
    (ascii-cloudy write {:x x :y (dec y)})
    (write (inc x) (+ 3 y)   "* * *"   )
    (write (+ 2 x) (+ 4 y)    "* * *"  )))
 
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
    :snow ascii-snowy
    ascii-unknown))

(defn generate
  "Creates the function that draws the weather for your specified location."
  [args]
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
          forecast (get-weather args)
          location (:name forecast)
          condition (-> forecast :weather first :main clojure.string/lower-case keyword)
          units (-> args :options :units)
          temp-fn (if (= units :c)
                    k->c
                    k->f)
          temp (str :current " " (temp-fn (-> forecast :main :temp)) DEGREES (name units))
          temp-min (str :low " " (temp-fn (-> forecast :main :temp_min)) DEGREES (name units))
          temp-max (str :high " " (temp-fn (-> forecast :main :temp_max)) DEGREES (name units))
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
