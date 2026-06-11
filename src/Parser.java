import java.util.*;

public class Parser {
    // -------------------------------------------------------------
    // Estruturas de Árvore Sintática Concreta (CST)
    // -------------------------------------------------------------
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

    // -------------------------------------------------------------
    // Não-terminais da gramática
    // -------------------------------------------------------------
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

    // -------------------------------------------------------------
    // Conjuntos Follow para Recuperação de Erros LL(1)
    // -------------------------------------------------------------
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

    // -------------------------------------------------------------
    // Produções: índice → lista de símbolos (terminais/não-terminais)
    // -------------------------------------------------------------
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
        
        // FOR_S com suporte a declaração de variável ou atribuição
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
        
        // Produção principal do FOR
        PRODUCOES.put(34, List.of("for", "lparen", "FOR_INIT", "semicolon", "EXP", "semicolon", "ATRIB_S", "rparen", "BLOCO"));
        
        PRODUCOES.put(35, List.of("identifier", "assign", "EXP"));
        PRODUCOES.put(36, List.of("EXP"));
        PRODUCOES.put(37, List.of());
        PRODUCOES.put(38, List.of("switch", "lparen", "identifier", "rparen", "lbrace", "LISTA_CASES", "DEF_OPC", "rbrace"));
        PRODUCOES.put(39, List.of("CASE_I", "LISTA_CASES"));
        PRODUCOES.put(40, List.of());
        
        // CASE_I simplificado para não conflitar com break na LISTA_COM
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
        
        // FOR_INIT e DECL_REST_FOR
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

    // -------------------------------------------------------------
    // Tabela LL(1): TABELA[não-terminal][terminal] = índice produção
    // -------------------------------------------------------------
    private static final Map<String, Map<String, Integer>> TABELA = new HashMap<>();

    private static void addTransicao(String naoTerminal, String terminal, int producaoIdx) {
        TABELA.computeIfAbsent(naoTerminal, k -> new HashMap<>()).put(terminal, producaoIdx);
    }

    static {
        // P
        for (String t : List.of("int", "float", "string", "identifier", "func", "proc", "$", "if", "while", "for", "switch", "return", "break", "continue")) {
            addTransicao("P", t, 0);
        }

        // LG
        for (String t : List.of("int", "float", "string", "identifier", "func", "proc", "if", "while", "for", "switch", "return", "break", "continue")) {
            addTransicao("LG", t, 1);
        }
        addTransicao("LG", "$", 2);

        // EL
        for (String t : List.of("int", "float", "string", "identifier", "if", "while", "for", "switch", "return", "break", "continue")) {
            addTransicao("EL", t, 3);
        }
        addTransicao("EL", "func", 4);
        addTransicao("EL", "proc", 5);

        // DECL
        for (String t : List.of("int", "float", "string")) {
            addTransicao("DECL", t, 6);
        }
        addTransicao("DECL", "identifier", 7);

        // DECL_REST
        addTransicao("DECL_REST", "semicolon", 83);
        addTransicao("DECL_REST", "assign", 84);

        // TIPO
        addTransicao("TIPO", "int", 8);
        addTransicao("TIPO", "float", 9);
        addTransicao("TIPO", "string", 10);

        // FUNC_DEF
        addTransicao("FUNC_DEF", "func", 11);

        // FUNC_REST
        for (String t : List.of("int", "float", "string")) {
            addTransicao("FUNC_REST", t, 85);
        }
        addTransicao("FUNC_REST", "identifier", 86);

        // PROC_DEF
        addTransicao("PROC_DEF", "proc", 12);

        // PARAMS
        for (String t : List.of("int", "float", "string")) {
            addTransicao("PARAMS", t, 13);
        }
        addTransicao("PARAMS", "rparen", 14);

        // PARAMS_L
        addTransicao("PARAMS_L", "comma", 15);
        addTransicao("PARAMS_L", "rparen", 16);

        // PARAM
        for (String t : List.of("int", "float", "string")) {
            addTransicao("PARAM", t, 17);
        }

        // BLOCO
        addTransicao("BLOCO", "lbrace", 18);

        // LISTA_COM
        for (String t : List.of("int", "float", "string", "identifier", "if", "while", "for", "switch", "return", "break", "continue")) {
            addTransicao("LISTA_COM", t, 19);
        }
        addTransicao("LISTA_COM", "rbrace", 20);
        addTransicao("LISTA_COM", "case", 20);
        addTransicao("LISTA_COM", "default", 20);

        // COM
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

        // IF_S
        addTransicao("IF_S", "if", 30);

        // ELSE_OPC
        addTransicao("ELSE_OPC", "else", 31);
        for (String t : List.of("int", "float", "string", "identifier", "if", "while", "for", "switch", "return", "break", "continue", "rbrace")) {
            addTransicao("ELSE_OPC", t, 32);
        }

        // WHILE_S
        addTransicao("WHILE_S", "while", 33);

        // FOR_S
        addTransicao("FOR_S", "for", 34);

        // FOR_INIT
        for (String t : List.of("int", "float", "string")) {
            addTransicao("FOR_INIT", t, 87);
        }
        addTransicao("FOR_INIT", "identifier", 88);
        addTransicao("FOR_INIT", "semicolon", 89);

        // DECL_REST_FOR
        addTransicao("DECL_REST_FOR", "assign", 90);
        addTransicao("DECL_REST_FOR", "semicolon", 91);

        // ATRIB_S
        addTransicao("ATRIB_S", "identifier", 35);

        // EXP_OPC
        for (String t : List.of("not", "minus", "lparen", "identifier", "int_literal", "float_literal", "string_literal")) {
            addTransicao("EXP_OPC", t, 36);
        }
        addTransicao("EXP_OPC", "semicolon", 37);

        // SWITCH_S
        addTransicao("SWITCH_S", "switch", 38);

        // LISTA_CASES
        addTransicao("LISTA_CASES", "case", 39);
        for (String t : List.of("default", "rbrace")) {
            addTransicao("LISTA_CASES", t, 40);
        }

        // CASE_I
        addTransicao("CASE_I", "case", 41);

        // DEF_OPC
        addTransicao("DEF_OPC", "default", 42);
        addTransicao("DEF_OPC", "rbrace", 43);

        // EXP
        for (String t : List.of("not", "minus", "lparen", "identifier", "int_literal", "float_literal", "string_literal")) {
            addTransicao("EXP", t, 44);
        }

        // EXP_L
        addTransicao("EXP_L", "or", 45);
        for (String t : List.of("semicolon", "rparen", "comma", "colon")) {
            addTransicao("EXP_L", t, 46);
        }

        // E_AND
        for (String t : List.of("not", "minus", "lparen", "identifier", "int_literal", "float_literal", "string_literal")) {
            addTransicao("E_AND", t, 47);
        }

        // E_AND_L
        addTransicao("E_AND_L", "and", 48);
        for (String t : List.of("or", "semicolon", "rparen", "comma", "colon")) {
            addTransicao("E_AND_L", t, 49);
        }

        // E_COMP
        for (String t : List.of("not", "minus", "lparen", "identifier", "int_literal", "float_literal", "string_literal")) {
            addTransicao("E_COMP", t, 50);
        }

        // E_COMP_L
        for (String t : List.of("greater", "less", "greater_equal", "less_equal", "equal_equal", "not_equal")) {
            addTransicao("E_COMP_L", t, 51);
        }
        for (String t : List.of("and", "or", "semicolon", "rparen", "comma", "colon")) {
            addTransicao("E_COMP_L", t, 52);
        }

        // OP_REL
        addTransicao("OP_REL", "greater", 53);
        addTransicao("OP_REL", "less", 54);
        addTransicao("OP_REL", "greater_equal", 55);
        addTransicao("OP_REL", "less_equal", 56);
        addTransicao("OP_REL", "equal_equal", 57);
        addTransicao("OP_REL", "not_equal", 58);

        // E_ADD
        for (String t : List.of("not", "minus", "lparen", "identifier", "int_literal", "float_literal", "string_literal")) {
            addTransicao("E_ADD", t, 59);
        }

        // E_ADD_L
        addTransicao("E_ADD_L", "plus", 60);
        addTransicao("E_ADD_L", "minus", 61);
        for (String t : List.of("greater", "less", "greater_equal", "less_equal", "equal_equal", "not_equal", "and", "or", "semicolon", "rparen", "comma", "colon")) {
            addTransicao("E_ADD_L", t, 62);
        }

        // E_MULT
        for (String t : List.of("not", "minus", "lparen", "identifier", "int_literal", "float_literal", "string_literal")) {
            addTransicao("E_MULT", t, 63);
        }

        // E_MULT_L
        addTransicao("E_MULT_L", "multiply", 64);
        addTransicao("E_MULT_L", "divide", 65);
        addTransicao("E_MULT_L", "modulo", 66);
        for (String t : List.of("plus", "minus", "greater", "less", "greater_equal", "less_equal", "equal_equal", "not_equal", "and", "or", "semicolon", "rparen", "comma", "colon")) {
            addTransicao("E_MULT_L", t, 67);
        }

        // E_UN
        addTransicao("E_UN", "not", 68);
        addTransicao("E_UN", "minus", 69);
        for (String t : List.of("lparen", "identifier", "int_literal", "float_literal", "string_literal")) {
            addTransicao("E_UN", t, 70);
        }

        // FINAL
        addTransicao("FINAL", "lparen", 71);
        addTransicao("FINAL", "identifier", 72);
        for (String t : List.of("int_literal", "float_literal", "string_literal")) {
            addTransicao("FINAL", t, 73);
        }

        // FINAL_L
        addTransicao("FINAL_L", "lparen", 74);
        for (String t : List.of("multiply", "divide", "modulo", "plus", "minus", "greater", "less", "greater_equal", "less_equal", "equal_equal", "not_equal", "and", "or", "semicolon", "rparen", "comma", "colon")) {
            addTransicao("FINAL_L", t, 75);
        }

        // ARGS
        for (String t : List.of("not", "minus", "lparen", "identifier", "int_literal", "float_literal", "string_literal")) {
            addTransicao("ARGS", t, 76);
        }
        addTransicao("ARGS", "rparen", 77);

        // ARGS_L
        addTransicao("ARGS_L", "comma", 78);
        addTransicao("ARGS_L", "rparen", 79);

        // LITERAL
        addTransicao("LITERAL", "int_literal", 80);
        addTransicao("LITERAL", "float_literal", 81);
        addTransicao("LITERAL", "string_literal", 82);
    }

    // -------------------------------------------------------------
    // Mapeamento de Token.type para Símbolo da Gramática
    // -------------------------------------------------------------
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
            default: return "símbolo '" + symbol + "' esperado";
        }
    }

    // -------------------------------------------------------------
    // Analisador Sintático LL(1) orientado por tabela com CST/AST
    // -------------------------------------------------------------
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
                Map<String, Integer> entradaTabela = TABELA.getOrDefault(topo, Map.of());
                System.out.println("ENTRADA_TABELA: " + entradaTabela);

                Integer prodIdx = null;
                // Resolução de conflito por lookahead (LL(2)) para COM e identifier
                if (topo.equals("COM") && tipo.equals("identifier")) {
                    if (idx + 1 < tokens.size() && tokens.get(idx + 1).type == TokenType.LPAREN) {
                        prodIdx = 26; // COM -> identifier lparen ARGS rparen semicolon
                    } else {
                        prodIdx = 21; // COM -> DECL
                    }
                } else {
                    prodIdx = entradaTabela.get(tipo);
                }

                if (prodIdx != null) {
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
                    
                    // Empilha na ordem reversa
                    for (int i = toPush.size() - 1; i >= 0; i--) {
                        pilha.add(toPush.get(i));
                    }
                    System.out.println();
                } else {
                    if (SILENT_NON_TERMINALS.contains(topo)) {
                        // Desempilha silenciosamente pois é uma variável interna de recursão
                        pilha.remove(pilha.size() - 1);
                    } else {
                        if (idx != lastErrorIdx) {
                            String msg = "Erro na linha " + linha + ": " + getFriendlyExpected(topo) + ".";
                            System.out.println(msg);
                            erros.add(msg);
                            lastErrorIdx = idx;
                        }
                        
                        // Error recovery: check follow set
                        Set<String> followSet = FOLLOW.get(topo);
                        if (tipo.equals("$") || (followSet != null && followSet.contains(tipo))) {
                            // Pop topo without advancing input stream
                            pilha.remove(pilha.size() - 1);
                        } else {
                            // Advance input stream
                            idx++;
                        }
                    }
                }
            } else {
                if (idx != lastErrorIdx) {
                    String msg = "Erro na linha " + linha + ": esperado '" + getFriendlyExpected(topo) + "', encontrado '" + tokenAtual.lexeme + "'.";
                    System.out.println(msg);
                    erros.add(msg);
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
            
            // Gerar AST a partir da CST
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

    // -------------------------------------------------------------
    // Tradução de CST para AST
    // -------------------------------------------------------------
    private static ASTNode toAST(CSTNode node) {
        if (node == null) return null;
        
        switch (node.name) {
            case "P":
                if (node.children.isEmpty()) return null;
                return toAST(node.children.get(0)); // LG
                
            case "LG": {
                List<ASTNode> bodyNodes = new ArrayList<>();
                CSTNode currentLG = node;
                while (currentLG != null && !currentLG.children.isEmpty()) {
                    ASTNode el = toAST(currentLG.children.get(0)); // EL
                    if (el != null) {
                        bodyNodes.add(el);
                    }
                    currentLG = currentLG.children.get(1); // LG
                }
                return new ProgramNode(bodyNodes);
            }
                
            case "EL":
                if (node.children.isEmpty()) return null;
                return toAST(node.children.get(0)); // COM or FUNC_DEF or PROC_DEF
                
            case "DECL": {
                CSTNode first = node.children.get(0);
                if (first.name.equals("TIPO")) {
                    String varType = first.children.get(0).name; // int, float, string
                    String name = node.children.get(1).lexeme;
                    ASTNode val = null;
                    if (node.children.size() > 2 && node.children.get(2).name.equals("DECL_REST")) {
                        CSTNode declRest = node.children.get(2);
                        if (!declRest.children.isEmpty() && declRest.children.get(0).name.equals("assign")) {
                            val = toAST(declRest.children.get(1)); // EXP
                        }
                    }
                    return new VariableDeclarationNode(varType, name, val);
                } else {
                    // identifier assign EXP semicolon
                    String name = first.lexeme;
                    ASTNode val = toAST(node.children.get(2)); // EXP
                    return new AssignmentNode(name, val);
                }
            }
            
            case "FUNC_DEF": {
                // func FUNC_REST
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
                    type = "void"; // tipo de retorno omitido
                    name = funcRest.children.get(0).lexeme;
                    params = flattenParams(funcRest.children.get(2));
                    bodyNode = toAST(funcRest.children.get(4));
                }
                return new FunctionDeclarationNode(type, name, params, bodyNode);
            }
            
            case "PROC_DEF": {
                // proc identifier lparen PARAMS rparen BLOCO
                String name = node.children.get(1).lexeme;
                List<ParameterNode> params = flattenParams(node.children.get(3));
                ASTNode bodyNode = toAST(node.children.get(5));
                return new ProcedureDeclarationNode(name, params, bodyNode);
            }
            
            case "BLOCO":
                // lbrace LISTA_COM rbrace
                return toAST(node.children.get(1)); // LISTA_COM
                
            case "LISTA_COM": {
                List<ASTNode> stmts = new ArrayList<>();
                CSTNode currentLC = node;
                while (currentLC != null && !currentLC.children.isEmpty()) {
                    ASTNode com = toAST(currentLC.children.get(0)); // COM
                    if (com != null) {
                        stmts.add(com);
                    }
                    currentLC = currentLC.children.get(1); // LISTA_COM
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
                    List<ASTNode> args = flattenArgs(node.children.get(2)); // ARGS
                    return new FunctionCallNode(name, args);
                } else if (first.name.equals("return")) {
                    CSTNode expOpc = node.children.get(1);
                    ASTNode arg = null;
                    if (!expOpc.children.isEmpty()) {
                        arg = toAST(expOpc.children.get(0)); // EXP
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
                // if lparen EXP rparen BLOCO ELSE_OPC
                ASTNode cond = toAST(node.children.get(2));
                ASTNode thenBranch = toAST(node.children.get(4));
                CSTNode elseOpc = node.children.get(5);
                ASTNode elseBranch = null;
                if (!elseOpc.children.isEmpty()) {
                    elseBranch = toAST(elseOpc.children.get(1)); // BLOCO do else
                }
                return new IfStatementNode(cond, thenBranch, elseBranch);
            }
            
            case "WHILE_S": {
                // while lparen EXP rparen BLOCO
                ASTNode cond = toAST(node.children.get(2));
                ASTNode whileBody = toAST(node.children.get(4));
                return new WhileStatementNode(cond, whileBody);
            }
            
            case "FOR_S": {
                // for lparen FOR_INIT semicolon EXP semicolon ATRIB_S rparen BLOCO
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
                        val = toAST(declRest.children.get(1)); // EXP
                    }
                    return new VariableDeclarationNode(varType, name, val);
                } else {
                    String name = first.lexeme;
                    ASTNode val = toAST(node.children.get(2)); // EXP
                    return new AssignmentNode(name, val);
                }
            }
            
            case "ATRIB_S": {
                // identifier assign EXP
                String name = node.children.get(0).lexeme;
                ASTNode val = toAST(node.children.get(2));
                return new AssignmentNode(name, val);
            }
            
            case "SWITCH_S": {
                // switch lparen identifier rparen lbrace LISTA_CASES DEF_OPC rbrace
                String disc = node.children.get(2).lexeme;
                List<SwitchCaseNode> cases = flattenCases(node.children.get(5));
                CSTNode defOpc = node.children.get(6);
                ASTNode defaultBranch = null;
                if (!defOpc.children.isEmpty()) {
                    defaultBranch = toAST(defOpc.children.get(2)); // LISTA_COM -> AST
                }
                return new SwitchStatementNode(disc, cases, defaultBranch);
            }

            case "CASE_I": {
                // case LITERAL colon LISTA_COM
                ASTNode test = toAST(node.children.get(1));
                List<ASTNode> consequent = new ArrayList<>();
                CSTNode currentLC = node.children.get(3); // LISTA_COM
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
                // E_AND EXP_L
                return buildBinaryExpression(node.children.get(0), node.children.get(1));
                
            case "E_AND":
                // E_COMP E_AND_L
                return buildBinaryExpression(node.children.get(0), node.children.get(1));
                
            case "E_COMP":
                // E_ADD E_COMP_L
                return buildBinaryExpression(node.children.get(0), node.children.get(1));
                
            case "E_ADD":
                // E_MULT E_ADD_L
                return buildBinaryExpression(node.children.get(0), node.children.get(1));
                
            case "E_MULT":
                // E_UN E_MULT_L
                return buildBinaryExpression(node.children.get(0), node.children.get(1));
                
            case "E_UN":
                if (node.children.get(0).name.equals("not") || node.children.get(0).name.equals("minus")) {
                    String op = node.children.get(0).name;
                    ASTNode arg = toAST(node.children.get(1)); // E_UN
                    return new UnaryExpressionNode(op, arg);
                } else {
                    return toAST(node.children.get(0)); // FINAL
                }
                
            case "FINAL": {
                CSTNode first = node.children.get(0);
                if (first.name.equals("lparen")) {
                    return toAST(node.children.get(1)); // EXP
                } else if (first.name.equals("identifier")) {
                    String name = first.lexeme;
                    CSTNode finalL = node.children.get(1);
                    if (finalL.children.isEmpty()) {
                        return new IdentifierNode(name);
                    } else {
                        List<ASTNode> args = flattenArgs(finalL.children.get(1)); // ARGS
                        return new FunctionCallNode(name, args);
                    }
                } else {
                    return toAST(first); // LITERAL
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
        CSTNode current = paramsNode.children.get(1); // PARAMS_L
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
        CSTNode current = argsNode.children.get(1); // ARGS_L
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
            CSTNode currentLC = caseI.children.get(3); // LISTA_COM
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
