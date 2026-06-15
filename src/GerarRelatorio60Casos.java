import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class GerarRelatorio60Casos {
    static class TestCase {
        final int id;
        final String name;
        final String code;
        final String expectedSubstring;

        TestCase(int id, String name, String code, String expectedSubstring) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.expectedSubstring = expectedSubstring;
        }
    }

    static List<String> extractErrors(String output) {
        Matcher m = Pattern.compile("Erro na linha \\d+: ([^\r\n]+)").matcher(output);
        LinkedHashSet<String> msgs = new LinkedHashSet<>();
        while (m.find()) {
            msgs.add(m.group(1).trim());
        }
        return new ArrayList<>(msgs);
    }

    static String runParse(String code) throws Exception {
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.scan(code);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(baos, true, "UTF-8"));
        Parser.parse(tokens, "ast_test.json");
        System.setOut(old);
        return baos.toString("UTF-8");
    }

    static List<TestCase> buildCases() {
        List<TestCase> cases = new ArrayList<>();
        cases.add(new TestCase(1, "Ausência de Ponto e Vírgula (Declaração)", "int contador = 0\n", "Ponto e vírgula"));
        cases.add(new TestCase(2, "Ausência de Parênteses em Condicional (if)", "if x > 10 && y == 0 {\n}\n", "Parênteses () esperados após 'if'"));
        cases.add(new TestCase(3, "Ausência de Chaves em Condicional", "proc principal() {\n    if (x > 10)\n        contador = contador + 1;\n}\n", "Abre chaves '{'"));
        cases.add(new TestCase(4, "Malformação da Estrutura do Loop for", "proc principal() {\n    for (int i = 0; i < 10) {\n    }\n}\n", "Cabeçalho do laço 'for' inválido"));
        cases.add(new TestCase(5, "Malformação na Definição de Parâmetros de Funções", "func somar int a, int b {\n    return a + b;\n}\n", "Parênteses () esperados na definição dos parâmetros da função"));
        cases.add(new TestCase(6, "Operador Aritmético Órfão", "int x = 5 + ;\n", "Expressão ou valor esperado"));
        cases.add(new TestCase(7, "Parênteses Não Balanceados", "int x = (5 + 3;\n", "parênteses ')'"));
        cases.add(new TestCase(8, "Declaração Sem Identificador", "int = 10;\n", "identificador"));
        cases.add(new TestCase(9, "Estrutura else Isolada", "else {\n    string status = \"Reprovado\";\n}\n", "Estrutura 'else' sem 'if' correspondente"));
        cases.add(new TestCase(10, "Falta de Vírgula em Argumentos", "proc principal() {\n    float media = calcularMedia(nota1 nota2);\n}\n", "Vírgula ',' esperada para separar os argumentos"));
        cases.add(new TestCase(11, "Chave Não Fechada ao Fim do Arquivo", "proc principal() {\n    float nota1 = 7.5;\n", "Fim de arquivo inesperado"));
        cases.add(new TestCase(12, "Atribuição com Literal no Lado Esquerdo", "proc principal() {\n    10 = x;\n}\n", "Identificador esperado à esquerda do operador de atribuição"));
        cases.add(new TestCase(13, "Dois Operadores Binários Consecutivos", "int x = 5 * / 2;\n", "Expressão ou valor esperado"));
        cases.add(new TestCase(14, "Ausência de Dois Pontos no case", "proc principal() {\n    switch (opcao) {\n        case 1\n            break;\n    }\n}\n", "dois pontos ':'"));
        cases.add(new TestCase(15, "Definição de Função Sem Nome", "func (int a, int b) {\n    return a + b;\n}\n", "Nome da função esperado após 'func'"));
        cases.add(new TestCase(16, "Ausência de Chaves na Estrutura switch", "proc principal() {\n    switch (opcao)\n        case 1:\n            break;\n}\n", "abre chaves '{'"));
        cases.add(new TestCase(17, "Operador de Incremento Unário Não Suportado", "proc principal() {\n    for (int i = 0; i < 10; i++) {\n    }\n}\n", "Expressão ou valor esperado"));
        cases.add(new TestCase(18, "Declaração Múltipla de Variáveis", "int x, y, z;\n", "Ponto e vírgula ';' esperado após o identificador 'x'"));
        cases.add(new TestCase(19, "Condição Vazia em Estruturas de Controle", "proc principal() {\n    if () {\n    }\n}\n", "Expressão condicional esperada dentro dos parênteses do 'if'"));
        cases.add(new TestCase(20, "Omissão de Tipo nos Parâmetros", "func somar(int a, b) {\n    return a + b;\n}\n", "Tipo de dado esperado para o parâmetro 'b'"));
        cases.add(new TestCase(21, "Vírgula Sobressalente na Lista de Parâmetros", "proc principal(float n1, float n2,) {\n}\n", "Identificador ou definição de parâmetro esperada após a vírgula"));
        cases.add(new TestCase(22, "Declaração de Variável Dentro da Condição", "proc principal() {\n    if (int x = 5) {\n    }\n}\n", "declarações de variáveis não são permitidas na condição do 'if'"));
        cases.add(new TestCase(23, "Palavra-Chave Reservada como Identificador", "float while = 1.5;\n", "A palavra 'while' é reservada"));
        cases.add(new TestCase(24, "Declaração de Função sem Corpo", "func somar(int a, int b);\n", "Abre chaves '{' esperado para iniciar o corpo da função"));
        cases.add(new TestCase(25, "Comando return sem Expressão", "func calcularMedia(float n1, float n2) {\n    return;\n}\n", "Expressão de retorno esperada após a palavra-chave 'return'"));
        cases.add(new TestCase(26, "Comando Solto no Escopo Global", "func calcularMedia(float n1, float n2) {\n    return (n1 + n2) / 2;\n}\n\nmedia = 8.0;\n\nproc principal() { }\n", "Declaração de variável, função ou procedimento esperada no escopo global"));
        cases.add(new TestCase(27, "switch sem Parênteses na Escolha", "proc principal() {\n    switch opcao {\n        case 1:\n            break;\n    }\n}\n", "Parênteses () esperados ao redor da expressão de escolha do 'switch'"));
        cases.add(new TestCase(28, "Operador ! Posicionado Incorretamente", "proc principal() {\n    int x = 5 !;\n}\n", "O operador '!' deve preceder a expressão"));
        cases.add(new TestCase(29, "Chave de Fechamento Órfã", "proc principal() {\n    float nota1 = 7.5;\n}\n}\n", "Token inesperado '}' fora de qualquer declaração de bloco"));
        cases.add(new TestCase(30, "Separadores Incorretos no Cabeçalho for", "proc principal() {\n    for (int i = 0, i < 3, i = i + 1) {\n    }\n}\n", "Ponto e vírgula ';' esperado para separar as seções do laço 'for'"));
        cases.add(new TestCase(31, "Tentativa de Uso de Vetores/Arrays", "int notas[5];\n", "Vetores não são suportados na linguagem"));
        cases.add(new TestCase(32, "Bloco Incompleto após else", "proc principal() {\n    if (media >= 7.0) {\n        string status = \"Aprovado\";\n    } else\n}\n", "Abre chaves '{' ou estrutura condicional 'if' esperada após a palavra-chave 'else'"));
        cases.add(new TestCase(33, "break com Identificador de Rótulo", "proc principal() {\n    break bloco_externo;\n}\n", "Ponto e vírgula ';' esperado imediatamente após a palavra-chave 'break'"));
        cases.add(new TestCase(34, "Atribuição de Estrutura de Fluxo a Variável", "proc principal() {\n    int x = if (y > 5) { 10; } else { 20; };\n}\n", "Expressão matemática, lógica ou constante esperada após o operador '='"));
        cases.add(new TestCase(35, "Bloco switch Totalmente Vazio", "proc principal() {\n    switch (opcao) {\n    }\n}\n", "Pelo menos um rótulo 'case' ou 'default' é esperado dentro do bloco 'switch'"));
        cases.add(new TestCase(36, "Declaração Após Comando Executável", "proc principal() {\n    nota1 = 7.5;\n    float nota1;\n}\n", "Declarações de variáveis devem vir no início do escopo"));
        cases.add(new TestCase(37, "Parâmetros Apenas com Tipos", "func calcularMedia(float, float) {\n}\n", "identificador"));
        cases.add(new TestCase(38, "Operador Multiplicativo Usado como Unário", "proc principal() {\n    int x = * 5;\n}\n", "Expressão ou valor esperado"));
        cases.add(new TestCase(39, "Uso Consecutivo do Separador Vírgula", "func calcularMedia(float n1,, float n2) {\n}\n", "Tipo de dado ou parâmetro esperado após a vírgula"));
        cases.add(new TestCase(40, "while sem Corpo Associado", "proc principal() {\n    while (condicao)\n}\n", "Abre chaves '{'"));
        cases.add(new TestCase(41, "case Fora de um Bloco switch", "proc principal() {\n    case 1:\n        float nota1 = 7.5;\n}\n", "Instrução 'case' inválida fora de um escopo de 'switch'"));
        cases.add(new TestCase(42, "Chamada de Função Sem Parênteses", "proc principal() {\n    float media = calcularMedia nota1, nota2;\n}\n", "Parênteses () esperados ao redor dos argumentos na chamada da função"));
        cases.add(new TestCase(43, "Operadores Lógicos Binários Consecutivos", "proc principal() {\n    if (x > 10 && || y == 0) {\n    }\n}\n", "Sintaxe de expressão condicional inválida. Operando esperado entre os operadores"));
        cases.add(new TestCase(44, "Atribuição Encadeada", "proc principal() {\n    x = y = 5;\n}\n", "Expressão inválida após o operador de atribuição '='"));
        cases.add(new TestCase(45, "Tipo void não Suportado", "void principal() {\n}\n", "Declaração de variável, função ou procedimento esperada no escopo global"));
        cases.add(new TestCase(46, "Bloco case com Identificador/Variável", "proc principal() {\n    switch (opcao) {\n        case variavel:\n            break;\n    }\n}\n", "Valor constante literal esperado após a palavra-chave 'case'"));
        cases.add(new TestCase(47, "Múltiplas Expressões no return", "func somar(int a, int b) {\n    return a, b;\n}\n", "Ponto e vírgula ';' esperado após a expressão de retorno"));
        cases.add(new TestCase(48, "return no Escopo Global", "int contador = 10;\nreturn contador;\n", "Instrução 'return' inválida fora de uma função ou procedimento"));
        cases.add(new TestCase(49, "Ponto e Vírgula Duplo", "proc principal() {\n    int x = 5;;\n}\n", "Instrução vazia ou inválida detectada"));
        cases.add(new TestCase(50, "for sem Parênteses de Abertura", "proc principal() {\n    for int i = 0; i < 10; i = i + 1 {\n    }\n}\n", "Parênteses '(' esperado após a palavra-chave 'for'"));
        cases.add(new TestCase(51, "while Sem Parêntese de Fechamento", "proc principal() {\n    while (condicao {\n    }\n}\n", "Fecha parênteses ')' esperado"));
        cases.add(new TestCase(52, "Sequência == =", "proc principal() {\n    x == = 5;\n}\n", "Sintaxe de expressão inválida"));
        cases.add(new TestCase(53, "Procedimento Anônimo", "proc () {\n}\n", "Identificador esperado após a palavra-chave 'proc'"));
        cases.add(new TestCase(54, "switch Sem Chave de Fechamento (EOF)", "proc principal() {\n    switch (opcao) {\n        case 1:\n            break;\n", "Fecha chaves '}' esperado para encerrar o bloco do 'switch'"));
        cases.add(new TestCase(55, "Duplicidade da Cláusula default", "proc principal() {\n    switch (opcao) {\n        default:\n            break;\n        default:\n            break;\n    }\n}\n", "Apenas uma cláusula 'default' é permitida dentro da estrutura 'switch'"));
        cases.add(new TestCase(56, "Declaração Sem ; e Sem Atribuição", "proc principal() {\n    int contador\n}\n", "Ponto e vírgula ';' esperado após o identificador 'contador'"));
        cases.add(new TestCase(57, "Negação Unária Sem Expressão", "proc principal() {\n    if (!) {\n    }\n}\n", "Expressão lógica esperada após o operador de negação unário '!'"));
        cases.add(new TestCase(58, "Múltiplos Tipos para Mesma Variável", "int float x = 5.0;\n", "A palavra 'float' é reservada"));
        cases.add(new TestCase(59, "Chaves Consecutivas Órfãs no Fim do Bloco", "proc principal() {\n}}\n", "Token inesperado '}' fora de qualquer declaração de bloco"));
        cases.add(new TestCase(60, "Bloco Anônimo Solto", "proc principal() {\n    {\n        int x = 5;\n    }\n}\n", "Bloco de chaves anônimo ou solto não é permitido"));
        return cases;
    }

    static String escapeMd(String s) {
        return s.replace("|", "\\|").replace("\n", "<br>");
    }

    static String status(List<String> errors, String expected) {
        boolean hasExpected = errors.stream().anyMatch(e -> e.contains(expected));
        if (!hasExpected) return "FALHOU";
        if (errors.size() == 1) return "OK";
        return "PARCIAL";
    }

    public static void main(String[] args) throws Exception {
        List<TestCase> cases = buildCases();
        StringBuilder md = new StringBuilder();
        int ok = 0, parcial = 0, falhou = 0;

        md.append("# Relatório de Testes — 60 Casos de Erro Sintático (.CORA)\n\n");
        md.append("Gerado automaticamente pelo analisador sintático em `Parser.java`.\n\n");
        md.append("## Legenda de status\n\n");
        md.append("| Status | Significado |\n");
        md.append("|--------|-------------|\n");
        md.append("| **OK** | Mensagem esperada correta, sem erros extras |\n");
        md.append("| **PARCIAL** | Mensagem principal correta, com erros secundários em cascata |\n");
        md.append("| **FALHOU** | Mensagem diferente da esperada ou ausente |\n\n");

        md.append("## Resumo\n\n");
        md.append("| Métrica | Valor |\n");
        md.append("|---------|-------|\n");

        StringBuilder details = new StringBuilder();
        details.append("## Detalhamento por caso\n\n");

        for (TestCase tc : cases) {
            String output = runParse(tc.code);
            List<String> errors = extractErrors(output);
            String st = status(errors, tc.expectedSubstring);
            switch (st) {
                case "OK" -> ok++;
                case "PARCIAL" -> parcial++;
                default -> falhou++;
            }

            details.append("### Caso ").append(String.format("%02d", tc.id)).append(": ").append(tc.name).append("\n\n");
            details.append("**Status:** ").append(st).append("\n\n");
            details.append("**Mensagem esperada (contém):** ").append(tc.expectedSubstring).append("\n\n");
            details.append("**Código de teste:**\n\n");
            details.append("```cora\n").append(tc.code).append("```\n\n");
            details.append("**Erros obtidos");
            if (errors.isEmpty()) {
                details.append(":** *(nenhum erro reportado)*\n\n");
            } else {
                details.append(" (").append(errors.size()).append("):**\n\n");
                for (int i = 0; i < errors.size(); i++) {
                    details.append(i + 1).append(". `Erro na linha N: ").append(errors.get(i)).append("`\n");
                }
                details.append("\n");
            }
            details.append("---\n\n");
        }

        md.append("| Total de casos | 60 |\n");
        md.append("| OK | ").append(ok).append(" |\n");
        md.append("| PARCIAL | ").append(parcial).append(" |\n");
        md.append("| FALHOU | ").append(falhou).append(" |\n\n");

        md.append("## Tabela resumida\n\n");
        md.append("| Caso | Nome | Status | Mensagem principal obtida |\n");
        md.append("|------|------|--------|---------------------------|\n");

        for (TestCase tc : cases) {
            String output = runParse(tc.code);
            List<String> errors = extractErrors(output);
            String st = status(errors, tc.expectedSubstring);
            String mainMsg = errors.isEmpty() ? "*(nenhum)*" : errors.get(0);
            md.append("| ").append(tc.id).append(" | ").append(escapeMd(tc.name)).append(" | **").append(st).append("** | ").append(escapeMd(mainMsg)).append(" |\n");
        }

        md.append("\n");
        md.append(details);

        Path out = Paths.get("docs/erros_estruturais/relatorio_60_casos.md");
        Files.writeString(out, md.toString(), StandardCharsets.UTF_8);
        System.out.println("Relatório gerado em: " + out.toAbsolutePath());
        System.out.printf("Resumo: %d OK, %d PARCIAL, %d FALHOU%n", ok, parcial, falhou);
    }
}
