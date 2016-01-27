(ns kramberry.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [kramberry.core-test]))

(enable-console-print!)

(doo-tests 'kramberry.core-test)
