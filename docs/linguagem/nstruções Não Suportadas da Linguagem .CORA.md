# Restrições Sintáticas e Construções Não Suportadas da Linguagem .CORA

**Equipe:** Álex de Souza Lima Rios, Otávio Costa de Oliveira, Rafael Figueiredo de Souza e Pedro Antonio Mota de Araújo.
**Documento:** Especificação de limitações da linguagem.
**Disciplina:** Compiladores — Engenharia da Computação.

---

## Resumo

Este documento cataloga as construções **rejeitadas** pelo analisador sintático da linguagem .CORA, organizadas por categoria gramatical. A catalogação fundamenta-se na especificação oficial, na gramática LL(1) implementada e na bateria de **60 casos de teste** documentados em [relatorio_60_casos.md](../erros_estruturais/relatorio_60_casos.md), todos validados com mensagens de erro contextualizadas.

---

## 1. Introdução: natureza das restrições

As limitações aqui descritas possuem duas origens complementares:

1. **Restrições intencionais** — simplificações pedagógicas que reduzem a complexidade da gramática (ex.: ausência de arrays, de operador `++`, de tipo `void`).
2. **Restrições estruturais** — regras de boa-formação que garantem programas analisáveis de forma determinística (ex.: balanceamento de delimitadores, ordenação de declarações em blocos).

Quando um programa viola qualquer regra deste documento, o analisador sintático interrompe o reconhecimento e emite mensagem de erro em português, indicando o token ou símbolo esperado e o contexto gramatical (estrutura de controle, subprograma, expressão, etc.).

---

## 2. Objetivos de aprendizagem

Ao estudar este documento, o aluno deve ser capaz de:

1. Diferenciar construções válidas em C/Java que **não** pertencem à .CORA;
2. Antecipar erros sintáticos comuns antes da compilação;
3. Relacionar mensagens de erro do analisador à regra gramatical violada;
4. Justificar, em termos de escopo e tipagem, por que determinados padrões são rejeitados.

---

## 3. Restrições sobre declarações e sistema de tipos


| #   | Restrição                                | Exemplo inválido          | Fundamentação técnica                                 |
| --- | ---------------------------------------- | ------------------------- | ----------------------------------------------------- |
| 1   | Declaração sem terminador `;`            | `int contador = 0`        | Produção `DECL` exige `;` como delimitador de comando |
| 2   | Tipo sem identificador                   | `int = 10;`               | `TIPO identifier` é forma obrigatória                 |
| 3   | Declaração simples sem `;`               | `int contador` (em bloco) | `DECL_REST → ε` ainda requer `;` final                |
| 4   | Declaração múltipla na mesma instrução   | `int x, y, z;`            | Gramática admite um identificador por declaração      |
| 5   | Tipos consecutivos para um identificador | `int float x = 5.0;`      | Apenas um modificador de tipo por variável            |
| 6   | Palavra reservada como identificador     | `float while = 1.5;`      | Conflito léxico-sintático com keyword                 |
| 7   | Tipo `void`                              | `void principal() { }`    | Conjunto de tipos limitado a `{int, float, string}`   |
| 8   | Arrays e indexação                       | `int notas[5];`           | Não-terminal de vetores ausente da gramática          |
| 9   | Declaração após comando executável       | `x = 1; float x;`         | Política de escopo: declarações precedem instruções   |


---

## 4. Restrições sobre expressões e atribuições


| #   | Restrição                         | Exemplo inválido        | Fundamentação técnica                                 |
| --- | --------------------------------- | ----------------------- | ----------------------------------------------------- |
| 10  | L-value inválido (literal à esq.) | `10 = x;`               | Apenas `identifier` pode receber atribuição           |
| 11  | Atribuição encadeada              | `x = y = 5;`            | Uma única atribuição por comando                      |
| 12  | Operando ausente                  | `int x = 5 + ;`         | Expressão incompleta na hierarquia de operadores      |
| 13  | Operadores binários consecutivos  | `int x = 5 * / 2;`      | Violação da forma indutiva de `E_MULT`, `E_ADD`, etc. |
| 14  | Operador `*` como unário          | `int x = * 5;`          | Gramática de `E_UN` não inclui `*` unário             |
| 15  | Incremento/decremento (`++`/`--`) | `for (...; i++)`        | Operadores não definidos no léxico                    |
| 16  | Parênteses desbalanceados         | `int x = (5 + 3;`       | Violação da produção `FINAL → ( EXP )`                |
| 17  | Negação pós-fixa                  | `int x = 5 !;`          | `!` deve ser operador unário prefixado (`E_UN`)       |
| 18  | Negação sem operando              | `if (!) { }`            | `E_UN → ! E_UN` exige subexpressão                    |
| 19  | Sequência inválida de operadores  | `x == = 5;`             | Conflito entre `OP_REL` e `assign`                    |
| 20  | Conectivos lógicos consecutivos   | `if (x &&               |                                                       |
| 21  | Atribuição de comando a variável  | `int x = if (...) { };` | Lado direito de `=` deve ser expressão, não comando   |
| 22  | Múltiplos valores em `return`     | `return a, b;`          | `return` associa-se a uma única `EXP`                 |


---

## 5. Restrições sobre estruturas de controle

### 5.1 Seleção: `if` / `else`


| Restrição                   | Exemplo inválido     | Regra violada                        |
| --------------------------- | -------------------- | ------------------------------------ |
| Condição sem parênteses     | `if x > 10 { }`      | `IF_S → if ( EXP ) BLOCO`            |
| Ramo sem bloco composto     | `if (x) x = 1;`      | `BLOCO → { LISTA_COM }`              |
| Condição vazia              | `if () { }`          | `EXP` não pode ser ε dentro de `( )` |
| Declaração na condição      | `if (int x = 5) { }` | `EXP` não admite `DECL`              |
| `else` sem `if` antecedente | `else { }`           | `ELSE_OPC` depende de `IF_S`         |
| `else` sem bloco            | `if (a) { } else`    | `ELSE_OPC → else BLOCO`              |


### 5.2 Repetição: `while` e `for`


| Restrição                       | Exemplo inválido                    | Regra violada                           |
| ------------------------------- | ----------------------------------- | --------------------------------------- |
| `while` sem corpo `{ }`         | `while (c)`                         | Corpo deve ser `BLOCO`                  |
| Parêntese de fechamento ausente | `while (x < 5 { }`                  | Balanceamento de `( )`                  |
| `for` sem `(` após keyword      | `for int i = 0; ...`                | `FOR_S → for ( ... )`                   |
| Cabeçalho incompleto            | `for (int i = 0; i < 10)`           | Três cláusulas separadas por `;`        |
| Separadores incorretos          | `for (int i = 0, i < 3, i = i + 1)` | Vírgula no lugar de `;` entre cláusulas |


### 5.3 Seleção múltipla: `switch`


| Restrição                | Exemplo inválido                | Regra violada                     |
| ------------------------ | ------------------------------- | --------------------------------- |
| Expressão sem parênteses | `switch x { }`                  | `switch ( identifier )`           |
| Corpo sem chaves         | `switch (x) case 1: break;`     | Delimitador `{ }` obrigatório     |
| `case` fora de `switch`  | `case 1:` em bloco comum        | `CASE_I` só sob `SWITCH_S`        |
| Rótulo com variável      | `case variavel:`                | `CASE_I → case LITERAL`           |
| Dois-pontos ausentes     | `case 1 break;`                 | `colon` terminal obrigatório      |
| Bloco vazio              | `switch (x) { }`                | Pelo menos um `case` ou `default` |
| Bloco não fechado (EOF)  | Arquivo termina antes de `}`    | Balanceamento de `{ }`            |
| Duplicidade de `default` | Dois `default:` no mesmo switch | Unicidade da cláusula default     |


### 5.4 Desvio de fluxo: `break` / `continue`


| Restrição              | Exemplo inválido   | Regra violada                          |
| ---------------------- | ------------------ | -------------------------------------- |
| Rótulo após `break`    | `break rotulo;`    | `COM → break ;` — sem identificador    |
| Rótulo após `continue` | `continue rotulo;` | `COM → continue ;` — sem identificador |


---

## 6. Restrições sobre subprogramas e escopo


| #   | Restrição                        | Exemplo inválido                | Fundamentação técnica                             |
| --- | -------------------------------- | ------------------------------- | ------------------------------------------------- |
| 23  | Função sem identificador         | `func (int a, int b) { }`       | `FUNC_REST` exige `identifier`                    |
| 24  | Procedimento anônimo             | `proc () { }`                   | `PROC_DEF → proc identifier (...)`                |
| 25  | Protótipo sem corpo              | `func f(int a);`                | Definição exige `BLOCO`, não apenas `;`           |
| 26  | Parâmetro sem tipo               | `func f(int a, b)`              | `PARAM → TIPO identifier`                         |
| 27  | Parâmetro sem identificador      | `func f(float, float)`          | Identificador formal obrigatório                  |
| 28  | Vírgula terminal na lista        | `proc p(float a, float b,) { }` | Elemento esperado após `,`                        |
| 29  | Vírgulas consecutivas            | `func f(float a,, float b)`     | Separador duplo inválido                          |
| 30  | Parâmetros fora de parênteses    | `func somar int a, int b { }`   | `lparen PARAMS rparen`                            |
| 31  | `return` no escopo global        | `return 1;`                     | `return` ∈ `COM`, não ∈ `EL` global para comandos |
| 32  | `return` sem expressão em `func` | `return;`                       | `EXP_OPC` exige expressão em funções              |
| 33  | Comando solto no escopo global   | `media = 8.0;` (global)         | Global: apenas `DECL`, `FUNC_DEF`, `PROC_DEF`     |
| 34  | Chamada sem parênteses           | `f a, b;`                       | `FINAL_L → ( ARGS )`                              |
| 35  | Argumentos sem separador `,`     | `f(a b)`                        | `ARGS_L → , EXP ARGS_L`                           |


---

## 7. Restrições estruturais e delimitadores


| #   | Restrição                | Exemplo inválido                 | Fundamentação técnica               |
| --- | ------------------------ | -------------------------------- | ----------------------------------- |
| 36  | Ponto e vírgula duplo    | `int x = 5;;`                    | Comando vazio após `;`              |
| 37  | Bloco anônimo solto      | `{ int x = 5; }` dentro de bloco | Blocos só em construtos gramaticais |
| 38  | Chave de fechamento órfã | `} }`                            | `}` sem `{` correspondente          |
| 39  | EOF com bloco aberto     | `proc p() { int x = 1;`          | Balanceamento `{ }` até EOF         |


---

## 8. Recursos de linguagens generalistas ausentes na .CORA

Por decisão de projeto, os seguintes mecanismos **não possuem produção gramatical** correspondente e, portanto, não devem ser empregados:


| Categoria             | Recursos ausentes                                        |
| --------------------- | -------------------------------------------------------- |
| Tipos estendidos      | `void`, `char`, `bool`, `double`, `long`, unsigned       |
| Operadores compostos  | `++`, `--`, `+=`, `-=`, `*=`, `/=`, `?:` (ternário)      |
| Estruturas de dados   | Arrays, ponteiros, structs, unions, enums, classes       |
| Pré-processamento     | `#include`, `#define`, macros                            |
| Genericidade          | Templates, type parameters                               |
| Exceções              | `try`, `catch`, `throw`, `finally`                       |
| Organização de código | Namespaces, módulos, packages                            |
| Literais especiais    | `true`, `false`, `'c'` (char), `null`                    |
| Controle avançado     | `goto`, labels em `break`/`continue`                     |
| Declaração moderna    | Inferência (`var`, `auto`), declaração múltipla estilo C |
| Programação funcional | Funções anônimas, closures, lambdas                      |


**Observação didática:** a ausência deliberada desses recursos permite concentrar o estudo nas fases léxica e sintática sobre uma gramática LL(1) de complexidade moderada, sem comprometer a expressividade procedural essencial.

---

## 9. Considerações finais

O conhecimento das restrições sintáticas da .CORA é condição necessária — embora não suficiente — para o desenvolvimento correto de analisadores e, posteriormente, para a extensão da linguagem em trabalhos futuros. Recomenda-se a leitura complementar da "Construções Sintaticamente Válidas da Linguagem .CORA" para contraste sistemático entre formas **admitidas** e **rejeitadas** pela gramática.

---

## Referências

1. Especificação da Linguagem .CORA — documento oficial do projeto (Compiladores, Engenharia da Computação).
2. Construções válidas: Construções Sintaticamente Válidas da Linguagem .CORA.
3. Implementação do analisador: `src/Parser.java`.

