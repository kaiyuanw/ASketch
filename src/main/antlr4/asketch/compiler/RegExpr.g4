// Regular Expression Grammar.
grammar RegExpr;

program  : expr EOF # Root
            ;
expr     : TERM # TermNode
            | expr '?' # OptionalNode
            | '(' expr ')' # OrdinaryNode
            | expr expr # ConcatNode
            | expr '|' expr # OrNode
            ;
fragment ESC      : '\\' . ;
TERM     : ([a-zA-Z0-9,.*^~+\-&'":><#![\]=ɛ_] | ESC)+ ;
WS       : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines