(ns kramberry.parser
  (:require [instaparse.core :as insta]))

(def parse
  (insta/parser
    "
<S>               = <Whitespace>? Newlines1 | (Newlines1 Block BlockTerminator | Block BlockTerminator)*

<Block>           = h1 / h2 / h3 / h4 / h5 / h6 / pre / p

p                 = Line (WLine Newline)* WLine &BlockTerminator
pre               = code
code              = (CodeLine Newline)+ &BlockTerminator

h1                = (<'# '> WLine | WLine <Newline> <'===='> <'='>*) &BlockTerminator
h2                = (<'## '> WLine | WLine <Newline> <'----'> <'-'>*) &BlockTerminator
h3                = <'### '> WLine &BlockTerminator
h4                = <'#### '> WLine &BlockTerminator
h5                = <'##### '> WLine &BlockTerminator
h6                = <'###### '> WLine &BlockTerminator


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

<BlockTerminator> = Newlines2 | Newline? EndOfInput
<EndOfInput>      = #'\\Z'
"))

