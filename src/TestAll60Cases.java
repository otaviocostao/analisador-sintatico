import java.util.List;

public class TestAll60Cases {

    public static void main(String[] args) throws Exception {
        boolean strict = false;
        for (String arg : args) {
            if ("--strict".equals(arg)) {
                strict = true;
            }
        }

        List<GerarRelatorio60Casos.TestCase> cases = GerarRelatorio60Casos.buildCases();
        int ok = 0;
        int parcial = 0;
        int falhou = 0;

        System.out.println("=".repeat(72));
        System.out.println("TESTE DOS 60 CASOS DE ERRO SINTÁTICO (.CORA)");
        System.out.println("=".repeat(72));
        System.out.printf("%-6s %-8s %-40s %s%n", "Caso", "Status", "Nome", "Mensagem principal");
        System.out.println("-".repeat(72));

        for (GerarRelatorio60Casos.TestCase tc : cases) {
            String output = GerarRelatorio60Casos.runParse(tc.code);
            List<String> errors = GerarRelatorio60Casos.extractErrors(output);
            String status = GerarRelatorio60Casos.status(errors, tc.expectedSubstring);

            switch (status) {
                case "OK" -> ok++;
                case "PARCIAL" -> parcial++;
                default -> falhou++;
            }

            String mainMsg = errors.isEmpty() ? "(nenhum erro)" : errors.get(0);
            if (mainMsg.length() > 48) {
                mainMsg = mainMsg.substring(0, 45) + "...";
            }

            System.out.printf("%02d     %-8s %-40s %s%n", tc.id, status, tc.name, mainMsg);

            if ("FALHOU".equals(status)) {
                System.out.println("       Esperado (contém): " + tc.expectedSubstring);
                if (errors.isEmpty()) {
                    System.out.println("       Obtido: nenhum erro reportado");
                } else {
                    System.out.println("       Obtido (" + errors.size() + " erro(s)):");
                    for (int i = 0; i < errors.size(); i++) {
                        System.out.println("         " + (i + 1) + ". " + errors.get(i));
                    }
                }
            }
        }

        System.out.println("-".repeat(72));
        System.out.printf("Resumo: %d OK | %d PARCIAL | %d FALHOU | Total: %d%n", ok, parcial, falhou, cases.size());
        System.out.println("=".repeat(72));

        if (strict) {
            if (falhou > 0 || parcial > 0) {
                System.exit(1);
            }
        } else if (falhou > 0) {
            System.exit(1);
        }
    }
}
