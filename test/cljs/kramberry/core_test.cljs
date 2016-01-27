(ns kramberry.core-test
  (:require-macros [cljs.test :refer (is deftest testing)])
  (:require [cljs.test]
            [kramberry.shared :refer [magic]]))

(deftest example-passing-test
  (is (= 42 magic)))
