(ns kramberry.parser-test
  (:require [clojure.test :refer :all]
            [kramberry.parser :refer [parse]]
            [hiccup.core :as hiccup]
            [hickory.core :as hickory]
            [clojure.java.io :as io]
            [pjstadig.humane-test-output :as humane-output]))

(humane-output/activate!)

(def kramdown-testcases [
                         "block/01_blank_line/spaces"
                         "block/01_blank_line/tabs"

                         "block/02_eob/beginning"
                         "block/02_eob/end"
                         "block/02_eob/middle"

                         "block/03_paragraph/indented"
                         "block/03_paragraph/no_newline_at_end"
                         "block/03_paragraph/one_para"
                         "block/03_paragraph/two_para"

                         ;;;; These all use options or attribute lists

                         ;; "block/04_header/setext_header"
                         ;; "block/04_header/with_auto_id_stripping"
                         ;; "block/04_header/with_auto_id_prefix"
                         ;; "block/04_header/with_auto_ids"
                         ;; "block/04_header/setext_header_no_newline_at_end"
                         ;; "block/04_header/header_type_offset"
                         ;; "block/04_header/atx_header"
                         ;; "block/04_header/atx_header_no_newline_at_end"

                         "block/05_blockquote/indented"
                         "block/05_blockquote/nested"
                         "block/05_blockquote/no_newline_at_end"
                         "block/05_blockquote/very_long_line"
                         "block/05_blockquote/with_code_blocks"

                         ;; "block/05_blockquote/lazy"

                         "block/06_codeblock/with_blank_line"
                         "block/06_codeblock/error"
                         "block/06_codeblock/rouge/disabled"
                         "block/06_codeblock/with_eob_marker"

                         ;; "block/06_codeblock/highlighting"
                         ;; "block/06_codeblock/highlighting-minted-with-opts"

                         "block/06_codeblock/tilde_syntax"
                         ;; "block/06_codeblock/with_lang_in_fenced_block"
                         ;; "block/06_codeblock/disable-highlighting"
                         ;; "block/06_codeblock/rouge/simple"
                         ;; "block/06_codeblock/highlighting-opts"
                         ;; "block/06_codeblock/with_lang_in_fenced_block_name_with_dash"
                         ;; "block/06_codeblock/normal"
                         ;; "block/06_codeblock/lazy"
                         ;; "block/06_codeblock/highlighting-minted"
                         ;; "block/06_codeblock/with_ial"
                         ;; "block/06_codeblock/no_newline_at_end"
                         ;; "block/06_codeblock/whitespace"
                         ;; "block/06_codeblock/no_newline_at_end_1"
                         ])



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

  (count kramdown-testcases)

(parse (load-md (str "testcases/" (last extra-testcases))))





  )

