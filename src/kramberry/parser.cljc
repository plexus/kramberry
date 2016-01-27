(ns kramberry.parser
  (:require [instaparse.core :as insta]))

(def parse
  (insta/parser
    "
<S>               = (Block BlockTerminator)*

<Block>           = pre / p

p                 = Line (WLine Newline)* WLine &BlockTerminator
pre               = code
code              = (CodeLine Newline)+ &BlockTerminator

<Line>            = <Whitespace>? Word (Whitespace Word)*
<WLine>           = Whitespace? Word (Whitespace Word)*
<CodeLine>        = <FourSpaces> Whitespace? Word (Whitespace Word)*

<Whitespace>      = #'(\\ |\\t)+'
<FourSpaces>      = '    '
<Word>            = #'\\S+'
<Newline>         = '\\n'
<Newlines>        = Newline <Whitespace>? Newline (<Whitespace>? <Newline>)*
<BlockTerminator> = Newlines | Newline? EndOfInput
<EndOfInput>      = #'\\Z'
<EOL>             = <'\\n'>"))


  actual: (not (= 
([:p {} "This is a para."]
 "\n\n"
 [:p {} "This is a para."]
 "\n\n"
 [:p {} "This is a para."]
 "\n\n"
 [:p {} "This is a para."]
 "\n\n"
 [:pre {} [:code {} "    This is a code block."]]
 "\n\n"
 [:p {} "And this is another."]
 "\n\n"
 [:p
 {}
 "A para\n with\n  mixed\nindents.\n   and with much indent"
]
 "\n"
) ([:p {} "This is a para."] "\n\n" [:p {} "This is a para."] "\n\n" [:p {} "This is a para."] "\n\n" [:p {} "This is a para."] "\n\n" [:pre {} [:code {} "This is a code block.\n"]] "\n\n" [:p {} "And this is another."] "\n\n" [:p {} "A para\n with\n  mixed\nindents.\n   and with much indent"] "\n")))
