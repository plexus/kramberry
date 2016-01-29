(ns kramberry.parser-test
  (:require [clojure.test :refer :all]
            [kramberry.parser :refer [parse]]
            [hiccup.core :as hiccup]
            [hickory.core :as hickory]
            [clojure.java.io :as io]
            [pjstadig.humane-test-output :as humane-output]))

(humane-output/activate!)

(def kramdown-testcases ["block/01_blank_line/spaces"
                         "block/01_blank_line/tabs"
                         "block/02_eob/beginning"
                         "block/02_eob/middle"
                         "block/02_eob/end"
                         "block/03_paragraph/one_para"
                         "block/03_paragraph/indented"
                         "block/03_paragraph/no_newline_at_end"
                         "block/03_paragraph/two_para"])

(def extra-testcases ["basic_headers"
                      "basic_paragraphs"
                      "blockquotes"])

(defn html->hiccup [h]
  (map hickory/as-hiccup (hickory/parse-fragment h)))

(defn md->hiccup [m]
  (-> m parse hiccup/html html->hiccup))

(defn load-html [c]
  (-> (str c ".html")
      io/resource
      slurp))

(defn load-md [c]
  (-> (str c ".text")
      io/resource
      slurp))

(defn run-test-cases [names]
  (doseq [c names]
    (testing c
      (let [m (load-md c)
            h (load-html c)]
        (is (= (md->hiccup m) (html->hiccup h)))))))

(deftest run-kramdown-tests
  (run-test-cases 
   (map #(str "kramdown_test_suite/" %) 
        kramdown-testcases)))

(deftest run-extra-tests
  (run-test-cases 
   (map #(str "testcases/" %) 
        extra-testcases)))


(comment
  (run-kramdown-tests)
  (run-extra-tests)
  (parse (load-md (second testcases)))

  (count testcases)

(parse (load-md (str "testcases/" (last extra-testcases))))
([:p "this" " " "is" " " "a" " " "paragraph"]
 "\n" "\n"
 [:blockquote "\n  " [:p "this" " " "is" " " "a" " " "blockquote"] "\n"]
 "\n" "\n"
 [:blockquote "\n  " [:p "this" " " "is" " " "a" " " "blockquote" "\n" "as" " " "well"] "\n"]
 "\n" "\n" [:blockquote "\n  " [:p "and" " " "so" " " "is" "\n" ">" " " "this" " " "one"] "\n"] "\n" "")


([:p "this" " " "is" " " "a" " " "paragraph"]
 "\n" 
 "\n"
 [:blockquote 
  "\n  "
  [:p "this" " " "is" " " "a" " " "blockquote"]
  "\n" "\n"
  [:p "this" " " "is" " " "a" " " "blockquote" "\n" "as" " " "well"]
  "\n" "\n" 
  [:p "and" " " "so" " " "is" "\n" ">" " " "this" " " "one"] "" "\n"] "\n" "")

  )

