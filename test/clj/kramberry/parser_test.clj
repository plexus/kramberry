(ns kramberry.parser-test
  (:require [clojure.test :refer :all]
            [kramberry.parser :refer [parse]]
            [hiccup.core :as hiccup]
            [hickory.core :as hickory]
            [clojure.java.io :as io]
            [pjstadig.humane-test-output :as humane-output]))

(humane-output/activate!)

(def testcases ["block/03_paragraph/one_para"
                "block/03_paragraph/indented"
                "block/03_paragraph/no_newline_at_end"
                "block/03_paragraph/two_para"])




(defn html->hiccup [h]
  (map hickory/as-hiccup (hickory/parse-fragment h)))

(defn md->hiccup [m]
  (-> m parse hiccup/html html->hiccup))

(defn load-html [c]
  (-> (str "testcases/" c ".html")
      io/resource
      slurp))

(defn load-md [c]
  (-> (str "testcases/" c ".text")
      io/resource
      slurp))

(deftest run-kramdown-tests
  (doseq [c testcases]
    (testing c
      (let [m (load-md c)
            h (load-html c)]
        (is (= (md->hiccup m) (html->hiccup h)))))))

