(ns finestra.weather-test
  (:require [clojure.test :refer :all]
            [finestra.weather :refer :all]))

(deftest temperature-conversions
  (testing "Temperature conversions."
    (is (=
         (k->f 400)
         260))
    (is (=
         (k->c 0)
         -273))))
