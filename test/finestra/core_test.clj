(ns finestra.core-test
  (:require [clojure.test :refer :all]
            [finestra.core :refer :all]))

(deftest constants
  (testing "Constants are set to expected values."
    ;TODO: Debug should be false on release.
    (is (not= DEBUG false))))
