(ns kramberry.parser
  (:require [instaparse.core :as insta]
            [clojure.string :as s]))



(def grammar
  "
<S>               = EOBMarker 
                    | <#'\\A'> <Whitespace>? Newlines1
                    | (Block BlockTerminator / Newlines1 Block BlockTerminator)*

<Block>           = h1 / h2 / h3 / h4 / h5 / h6 / pre / blockquote / p

p                 = (Line | Line (Newline WLine)+) &BlockTerminator
pre               = code
code              = CodeLine (Newline Newline CodeLine / Newline CodeLine)* &BlockTerminator / {fenced-code-blocks}
                    
h1                = (<'# '> WLine | WLine <Newline> <'===='> <'='>*) &BlockTerminator
h2                = (<'## '> WLine | WLine <Newline> <'----'> <'-'>*) &BlockTerminator
h3                = <'### '> WLine &BlockTerminator
h4                = <'#### '> WLine &BlockTerminator
h5                = <'##### '> WLine &BlockTerminator
h6                = <'###### '> WLine &BlockTerminator

(* The inside of a blockquote is parsed recursively in postprocessing *)
blockquote        = <BQ_Marker> WLine (Newline (<BQ_Marker> / Epsilon) WLine?)* &BlockTerminator

<BQ_Marker>       = <#' {1,3}'>? (<'> '> / <'>'>)

<Line>            = <Whitespace>? Word (Whitespace Word)*
<WLine>           = Whitespace? Word (Whitespace Word)*

<CodeLine>        = <FourSpaces> WLine

<Whitespace>      = #'(\\ |\\t)+'
<FourSpaces>      = '    '
<Word>            = #'\\S+'

(* Exactly one \\n *)
<Newline>         = '\\n'

(* One or more blank lines, optionally with whitespace. Outputs \\n *)
<Newlines1>       = Newline (<Whitespace>? <Newline>)*

(* Two or more newlines, optionally with whitespace. Outputs \\n\\n *)
<Newlines2>       = Newline <Whitespace>? Newline (<Whitespace>? <Newline>)*

<EOBMarker>       = <Newline>* <#'^\\^'> Newline <Newline>*

<BlockTerminator> = Newlines2 | Newline? EndOfInput | EOBMarker

<EndOfInput>      = #'\\Z'
")

                    
;; e.g. <'~~~~'> Newline (Whitespace? (Whitespace Word)* Newline !<'~~~~'>)+ <'~~~~'> <'~'>* &BlockTerminator
(defn fenced-code-rule [fence-type count]
  (let [fence (apply str (map (constantly fence-type) (range count)))]
    (str "<'" fence "'> <Newline> ((Whitespace? !<'" fence "'> Word)* Newline?)+ <Newline> <'" fence "'> <'" fence-type "'>* &BlockTerminator")))

(defn fenced-code-blocks []
  (let [counts (reverse (range 3 30))
        rules (concat (map (partial fenced-code-rule "~") counts)
                      (map (partial fenced-code-rule "`") counts))]
    (s/join " / " rules)))

(defn inject-generated-rules [grammar]
  (s/replace grammar "{fenced-code-blocks}" (fenced-code-blocks)))

(defn indent [h]
  (cond 
    (seq? h)    (map indent h)
    (vector? h) (if (= :pre (first h))
                  h
                  (mapv indent h))
    (string? h) (s/replace h #"\n" "\n  ")
    :default    h))

(declare postprocess)
(declare parse)

(defmulti postprocess* first)

(defmethod postprocess* :default 
  [x] (mapv postprocess x))

(defmethod postprocess* :BQ_p 
  [[_ & xs]] 
  `[:p ~@(map postprocess xs) ])

(defmethod postprocess* :code
  [[_ & xs]] 
  `[:code ~@xs "\n"])

(defmethod postprocess* :blockquote 
  [[_ & xs]] 
  `[:blockquote "\n  " ~@(indent (parse (apply str xs))) "\n"])

(defn postprocess [x]
  (if (vector? x) 
    (postprocess* x) 
    x))

(def parse (comp #(map postprocess %) (insta/parser (inject-generated-rules grammar))))
