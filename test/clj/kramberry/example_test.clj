(ns kramberry.example-test
  (:require [clojure.test :refer :all]
            [kramberry.shared :refer [magic]]))

(deftest example-passing-test
  (is (= 41 magic)))
