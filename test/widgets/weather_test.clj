(ns widgets.weather-test
  (:require [clojure.test :refer :all]
            [widgets.weather :refer :all]))

(deftest temperature-conversions
  (testing "Temperature conversions."
    (is (=
         (k->f 400)
         260))
    (is (=
         (k->c 0)
         -273))))
