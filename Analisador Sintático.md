# Analisador Sintático - Tabela LL

Terminais:

`Tipos:
INT, FLOAT, STRING,` 

`Condicionais:
IF, ELSE,` 

`Laços:
WHILE, FOR,` 

`Switch
SWITCH, CASE, DEFAULT,` 

`Métodos
FUNC, PROC,`

`Retornos:
RETURN, BREAK, CONTINUE,`

`Operadores:
PLUS, MINUS, 
MULTIPLY, DIVIDE, MODULO,`

`Portas Logicas
AND, OR, NOT,`

`Operados de comparação
GREATER, LESS, GREATER_EQUAL, LESS_EQUAL, EQUAL_EQUAL, NOT_EQUAL,`

`Simbolos:
ASSIGN, LPAREN, RPAREN, LBRACE, RBRACE, SEMICOLON, COMMA, COLON,`

`Identificador
IDENTIFIER,` 

`Values
INT_LITERAL, FLOAT_LITERAL, STRING_LITERAL,`

`Fim
EOF`

### Gramática original (nosso compilador):

PROGRAMA → LISTA_GLOBAL EOF
LISTA_GLOBAL → LISTA_GLOBAL ELEMENTO | ELEMENTO | ε
ELEMENTO → DECLARACAO | FUNCAO | PROCEDIMENTO
DECLARACAO → TIPO IDENTIFIER SEMICOLON | IDENTIFIER ASSIGN EXPRESSAO SEMICOLON
TIPO → INT | FLOAT | STRING

FUNCAO → FUNC TIPO IDENTIFIER LPAREN LISTA_PARAMS RPAREN BLOCO
PROCEDIMENTO → PROC IDENTIFIER LPAREN LISTA_PARAMS RPAREN BLOCO
LISTA_PARAMS → LISTA_PARAMS COMMA PARAM | PARAM | ε
PARAM → TIPO IDENTIFIER
BLOCO → LBRACE LISTA_COMANDOS RBRACE
LISTA_COMANDOS → LISTA_COMANDOS COMANDO | ε

FUNCAO → FUNC TIPO IDENTIFIER LPAREN LISTA_PARAMS RPAREN BLOCO
PROCEDIMENTO → PROC IDENTIFIER LPAREN LISTA_PARAMS RPAREN BLOCO
LISTA_PARAMS → LISTA_PARAMS COMMA PARAM | PARAM | ε
PARAM → TIPO IDENTIFIER
BLOCO → LBRACE LISTA_COMANDOS RBRACE
LISTA_COMANDOS → LISTA_COMANDOS COMANDO | ε

SWITCH_STMT → SWITCH LPAREN IDENTIFIER RPAREN LBRACE LISTA_CASES DEFAULT_OPCIONAL RBRACE
LISTA_CASES → LISTA_CASES CASE_ITEM | CASE_ITEM
CASE_ITEM → CASE LITERAL COLON LISTA_COMANDOS BREAK SEMICOLON
DEFAULT_OPCIONAL → DEFAULT COLON LISTA_COMANDOS | ε

EXPRESSAO → EXPRESSAO OR EXP_AND | EXP_AND
EXP_AND → EXP_AND AND EXP_COMP | EXP_COMP
EXP_COMP → EXP_COMP OP_RELACIONAL EXP_ADD | EXP_ADD
OP_RELACIONAL → GREATER | LESS | GREATER_EQUAL | LESS_EQUAL | EQUAL_EQUAL | NOT_EQUAL
EXP_ADD → EXP_ADD PLUS EXP_MULT | EXP_ADD MINUS EXP_MULT | EXP_MULT
EXP_MULT → EXP_MULT MULTIPLY EXP_UNARY | EXP_MULT DIVIDE EXP_UNARY | EXP_MULT MODULO EXP_UNARY | EXP_UNARY
EXP_UNARY → NOT EXP_UNARY | MINUS EXP_UNARY | FINAL
FINAL → LPAREN EXPRESSAO RPAREN | IDENTIFIER | LITERAL | CHAMADA_FUNCAO
CHAMADA_FUNCAO → IDENTIFIER LPAREN LISTA_ARGS RPAREN
CHAMADA_PROC → IDENTIFIER LPAREN LISTA_ARGS RPAREN
LISTA_ARGS → LISTA_ARGS COMMA EXPRESSAO | EXPRESSAO | ε
LITERAL → INT_LITERAL | FLOAT_LITERAL | STRING_LITERAL

### Gramática utilizada:

**(Estrutura Global)**

(0) P → LG EOF

(1) LG → EL LG

(2) LG → ε

(3) EL → DECL

(4) EL → FUNC_DEF

(5) EL → PROC_DEF

**(Declarações e Tipos)**

(6) DECL → TIPO identifier semicolon

(7) DECL → identifier assign EXP semicolon

(8) TIPO → int

(9) TIPO → float

(10) TIPO → string

**(Funções e Procedimentos)**

(11) FUNC_DEF → func TIPO identifier lparen PARAMS rparen BLOCO

(12) PROC_DEF → proc identifier lparen PARAMS rparen BLOCO

(13) PARAMS → PARAM PARAMS_L

(14) PARAMS → ε

(15) PARAMS_L → comma PARAM PARAMS_L

(16) PARAMS_L → ε

(17) PARAM → TIPO identifier

**(Blocos e Comandos)**

(18) BLOCO → lbrace LISTA_COM rbrace

(19) LISTA_COM → COM LISTA_COM

(20) LISTA_COM → ε

(21) COM → DECL

(22) COM → IF_S

(23) COM → WHILE_S

(24) COM → FOR_S

(25) COM → SWITCH_S

(26) COM → identifier lparen ARGS rparen semicolon

(27) COM → return EXP_OPC semicolon

(28) COM → break semicolon

(29) COM → continue semicolon

**(Estruturas de Controle)**

(30) IF_S → if lparen EXP rparen BLOCO ELSE_OPC

(31) ELSE_OPC → else BLOCO

(32) ELSE_OPC → ε

(33) WHILE_S → while lparen EXP rparen BLOCO

(34) FOR_S → for lparen ATRIB_S semicolon EXP semicolon ATRIB_S rparen BLOCO

(35) ATRIB_S → identifier assign EXP

(36) EXP_OPC → EXP

(37) EXP_OPC → ε

**(Switch Case)**

(38) SWITCH_S → switch lparen identifier rparen lbrace LISTA_CASES DEF_OPC rbrace

(39) LISTA_CASES → CASE_I LISTA_CASES

(40) LISTA_CASES → ε

(41) CASE_I → case LITERAL colon LISTA_COM break semicolon

(42) DEF_OPC → default colon LISTA_COM

(43) DEF_OPC → ε

**(Expressões - Sem recursão à esquerda)**

(44) EXP → E_AND EL

(45) EL → or E_AND EL

(46) EL → ε

(47) E_AND → E_COMP E_AND_L

(48) E_AND_L → and E_COMP E_AND_L

(49) E_AND_L → ε

(50) E_COMP → E_ADD E_COMP_L

(51) E_COMP_L → OP_REL E_ADD E_COMP_L

(52) E_COMP_L → ε

(53) OP_REL → greater

(54) OP_REL → less

(55) OP_REL → greater_equal

(56) OP_REL → less_equal

(57) OP_REL → equal_equal

(58) OP_REL → not_equal

(59) E_ADD → E_MULT E_ADD_L

(60) E_ADD_L → plus E_MULT E_ADD_L

(61) E_ADD_L → minus E_MULT E_ADD_L

(62) E_ADD_L → ε

(63) E_MULT → E_UN E_MULT_L

(64) E_MULT_L → multiply E_UN E_MULT_L

(65) E_MULT_L → divide E_UN E_MULT_L

(66) E_MULT_L → modulo E_UN E_MULT_L

(67) E_MULT_L → ε

(68) E_UN → not E_UN

(69) E_UN → minus E_UN

(70) E_UN → FINAL

**(Base e Chamadas)**

(71) FINAL → lparen EXP rparen

(72) FINAL → identifier FINAL_L

(73) FINAL → LITERAL

(74) FINAL_L → lparen ARGS rparen

(75) FINAL_L → ε

(76) ARGS → EXP ARGS_L

(77) ARGS → ε

(78) ARGS_L → comma EXP ARGS_L

(79) ARGS_L → ε

(80) LITERAL → int_literal

(81) LITERAL → float_literal

(82) LITERAL → string_literal

### Conjunto First

### Expressões (Base e Matemática)

**FIRST(LITERAL)** = { int_literal, float_literal, string_literal }

**FIRST(FINAL_L)** = { lparen, ε }

**FIRST(FINAL)** = { lparen, identifier, int_literal, float_literal, string_literal }

→ Pelas produções (71), (72) e (73).

**FIRST(E_UN)** = { not, minus, lparen, identifier, int_literal, float_literal, string_literal }

→ E_UN pode começar com os unários not/minus ou com o que está em FINAL.

**FIRST(E_MULT_L)** = { multiply, divide, modulo, ε }

**FIRST(E_MULT)** = FIRST(E_UN) = { not, minus, lparen, identifier, int_literal, float_literal, string_literal }

**FIRST(E_ADD_L)** = { plus, minus, ε }

**FIRST(E_ADD)** = FIRST(E_MULT) = { not, minus, lparen, identifier, int_literal, float_literal, string_literal }

---

### Expressões (Lógica e Comparação)

**FIRST(OP_REL)** = { greater, less, greater_equal, less_equal, equal_equal, not_equal }

**FIRST(E_COMP_L)** = FIRST(OP_REL) ∪ { ε } = { greater, less, greater_equal, less_equal, equal_equal, not_equal, ε }

**FIRST(E_COMP)** = FIRST(E_ADD) = { not, minus, lparen, identifier, int_literal, float_literal, string_literal }

**FIRST(E_AND_L)** = { and, ε }

**FIRST(E_AND)** = FIRST(E_COMP) = { not, minus, lparen, identifier, int_literal, float_literal, string_literal }

**FIRST(EL)** = { or, ε }

**FIRST(EXP)** = FIRST(E_AND) = { not, minus, lparen, identifier, int_literal, float_literal, string_literal }

---

### Comandos e Estruturas

**FIRST(ATRIB_S)** = { identifier }

**FIRST(IF_S)** = { if }

**FIRST(ELSE_OPC)** = { else, ε }

**FIRST(WHILE_S)** = { while }

**FIRST(FOR_S)** = { for }

**FIRST(SWITCH_S)** = { switch }

**FIRST(LISTA_CASES)** = { case, ε }

**FIRST(DEF_OPC)** = { default, ε }

**FIRST(TIPO)** = { int, float, string }

**FIRST(DECL)** = { int, float, string, identifier }

→ Pois começa com TIPO (int, float, string) ou com identifier (atribuição).

**FIRST(COM)** = { int, float, string, identifier, if, while, for, switch, return, break, continue }

→ União de todas as cabeças de comando.

**FIRST(BLOCO)** = { lbrace }

---

### Estrutura Global

**FIRST(PARAM)** = FIRST(TIPO) = { int, float, string }

**FIRST(PARAMS)** = FIRST(PARAM) ∪ { ε } = { int, float, string, ε }

**FIRST(FUNC_DEF)** = { func }

**FIRST(PROC_DEF)** = { proc }

**FIRST(EL)** (Elemento Global) = { int, float, string, identifier, func, proc }

→ União de DECL, FUNC_DEF e PROC_DEF.

**FIRST(LG)** = FIRST(EL) ∪ { ε } = { int, float, string, identifier, func, proc, ε }

**FIRST(P)** = FIRST(LG) = { int, float, string, identifier, func, proc, ε }

### Conjunto Follow:

### Estrutura Global e Blocos

**FOLLOW(P)** = { $ }

→ P é o símbolo inicial.

**FOLLOW(LG)** = { $ }

→ P → LG EOF.

**FOLLOW(EL)** = FIRST(LG) - {ε} ∪ { $ }

= { int, float, string, identifier, func, proc, $ }

→ LG → EL LG.

**FOLLOW(BLOCO)** = FOLLOW(FUNC_DEF) ∪ FOLLOW(PROC_DEF) ∪ FOLLOW(COM) ∪ FOLLOW(IF_S) ...

= { int, float, string, identifier, func, proc, if, while, for, switch, return, break, continue, rbrace, else, $ }

→ O bloco termina e pode vir um novo comando, uma nova definição global ou o fim do escopo (rbrace).

**FOLLOW(LISTA_COM)** = { rbrace }

→ BLOCO → lbrace LISTA_COM rbrace.

**FOLLOW(COM)** = FIRST(LISTA_COM) - {ε} ∪ FOLLOW(LISTA_COM)

= { int, float, string, identifier, if, while, for, switch, return, break, continue, rbrace }

→ LISTA_COM → COM LISTA_COM. O que segue um comando é o próximo comando ou o rbrace.

---

### Declarações e Parâmetros

**FOLLOW(DECL)** = FOLLOW(EL) ∪ FOLLOW(COM)

= { int, float, string, identifier, func, proc, if, while, for, switch, return, break, continue, rbrace, $ }

**FOLLOW(TIPO)** = { identifier }

→ DECL → TIPO identifier... ; PARAM → TIPO identifier.

**FOLLOW(PARAMS)** = { rparen }

→ FUNC_DEF → ... lparen PARAMS rparen.

**FOLLOW(ARGS)** = { rparen }

→ FINAL_L → lparen ARGS rparen.

---

### Expressões

Aqui, o FOLLOW de uma expressão "sobe" a hierarquia de baixo para cima.

**FOLLOW(EXP)** = { semicolon, rparen, comma, colon }

→ DECL/RETURN → EXP semicolon; IF/WHILE/FINAL → ( EXP ); ARGS → EXP comma... ; CASE → case literal colon...

**FOLLOW(EL)** (Não-terminal da recursão da expressão lógica OR) = FOLLOW(EXP)

= { semicolon, rparen, comma, colon }

**FOLLOW(E_AND)** = FIRST(EL) - {ε} ∪ FOLLOW(EL)

= { or, semicolon, rparen, comma, colon }

→ EXP → E_AND EL.

**FOLLOW(E_COMP)** = FIRST(E_AND_L) - {ε} ∪ FOLLOW(E_AND)

= { and, or, semicolon, rparen, comma, colon }

→ E_AND → E_COMP E_AND_L.

**FOLLOW(E_ADD)** = FIRST(E_COMP_L) - {ε} ∪ FOLLOW(E_COMP)

= { greater, less, greater_equal, less_equal, equal_equal, not_equal, and, or, semicolon, rparen, comma, colon }

→ E_COMP → E_ADD E_COMP_L.

**FOLLOW(E_MULT)** = FIRST(E_ADD_L) - {ε} ∪ FOLLOW(E_ADD)

= { plus, minus, greater, less, greater_equal, less_equal, equal_equal, not_equal, and, or, semicolon, rparen, comma, colon }

→ E_ADD → E_MULT E_ADD_L.

**FOLLOW(E_UN)** = FIRST(E_MULT_L) - {ε} ∪ FOLLOW(E_MULT)

= { multiply, divide, modulo, plus, minus, greater, less, greater_equal, less_equal, equal_equal, not_equal, and, or, semicolon, rparen, comma, colon }

→ E_MULT → E_UN E_MULT_L.

**FOLLOW(FINAL)** = FOLLOW(E_UN)

= { multiply, divide, modulo, plus, minus, greater, less, greater_equal, less_equal, equal_equal, not_equal, and, or, semicolon, rparen, comma, colon }

---

### Estruturas de Controle

**FOLLOW(IF_S)** = FOLLOW(COM)

= { int, float, string, identifier, if, while, for, switch, return, break, continue, rbrace }

**FOLLOW(ELSE_OPC)** = FOLLOW(IF_S)

= { int, float, string, identifier, if, while, for, switch, return, break, continue, rbrace }

**FOLLOW(WHILE_S)** = FOLLOW(COM)

**FOLLOW(FOR_S)** = FOLLOW(COM)

**FOLLOW(SWITCH_S)** = FOLLOW(COM)

**FOLLOW(LISTA_CASES)** = { default, rbrace }

→ SWITCH_S → ... lbrace LISTA_CASES DEF_OPC rbrace.

**FOLLOW(CASE_I)** = FIRST(LISTA_CASES) - {ε} ∪ FOLLOW(LISTA_CASES)

= { case, default, rbrace }

**FOLLOW(DEF_OPC)** = { rbrace }

→ SWITCH_S → ... DEF_OPC rbrace.

---