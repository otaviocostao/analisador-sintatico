import java.util.List;

public abstract class ASTNode {
    public abstract String toJson(int indent);
    
    protected String getIndent(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }
    
    protected String escape(String str) {
        if (str == null) return "null";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}

class ProgramNode extends ASTNode {
    public final List<ASTNode> body;
    
    public ProgramNode(List<ASTNode> body) {
        this.body = body;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"Program\",\n");
        sb.append(subInd).append("\"body\": [\n");
        for (int i = 0; i < body.size(); i++) {
            sb.append(body.get(i).toJson(indent + 2));
            if (i < body.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append(subInd).append("]\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}

class VariableDeclarationNode extends ASTNode {
    public final String varType;
    public final String name;
    public final ASTNode value;
    
    public VariableDeclarationNode(String varType, String name, ASTNode value) {
        this.varType = varType;
        this.name = name;
        this.value = value;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"VariableDeclaration\",\n");
        sb.append(subInd).append("\"varType\": \"").append(escape(varType)).append("\",\n");
        sb.append(subInd).append("\"name\": \"").append(escape(name)).append("\",\n");
        sb.append(subInd).append("\"value\": ").append(value == null ? "null" : value.toJson(indent + 1)).append("\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}

class AssignmentNode extends ASTNode {
    public final String name;
    public final ASTNode value;
    
    public AssignmentNode(String name, ASTNode value) {
        this.name = name;
        this.value = value;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"Assignment\",\n");
        sb.append(subInd).append("\"name\": \"").append(escape(name)).append("\",\n");
        sb.append(subInd).append("\"value\": ").append(value.toJson(indent + 1)).append("\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}

class FunctionDeclarationNode extends ASTNode {
    public final String returnType;
    public final String name;
    public final List<ParameterNode> params;
    public final ASTNode body;
    
    public FunctionDeclarationNode(String returnType, String name, List<ParameterNode> params, ASTNode body) {
        this.returnType = returnType;
        this.name = name;
        this.params = params;
        this.body = body;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"FunctionDeclaration\",\n");
        sb.append(subInd).append("\"returnType\": \"").append(escape(returnType)).append("\",\n");
        sb.append(subInd).append("\"name\": \"").append(escape(name)).append("\",\n");
        sb.append(subInd).append("\"params\": [\n");
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).toJson(indent + 2));
            if (i < params.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append(subInd).append("],\n");
        sb.append(subInd).append("\"body\": ").append(body.toJson(indent + 1)).append("\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}

class ProcedureDeclarationNode extends ASTNode {
    public final String name;
    public final List<ParameterNode> params;
    public final ASTNode body;
    
    public ProcedureDeclarationNode(String name, List<ParameterNode> params, ASTNode body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"ProcedureDeclaration\",\n");
        sb.append(subInd).append("\"name\": \"").append(escape(name)).append("\",\n");
        sb.append(subInd).append("\"params\": [\n");
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).toJson(indent + 2));
            if (i < params.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append(subInd).append("],\n");
        sb.append(subInd).append("\"body\": ").append(body.toJson(indent + 1)).append("\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}

class ParameterNode extends ASTNode {
    public final String paramType;
    public final String name;
    
    public ParameterNode(String paramType, String name) {
        this.paramType = paramType;
        this.name = name;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"Parameter\",\n");
        sb.append(subInd).append("\"paramType\": \"").append(escape(paramType)).append("\",\n");
        sb.append(subInd).append("\"name\": \"").append(escape(name)).append("\"\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}

class BlockNode extends ASTNode {
    public final List<ASTNode> statements;
    
    public BlockNode(List<ASTNode> statements) {
        this.statements = statements;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"Block\",\n");
        sb.append(subInd).append("\"statements\": [\n");
        for (int i = 0; i < statements.size(); i++) {
            sb.append(statements.get(i).toJson(indent + 2));
            if (i < statements.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append(subInd).append("]\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}

class IfStatementNode extends ASTNode {
    public final ASTNode condition;
    public final ASTNode thenBranch;
    public final ASTNode elseBranch;
    
    public IfStatementNode(ASTNode condition, ASTNode thenBranch, ASTNode elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"IfStatement\",\n");
        sb.append(subInd).append("\"condition\": ").append(condition.toJson(indent + 1)).append(",\n");
        sb.append(subInd).append("\"thenBranch\": ").append(thenBranch.toJson(indent + 1)).append(",\n");
        sb.append(subInd).append("\"elseBranch\": ").append(elseBranch == null ? "null" : elseBranch.toJson(indent + 1)).append("\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}

class WhileStatementNode extends ASTNode {
    public final ASTNode condition;
    public final ASTNode body;
    
    public WhileStatementNode(ASTNode condition, ASTNode body) {
        this.condition = condition;
        this.body = body;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"WhileStatement\",\n");
        sb.append(subInd).append("\"condition\": ").append(condition.toJson(indent + 1)).append(",\n");
        sb.append(subInd).append("\"body\": ").append(body.toJson(indent + 1)).append("\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}

class ForStatementNode extends ASTNode {
    public final ASTNode init;
    public final ASTNode condition;
    public final ASTNode increment;
    public final ASTNode body;
    
    public ForStatementNode(ASTNode init, ASTNode condition, ASTNode increment, ASTNode body) {
        this.init = init;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"ForStatement\",\n");
        sb.append(subInd).append("\"init\": ").append(init == null ? "null" : init.toJson(indent + 1)).append(",\n");
        sb.append(subInd).append("\"condition\": ").append(condition.toJson(indent + 1)).append(",\n");
        sb.append(subInd).append("\"increment\": ").append(increment.toJson(indent + 1)).append(",\n");
        sb.append(subInd).append("\"body\": ").append(body.toJson(indent + 1)).append("\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}

class SwitchStatementNode extends ASTNode {
    public final String discriminant;
    public final List<SwitchCaseNode> cases;
    public final ASTNode defaultBranch;
    
    public SwitchStatementNode(String discriminant, List<SwitchCaseNode> cases, ASTNode defaultBranch) {
        this.discriminant = discriminant;
        this.cases = cases;
        this.defaultBranch = defaultBranch;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"SwitchStatement\",\n");
        sb.append(subInd).append("\"discriminant\": \"").append(escape(discriminant)).append("\",\n");
        sb.append(subInd).append("\"cases\": [\n");
        for (int i = 0; i < cases.size(); i++) {
            sb.append(cases.get(i).toJson(indent + 2));
            if (i < cases.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append(subInd).append("],\n");
        sb.append(subInd).append("\"defaultBranch\": ").append(defaultBranch == null ? "null" : defaultBranch.toJson(indent + 1)).append("\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}

class SwitchCaseNode extends ASTNode {
    public final ASTNode test;
    public final List<ASTNode> consequent;
    
    public SwitchCaseNode(ASTNode test, List<ASTNode> consequent) {
        this.test = test;
        this.consequent = consequent;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"SwitchCase\",\n");
        sb.append(subInd).append("\"test\": ").append(test.toJson(indent + 1)).append(",\n");
        sb.append(subInd).append("\"consequent\": [\n");
        for (int i = 0; i < consequent.size(); i++) {
            sb.append(consequent.get(i).toJson(indent + 2));
            if (i < consequent.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append(subInd).append("]\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}

class ReturnStatementNode extends ASTNode {
    public final ASTNode argument;
    
    public ReturnStatementNode(ASTNode argument) {
        this.argument = argument;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"ReturnStatement\",\n");
        sb.append(subInd).append("\"argument\": ").append(argument == null ? "null" : argument.toJson(indent + 1)).append("\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}

class BreakStatementNode extends ASTNode {
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        return "{\n" + subInd + "\"type\": \"BreakStatement\"\n" + ind + "}";
    }
}

class ContinueStatementNode extends ASTNode {
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        return "{\n" + subInd + "\"type\": \"ContinueStatement\"\n" + ind + "}";
    }
}

class FunctionCallNode extends ASTNode {
    public final String name;
    public final List<ASTNode> arguments;
    
    public FunctionCallNode(String name, List<ASTNode> arguments) {
        this.name = name;
        this.arguments = arguments;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"FunctionCall\",\n");
        sb.append(subInd).append("\"name\": \"").append(escape(name)).append("\",\n");
        sb.append(subInd).append("\"arguments\": [\n");
        for (int i = 0; i < arguments.size(); i++) {
            sb.append(arguments.get(i).toJson(indent + 2));
            if (i < arguments.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append(subInd).append("]\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}

class BinaryExpressionNode extends ASTNode {
    public final String operator;
    public final ASTNode left;
    public final ASTNode right;
    
    public BinaryExpressionNode(String operator, ASTNode left, ASTNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"BinaryExpression\",\n");
        sb.append(subInd).append("\"operator\": \"").append(escape(operator)).append("\",\n");
        sb.append(subInd).append("\"left\": ").append(left.toJson(indent + 1)).append(",\n");
        sb.append(subInd).append("\"right\": ").append(right.toJson(indent + 1)).append("\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}

class UnaryExpressionNode extends ASTNode {
    public final String operator;
    public final ASTNode argument;
    
    public UnaryExpressionNode(String operator, ASTNode argument) {
        this.operator = operator;
        this.argument = argument;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"UnaryExpression\",\n");
        sb.append(subInd).append("\"operator\": \"").append(escape(operator)).append("\",\n");
        sb.append(subInd).append("\"argument\": ").append(argument.toJson(indent + 1)).append("\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}

class LiteralNode extends ASTNode {
    public final String valueType;
    public final String value;
    
    public LiteralNode(String valueType, String value) {
        this.valueType = valueType;
        this.value = value;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"Literal\",\n");
        sb.append(subInd).append("\"valueType\": \"").append(escape(valueType)).append("\",\n");
        sb.append(subInd).append("\"value\": \"").append(escape(value)).append("\"\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}

class IdentifierNode extends ASTNode {
    public final String name;
    
    public IdentifierNode(String name) {
        this.name = name;
    }
    
    @Override
    public String toJson(int indent) {
        String ind = getIndent(indent);
        String subInd = getIndent(indent + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(subInd).append("\"type\": \"Identifier\",\n");
        sb.append(subInd).append("\"name\": \"").append(escape(name)).append("\"\n");
        sb.append(ind).append("}");
        return sb.toString();
    }
}
