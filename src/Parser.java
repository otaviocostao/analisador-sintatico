import java.util.*;

public class Parser {



    public static class CSTNode {
        public final String name;
        public String lexeme;
        public final List<CSTNode> children = new ArrayList<>();

        public CSTNode(String name) {
            this.name = name;
        }

        public CSTNode(String name, String lexeme) {
            this.name = name;
            this.lexeme = lexeme;
        }

        @Override
        public String toString() {
            return lexeme != null ? name + "(" + lexeme + ")" : name;
        }
    }

    private static class StackItem {
        final String symbol;
        final CSTNode node;

        StackItem(String symbol, CSTNode node) {
            this.symbol = symbol;
            this.node = node;
        }
    }




    private static final Set<String> NAO_TERMINAIS = Set.of(
        "P", "LG", "EL", "DECL", "DECL_REST", "TIPO", "FUNC_DEF", "FUNC_REST", "PROC_DEF", "PARAMS", "PARAMS_L", "PARAM",
        "BLOCO", "LISTA_COM", "COM", "IF_S", "ELSE_OPC", "WHILE_S", "FOR_S", "FOR_INIT", "DECL_REST_FOR", "ATRIB_S", "EXP_OPC",
        "SWITCH_S", "LISTA_CASES", "CASE_I", "DEF_OPC", "EXP", "EXP_L", "E_AND", "E_AND_L", "E_COMP",
        "E_COMP_L", "OP_REL", "E_ADD", "E_ADD_L", "E_MULT", "E_MULT_L", "E_UN", "FINAL", "FINAL_L",
        "ARGS", "ARGS_L", "LITERAL"
    );

    private static final Set<String> SILENT_NON_TERMINALS = Set.of(
        "EXP_L", "E_AND_L", "E_COMP_L", "E_ADD_L", "E_MULT_L", "FINAL_L", "PARAMS_L", "ARGS_L"
    );




    private static final Map<String, Set<String>> FOLLOW = new HashMap<>();

    static {
        FOLLOW.put("P", Set.of("$"));
        FOLLOW.put("LG", Set.of("$"));
        FOLLOW.put("EL", Set.of("int", "float", "string", "identifier", "func", "proc", "$"));
        FOLLOW.put("DECL", Set.of("int", "float", "string", "identifier", "func", "proc", "if", "while", "for", "switch", "return", "break", "continue", "rbrace", "$"));
        FOLLOW.put("DECL_REST", Set.of("int", "float", "string", "identifier", "func", "proc", "if", "while", "for", "switch", "return", "break", "continue", "rbrace", "$"));
        FOLLOW.put("TIPO", Set.of("identifier"));
        FOLLOW.put("FUNC_DEF", Set.of("int", "float", "string", "identifier", "func", "proc", "$"));
        FOLLOW.put("FUNC_REST", Set.of("int", "float", "string", "identifier", "func", "proc", "$"));
        FOLLOW.put("PROC_DEF", Set.of("int", "float", "string", "identifier", "func", "proc", "$"));
        FOLLOW.put("PARAMS", Set.of("rparen"));
        FOLLOW.put("PARAMS_L", Set.of("rparen"));
        FOLLOW.put("PARAM", Set.of("comma", "rparen"));
        FOLLOW.put("BLOCO", Set.of("int", "float", "string", "identifier", "func", "proc", "if", "while", "for", "switch", "return", "break", "continue", "rbrace", "else", "$"));
        FOLLOW.put("LISTA_COM", Set.of("rbrace"));
        FOLLOW.put("COM", Set.of("int", "float", "string", "identifier", "if", "while", "for", "switch", "return", "break", "continue", "rbrace"));
        FOLLOW.put("IF_S", Set.of("int", "float", "string", "identifier", "if", "while", "for", "switch", "return", "break", "continue", "rbrace"));
        FOLLOW.put("ELSE_OPC", Set.of("int", "float", "string", "identifier", "if", "while", "for", "switch", "return", "break", "continue", "rbrace"));
        FOLLOW.put("WHILE_S", Set.of("int", "float", "string", "identifier", "if", "while", "for", "switch", "return", "break", "continue", "rbrace"));
        FOLLOW.put("FOR_S", Set.of("int", "float", "string", "identifier", "if", "while", "for", "switch", "return", "break", "continue", "rbrace"));
        FOLLOW.put("FOR_INIT", Set.of("semicolon"));
        FOLLOW.put("DECL_REST_FOR", Set.of("semicolon"));
        FOLLOW.put("ATRIB_S", Set.of("semicolon", "rparen"));
        FOLLOW.put("EXP_OPC", Set.of("semicolon"));
        FOLLOW.put("SWITCH_S", Set.of("int", "float", "string", "identifier", "if", "while", "for", "switch", "return", "break", "continue", "rbrace"));
        FOLLOW.put("LISTA_CASES", Set.of("default", "rbrace"));
        FOLLOW.put("CASE_I", Set.of("case", "default", "rbrace"));
        FOLLOW.put("DEF_OPC", Set.of("rbrace"));
        FOLLOW.put("EXP", Set.of("semicolon", "rparen", "comma", "colon"));
        FOLLOW.put("EXP_L", Set.of("semicolon", "rparen", "comma", "colon"));
        FOLLOW.put("E_AND", Set.of("or", "semicolon", "rparen", "comma", "colon"));
        FOLLOW.put("E_AND_L", Set.of("or", "semicolon", "rparen", "comma", "colon"));
        FOLLOW.put("E_COMP", Set.of("and", "or", "semicolon", "rparen", "comma", "colon"));
        FOLLOW.put("E_COMP_L", Set.of("and", "or", "semicolon", "rparen", "comma", "colon"));
        FOLLOW.put("E_ADD", Set.of("greater", "less", "greater_equal", "less_equal", "equal_equal", "not_equal", "and", "or", "semicolon", "rparen", "comma", "colon"));
        FOLLOW.put("E_ADD_L", Set.of("greater", "less", "greater_equal", "less_equal", "equal_equal", "not_equal", "and", "or", "semicolon", "rparen", "comma", "colon"));
        FOLLOW.put("E_MULT", Set.of("plus", "minus", "greater", "less", "greater_equal", "less_equal", "equal_equal", "not_equal", "and", "or", "semicolon", "rparen", "comma", "colon"));
        FOLLOW.put("E_MULT_L", Set.of("plus", "minus", "greater", "less", "greater_equal", "less_equal", "equal_equal", "not_equal", "and", "or", "semicolon", "rparen", "comma", "colon"));
        FOLLOW.put("E_UN", Set.of("multiply", "divide", "modulo", "plus", "minus", "greater", "less", "greater_equal", "less_equal", "equal_equal", "not_equal", "and", "or", "semicolon", "rparen", "comma", "colon"));
        FOLLOW.put("OP_REL", Set.of("not", "minus", "lparen", "identifier", "int_literal", "float_literal", "string_literal"));
        FOLLOW.put("FINAL", Set.of("multiply", "divide", "modulo", "plus", "minus", "greater", "less", "greater_equal", "less_equal", "equal_equal", "not_equal", "and", "or", "semicolon", "rparen", "comma", "colon"));
        FOLLOW.put("FINAL_L", Set.of("multiply", "divide", "modulo", "plus", "minus", "greater", "less", "greater_equal", "less_equal", "equal_equal", "not_equal", "and", "or", "semicolon", "rparen", "comma", "colon"));
        FOLLOW.put("ARGS", Set.of("rparen"));
        FOLLOW.put("ARGS_L", Set.of("rparen"));
        FOLLOW.put("LITERAL", Set.of("colon", "multiply", "divide", "modulo", "plus", "minus", "greater", "less", "greater_equal", "less_equal", "equal_equal", "not_equal", "and", "or", "semicolon", "rparen", "comma"));
    }




    private static final Map<Integer, List<String>> PRODUCOES = new HashMap<>();
    private static final Map<Integer, String> DESCRICAO_PRODUCAO = new HashMap<>();

    static {
        PRODUCOES.put(0, List.of("LG", "$"));
        PRODUCOES.put(1, List.of("EL", "LG"));
        PRODUCOES.put(2, List.of());

        PRODUCOES.put(3, List.of("COM"));
        PRODUCOES.put(4, List.of("FUNC_DEF"));
        PRODUCOES.put(5, List.of("PROC_DEF"));

        PRODUCOES.put(6, List.of("TIPO", "identifier", "DECL_REST"));
        PRODUCOES.put(7, List.of("identifier", "assign", "EXP", "semicolon"));

        PRODUCOES.put(8, List.of("int"));
        PRODUCOES.put(9, List.of("float"));
        PRODUCOES.put(10, List.of("string"));

        PRODUCOES.put(11, List.of("func", "FUNC_REST"));
        PRODUCOES.put(12, List.of("proc", "identifier", "lparen", "PARAMS", "rparen", "BLOCO"));
        PRODUCOES.put(13, List.of("PARAM", "PARAMS_L"));
        PRODUCOES.put(14, List.of());
        PRODUCOES.put(15, List.of("comma", "PARAM", "PARAMS_L"));
        PRODUCOES.put(16, List.of());
        PRODUCOES.put(17, List.of("TIPO", "identifier"));
        PRODUCOES.put(18, List.of("lbrace", "LISTA_COM", "rbrace"));
        PRODUCOES.put(19, List.of("COM", "LISTA_COM"));
        PRODUCOES.put(20, List.of());
        PRODUCOES.put(21, List.of("DECL"));
        PRODUCOES.put(22, List.of("IF_S"));
        PRODUCOES.put(23, List.of("WHILE_S"));


        PRODUCOES.put(24, List.of("FOR_S"));

        PRODUCOES.put(25, List.of("SWITCH_S"));
        PRODUCOES.put(26, List.of("identifier", "lparen", "ARGS", "rparen", "semicolon"));
        PRODUCOES.put(27, List.of("return", "EXP_OPC", "semicolon"));
        PRODUCOES.put(28, List.of("break", "semicolon"));
        PRODUCOES.put(29, List.of("continue", "semicolon"));
        PRODUCOES.put(30, List.of("if", "lparen", "EXP", "rparen", "BLOCO", "ELSE_OPC"));
        PRODUCOES.put(31, List.of("else", "BLOCO"));
        PRODUCOES.put(32, List.of());
        PRODUCOES.put(33, List.of("while", "lparen", "EXP", "rparen", "BLOCO"));


        PRODUCOES.put(34, List.of("for", "lparen", "FOR_INIT", "semicolon", "EXP", "semicolon", "ATRIB_S", "rparen", "BLOCO"));

        PRODUCOES.put(35, List.of("identifier", "assign", "EXP"));
        PRODUCOES.put(36, List.of("EXP"));
        PRODUCOES.put(37, List.of());
        PRODUCOES.put(38, List.of("switch", "lparen", "identifier", "rparen", "lbrace", "LISTA_CASES", "DEF_OPC", "rbrace"));
        PRODUCOES.put(39, List.of("CASE_I", "LISTA_CASES"));
        PRODUCOES.put(40, List.of());


        PRODUCOES.put(41, List.of("case", "LITERAL", "colon", "LISTA_COM"));

        PRODUCOES.put(42, List.of("default", "colon", "LISTA_COM"));
        PRODUCOES.put(43, List.of());
        PRODUCOES.put(44, List.of("E_AND", "EXP_L"));
        PRODUCOES.put(45, List.of("or", "E_AND", "EXP_L"));
        PRODUCOES.put(46, List.of());
        PRODUCOES.put(47, List.of("E_COMP", "E_AND_L"));
        PRODUCOES.put(48, List.of("and", "E_COMP", "E_AND_L"));
        PRODUCOES.put(49, List.of());
        PRODUCOES.put(50, List.of("E_ADD", "E_COMP_L"));
        PRODUCOES.put(51, List.of("OP_REL", "E_ADD", "E_COMP_L"));
        PRODUCOES.put(52, List.of());
        PRODUCOES.put(53, List.of("greater"));
        PRODUCOES.put(54, List.of("less"));
        PRODUCOES.put(55, List.of("greater_equal"));
        PRODUCOES.put(56, List.of("less_equal"));
        PRODUCOES.put(57, List.of("equal_equal"));
        PRODUCOES.put(58, List.of("not_equal"));
        PRODUCOES.put(59, List.of("E_MULT", "E_ADD_L"));
        PRODUCOES.put(60, List.of("plus", "E_MULT", "E_ADD_L"));
        PRODUCOES.put(61, List.of("minus", "E_MULT", "E_ADD_L"));
        PRODUCOES.put(62, List.of());
        PRODUCOES.put(63, List.of("E_UN", "E_MULT_L"));
        PRODUCOES.put(64, List.of("multiply", "E_UN", "E_MULT_L"));
        PRODUCOES.put(65, List.of("divide", "E_UN", "E_MULT_L"));
        PRODUCOES.put(66, List.of("modulo", "E_UN", "E_MULT_L"));
        PRODUCOES.put(67, List.of());
        PRODUCOES.put(68, List.of("not", "E_UN"));
        PRODUCOES.put(69, List.of("minus", "E_UN"));
        PRODUCOES.put(70, List.of("FINAL"));
        PRODUCOES.put(71, List.of("lparen", "EXP", "rparen"));
        PRODUCOES.put(72, List.of("identifier", "FINAL_L"));
        PRODUCOES.put(73, List.of("LITERAL"));
        PRODUCOES.put(74, List.of("lparen", "ARGS", "rparen"));
        PRODUCOES.put(75, List.of());
        PRODUCOES.put(76, List.of("EXP", "ARGS_L"));
        PRODUCOES.put(77, List.of());
        PRODUCOES.put(78, List.of("comma", "EXP", "ARGS_L"));
        PRODUCOES.put(79, List.of());
        PRODUCOES.put(80, List.of("int_literal"));
        PRODUCOES.put(81, List.of("float_literal"));
        PRODUCOES.put(82, List.of("string_literal"));

        PRODUCOES.put(83, List.of("semicolon"));
        PRODUCOES.put(84, List.of("assign", "EXP", "semicolon"));
        PRODUCOES.put(85, List.of("TIPO", "identifier", "lparen", "PARAMS", "rparen", "BLOCO"));
        PRODUCOES.put(86, List.of("identifier", "lparen", "PARAMS", "rparen", "BLOCO"));


        PRODUCOES.put(87, List.of("TIPO", "identifier", "DECL_REST_FOR"));
        PRODUCOES.put(88, List.of("identifier", "assign", "EXP"));
        PRODUCOES.put(89, List.of());
        PRODUCOES.put(90, List.of("assign", "EXP"));
        PRODUCOES.put(91, List.of());

        DESCRICAO_PRODUCAO.put(0, "P  -> LG EOF");
        DESCRICAO_PRODUCAO.put(1, "LG -> EL LG");
        DESCRICAO_PRODUCAO.put(2, "LG -> ε");
        DESCRICAO_PRODUCAO.put(3, "EL -> COM");
        DESCRICAO_PRODUCAO.put(4, "EL -> FUNC_DEF");
        DESCRICAO_PRODUCAO.put(5, "EL -> PROC_DEF");
        DESCRICAO_PRODUCAO.put(6, "DECL -> TIPO identifier DECL_REST");
        DESCRICAO_PRODUCAO.put(7, "DECL -> identifier = EXP ;");
        DESCRICAO_PRODUCAO.put(8, "TIPO -> int");
        DESCRICAO_PRODUCAO.put(9, "TIPO -> float");
        DESCRICAO_PRODUCAO.put(10, "TIPO -> string");
        DESCRICAO_PRODUCAO.put(11, "FUNC_DEF -> func FUNC_REST");
        DESCRICAO_PRODUCAO.put(12, "PROC_DEF -> proc identifier ( PARAMS ) BLOCO");
        DESCRICAO_PRODUCAO.put(13, "PARAMS -> PARAM PARAMS_L");
        DESCRICAO_PRODUCAO.put(14, "PARAMS -> ε");
        DESCRICAO_PRODUCAO.put(15, "PARAMS_L -> , PARAM PARAMS_L");
        DESCRICAO_PRODUCAO.put(16, "PARAMS_L -> ε");
        DESCRICAO_PRODUCAO.put(17, "PARAM -> TIPO identifier");
        DESCRICAO_PRODUCAO.put(18, "BLOCO -> { LISTA_COM }");
        DESCRICAO_PRODUCAO.put(19, "LISTA_COM -> COM LISTA_COM");
        DESCRICAO_PRODUCAO.put(20, "LISTA_COM -> ε");
        DESCRICAO_PRODUCAO.put(21, "COM -> DECL");
        DESCRICAO_PRODUCAO.put(22, "COM -> IF_S");
        DESCRICAO_PRODUCAO.put(23, "COM -> WHILE_S");
        DESCRICAO_PRODUCAO.put(24, "COM -> FOR_S");
        DESCRICAO_PRODUCAO.put(25, "COM -> SWITCH_S");
        DESCRICAO_PRODUCAO.put(26, "COM -> identifier ( ARGS ) ;");
        DESCRICAO_PRODUCAO.put(27, "COM -> return EXP_OPC ;");
        DESCRICAO_PRODUCAO.put(28, "COM -> break ;");
        DESCRICAO_PRODUCAO.put(29, "COM -> continue ;");
        DESCRICAO_PRODUCAO.put(30, "IF_S -> if ( EXP ) BLOCO ELSE_OPC");
        DESCRICAO_PRODUCAO.put(31, "ELSE_OPC -> else BLOCO");
        DESCRICAO_PRODUCAO.put(32, "ELSE_OPC -> ε");
        DESCRICAO_PRODUCAO.put(33, "WHILE_S -> while ( EXP ) BLOCO");
        DESCRICAO_PRODUCAO.put(34, "FOR_S -> for ( FOR_INIT ; EXP ; ATRIB_S ) BLOCO");
        DESCRICAO_PRODUCAO.put(35, "ATRIB_S -> identifier = EXP");
        DESCRICAO_PRODUCAO.put(36, "EXP_OPC -> EXP");
        DESCRICAO_PRODUCAO.put(37, "EXP_OPC -> ε");
        DESCRICAO_PRODUCAO.put(38, "SWITCH_S -> switch ( identifier ) { LISTA_CASES DEF_OPC }");
        DESCRICAO_PRODUCAO.put(39, "LISTA_CASES -> CASE_I LISTA_CASES");
        DESCRICAO_PRODUCAO.put(40, "LISTA_CASES -> ε");
        DESCRICAO_PRODUCAO.put(41, "CASE_I -> case LITERAL : LISTA_COM");
        DESCRICAO_PRODUCAO.put(42, "DEF_OPC -> default : LISTA_COM");
        DESCRICAO_PRODUCAO.put(43, "DEF_OPC -> ε");
        DESCRICAO_PRODUCAO.put(44, "EXP -> E_AND EXP_L");
        DESCRICAO_PRODUCAO.put(45, "EXP_L -> or E_AND EXP_L");
        DESCRICAO_PRODUCAO.put(46, "EXP_L -> ε");
        DESCRICAO_PRODUCAO.put(47, "E_AND -> E_COMP E_AND_L");
        DESCRICAO_PRODUCAO.put(48, "E_AND_L -> and E_COMP E_AND_L");
        DESCRICAO_PRODUCAO.put(49, "E_AND_L -> ε");
        DESCRICAO_PRODUCAO.put(50, "E_COMP -> E_ADD E_COMP_L");
        DESCRICAO_PRODUCAO.put(51, "E_COMP_L -> OP_REL E_ADD E_COMP_L");
        DESCRICAO_PRODUCAO.put(52, "E_COMP_L -> ε");
        DESCRICAO_PRODUCAO.put(53, "OP_REL -> >");
        DESCRICAO_PRODUCAO.put(54, "OP_REL -> <");
        DESCRICAO_PRODUCAO.put(55, "OP_REL -> >=");
        DESCRICAO_PRODUCAO.put(56, "OP_REL -> <=");
        DESCRICAO_PRODUCAO.put(57, "OP_REL -> ==");
        DESCRICAO_PRODUCAO.put(58, "OP_REL -> !=");
        DESCRICAO_PRODUCAO.put(59, "E_ADD -> E_MULT E_ADD_L");
        DESCRICAO_PRODUCAO.put(60, "E_ADD_L -> + E_MULT E_ADD_L");
        DESCRICAO_PRODUCAO.put(61, "E_ADD_L -> - E_MULT E_ADD_L");
        DESCRICAO_PRODUCAO.put(62, "E_ADD_L -> ε");
        DESCRICAO_PRODUCAO.put(63, "E_MULT -> E_UN E_MULT_L");
        DESCRICAO_PRODUCAO.put(64, "E_MULT_L -> * E_UN E_MULT_L");
        DESCRICAO_PRODUCAO.put(65, "E_MULT_L -> / E_UN E_MULT_L");
        DESCRICAO_PRODUCAO.put(66, "E_MULT_L -> % E_UN E_MULT_L");
        DESCRICAO_PRODUCAO.put(67, "E_MULT_L -> ε");
        DESCRICAO_PRODUCAO.put(68, "E_UN -> ! E_UN");
        DESCRICAO_PRODUCAO.put(69, "E_UN -> - E_UN");
        DESCRICAO_PRODUCAO.put(70, "E_UN -> FINAL");
        DESCRICAO_PRODUCAO.put(71, "FINAL -> ( EXP )");
        DESCRICAO_PRODUCAO.put(72, "FINAL -> identifier FINAL_L");
        DESCRICAO_PRODUCAO.put(73, "FINAL -> LITERAL");
        DESCRICAO_PRODUCAO.put(74, "FINAL_L -> ( ARGS )");
        DESCRICAO_PRODUCAO.put(75, "FINAL_L -> ε");
        DESCRICAO_PRODUCAO.put(76, "ARGS -> EXP ARGS_L");
        DESCRICAO_PRODUCAO.put(77, "ARGS -> ε");
        DESCRICAO_PRODUCAO.put(78, "ARGS_L -> , EXP ARGS_L");
        DESCRICAO_PRODUCAO.put(79, "ARGS_L -> ε");
        DESCRICAO_PRODUCAO.put(80, "LITERAL -> int_literal");
        DESCRICAO_PRODUCAO.put(81, "LITERAL -> float_literal");
        DESCRICAO_PRODUCAO.put(82, "LITERAL -> string_literal");

        DESCRICAO_PRODUCAO.put(83, "DECL_REST -> ;");
        DESCRICAO_PRODUCAO.put(84, "DECL_REST -> = EXP ;");
        DESCRICAO_PRODUCAO.put(85, "FUNC_REST -> TIPO identifier ( PARAMS ) BLOCO");
        DESCRICAO_PRODUCAO.put(86, "FUNC_REST -> identifier ( PARAMS ) BLOCO");

        DESCRICAO_PRODUCAO.put(87, "FOR_INIT -> TIPO identifier DECL_REST_FOR");
        DESCRICAO_PRODUCAO.put(88, "FOR_INIT -> identifier = EXP");
        DESCRICAO_PRODUCAO.put(89, "FOR_INIT -> ε");
        DESCRICAO_PRODUCAO.put(90, "DECL_REST_FOR -> = EXP");
        DESCRICAO_PRODUCAO.put(91, "DECL_REST_FOR -> ε");
    }




    private static final Map<String, Map<String, Integer>> TABELA = new HashMap<>();

    private static void addTransicao(String naoTerminal, String terminal, int producaoIdx) {
        TABELA.computeIfAbsent(naoTerminal, k -> new HashMap<>()).put(terminal, producaoIdx);
    }

    static {

        for (String t : List.of("int", "float", "string", "identifier", "func", "proc", "$", "if", "while", "for", "switch", "return", "break", "continue", "int_literal", "float_literal", "string_literal", "lparen", "minus", "not")) {
            addTransicao("P", t, 0);
        }


        for (String t : List.of("int", "float", "string", "identifier", "func", "proc", "if", "while", "for", "switch", "return", "break", "continue", "int_literal", "float_literal", "string_literal", "lparen", "minus", "not")) {
            addTransicao("LG", t, 1);
        }
        addTransicao("LG", "$", 2);


        for (String t : List.of("int", "float", "string", "identifier", "if", "while", "for", "switch", "return", "break", "continue")) {
            addTransicao("EL", t, 3);
        }
        addTransicao("EL", "func", 4);
        addTransicao("EL", "proc", 5);


        for (String t : List.of("int", "float", "string")) {
            addTransicao("DECL", t, 6);
        }
        addTransicao("DECL", "identifier", 7);


        addTransicao("DECL_REST", "semicolon", 83);
        addTransicao("DECL_REST", "assign", 84);


        addTransicao("TIPO", "int", 8);
        addTransicao("TIPO", "float", 9);
        addTransicao("TIPO", "string", 10);


        addTransicao("FUNC_DEF", "func", 11);


        for (String t : List.of("int", "float", "string")) {
            addTransicao("FUNC_REST", t, 85);
        }
        addTransicao("FUNC_REST", "identifier", 86);


        addTransicao("PROC_DEF", "proc", 12);


        for (String t : List.of("int", "float", "string")) {
            addTransicao("PARAMS", t, 13);
        }
        addTransicao("PARAMS", "rparen", 14);


        addTransicao("PARAMS_L", "comma", 15);
        addTransicao("PARAMS_L", "rparen", 16);


        for (String t : List.of("int", "float", "string")) {
            addTransicao("PARAM", t, 17);
        }


        addTransicao("BLOCO", "lbrace", 18);


        for (String t : List.of("int", "float", "string", "identifier", "if", "while", "for", "switch", "return", "break", "continue")) {
            addTransicao("LISTA_COM", t, 19);
        }
        addTransicao("LISTA_COM", "rbrace", 20);
        addTransicao("LISTA_COM", "case", 20);
        addTransicao("LISTA_COM", "default", 20);


        for (String t : List.of("int", "float", "string")) {
            addTransicao("COM", t, 21);
        }
        addTransicao("COM", "if", 22);
        addTransicao("COM", "while", 23);
        addTransicao("COM", "for", 24);
        addTransicao("COM", "switch", 25);
        addTransicao("COM", "return", 27);
        addTransicao("COM", "break", 28);
        addTransicao("COM", "continue", 29);


        addTransicao("IF_S", "if", 30);


        addTransicao("ELSE_OPC", "else", 31);
        for (String t : List.of("int", "float", "string", "identifier", "if", "while", "for", "switch", "return", "break", "continue", "rbrace")) {
            addTransicao("ELSE_OPC", t, 32);
        }


        addTransicao("WHILE_S", "while", 33);


        addTransicao("FOR_S", "for", 34);


        for (String t : List.of("int", "float", "string")) {
            addTransicao("FOR_INIT", t, 87);
        }
        addTransicao("FOR_INIT", "identifier", 88);
        addTransicao("FOR_INIT", "semicolon", 89);


        addTransicao("DECL_REST_FOR", "assign", 90);
        addTransicao("DECL_REST_FOR", "semicolon", 91);


        addTransicao("ATRIB_S", "identifier", 35);


        for (String t : List.of("not", "minus", "lparen", "identifier", "int_literal", "float_literal", "string_literal")) {
            addTransicao("EXP_OPC", t, 36);
        }
        addTransicao("EXP_OPC", "semicolon", 37);


        addTransicao("SWITCH_S", "switch", 38);


        addTransicao("LISTA_CASES", "case", 39);
        for (String t : List.of("default", "rbrace")) {
            addTransicao("LISTA_CASES", t, 40);
        }


        addTransicao("CASE_I", "case", 41);


        addTransicao("DEF_OPC", "default", 42);
        addTransicao("DEF_OPC", "rbrace", 43);


        for (String t : List.of("not", "minus", "lparen", "identifier", "int_literal", "float_literal", "string_literal")) {
            addTransicao("EXP", t, 44);
        }


        addTransicao("EXP_L", "or", 45);
        for (String t : List.of("semicolon", "rparen", "comma", "colon")) {
            addTransicao("EXP_L", t, 46);
        }


        for (String t : List.of("not", "minus", "lparen", "identifier", "int_literal", "float_literal", "string_literal")) {
            addTransicao("E_AND", t, 47);
        }


        addTransicao("E_AND_L", "and", 48);
        for (String t : List.of("or", "semicolon", "rparen", "comma", "colon")) {
            addTransicao("E_AND_L", t, 49);
        }


        for (String t : List.of("not", "minus", "lparen", "identifier", "int_literal", "float_literal", "string_literal")) {
            addTransicao("E_COMP", t, 50);
        }


        for (String t : List.of("greater", "less", "greater_equal", "less_equal", "equal_equal", "not_equal")) {
            addTransicao("E_COMP_L", t, 51);
        }
        for (String t : List.of("and", "or", "semicolon", "rparen", "comma", "colon")) {
            addTransicao("E_COMP_L", t, 52);
        }


        addTransicao("OP_REL", "greater", 53);
        addTransicao("OP_REL", "less", 54);
        addTransicao("OP_REL", "greater_equal", 55);
        addTransicao("OP_REL", "less_equal", 56);
        addTransicao("OP_REL", "equal_equal", 57);
        addTransicao("OP_REL", "not_equal", 58);


        for (String t : List.of("not", "minus", "lparen", "identifier", "int_literal", "float_literal", "string_literal")) {
            addTransicao("E_ADD", t, 59);
        }


        addTransicao("E_ADD_L", "plus", 60);
        addTransicao("E_ADD_L", "minus", 61);
        for (String t : List.of("greater", "less", "greater_equal", "less_equal", "equal_equal", "not_equal", "and", "or", "semicolon", "rparen", "comma", "colon")) {
            addTransicao("E_ADD_L", t, 62);
        }


        for (String t : List.of("not", "minus", "lparen", "identifier", "int_literal", "float_literal", "string_literal")) {
            addTransicao("E_MULT", t, 63);
        }


        addTransicao("E_MULT_L", "multiply", 64);
        addTransicao("E_MULT_L", "divide", 65);
        addTransicao("E_MULT_L", "modulo", 66);
        for (String t : List.of("plus", "minus", "greater", "less", "greater_equal", "less_equal", "equal_equal", "not_equal", "and", "or", "semicolon", "rparen", "comma", "colon")) {
            addTransicao("E_MULT_L", t, 67);
        }


        addTransicao("E_UN", "not", 68);
        addTransicao("E_UN", "minus", 69);
        for (String t : List.of("lparen", "identifier", "int_literal", "float_literal", "string_literal")) {
            addTransicao("E_UN", t, 70);
        }


        addTransicao("FINAL", "lparen", 71);
        addTransicao("FINAL", "identifier", 72);
        for (String t : List.of("int_literal", "float_literal", "string_literal")) {
            addTransicao("FINAL", t, 73);
        }


        addTransicao("FINAL_L", "lparen", 74);
        for (String t : List.of("multiply", "divide", "modulo", "plus", "minus", "greater", "less", "greater_equal", "less_equal", "equal_equal", "not_equal", "and", "or", "semicolon", "rparen", "comma", "colon")) {
            addTransicao("FINAL_L", t, 75);
        }


        for (String t : List.of("not", "minus", "lparen", "identifier", "int_literal", "float_literal", "string_literal")) {
            addTransicao("ARGS", t, 76);
        }
        addTransicao("ARGS", "rparen", 77);


        addTransicao("ARGS_L", "comma", 78);
        addTransicao("ARGS_L", "rparen", 79);


        addTransicao("LITERAL", "int_literal", 80);
        addTransicao("LITERAL", "float_literal", 81);
        addTransicao("LITERAL", "string_literal", 82);
    }




    private static String getGrammarSymbol(Token token) {
        if (token == null) return "$";
        switch (token.type) {
            case INT: return "int";
            case FLOAT: return "float";
            case STRING: return "string";
            case IF: return "if";
            case ELSE: return "else";
            case WHILE: return "while";
            case FOR: return "for";
            case SWITCH: return "switch";
            case CASE: return "case";
            case DEFAULT: return "default";
            case FUNC: return "func";
            case PROC: return "proc";
            case RETURN: return "return";
            case BREAK: return "break";
            case CONTINUE: return "continue";
            case PLUS: return "plus";
            case MINUS: return "minus";
            case MULTIPLY: return "multiply";
            case DIVIDE: return "divide";
            case MODULO: return "modulo";
            case AND: return "and";
            case OR: return "or";
            case NOT: return "not";
            case GREATER: return "greater";
            case LESS: return "less";
            case GREATER_EQUAL: return "greater_equal";
            case LESS_EQUAL: return "less_equal";
            case EQUAL_EQUAL: return "equal_equal";
            case NOT_EQUAL: return "not_equal";
            case ASSIGN: return "assign";
            case LPAREN: return "lparen";
            case RPAREN: return "rparen";
            case LBRACE: return "lbrace";
            case RBRACE: return "rbrace";
            case SEMICOLON: return "semicolon";
            case COMMA: return "comma";
            case COLON: return "colon";
            case IDENTIFIER: return "identifier";
            case INT_LITERAL: return "int_literal";
            case FLOAT_LITERAL: return "float_literal";
            case STRING_LITERAL: return "string_literal";
            case LBRACKET: return "lbracket";
            case RBRACKET: return "rbracket";
            case EOF: return "$";
            default: return "$";
        }
    }

    private static String getFriendlyExpected(String symbol) {
        switch (symbol) {
            case "semicolon": return "ponto e vírgula ';' no final da linha";
            case "lparen": return "parênteses '('";
            case "rparen": return "parênteses ')'";
            case "lbrace": return "abre chaves '{'";
            case "rbrace": return "fecha chaves '}'";
            case "lbracket": return "abre colchetes '['";
            case "rbracket": return "fecha colchetes ']'";
            case "assign": return "operador de atribuição '='";
            case "colon": return "dois pontos ':'";
            case "identifier": return "identificador";
            case "TIPO": return "Tipo (int, float, string) esperado";
            case "int": return "palavra-chave 'int'";
            case "float": return "palavra-chave 'float'";
            case "string": return "palavra-chave 'string'";
            case "EXP":
            case "E_AND":
            case "E_COMP":
            case "E_ADD":
            case "E_MULT":
            case "E_UN":
            case "FINAL":
                return "Expressão ou valor esperado";
            case "BLOCO":
                return "Abre chaves '{' esperado após a condição";
            case "LG":
                return "declaração, definição de função/procedimento ou comando";
            case "LISTA_COM":
                return "comando ou fecha chaves '}'";
            case "FUNC_REST":
                return "tipo ou nome da função após 'func'";
            default: return "símbolo '" + symbol + "' esperado";
        }
    }

    private static boolean pilhaContains(List<StackItem> pilha, String symbol) {
        for (StackItem item : pilha) {
            if (item.symbol.equals(symbol)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasDuplicateDefaultBefore(List<Token> tokens, int idx) {
        for (int i = idx - 1; i >= 0; i--) {
            Token t = tokens.get(i);
            if (t.type == TokenType.SWITCH) {
                return false;
            }
            if (t.type == TokenType.DEFAULT) {
                return true;
            }
        }
        return false;
    }

    private static int findUnclosedSwitchLine(List<Token> tokens, int idx) {
        for (int i = idx - 1; i >= 0; i--) {
            if (tokens.get(i).type == TokenType.SWITCH) {
                int depth = 0;
                boolean foundLbrace = false;
                int lbraceLine = -1;
                for (int j = i; j < idx; j++) {
                    if (tokens.get(j).type == TokenType.LBRACE) {
                        depth++;
                        foundLbrace = true;
                        lbraceLine = tokens.get(j).line;
                    } else if (tokens.get(j).type == TokenType.RBRACE) {
                        depth--;
                    }
                }
                if (foundLbrace && depth > 0) {
                    return lbraceLine > 0 ? lbraceLine : tokens.get(i).line;
                }
                return -1;
            }
        }
        return -1;
    }

    private static boolean hasErrorContaining(List<String> erros, String text) {
        for (String erro : erros) {
            if (erro.contains(text)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isControlKeyword(String lexeme) {
        return lexeme.equals("if") || lexeme.equals("while") || lexeme.equals("for") || lexeme.equals("switch");
    }

    private static boolean isInsideFunctionCallArgs(List<Token> tokens, int idx) {
        int depth = 0;
        for (int i = idx - 1; i >= 0; i--) {
            Token t = tokens.get(i);
            if (t.type == TokenType.RPAREN) {
                depth++;
            } else if (t.type == TokenType.LPAREN) {
                if (depth > 0) {
                    depth--;
                } else {
                    if (i > 0 && tokens.get(i - 1).type == TokenType.IDENTIFIER) {
                        return !isControlKeyword(tokens.get(i - 1).lexeme);
                    }
                    return false;
                }
            }
        }
        return false;
    }

    private static int findUnclosedBlockLine(List<Token> tokens, int idx) {
        int depth = 0;
        int lastOpenLine = -1;
        for (int i = 0; i < idx; i++) {
            Token t = tokens.get(i);
            if (t.type == TokenType.LBRACE) {
                depth++;
                lastOpenLine = t.line;
            } else if (t.type == TokenType.RBRACE) {
                depth--;
            }
        }
        return depth > 0 ? lastOpenLine : -1;
    }

    private static boolean isExpressionToken(TokenType type) {
        return type == TokenType.IDENTIFIER || type == TokenType.INT_LITERAL
            || type == TokenType.FLOAT_LITERAL || type == TokenType.STRING_LITERAL;
    }

    private static boolean isInFuncParams(List<Token> tokens, int idx) {
        for (int i = idx - 1; i >= 0; i--) {
            if (tokens.get(i).type == TokenType.LPAREN) {
                return i > 0 && tokens.get(i - 1).type == TokenType.IDENTIFIER;
            }
            if (tokens.get(i).type == TokenType.RBRACE || tokens.get(i).type == TokenType.SEMICOLON) {
                return false;
            }
        }
        return false;
    }

    private static boolean isInForHeaderUpdate(List<Token> tokens, int idx) {
        int semi = 0;
        for (int i = idx - 1; i >= 0; i--) {
            Token t = tokens.get(i);
            if (t.type == TokenType.FOR) {
                return semi >= 2;
            }
            if (t.type == TokenType.SEMICOLON) {
                semi++;
            }
        }
        return false;
    }

    private static String checkSpecificError(String topo, Token tokenAtual, int idx, List<Token> tokens, int linha, List<StackItem> pilha, List<String> erros) {

        if (tokenAtual.type == TokenType.INT_LITERAL ||
            tokenAtual.type == TokenType.FLOAT_LITERAL ||
            tokenAtual.type == TokenType.STRING_LITERAL) {
            if (idx + 1 < tokens.size() && tokens.get(idx + 1).type == TokenType.ASSIGN) {
                return "Erro na linha " + linha + ": Identificador esperado à esquerda do operador de atribuição '='.";
            }
        }


        if (topo.equals("FUNC_REST") && tokenAtual.type == TokenType.LPAREN) {
            return "Erro na linha " + linha + ": Nome da função esperado após 'func'.";
        }


        if (topo.equals("DECL_REST") && tokenAtual.type == TokenType.COMMA) {
            if (idx > 0) {
                String varName = tokens.get(idx - 1).lexeme;
                return "Erro na linha " + linha + ": Ponto e vírgula ';' esperado após o identificador '" + varName + "'.";
            }
        }


        if (topo.equals("EXP") && tokenAtual.type == TokenType.RPAREN) {
            if (idx > 0 && tokens.get(idx - 1).type == TokenType.LPAREN) {
                String controle = "";
                for (int i = idx - 2; i >= 0; i--) {
                    Token t = tokens.get(i);
                    if (t.lexeme.equals(";") || t.lexeme.equals("{") || t.lexeme.equals("}")) {
                        break;
                    }
                    if (t.type == TokenType.IF) {
                        controle = "if";
                        break;
                    }
                    if (t.type == TokenType.WHILE) {
                        controle = "while";
                        break;
                    }
                }
                if (!controle.isEmpty()) {
                    return "Erro na linha " + linha + ": Expressão condicional esperada dentro dos parênteses do '" + controle + "'.";
                }
            }
        }


        if (topo.equals("PARAM") && tokenAtual.type == TokenType.IDENTIFIER) {
            if (idx > 0 && tokens.get(idx - 1).type == TokenType.COMMA) {
                return "Erro na linha " + linha + ": Tipo de dado esperado para o parâmetro '" + tokenAtual.lexeme + "'.";
            }
        }


        if (topo.equals("PARAM") && tokenAtual.type == TokenType.RPAREN) {
            if (idx > 0 && tokens.get(idx - 1).type == TokenType.COMMA) {
                return "Erro na linha " + linha + ": Identificador ou definição de parâmetro esperada após a vírgula ','.";
            }
        }


        if (topo.equals("EXP") && (tokenAtual.type == TokenType.INT || tokenAtual.type == TokenType.FLOAT || tokenAtual.type == TokenType.STRING)) {
            String controle = "";
            for (int i = idx - 1; i >= 0; i--) {
                Token t = tokens.get(i);
                if (t.lexeme.equals(";") || t.lexeme.equals("{") || t.lexeme.equals("}")) {
                    break;
                }
                if (t.type == TokenType.IF) {
                    controle = "if";
                    break;
                }
                if (t.type == TokenType.WHILE) {
                    controle = "while";
                    break;
                }
            }
            if (!controle.isEmpty()) {
                return "Erro na linha " + linha + ": Expressão esperada; declarações de variáveis não são permitidas na condição do '" + controle + "'.";
            }
        }


        if (topo.equals("identifier")) {
            boolean isReserved = false;
            switch (tokenAtual.type) {
                case IF: case ELSE: case WHILE: case FOR: case SWITCH: case CASE: case DEFAULT:
                case FUNC: case PROC: case RETURN: case BREAK: case CONTINUE:
                case INT: case FLOAT: case STRING:
                    isReserved = true;
                    break;
                default:
                    break;
            }
            if (isReserved) {
                String tipoAnterior = "";
                if (idx > 0) {
                    tipoAnterior = tokens.get(idx - 1).lexeme;
                }
                return "Erro na linha " + linha + ": Identificador esperado após o tipo de dado '" + tipoAnterior + "'. A palavra '" + tokenAtual.lexeme + "' é reservada.";
            }
        }


        if (topo.equals("BLOCO") && tokenAtual.type == TokenType.SEMICOLON) {
            boolean isSubroutine = false;
            for (int i = idx - 1; i >= 0; i--) {
                Token t = tokens.get(i);
                if (t.lexeme.equals(";") || t.lexeme.equals("{") || t.lexeme.equals("}")) {
                    break;
                }
                if (t.type == TokenType.FUNC || t.type == TokenType.PROC) {
                    isSubroutine = true;
                    break;
                }
            }
            if (isSubroutine) {
                return "Erro na linha " + linha + ": Abre chaves '{' esperado para iniciar o corpo da função.";
            }
        }



        if (topo.equals("lparen") && tokenAtual.type == TokenType.IDENTIFIER) {
            if (idx > 0 && tokens.get(idx - 1).type == TokenType.SWITCH) {
                return "Erro na linha " + linha + ": Parênteses () esperados ao redor da expressão de escolha do 'switch'.";
            }
        }


        if (topo.equals("rparen") && tokenAtual.type == TokenType.LBRACE) {
            boolean hasSwitchNear = false;
            for (int i = idx - 1; i >= Math.max(0, idx - 4); i--) {
                if (tokens.get(i).type == TokenType.SWITCH) {
                    hasSwitchNear = true;
                    break;
                }
            }
            if (hasSwitchNear) {
                return "";
            }
        }


        if (topo.equals("semicolon") && tokenAtual.type == TokenType.NOT) {
            return "Erro na linha " + linha + ": Sintaxe de expressão inválida. O operador '!' deve preceder a expressão.";
        }


        if (topo.equals("LG") && tokenAtual.type == TokenType.RBRACE) {
            return "Erro na linha " + linha + ": Token inesperado '}' fora de qualquer declaração de bloco.";
        }


        if (topo.equals("semicolon") && tokenAtual.type == TokenType.COMMA) {
            if (pilhaContains(pilha, "ATRIB_S")) {
                return "Erro na linha " + linha + ": Ponto e vírgula ';' esperado para separar as seções do laço 'for'.";
            }
        }


        if (topo.equals("ATRIB_S") && tokenAtual.type == TokenType.RPAREN) {
            if (pilhaContains(pilha, "BLOCO")) {
                return "";
            }
        }


        if ((topo.equals("DECL_REST") || topo.equals("EXP") || topo.equals("EXP_OPC") || topo.equals("DECL_REST_FOR")) &&
            tokenAtual.type == TokenType.LBRACKET) {
            return "Erro na linha " + linha + ": Token inesperado '[' após o identificador. Vetores não são suportados na linguagem.";
        }


        if (topo.equals("BLOCO") && idx > 0 && tokens.get(idx - 1).type == TokenType.ELSE) {
            return "Erro na linha " + linha + ": Abre chaves '{' ou estrutura condicional 'if' esperada após a palavra-chave 'else'.";
        }


        if (topo.equals("semicolon") && idx > 0 &&
            (tokens.get(idx - 1).type == TokenType.BREAK || tokens.get(idx - 1).type == TokenType.CONTINUE)) {
            return "Erro na linha " + linha + ": Ponto e vírgula ';' esperado imediatamente após a palavra-chave '" + tokens.get(idx - 1).lexeme + "'.";
        }


        if (topo.equals("assign") && tokenAtual.type == TokenType.SEMICOLON) {
            boolean breakContinueNear = false;
            for (int i = idx - 1; i >= Math.max(0, idx - 4); i--) {
                if (tokens.get(i).type == TokenType.BREAK || tokens.get(i).type == TokenType.CONTINUE) {
                    breakContinueNear = true;
                    break;
                }
            }
            if (breakContinueNear) {
                return "";
            }
        }


        if ((topo.equals("EXP") || topo.equals("E_AND") || topo.equals("E_COMP") || topo.equals("E_ADD") || topo.equals("E_MULT") || topo.equals("E_UN") || topo.equals("FINAL") || topo.equals("LITERAL")) &&
            idx > 0 && tokens.get(idx - 1).type == TokenType.ASSIGN) {
            if (tokenAtual.type == TokenType.IF || tokenAtual.type == TokenType.WHILE || tokenAtual.type == TokenType.FOR || tokenAtual.type == TokenType.SWITCH || tokenAtual.type == TokenType.FUNC || tokenAtual.type == TokenType.PROC || tokenAtual.type == TokenType.LBRACE) {
                return "Erro na linha " + linha + ": Expressão matemática, lógica ou constante esperada após o operador '='.";
            }
        }


        if ((topo.equals("PARAM") || topo.equals("EXP") || topo.equals("E_AND") || topo.equals("E_COMP") || topo.equals("E_ADD") || topo.equals("E_MULT") || topo.equals("E_UN") || topo.equals("FINAL") || topo.equals("LITERAL")) &&
            tokenAtual.type == TokenType.COMMA && idx > 0 && tokens.get(idx - 1).type == TokenType.COMMA) {
            return "Erro na linha " + linha + ": Tipo de dado ou parâmetro esperado após a vírgula ','.";
        }


        if (topo.equals("rbrace") && (tokenAtual.type == TokenType.CASE || tokenAtual.type == TokenType.DEFAULT)) {
            if (!pilhaContains(pilha, "SWITCH_S")) {
                return "Erro na linha " + linha + ": Instrução 'case' inválida fora de um escopo de 'switch' correspondente.";
            }
        }


        if (topo.equals("LITERAL") && tokenAtual.type == TokenType.IDENTIFIER) {
            return "Erro na linha " + linha + ": Valor constante literal esperado após a palavra-chave 'case'.";
        }


        if (topo.equals("rparen") && tokenAtual.type == TokenType.LBRACE) {
            boolean isWhile = false;
            for (int i = idx - 1; i >= 0; i--) {
                Token t = tokens.get(i);
                if (t.lexeme.equals(";") || t.lexeme.equals("{") || t.lexeme.equals("}")) {
                    break;
                }
                if (t.type == TokenType.WHILE) {
                    isWhile = true;
                    break;
                }
            }
            if (isWhile) {
                return "Erro na linha " + linha + ": Fecha parênteses ')' esperado.";
            }
        }


        if (topo.equals("assign") && tokenAtual.type == TokenType.EQUAL_EQUAL) {
            return "Erro na linha " + linha + ": Sintaxe de expressão inválida.";
        }


        if (topo.equals("identifier") && tokenAtual.type == TokenType.LPAREN) {
            if (idx > 0 && (tokens.get(idx - 1).type == TokenType.PROC || tokens.get(idx - 1).type == TokenType.FUNC)) {
                return "Erro na linha " + linha + ": Identificador esperado após a palavra-chave '" + tokens.get(idx - 1).lexeme + "'.";
            }
        }


        if (topo.equals("identifier") && pilhaContains(pilha, "PARAM")) {
            if (tokenAtual.type == TokenType.COMMA || tokenAtual.type == TokenType.RPAREN) {
                if (idx > 0) {
                    Token prev = tokens.get(idx - 1);
                    if (prev.type == TokenType.INT || prev.type == TokenType.FLOAT || prev.type == TokenType.STRING
                        || prev.type == TokenType.COMMA) {
                        return "Erro na linha " + linha + ": esperado 'identificador', encontrado '" + tokenAtual.lexeme + "'.";
                    }
                }
            }
        }


        if (topo.equals("assign") && tokenAtual.type == TokenType.PLUS && isInForHeaderUpdate(tokens, idx)) {
            if (idx > 0 && tokens.get(idx - 1).type == TokenType.IDENTIFIER) {
                return "Erro na linha " + linha + ": Expressão ou valor esperado.";
            }
        }


        if (tokenAtual.type == TokenType.EOF) {
            int switchLine = findUnclosedSwitchLine(tokens, idx);
            if (switchLine > 0) {
                return "Erro na linha " + switchLine + ": Fecha chaves '}' esperado para encerrar o bloco do 'switch'.";
            }
            int blockLine = findUnclosedBlockLine(tokens, idx);
            if (blockLine > 0) {
                return "Erro na linha " + blockLine + ": Fim de arquivo inesperado. Fecha chaves '}' esperado para encerrar o bloco.";
            }
        }


        if (tokenAtual.type == TokenType.DEFAULT) {
            boolean hasDefaultBefore = false;
            for (int i = idx - 1; i >= 0; i--) {
                Token t = tokens.get(i);
                if (t.type == TokenType.SWITCH) {
                    break;
                }
                if (t.type == TokenType.DEFAULT) {
                    hasDefaultBefore = true;
                    break;
                }
            }
            if (hasDefaultBefore) {
                return "Erro na linha " + linha + ": Apenas uma cláusula 'default' é permitida dentro da estrutura 'switch'.";
            }
        }


        if (topo.equals("DECL_REST") && (tokenAtual.type == TokenType.RBRACE || tokenAtual.type == TokenType.EOF)) {
            if (idx > 0 && tokens.get(idx - 1).type == TokenType.IDENTIFIER) {
                if (idx > 1 && (tokens.get(idx - 2).type == TokenType.INT || tokens.get(idx - 2).type == TokenType.FLOAT || tokens.get(idx - 2).type == TokenType.STRING)) {
                    return "Erro na linha " + linha + ": Ponto e vírgula ';' esperado após o identificador '" + tokens.get(idx - 1).lexeme + "'.";
                }
            }
        }


        if ((topo.equals("E_UN") || topo.equals("FINAL") || topo.equals("LITERAL")) && tokenAtual.type == TokenType.RPAREN) {
            if (idx > 0 && tokens.get(idx - 1).type == TokenType.NOT) {
                return "Erro na linha " + linha + ": Expressão lógica esperada após o operador de negação unário '!'.";
            }
        }


        for (String erro : erros) {
            if (erro.startsWith("Erro na linha " + linha + ":")) {
                if (erro.contains("Expressão matemática, lógica ou constante esperada") ||
                    erro.contains("Token inesperado '[' após o identificador") ||
                    erro.contains("Ponto e vírgula ';' esperado imediatamente após a palavra-chave") ||
                    erro.contains("Ponto e vírgula ';' esperado para separar as seções") ||
                    erro.contains("Pelo menos um rótulo 'case' ou 'default'") ||
                    erro.contains("Tipo de dado ou parâmetro esperado após a vírgula") ||
                    erro.contains("Instrução 'case' inválida fora de um escopo") ||
                    erro.contains("Declarações de variáveis devem vir no início do escopo") ||
                    erro.contains("Parênteses () esperados ao redor dos argumentos") ||
                    erro.contains("Vírgula ',' esperada para separar os argumentos") ||
                    erro.contains("Sintaxe de expressão condicional inválida. Operando esperado") ||
                    erro.contains("Expressão inválida após o operador de atribuição") ||
                    erro.contains("Valor constante literal esperado após") ||
                    erro.contains("Ponto e vírgula ';' esperado após a expressão de retorno") ||
                    erro.contains("Instrução 'return' inválida fora de") ||
                    erro.contains("Instrução vazia ou inválida detectada") ||
                    erro.contains("Parênteses '(' esperado após a palavra-chave 'for'") ||
                    erro.contains("Parênteses () esperados após 'if'") ||
                    erro.contains("Fecha parênteses ')' esperado") ||
                    erro.contains("Sintaxe de expressão inválida") ||
                    erro.contains("Identificador esperado após a palavra-chave") ||
                    erro.contains("Identificador esperado à esquerda do operador de atribuição") ||
                    erro.contains("Fecha chaves '}' esperado para encerrar") ||
                    erro.contains("Fim de arquivo inesperado") ||
                    erro.contains("Abre chaves '{' esperado após a condição") ||
                    erro.contains("esperado 'abre chaves '{'") ||
                    erro.contains("Apenas uma cláusula 'default' é permitida") ||
                    erro.contains("Ponto e vírgula ';' esperado após o identificador") ||
                    erro.contains("Expressão lógica esperada após o operador") ||
                    erro.contains("Bloco de chaves anônimo ou solto não é permitido") ||
                    erro.contains("Declaração de variável, função ou procedimento esperada no escopo global")) {
                    return "";
                }
            }
        }


        if (hasErrorContaining(erros, "Parênteses () esperados após 'if'") &&
            (topo.equals("ELSE_OPC") || getFriendlyExpected(topo).contains("ELSE_OPC"))) {
            return "";
        }
        if (hasErrorContaining(erros, "Abre chaves '{' esperado após a condição")) {
            if (topo.equals("ELSE_OPC") || getFriendlyExpected(topo).contains("ELSE_OPC")) {
                return "";
            }
            if (topo.equals("LISTA_COM") && tokenAtual.type == TokenType.SEMICOLON) {
                return "";
            }
        }
        if (hasErrorContaining(erros, "Instrução 'return' inválida fora de") && topo.equals("EL")) {
            return "";
        }
        if (hasErrorContaining(erros, "esperado 'identificador'") && isInFuncParams(tokens, idx)
            && tokenAtual.type != TokenType.COMMA) {
            return "";
        }
        if (hasErrorContaining(erros, "Declaração de variável, função ou procedimento esperada no escopo global")
            && tokenAtual.lexeme.equals("void")) {
            return "";
        }
        if (hasErrorContaining(erros, "esperado 'abre chaves '{'") && topo.equals("LISTA_COM")) {
            return "";
        }
        if (hasErrorContaining(erros, "Identificador esperado à esquerda do operador de atribuição")
            && topo.equals("assign")) {
            return "";
        }

        return null;
    }




    public static void parse(List<Token> tokens, String outputJsonPath) {
        int idx = 0;
        int lastErrorIdx = -1;
        List<StackItem> pilha = new ArrayList<>();
        CSTNode root = new CSTNode("P");

        pilha.add(new StackItem("$", new CSTNode("$")));
        pilha.add(new StackItem("P", root));
        List<String> erros = new ArrayList<>();

        while (!pilha.get(pilha.size() - 1).symbol.equals("$")) {
            StackItem itemTopo = pilha.get(pilha.size() - 1);
            String topo = itemTopo.symbol;
            Token tokenAtual = (idx < tokens.size()) ? tokens.get(idx) : new Token(TokenType.EOF, "$", null, -1);
            String tipo = getGrammarSymbol(tokenAtual);
            int linha = tokenAtual.line;


            if ((tokenAtual.type == TokenType.AND || tokenAtual.type == TokenType.OR) &&
                idx > 0 &&
                (tokens.get(idx - 1).type == TokenType.AND || tokens.get(idx - 1).type == TokenType.OR)) {

                String msg = "Erro na linha " + linha + ": Sintaxe de expressão condicional inválida. Operando esperado entre os operadores '" + tokens.get(idx - 1).lexeme + "' e '" + tokenAtual.lexeme + "'.";
                System.out.println(msg);
                erros.add(msg);
                lastErrorIdx = idx;

                idx++;
                continue;
            }


            if (isInsideFunctionCallArgs(tokens, idx) && isExpressionToken(tokenAtual.type)
                && idx > 0 && isExpressionToken(tokens.get(idx - 1).type)) {
                String msg = "Erro na linha " + linha + ": Vírgula ',' esperada para separar os argumentos na chamada da função.";
                System.out.println(msg);
                erros.add(msg);
                lastErrorIdx = idx;
                while (idx < tokens.size()) {
                    Token t = tokens.get(idx);
                    if (t.type == TokenType.RPAREN || t.type == TokenType.SEMICOLON || t.type == TokenType.EOF) {
                        break;
                    }
                    idx++;
                }
                while (pilha.size() > 0) {
                    String sym = pilha.get(pilha.size() - 1).symbol;
                    if (sym.equals("rparen") || sym.equals("semicolon") || sym.equals("$")) {
                        break;
                    }
                    pilha.remove(pilha.size() - 1);
                }
                continue;
            }


            if (topo.equals("assign") && tokenAtual.type == TokenType.PLUS
                && idx > 0 && tokens.get(idx - 1).type == TokenType.IDENTIFIER
                && isInForHeaderUpdate(tokens, idx)) {
                String msg = "Erro na linha " + linha + ": Expressão ou valor esperado.";
                System.out.println(msg);
                erros.add(msg);
                lastErrorIdx = idx;
                idx++;
                if (idx < tokens.size() && tokens.get(idx).type == TokenType.PLUS) {
                    idx++;
                }
                while (idx < tokens.size() && tokens.get(idx).type != TokenType.RPAREN) {
                    idx++;
                }
                while (pilha.size() > 0) {
                    String sym = pilha.get(pilha.size() - 1).symbol;
                    if (sym.equals("rparen") || sym.equals("BLOCO") || sym.equals("FOR_S")) {
                        break;
                    }
                    pilha.remove(pilha.size() - 1);
                }
                continue;
            }


            if ((tokenAtual.type == TokenType.INT_LITERAL || tokenAtual.type == TokenType.FLOAT_LITERAL
                || tokenAtual.type == TokenType.STRING_LITERAL)
                && idx + 1 < tokens.size() && tokens.get(idx + 1).type == TokenType.ASSIGN) {
                String msg = "Erro na linha " + linha + ": Identificador esperado à esquerda do operador de atribuição '='.";
                System.out.println(msg);
                erros.add(msg);
                lastErrorIdx = idx;
                while (idx < tokens.size()) {
                    Token t = tokens.get(idx);
                    if (t.type == TokenType.SEMICOLON || t.type == TokenType.RBRACE || t.type == TokenType.EOF) {
                        break;
                    }
                    idx++;
                }
                if (idx < tokens.size() && tokens.get(idx).type == TokenType.SEMICOLON) {
                    idx++;
                }
                while (pilha.size() > 0) {
                    String sym = pilha.get(pilha.size() - 1).symbol;
                    if (sym.equals("LISTA_COM") || sym.equals("LG") || sym.equals("$") || sym.equals("semicolon")) {
                        break;
                    }
                    pilha.remove(pilha.size() - 1);
                }
                continue;
            }


            if ((topo.equals("EL") || topo.equals("LG")) && tokenAtual.type == TokenType.IDENTIFIER
                && tokenAtual.lexeme.equals("void")) {
                String msg = "Erro na linha " + linha + ": Declaração de variável, função ou procedimento esperada no escopo global.";
                System.out.println(msg);
                erros.add(msg);
                lastErrorIdx = idx;
                int braceCount = 0;
                boolean started = false;
                while (idx < tokens.size()) {
                    Token t = tokens.get(idx);
                    if (t.type == TokenType.LBRACE) {
                        braceCount++;
                        started = true;
                    } else if (t.type == TokenType.RBRACE) {
                        braceCount--;
                        idx++;
                        if (started && braceCount == 0) {
                            break;
                        }
                        continue;
                    }
                    idx++;
                }
                continue;
            }


            if (topo.equals("BLOCO") && tokenAtual.type != TokenType.LBRACE) {
                boolean afterIf = false;
                for (int i = idx - 1; i >= 0; i--) {
                    Token t = tokens.get(i);
                    if (t.type == TokenType.IF) {
                        afterIf = true;
                        break;
                    }
                    if (t.type == TokenType.SEMICOLON || t.type == TokenType.LBRACE) {
                        break;
                    }
                }
                if (afterIf) {
                    String msg = "Erro na linha " + linha + ": Abre chaves '{' esperado após a condição.";
                    System.out.println(msg);
                    erros.add(msg);
                    lastErrorIdx = idx;
                    while (idx < tokens.size()) {
                        Token t = tokens.get(idx);
                        if (t.type == TokenType.SEMICOLON) {
                            idx++;
                            break;
                        }
                        if (t.type == TokenType.RBRACE || t.type == TokenType.EOF) {
                            break;
                        }
                        idx++;
                    }
                    while (pilha.size() > 0) {
                        String sym = pilha.get(pilha.size() - 1).symbol;
                        if (sym.equals("LISTA_COM") || sym.equals("LG") || sym.equals("$")) {
                            break;
                        }
                        pilha.remove(pilha.size() - 1);
                    }
                    continue;
                }
            }


            if (topo.equals("lbrace") && (tokenAtual.type == TokenType.CASE || tokenAtual.type == TokenType.DEFAULT)) {
                boolean afterSwitch = false;
                for (int i = idx - 1; i >= 0; i--) {
                    Token t = tokens.get(i);
                    if (t.type == TokenType.SWITCH) {
                        afterSwitch = true;
                        break;
                    }
                    if (t.type == TokenType.LBRACE || t.type == TokenType.SEMICOLON) {
                        break;
                    }
                }
                if (afterSwitch) {
                    String msg = "Erro na linha " + linha + ": esperado 'abre chaves '{'', encontrado '" + tokenAtual.lexeme + "'.";
                    System.out.println(msg);
                    erros.add(msg);
                    lastErrorIdx = idx;
                    pilha.remove(pilha.size() - 1);
                    while (idx < tokens.size()) {
                        Token t = tokens.get(idx);
                        if (t.type == TokenType.RBRACE || t.type == TokenType.EOF) {
                            break;
                        }
                        idx++;
                    }
                    continue;
                }
            }


            if (isInFuncParams(tokens, idx) && idx > 0) {
                Token prev = tokens.get(idx - 1);
                boolean missingIdAfterType = prev.type == TokenType.INT || prev.type == TokenType.FLOAT
                    || prev.type == TokenType.STRING;
                if (missingIdAfterType
                    && (tokenAtual.type == TokenType.COMMA || tokenAtual.type == TokenType.RPAREN)) {
                    if (!hasErrorContaining(erros, "esperado 'identificador'")) {
                        String msg = "Erro na linha " + linha + ": esperado 'identificador', encontrado '" + tokenAtual.lexeme + "'.";
                        System.out.println(msg);
                        erros.add(msg);
                        lastErrorIdx = idx;
                    }
                    while (idx < tokens.size() && tokens.get(idx).type != TokenType.RPAREN) {
                        idx++;
                    }
                    while (pilha.size() > 0) {
                        String sym = pilha.get(pilha.size() - 1).symbol;
                        if (sym.equals("rparen") || sym.equals("BLOCO") || sym.equals("lbrace")) {
                            break;
                        }
                        pilha.remove(pilha.size() - 1);
                    }
                    if (idx < tokens.size() && tokens.get(idx).type == TokenType.RPAREN) {
                        idx++;
                    }
                    continue;
                }
            }


            if (topo.equals("FINAL_L") &&
                (tipo.equals("identifier") || tipo.equals("int_literal") || tipo.equals("float_literal") || tipo.equals("string_literal")) &&
                idx > 0 && tokens.get(idx - 1).type == TokenType.IDENTIFIER &&
                !isInsideFunctionCallArgs(tokens, idx) &&
                !tokens.get(idx - 1).lexeme.equals("void")) {

                String funcName = tokens.get(idx - 1).lexeme;
                String msg = "Erro na linha " + linha + ": Parênteses () esperados ao redor dos argumentos na chamada da função '" + funcName + "'.";
                System.out.println(msg);
                erros.add(msg);
                lastErrorIdx = idx;


                while (idx < tokens.size()) {
                    Token t = tokens.get(idx);
                    if (t.type == TokenType.SEMICOLON || t.type == TokenType.RBRACE || t.type == TokenType.EOF) {
                        break;
                    }
                    idx++;
                }

                pilha.remove(pilha.size() - 1);
                continue;
            }


            if (topo.equals("assign") &&
                (tipo.equals("identifier") || tipo.equals("int_literal") || tipo.equals("float_literal") || tipo.equals("string_literal")) &&
                pilha.size() >= 3 &&
                pilha.get(pilha.size() - 2).symbol.equals("EXP") &&
                pilha.get(pilha.size() - 3).symbol.equals("semicolon") &&
                idx > 0 && tokens.get(idx - 1).type == TokenType.IDENTIFIER &&
                !tokens.get(idx - 1).lexeme.equals("void")) {

                String funcName = tokens.get(idx - 1).lexeme;
                String msg = "Erro na linha " + linha + ": Parênteses () esperados ao redor dos argumentos na chamada da função '" + funcName + "'.";
                System.out.println(msg);
                erros.add(msg);
                lastErrorIdx = idx;


                while (idx < tokens.size()) {
                    Token t = tokens.get(idx);
                    if (t.type == TokenType.SEMICOLON || t.type == TokenType.RBRACE || t.type == TokenType.EOF) {
                        break;
                    }
                    idx++;
                }


                pilha.remove(pilha.size() - 1);
                pilha.remove(pilha.size() - 1);
                continue;
            }


            if (tokenAtual.type == TokenType.ASSIGN && idx > 0 && tokens.get(idx - 1).type == TokenType.IDENTIFIER) {
                boolean assignBefore = false;
                for (int i = idx - 2; i >= 0; i--) {
                    Token t = tokens.get(i);
                    if (t.type == TokenType.SEMICOLON || t.type == TokenType.LBRACE || t.type == TokenType.RBRACE) {
                        break;
                    }
                    if (t.type == TokenType.ASSIGN) {
                        assignBefore = true;
                        break;
                    }
                }
                if (assignBefore) {
                    String msg = "Erro na linha " + linha + ": Expressão inválida após o operador de atribuição '='.";
                    System.out.println(msg);
                    erros.add(msg);
                    lastErrorIdx = idx;

                    while (idx < tokens.size()) {
                        Token t = tokens.get(idx);
                        if (t.type == TokenType.SEMICOLON || t.type == TokenType.RBRACE || t.type == TokenType.EOF) {
                            break;
                        }
                        idx++;
                    }

                    while (pilha.size() > 0) {
                        String sym = pilha.get(pilha.size() - 1).symbol;
                        if (sym.equals("semicolon") || sym.equals("$")) {
                            break;
                        }
                        pilha.remove(pilha.size() - 1);
                    }
                    continue;
                }
            }


            if (topo.equals("semicolon") && tokenAtual.type == TokenType.COMMA) {
                boolean hasReturn = false;
                for (int i = idx - 1; i >= 0; i--) {
                    Token t = tokens.get(i);
                    if (t.type == TokenType.SEMICOLON || t.type == TokenType.LBRACE || t.type == TokenType.RBRACE) {
                        break;
                    }
                    if (t.type == TokenType.RETURN) {
                        hasReturn = true;
                        break;
                    }
                }
                if (hasReturn) {
                    String msg = "Erro na linha " + linha + ": Ponto e vírgula ';' esperado após a expressão de retorno.";
                    System.out.println(msg);
                    erros.add(msg);
                    lastErrorIdx = idx;

                    while (idx < tokens.size()) {
                        Token t = tokens.get(idx);
                        if (t.type == TokenType.SEMICOLON || t.type == TokenType.RBRACE || t.type == TokenType.EOF) {
                            break;
                        }
                        idx++;
                    }
                    continue;
                }
            }


            if (topo.equals("LISTA_COM") && tokenAtual.type == TokenType.SEMICOLON) {
                String msg = "Erro na linha " + linha + ": Instrução vazia ou inválida detectada.";
                System.out.println(msg);
                erros.add(msg);
                lastErrorIdx = idx;
                idx++;
                continue;
            }


            if (topo.equals("lparen") && idx > 0 && tokens.get(idx - 1).type == TokenType.FOR) {
                if (tokenAtual.type != TokenType.LPAREN) {
                    String msg = "Erro na linha " + linha + ": Parênteses '(' esperado após a palavra-chave 'for'.";
                    System.out.println(msg);
                    erros.add(msg);
                    lastErrorIdx = idx;
                    pilha.remove(pilha.size() - 1);
                    continue;
                }
            }


            if (topo.equals("LISTA_COM") && tokenAtual.type == TokenType.LBRACE) {
                String msg = "Erro na linha " + linha + ": Bloco de chaves anônimo ou solto não é permitido.";
                System.out.println(msg);
                erros.add(msg);
                lastErrorIdx = idx;

                int lbraceCount = 1;
                idx++;
                while (idx < tokens.size() && lbraceCount > 0) {
                    Token t = tokens.get(idx);
                    if (t.type == TokenType.LBRACE) {
                        lbraceCount++;
                    } else if (t.type == TokenType.RBRACE) {
                        lbraceCount--;
                    }
                    idx++;
                }
                continue;
            }


            if (topo.equals("LITERAL") && tokenAtual.type == TokenType.IDENTIFIER &&
                idx > 0 && tokens.get(idx - 1).type == TokenType.CASE) {
                String msg = "Erro na linha " + linha + ": Valor constante literal esperado após a palavra-chave 'case'.";
                System.out.println(msg);
                erros.add(msg);
                lastErrorIdx = idx;
                idx++;
                if (idx < tokens.size() && tokens.get(idx).type == TokenType.COLON) {
                    idx++;
                }
                pilha.remove(pilha.size() - 1);
                if (!pilha.isEmpty() && pilha.get(pilha.size() - 1).symbol.equals("colon")) {
                    pilha.remove(pilha.size() - 1);
                }
                continue;
            }


            if (topo.equals("EL") && tokenAtual.type == TokenType.RETURN) {
                String msg = "Erro na linha " + linha + ": Instrução 'return' inválida fora de uma função ou procedimento.";
                System.out.println(msg);
                erros.add(msg);
                lastErrorIdx = idx;
                while (idx < tokens.size()) {
                    Token t = tokens.get(idx);
                    if (t.type == TokenType.SEMICOLON) {
                        idx++;
                        break;
                    }
                    if (t.type == TokenType.EOF) {
                        break;
                    }
                    idx++;
                }
                if (!pilha.isEmpty() && pilha.get(pilha.size() - 1).symbol.equals("EL")) {
                    pilha.remove(pilha.size() - 1);
                }
                continue;
            }


            if (tokenAtual.type == TokenType.DEFAULT && hasDuplicateDefaultBefore(tokens, idx)) {
                String msg = "Erro na linha " + linha + ": Apenas uma cláusula 'default' é permitida dentro da estrutura 'switch'.";
                System.out.println(msg);
                erros.add(msg);
                lastErrorIdx = idx;
                idx++;
                if (idx < tokens.size() && tokens.get(idx).type == TokenType.COLON) {
                    idx++;
                }
                continue;
            }


            if (tokenAtual.type == TokenType.EOF) {
                int switchLine = findUnclosedSwitchLine(tokens, idx);
                if (switchLine > 0) {
                    String msg = "Erro na linha " + switchLine + ": Fecha chaves '}' esperado para encerrar o bloco do 'switch'.";
                    System.out.println(msg);
                    erros.add(msg);
                    lastErrorIdx = idx;
                    break;
                }
                int blockLine = findUnclosedBlockLine(tokens, idx);
                if (blockLine > 0) {
                    String msg = "Erro na linha " + blockLine + ": Fim de arquivo inesperado. Fecha chaves '}' esperado para encerrar o bloco.";
                    System.out.println(msg);
                    erros.add(msg);
                    lastErrorIdx = idx;
                    break;
                }
            }

            System.out.println("TOKEN: Token(" + tokenAtual.type + ", '" + tokenAtual.lexeme + "')");

            if (topo.equals(tipo)) {
                System.out.println("TOPO: " + topo);
                System.out.println("TIPO: " + tipo);
                System.out.println("DESEMPILHA " + topo);
                System.out.println("VAI PARA O PRÓXIMO TOKEN\n");

                itemTopo.node.lexeme = tokenAtual.lexeme;

                pilha.remove(pilha.size() - 1);
                idx++;
            } else if (NAO_TERMINAIS.contains(topo)) {
                System.out.println("\nTOPO IGUAL A VARIÁVEL: " + topo);


                if ((topo.equals("LG") || topo.equals("LISTA_COM")) &&
                    (tipo.equals("case") || tipo.equals("default")) &&
                    !pilhaContains(pilha, "SWITCH_S") &&
                    !(tipo.equals("default") && hasDuplicateDefaultBefore(tokens, idx))) {

                    String msg = "Erro na linha " + linha + ": Instrução 'case' inválida fora de um escopo de 'switch' correspondente.";
                    System.out.println(msg);
                    erros.add(msg);
                    lastErrorIdx = idx;

                    if (tipo.equals("case")) {
                        idx++;
                        if (idx < tokens.size()) {
                            Token tNext = tokens.get(idx);
                            if (tNext.type == TokenType.INT_LITERAL ||
                                tNext.type == TokenType.FLOAT_LITERAL ||
                                tNext.type == TokenType.STRING_LITERAL) {
                                idx++;
                            }
                        }
                    } else {
                        idx++;
                    }
                    if (idx < tokens.size() && tokens.get(idx).type == TokenType.COLON) {
                        idx++;
                    }
                    continue;
                }

                Map<String, Integer> entradaTabela = TABELA.getOrDefault(topo, Map.of());
                System.out.println("ENTRADA_TABELA: " + entradaTabela);

                Integer prodIdx = null;

                if (topo.equals("COM") && tipo.equals("identifier")) {
                    if (idx + 1 < tokens.size() && tokens.get(idx + 1).type == TokenType.LPAREN) {
                        prodIdx = 26;
                    } else {
                        prodIdx = 21;
                    }
                } else {
                    prodIdx = entradaTabela.get(tipo);
                }
                if (prodIdx != null) {
                    if (prodIdx == 3) {
                        if (tokenAtual.type == TokenType.RETURN) {
                            String msg = "Erro na linha " + linha + ": Instrução 'return' inválida fora de uma função ou procedimento.";
                            System.out.println(msg);
                            erros.add(msg);
                        } else if (tokenAtual.type == TokenType.IDENTIFIER) {
                            String msg = "Erro na linha " + linha + ": Declaração de variável, função ou procedimento esperada no escopo global.";
                            System.out.println(msg);
                            erros.add(msg);
                        }
                    }
                    if (prodIdx == 21) {
                        if (tokenAtual.type == TokenType.INT || tokenAtual.type == TokenType.FLOAT || tokenAtual.type == TokenType.STRING) {
                            boolean executavelAntes = false;
                            int braceCount = 0;
                            for (int i = idx - 1; i >= 0; i--) {
                                Token t = tokens.get(i);
                                if (t.type == TokenType.RBRACE) {
                                    braceCount++;
                                } else if (t.type == TokenType.LBRACE) {
                                    if (braceCount > 0) {
                                        braceCount--;
                                    } else {
                                        break;
                                    }
                                } else if (braceCount == 0) {
                                    if (t.type == TokenType.IF || t.type == TokenType.WHILE || t.type == TokenType.FOR ||
                                        t.type == TokenType.SWITCH || t.type == TokenType.RETURN || t.type == TokenType.BREAK ||
                                        t.type == TokenType.CONTINUE) {
                                        executavelAntes = true;
                                        break;
                                    }
                                    if (t.type == TokenType.ASSIGN && i - 2 >= 0) {
                                        Token tPrev1 = tokens.get(i - 1);
                                        Token tPrev2 = tokens.get(i - 2);
                                        if (tPrev1.type == TokenType.IDENTIFIER &&
                                            tPrev2.type != TokenType.INT && tPrev2.type != TokenType.FLOAT && tPrev2.type != TokenType.STRING) {
                                            executavelAntes = true;
                                            break;
                                        }
                                    }
                                    if (t.type == TokenType.LPAREN && i - 1 >= 0) {
                                        Token tPrev = tokens.get(i - 1);
                                        if (tPrev.type == TokenType.IDENTIFIER) {
                                            boolean isDefOrControl = false;
                                            if (i - 2 >= 0) {
                                                Token tPrev2 = tokens.get(i - 2);
                                                if (tPrev2.type == TokenType.FUNC || tPrev2.type == TokenType.PROC ||
                                                    tPrev2.type == TokenType.IF || tPrev2.type == TokenType.WHILE ||
                                                    tPrev2.type == TokenType.FOR || tPrev2.type == TokenType.SWITCH ||
                                                    tPrev2.type == TokenType.INT || tPrev2.type == TokenType.FLOAT || tPrev2.type == TokenType.STRING) {
                                                    isDefOrControl = true;
                                                }
                                            }
                                            if (!isDefOrControl) {
                                                executavelAntes = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (executavelAntes) {
                                String msg = "Erro na linha " + linha + ": Declarações de variáveis devem vir no início do escopo, antes de qualquer comando executável.";
                                System.out.println(msg);
                                erros.add(msg);
                            }
                        }
                    }
                    if (prodIdx == 37) {

                        boolean inFunc = false;
                        for (int i = idx - 1; i >= 0; i--) {
                            Token t = tokens.get(i);
                            if (t.type == TokenType.FUNC) {
                                inFunc = true;
                                break;
                            }
                            if (t.type == TokenType.PROC) {
                                inFunc = false;
                                break;
                            }
                        }
                        if (inFunc) {
                            String msg = "Erro na linha " + linha + ": Expressão de retorno esperada após a palavra-chave 'return'.";
                            System.out.println(msg);
                            erros.add(msg);
                        }
                    }
                    if (prodIdx == 38) {
                        if (idx + 5 < tokens.size() &&
                            tokens.get(idx + 4).type == TokenType.LBRACE &&
                            tokens.get(idx + 5).type == TokenType.RBRACE) {
                            String msg = "Erro na linha " + tokens.get(idx + 4).line + ": Pelo menos um rótulo 'case' ou 'default' é esperado dentro do bloco 'switch'.";
                            System.out.println(msg);
                            erros.add(msg);
                        }
                    }
                    System.out.println("DESEMPILHA " + topo);
                    pilha.remove(pilha.size() - 1);
                    List<String> prod = PRODUCOES.get(prodIdx);
                    System.out.println("PRODUCAO: " + prod);
                    System.out.println(DESCRICAO_PRODUCAO.get(prodIdx));

                    List<StackItem> toPush = new ArrayList<>();
                    for (String simbolo : prod) {
                        CSTNode childNode = new CSTNode(simbolo);
                        itemTopo.node.children.add(childNode);
                        toPush.add(new StackItem(simbolo, childNode));
                    }


                    for (int i = toPush.size() - 1; i >= 0; i--) {
                        pilha.add(toPush.get(i));
                    }
                    System.out.println();
                } else {
                    if (SILENT_NON_TERMINALS.contains(topo)) {

                        pilha.remove(pilha.size() - 1);
                    } else {

                        if (erros.size() > 0 && (topo.equals("LG") || topo.equals("LISTA_COM")) &&
                            (tokenAtual.type == TokenType.SEMICOLON || tokenAtual.type == TokenType.RBRACE)) {
                            idx++;
                            continue;
                        }

                        if (idx != lastErrorIdx) {
                            String msg = checkSpecificError(topo, tokenAtual, idx, tokens, linha, pilha, erros);
                            if (msg == null) {
                                if (tokenAtual.type == TokenType.ELSE) {
                                    msg = "Erro na linha " + linha + ": Estrutura 'else' sem 'if' correspondente.";
                                } else {
                                    msg = "Erro na linha " + linha + ": " + getFriendlyExpected(topo) + ".";
                                }
                            }
                            if (msg != null && !msg.isEmpty()) {
                                System.out.println(msg);
                                erros.add(msg);
                            }
                            lastErrorIdx = idx;
                        }

                        if (topo.equals("LG") || topo.equals("LISTA_COM") || topo.equals("LISTA_CASES")) {
                            Set<String> syncSet = new HashSet<>();
                            if (topo.equals("LG")) {
                                syncSet.addAll(List.of("int", "float", "string", "identifier", "func", "proc", "if", "while", "for", "switch", "return", "break", "continue", "$"));
                            } else if (topo.equals("LISTA_COM")) {
                                syncSet.addAll(List.of("int", "float", "string", "identifier", "if", "while", "for", "switch", "return", "break", "continue", "rbrace", "$"));
                            } else {
                                syncSet.addAll(List.of("case", "default", "rbrace", "$"));
                            }

                            while (idx < tokens.size()) {
                                Token t = tokens.get(idx);
                                String tSym = getGrammarSymbol(t);
                                if (syncSet.contains(tSym)) {
                                    break;
                                }
                                idx++;
                            }
                            if (idx >= tokens.size() - 1) {
                                System.out.println("Sincronização atingiu o EOF. Removendo " + topo + " da pilha.");
                                pilha.remove(pilha.size() - 1);
                            }

                        } else {
                            Set<String> syncSet = new HashSet<>();
                            syncSet.add("semicolon");
                            syncSet.add("rbrace");
                            syncSet.add("$");


                            for (StackItem stackItem : pilha) {
                                String sym = stackItem.symbol;
                                if (!NAO_TERMINAIS.contains(sym)) {
                                    syncSet.add(sym);
                                }
                            }

                            while (idx < tokens.size()) {
                                Token t = tokens.get(idx);
                                String tSym = getGrammarSymbol(t);
                                if (syncSet.contains(tSym)) {
                                    break;
                                }
                                idx++;
                            }


                            pilha.remove(pilha.size() - 1);
                        }
                    }
                }
            } else {
                if (idx != lastErrorIdx) {
                    String msg = checkSpecificError(topo, tokenAtual, idx, tokens, linha, pilha, erros);
                    if (msg == null) {
                        if (topo.equals("semicolon")) {

                            boolean isForHeader = false;
                            if (pilha.size() >= 6) {
                                if (pilha.get(pilha.size() - 4).symbol.equals("ATRIB_S") &&
                                    pilha.get(pilha.size() - 5).symbol.equals("rparen") &&
                                    pilha.get(pilha.size() - 6).symbol.equals("BLOCO")) {
                                    isForHeader = true;
                                }
                            }
                            if (pilha.size() >= 4) {
                                if (pilha.get(pilha.size() - 2).symbol.equals("ATRIB_S") &&
                                    pilha.get(pilha.size() - 3).symbol.equals("rparen") &&
                                    pilha.get(pilha.size() - 4).symbol.equals("BLOCO")) {
                                    isForHeader = true;
                                }
                            }

                            if (isForHeader) {
                                msg = "Erro na linha " + linha + ": Cabeçalho do laço 'for' inválido. Esperadas três expressões separadas por ponto e vírgula.";
                            } else {
                                msg = "Erro na linha " + linha + ": Ponto e vírgula esperado no final da linha.";
                            }
                        } else if (topo.equals("lparen") || topo.equals("rparen")) {

                            String estruturaIniciadora = "";
                            for (int i = idx - 1; i >= 0; i--) {
                                Token t = tokens.get(i);
                                if (t.lexeme.equals(";") || t.lexeme.equals("{") || t.lexeme.equals("}")) {
                                    break;
                                }
                                if (t.type == TokenType.IF) {
                                    estruturaIniciadora = "if";
                                    break;
                                }
                                if (t.type == TokenType.WHILE) {
                                    estruturaIniciadora = "while";
                                    break;
                                }
                                if (t.type == TokenType.FUNC || t.type == TokenType.PROC) {
                                    estruturaIniciadora = "func";
                                    break;
                                }
                                if (t.type == TokenType.FOR) {
                                    estruturaIniciadora = "for";
                                    break;
                                }
                            }


                            boolean isFunctionCall = false;
                            if (topo.equals("rparen") && (tokenAtual.type == TokenType.IDENTIFIER || tokenAtual.type == TokenType.INT_LITERAL || tokenAtual.type == TokenType.FLOAT_LITERAL || tokenAtual.type == TokenType.STRING_LITERAL)) {
                                for (int i = idx - 1; i >= 0; i--) {
                                    Token t = tokens.get(i);
                                    if (t.lexeme.equals(";") || t.lexeme.equals("{") || t.lexeme.equals("}")) {
                                        break;
                                    }
                                    if (t.type == TokenType.LPAREN && i > 0 && tokens.get(i - 1).type == TokenType.IDENTIFIER) {
                                        String lex = tokens.get(i - 1).lexeme;
                                        if (!lex.equals("if") && !lex.equals("while") && !lex.equals("for") && !lex.equals("switch")) {
                                            isFunctionCall = true;
                                            break;
                                        }
                                    }
                                }
                            }

                            if (isFunctionCall) {
                                msg = "Erro na linha " + linha + ": Vírgula ',' esperada para separar os argumentos na chamada da função.";
                            } else if (estruturaIniciadora.equals("if")) {
                                msg = "Erro na linha " + linha + ": Parênteses () esperados após 'if'.";
                            } else if (estruturaIniciadora.equals("while")) {
                                msg = "Erro na linha " + linha + ": Parênteses () esperados após 'while'.";
                            } else if (estruturaIniciadora.equals("func")) {
                                msg = "Erro na linha " + linha + ": Parênteses () esperados na definição dos parâmetros da função.";
                            } else {
                                msg = "Erro na linha " + linha + ": esperado '" + getFriendlyExpected(topo) + "', encontrado '" + tokenAtual.lexeme + "'.";
                            }
                        } else if (topo.equals("rbrace") && tokenAtual.type == TokenType.EOF) {

                            msg = "Erro na linha " + linha + ": Fim de arquivo inesperado. Fecha chaves '}' esperado para encerrar o bloco.";
                        } else {
                            msg = "Erro na linha " + linha + ": esperado '" + getFriendlyExpected(topo) + "', encontrado '" + tokenAtual.lexeme + "'.";
                        }
                    }

                    if (msg != null && !msg.isEmpty()) {
                        System.out.println(msg);
                        erros.add(msg);
                    }
                    lastErrorIdx = idx;
                }
                pilha.remove(pilha.size() - 1);
            }
        }

        Token tokenFinal = (idx < tokens.size()) ? tokens.get(idx) : new Token(TokenType.EOF, "$", null, -1);
        String tipoFinal = getGrammarSymbol(tokenFinal);

        System.out.println("──────────────────────────────────────────────────");
        if (erros.size() > 0 || !tipoFinal.equals("$")) {
            System.out.println("PROGRAMA POSSUI ERROS SINTÁTICOS");
            for (String erro : erros) {
                System.out.println(erro);
            }
            if (!tipoFinal.equals("$")) {
                System.out.println("Erro: Sobraram tokens não consumidos ao fim da análise. Último token: '" + tokenFinal.lexeme + "'.");
            }
        } else {
            System.out.println("PROGRAMA SINTATICAMENTE CORRETO");


            try {
                ASTNode ast = toAST(root);
                if (ast != null) {
                    try (java.io.PrintWriter out = new java.io.PrintWriter(outputJsonPath)) {
                        out.println(ast.toJson(0));
                        System.out.println("AST JSON gerada com sucesso em: " + outputJsonPath);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao gerar/salvar a AST: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("──────────────────────────────────────────────────\n");
    }




    private static ASTNode toAST(CSTNode node) {
        if (node == null) return null;

        switch (node.name) {
            case "P":
                if (node.children.isEmpty()) return null;
                return toAST(node.children.get(0));

            case "LG": {
                List<ASTNode> bodyNodes = new ArrayList<>();
                CSTNode currentLG = node;
                while (currentLG != null && !currentLG.children.isEmpty()) {
                    ASTNode el = toAST(currentLG.children.get(0));
                    if (el != null) {
                        bodyNodes.add(el);
                    }
                    currentLG = currentLG.children.get(1);
                }
                return new ProgramNode(bodyNodes);
            }

            case "EL":
                if (node.children.isEmpty()) return null;
                return toAST(node.children.get(0));

            case "DECL": {
                CSTNode first = node.children.get(0);
                if (first.name.equals("TIPO")) {
                    String varType = first.children.get(0).name;
                    String name = node.children.get(1).lexeme;
                    ASTNode val = null;
                    if (node.children.size() > 2 && node.children.get(2).name.equals("DECL_REST")) {
                        CSTNode declRest = node.children.get(2);
                        if (!declRest.children.isEmpty() && declRest.children.get(0).name.equals("assign")) {
                            val = toAST(declRest.children.get(1));
                        }
                    }
                    return new VariableDeclarationNode(varType, name, val);
                } else {

                    String name = first.lexeme;
                    ASTNode val = toAST(node.children.get(2));
                    return new AssignmentNode(name, val);
                }
            }

            case "FUNC_DEF": {

                CSTNode funcRest = node.children.get(1);
                String type;
                String name;
                List<ParameterNode> params;
                ASTNode bodyNode;
                if (funcRest.children.get(0).name.equals("TIPO")) {
                    type = funcRest.children.get(0).children.get(0).name;
                    name = funcRest.children.get(1).lexeme;
                    params = flattenParams(funcRest.children.get(3));
                    bodyNode = toAST(funcRest.children.get(5));
                } else {
                    type = "void";
                    name = funcRest.children.get(0).lexeme;
                    params = flattenParams(funcRest.children.get(2));
                    bodyNode = toAST(funcRest.children.get(4));
                }
                return new FunctionDeclarationNode(type, name, params, bodyNode);
            }

            case "PROC_DEF": {

                String name = node.children.get(1).lexeme;
                List<ParameterNode> params = flattenParams(node.children.get(3));
                ASTNode bodyNode = toAST(node.children.get(5));
                return new ProcedureDeclarationNode(name, params, bodyNode);
            }

            case "BLOCO":

                return toAST(node.children.get(1));

            case "LISTA_COM": {
                List<ASTNode> stmts = new ArrayList<>();
                CSTNode currentLC = node;
                while (currentLC != null && !currentLC.children.isEmpty()) {
                    ASTNode com = toAST(currentLC.children.get(0));
                    if (com != null) {
                        stmts.add(com);
                    }
                    currentLC = currentLC.children.get(1);
                }
                return new BlockNode(stmts);
            }

            case "COM": {
                CSTNode first = node.children.get(0);
                if (first.name.equals("DECL") || first.name.equals("IF_S") ||
                    first.name.equals("WHILE_S") || first.name.equals("FOR_S") ||
                    first.name.equals("SWITCH_S")) {
                    return toAST(first);
                } else if (first.name.equals("identifier")) {
                    String name = first.lexeme;
                    List<ASTNode> args = flattenArgs(node.children.get(2));
                    return new FunctionCallNode(name, args);
                } else if (first.name.equals("return")) {
                    CSTNode expOpc = node.children.get(1);
                    ASTNode arg = null;
                    if (!expOpc.children.isEmpty()) {
                        arg = toAST(expOpc.children.get(0));
                    }
                    return new ReturnStatementNode(arg);
                } else if (first.name.equals("break")) {
                    return new BreakStatementNode();
                } else if (first.name.equals("continue")) {
                    return new ContinueStatementNode();
                }
                return null;
            }

            case "IF_S": {

                ASTNode cond = toAST(node.children.get(2));
                ASTNode thenBranch = toAST(node.children.get(4));
                CSTNode elseOpc = node.children.get(5);
                ASTNode elseBranch = null;
                if (!elseOpc.children.isEmpty()) {
                    elseBranch = toAST(elseOpc.children.get(1));
                }
                return new IfStatementNode(cond, thenBranch, elseBranch);
            }

            case "WHILE_S": {

                ASTNode cond = toAST(node.children.get(2));
                ASTNode whileBody = toAST(node.children.get(4));
                return new WhileStatementNode(cond, whileBody);
            }

            case "FOR_S": {

                ASTNode init = toAST(node.children.get(2));
                ASTNode cond = toAST(node.children.get(4));
                ASTNode incr = toAST(node.children.get(6));
                ASTNode body = toAST(node.children.get(8));
                return new ForStatementNode(init, cond, incr, body);
            }

            case "FOR_INIT": {
                if (node.children.isEmpty()) return null;
                CSTNode first = node.children.get(0);
                if (first.name.equals("TIPO")) {
                    String varType = first.children.get(0).name;
                    String name = node.children.get(1).lexeme;
                    ASTNode val = null;
                    CSTNode declRest = node.children.get(2);
                    if (!declRest.children.isEmpty() && declRest.children.get(0).name.equals("assign")) {
                        val = toAST(declRest.children.get(1));
                    }
                    return new VariableDeclarationNode(varType, name, val);
                } else {
                    String name = first.lexeme;
                    ASTNode val = toAST(node.children.get(2));
                    return new AssignmentNode(name, val);
                }
            }

            case "ATRIB_S": {

                String name = node.children.get(0).lexeme;
                ASTNode val = toAST(node.children.get(2));
                return new AssignmentNode(name, val);
            }

            case "SWITCH_S": {

                String disc = node.children.get(2).lexeme;
                List<SwitchCaseNode> cases = flattenCases(node.children.get(5));
                CSTNode defOpc = node.children.get(6);
                ASTNode defaultBranch = null;
                if (!defOpc.children.isEmpty()) {
                    defaultBranch = toAST(defOpc.children.get(2));
                }
                return new SwitchStatementNode(disc, cases, defaultBranch);
            }

            case "CASE_I": {

                ASTNode test = toAST(node.children.get(1));
                List<ASTNode> consequent = new ArrayList<>();
                CSTNode currentLC = node.children.get(3);
                while (currentLC != null && !currentLC.children.isEmpty()) {
                    ASTNode com = toAST(currentLC.children.get(0));
                    if (com != null) {
                        consequent.add(com);
                    }
                    currentLC = currentLC.children.get(1);
                }
                return new SwitchCaseNode(test, consequent);
            }

            case "EXP":

                return buildBinaryExpression(node.children.get(0), node.children.get(1));

            case "E_AND":

                return buildBinaryExpression(node.children.get(0), node.children.get(1));

            case "E_COMP":

                return buildBinaryExpression(node.children.get(0), node.children.get(1));

            case "E_ADD":

                return buildBinaryExpression(node.children.get(0), node.children.get(1));

            case "E_MULT":

                return buildBinaryExpression(node.children.get(0), node.children.get(1));

            case "E_UN":
                if (node.children.get(0).name.equals("not") || node.children.get(0).name.equals("minus")) {
                    String op = node.children.get(0).name;
                    ASTNode arg = toAST(node.children.get(1));
                    return new UnaryExpressionNode(op, arg);
                } else {
                    return toAST(node.children.get(0));
                }

            case "FINAL": {
                CSTNode first = node.children.get(0);
                if (first.name.equals("lparen")) {
                    return toAST(node.children.get(1));
                } else if (first.name.equals("identifier")) {
                    String name = first.lexeme;
                    CSTNode finalL = node.children.get(1);
                    if (finalL.children.isEmpty()) {
                        return new IdentifierNode(name);
                    } else {
                        List<ASTNode> args = flattenArgs(finalL.children.get(1));
                        return new FunctionCallNode(name, args);
                    }
                } else {
                    return toAST(first);
                }
            }

            case "LITERAL": {
                CSTNode lit = node.children.get(0);
                return new LiteralNode(lit.name, lit.lexeme);
            }

            default:
                return null;
        }
    }

    private static List<ParameterNode> flattenParams(CSTNode paramsNode) {
        List<ParameterNode> params = new ArrayList<>();
        if (paramsNode.children.isEmpty()) {
            return params;
        }
        params.add(toParam(paramsNode.children.get(0)));
        CSTNode current = paramsNode.children.get(1);
        while (current != null && !current.children.isEmpty()) {
            params.add(toParam(current.children.get(1)));
            current = current.children.get(2);
        }
        return params;
    }

    private static ParameterNode toParam(CSTNode paramNode) {
        String type = paramNode.children.get(0).children.get(0).name;
        String name = paramNode.children.get(1).lexeme;
        return new ParameterNode(type, name);
    }

    private static List<ASTNode> flattenArgs(CSTNode argsNode) {
        List<ASTNode> args = new ArrayList<>();
        if (argsNode.children.isEmpty()) {
            return args;
        }
        args.add(toAST(argsNode.children.get(0)));
        CSTNode current = argsNode.children.get(1);
        while (current != null && !current.children.isEmpty()) {
            args.add(toAST(current.children.get(1)));
            current = current.children.get(2);
        }
        return args;
    }

    private static List<SwitchCaseNode> flattenCases(CSTNode casesNode) {
        List<SwitchCaseNode> cases = new ArrayList<>();
        CSTNode current = casesNode;
        while (current != null && !current.children.isEmpty()) {
            CSTNode caseI = current.children.get(0);
            ASTNode test = toAST(caseI.children.get(1));

            List<ASTNode> consequent = new ArrayList<>();
            CSTNode currentLC = caseI.children.get(3);
            while (currentLC != null && !currentLC.children.isEmpty()) {
                ASTNode com = toAST(currentLC.children.get(0));
                if (com != null) {
                    consequent.add(com);
                }
                currentLC = currentLC.children.get(1);
            }

            cases.add(new SwitchCaseNode(test, consequent));
            current = current.children.get(1);
        }
        return cases;
    }

    private static ASTNode buildBinaryExpression(CSTNode base, CSTNode tail) {
        ASTNode left = toAST(base);
        CSTNode currentTail = tail;
        while (currentTail != null && !currentTail.children.isEmpty()) {
            CSTNode opNode = currentTail.children.get(0);
            String operator = opNode.name;
            if (operator.equals("OP_REL")) {
                operator = opNode.children.get(0).name;
            }
            ASTNode right = toAST(currentTail.children.get(1));
            left = new BinaryExpressionNode(operator, left, right);
            currentTail = currentTail.children.get(2);
        }
        return left;
    }
}