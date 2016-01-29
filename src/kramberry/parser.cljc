(ns kramberry.parser
  (:require [instaparse.core :as insta]))

(def parser
  (insta/parser
    "
<S>               = EOBMarker 
                    | <Whitespace>? Newlines1 
                    | (Newlines1 Block BlockTerminator | Block BlockTerminator)*

<Block>           = h1 / h2 / h3 / h4 / h5 / h6 / pre / blockquote / p

p                 = (Line | Line (Newline WLine)+) &BlockTerminator
pre               = code
code              = (CodeLine Newline)+ &BlockTerminator
h1                = (<'# '> WLine | WLine <Newline> <'===='> <'='>*) &BlockTerminator
h2                = (<'## '> WLine | WLine <Newline> <'----'> <'-'>*) &BlockTerminator
h3                = <'### '> WLine &BlockTerminator
h4                = <'#### '> WLine &BlockTerminator
h5                = <'##### '> WLine &BlockTerminator
h6                = <'###### '> WLine &BlockTerminator

(* All block level elements can be nested inside blockquotes, 
   so we need BQ variants of nearly everything *)
blockquote        = BQ_Block (BQ_BlockTerminator BQ_Block)*

<BQ_Block>        = BQ_p

BQ_p              = (BQ_Line | BQ_Line (Newline BQ_WLine)+) &BlockTerminator
<BQ_Line>         = <BQ_Marker> <Whitespace>? Word (Whitespace Word)*
<BQ_WLine>        = (<BQ_Marker> / Epsilon) Whitespace? Word (Whitespace Word)*

<BQ_Marker>       = <#' {1,3}'>? (<'> '> / <'>'>)

<Line>            = <Whitespace>? Word (Whitespace Word)*
<WLine>           = Whitespace? Word (Whitespace Word)*


<CodeLine>        = <FourSpaces> Whitespace? Word (Whitespace Word)*

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
<BQ_BlockTerminator> = Newline BQ_Marker Newline | Newline? BQ_Marker? EndOfInput | BQ_Marker EOBMarker

<EndOfInput>      = #'\\Z'
"))

(declare postprocess)

(defmulti postprocess* first)

(defmethod postprocess* :default 
  [x] (mapv postprocess x))

(defmethod postprocess* :BQ_p 
  [[_ & xs]] (apply vector :p (map postprocess xs)))

(defmethod postprocess* :blockquote 
  [[_ & xs]] (conj (apply vector :blockquote "\n  " (map postprocess xs)) "\n"))

(defn postprocess [x]
  (if (vector? x) 
    (postprocess* x) 
    x))

(def parse (comp #(map postprocess %) parser))
