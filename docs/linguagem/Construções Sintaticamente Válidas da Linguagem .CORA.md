# Construções Sintaticamente Válidas da Linguagem .CORA

**Equipe:** Álex de Souza Lima Rios, Otávio Costa de Oliveira, Rafael Figueiredo de Souza e Pedro Antonio Mota de Araújo.  
**Documento:** Especificação de capacidades da linguagem.  
**Disciplina:** Compiladores — Engenharia da Computação.

---

## Resumo

Este documento sistematiza as construções **permitidas** pela gramática da linguagem de programação **.CORA**, uma linguagem procedural fortemente tipada, concebida para fins didáticos no estudo das fases de análise léxica e sintática de compiladores. O texto complementa a especificação oficial do projeto e está alinhado ao analisador sintático implementado em `Parser.java`.

---

## 1. Introdução e objetivos de aprendizagem

A linguagem .CORA apresenta sintaxe inspirada em C e Java, porém com conjunto reduzido de recursos, de modo a facilitar a verificação formal da correção sintática. Ao final da leitura deste documento, o estudante deve ser capaz de:

1. Identificar os tipos primitivos e as palavras reservadas da linguagem;
2. Escrever declarações, expressões e comandos conforme a gramática;
3. Estruturar subprogramas (`func` e `proc`) e respeitar as regras de escopo;
4. Reconhecer os delimitadores obrigatórios em estruturas de controle e blocos compostos.

---

## 2. Modelo da linguagem

### 2.1 Paradigma e tipagem

A .CORA adota o paradigma **procedural** com **tipagem estática explícita**: toda variável deve ser declarada com um tipo conhecido em tempo de compilação (`int`, `float` ou `string`). Não há inferência de tipos nem polimorfismo.

### 2.2 Estrutura de um programa

Em nível sintático, um programa é uma sequência de **elementos globais** (`LG` na gramática), seguida pelo fim de arquivo (`EOF`). Cada elemento global pode ser:

- uma **declaração** de variável;
- uma **definição de função** (`func`);
- uma **definição de procedimento** (`proc`).

Comandos executáveis (atribuições, estruturas de controle, chamadas) **não** são admitidos diretamente no escopo global; devem residir no interior de blocos associados a subprogramas.

---

## 3. Tipos de dados primitivos

| Tipo     | Domínio (conceitual)              | Literal de exemplo | Palavra reservada |
|----------|-----------------------------------|--------------------|-------------------|
| `int`    | Inteiros de 32 bits com sinal     | `42`, `-7`         | `int`             |
| `float`  | Números em ponto flutuante        | `7.5`, `3.14`      | `float`           |
| `string` | Sequência de caracteres           | `"Aprovado"`       | `string`          |

**Observação didática:** literais booleanos (`true`/`false`) não existem na .CORA; condições lógicas são expressas por operadores de comparação e conectivos (`&&`, `||`, `!`).

---

## 4. Léxico: palavras reservadas

As seguintes palavras são **reservadas** e possuem significado fixo na gramática. Não podem ser reutilizadas como identificadores de variáveis, parâmetros ou subprogramas:

| Categoria        | Palavras reservadas                                              |
|------------------|------------------------------------------------------------------|
| Tipos            | `int`, `float`, `string`                                         |
| Seleção          | `if`, `else`, `switch`, `case`, `default`                       |
| Repetição        | `while`, `for`                                                   |
| Subprogramas     | `func`, `proc`, `return`                                         |
| Desvio de fluxo  | `break`, `continue`                                              |

---

## 5. Declarações, escopo e atribuição

### 5.1 Formas válidas de declaração

A gramática admite duas produções principais para declarações:

**Declaração simples** — tipo, identificador e terminador:

```cora
int contador;
float nota1;
string status;
```

**Declaração com inicialização** — tipo, identificador, atribuição de expressão e terminador:

```cora
int contador = 0;
float media = calcularMedia(nota1, nota2);
```

Toda declaração ou comando de atribuição deve encerrar-se com **ponto e vírgula** (`;`).

### 5.2 Regra de ordenação em blocos compostos

Dentro de um bloco delimitado por chaves (`BLOCO → { LISTA_COM }`), a gramática exige que **todas as declarações precedam os comandos executáveis**. Essa restrição modela a separação clássica entre região de declarações e região de instruções em linguagens procedurais estruturadas.

### 5.3 Atribuição

O operador `=` realiza atribição de expressão a um **L-value** válido — isto é, um identificador previamente declarado ou declarado na mesma instrução:

```cora
contador = contador + 1;
```

---

## 6. Expressões e operadores

### 6.1 Hierarquia e classes de operadores

As expressões são construídas recursivamente conforme a gramática de precedência implementada. Operadores admitidos:

| Classe            | Operadores              | Aridade   | Observação                              |
|-------------------|-------------------------|-----------|-----------------------------------------|
| Aritmética        | `+`, `-`, `*`, `/`, `%` | Binária   | Parênteses alteram precedência          |
| Relacional        | `>`, `<`, `>=`, `<=`, `==`, `!=` | Binária | Resultado usado em condições  |
| Lógica            | `&&`, `||`              | Binária   | Avaliação de condições compostas        |
| Unária lógica     | `!`                     | Unária    | Deve **preceder** o operando            |
| Atribuição        | `=`                     | Binária   | Apenas em comandos de atribuição/decl.  |

### 6.2 Operandos válidos

Um operando pode ser: literal numérico ou textual, identificador, expressão entre parênteses ou chamada de subprograma com argumentos.

---

## 7. Estruturas de controle de fluxo

### 7.1 Seleção binária: `if` / `else`

**Forma sintática obrigatória:**

```
if ( <expressão> ) { <comandos> } [ else { <comandos> } ]
```

Exemplo:

```cora
if (media >= 7.0 && media <= 10.0) {
    string status = "Aprovado";
} else {
    string status = "Reprovado";
}
```

A condição deve ser uma **expressão** delimitada por parênteses; os ramos devem ser **blocos compostos** delimitados por chaves.

### 7.2 Seleção múltipla: `switch` / `case` / `default`

**Forma sintática obrigatória:**

```
switch ( <identificador> ) { <rótulos> }
```

Exemplo:

```cora
switch (opcao) {
    case 1:
        break;
    default:
        break;
}
```

**Requisitos técnicos:**

- A expressão de seleção deve estar entre parênteses;
- O corpo deve estar entre chaves;
- Cada rótulo `case` associa-se a um **literal constante** (`int`, `float` ou `string`), seguido de dois pontos (`:`);
- Deve existir ao menos um rótulo `case` ou `default`;
- Apenas **uma** cláusula `default` é permitida por estrutura `switch`;
- O comando `break;` encerra a execução do case corrente.

### 7.3 Repetição condicionada: `while`

```cora
while (condicao) {
    // corpo do laço
}
```

A condição exige parênteses; o corpo exige bloco com chaves.

### 7.4 Repetição enumerada: `for`

O cabeçalho do laço `for` possui **três cláusulas separadas por ponto e vírgula**, todas entre parênteses:

```cora
for ( <inicialização> ; <condição> ; <atualização> ) { <corpo> }
```

Exemplo canônico:

```cora
for (int i = 0; i < 10; i = i + 1) {
    // corpo do laço
}
```

| Cláusula        | Conteúdo válido                                      |
|-----------------|------------------------------------------------------|
| Inicialização   | Declaração (`int i = 0`), atribuição ou ε (vazio)   |
| Condição        | Expressão booleana                                   |
| Atualização     | Atribuição (`i = i + 1`)                             |

**Observação didática:** operadores de incremento/decremento (`++`, `--`) **não** fazem parte da linguagem; a atualização deve ser expressa explicitamente por atribuição.

### 7.5 Interrupção de laços: `break` e `continue`

Ambos são comandos terminados por ponto e vírgula, **sem rótulo**:

```cora
break;
continue;
```

---

## 8. Modularização: funções e procedimentos

### 8.1 Função (`func`)

Subprograma que **obrigatoriamente retorna** um valor mediante o comando `return`:

```cora
func calcularMedia(float n1, float n2) {
    return (n1 + n2) / 2;
}
```

Forma alternativa com tipo de retorno explícito (conforme produção `FUNC_REST` da gramática):

```cora
func int soma(int a, int b) {
    return a + b;
}
```

### 8.2 Procedimento (`proc`)

Subprograma sem retorno de valor:

```cora
proc principal() {
    float nota1 = 7.5;
}
```

### 8.3 Lista de parâmetros formais

Cada parâmetro é descrito pelo par **tipo + identificador**, separados por vírgula quando houver mais de um:

```cora
func somar(int a, int b) {
    return a + b;
}
```

A ausência de parâmetros é denotada por parênteses vazios: `()`.

### 8.4 Chamada de subprograma

Toda invocação exige **parênteses** envolvendo a lista de argumentos, separados por vírgula:

```cora
float media = calcularMedia(nota1, nota2);
```

---

## 9. Comentários

A linguagem suporta documentação interna por dois mecanismos léxicos:

| Notação      | Classificação          | Escopo                          |
|--------------|------------------------|---------------------------------|
| `// ...`     | Comentário de linha    | Até o fim da linha              |
| `/* ... */`  | Comentário de bloco    | Entre os delimitadores indicados |

Comentários são ignorados pelo analisador léxico e não influenciam a estrutura sintática.

---

## 10. Exemplo integrador

O fragmento abaixo ilustra a composição válida dos elementos descritos neste documento:

```cora
func calcularMedia(float n1, float n2) {
    return (n1 + n2) / 2;
}

proc principal() {
    float nota1 = 7.5;
    float nota2 = 8.0;
    float media = calcularMedia(nota1, nota2);

    if (media >= 7.0 || media == 10.0) {
        string status = "Aprovado";
    } else {
        string status = "Reprovado";
    }

    for (int i = 0; i < 3; i = i + 1) {
        if (i == 1) {
            continue;
        }
    }
}
```

---

## 11. Síntese das regras estruturais

| Construto sintático   | Delimitadores e terminadores obrigatórios                    |
|-----------------------|--------------------------------------------------------------|
| Comando simples       | Encerramento com `;`                                         |
| Bloco composto        | `{` ... `}`                                                  |
| Condição (`if`, etc.) | `( )` em torno da expressão                                  |
| Laço `for`            | `( init ; cond ; atual )`                                    |
| Laço `while`          | `( cond )` + bloco `{ }`                                     |
| `switch`              | `( expr )` + `{ cases }`                                     |
| `case`                | `case literal :` ... `break ;`                               |
| Parâmetro formal      | `tipo identificador`                                         |
| `return` (em `func`)  | `return expressão ;`                                         |
| Escopo global         | Somente declarações, `func` e `proc`                         |

---

## Referências

1. Especificação da Linguagem .CORA — documento oficial do projeto (Compiladores, Engenharia da Computação).
4. Construções **proibidas**: Restrições Sintáticas e Construções Não Suportadas da Linguagem .CORA.
