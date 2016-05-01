
import java.util.ArrayList;
import java.util.List;

public class Parser {

    List<Token> tokens;
    Token lookahead;
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        nextToken();
    }

    //pega a lista de tokens do tokenizer
    public void parse() throws ParserException {
        Node root = main();

        SemanticAnalyser.postOrder(root);

    }

    public void nextToken() {
        lookahead = tokens.remove(0);
        System.out.printf(lookahead.c + " ");
    }

    // Non-terminals
    private Node main() throws ParserException {
        Node node = new Node("MAIN");

        node.children.add(programDecl(node));
        node.children.add(mainBlock(node));
        node.children.add(_period(node));

        return node;
    }

    private Node programDecl(Node parent) throws ParserException {
        Node node = new Node("PROGRAM_DECL");
        node.parent = parent;

        node.children.add(_program(node));
        node.children.add(_id(node));
        node.children.add(_sc(node));

        return node;
    }

    private Node mainBlock(Node parent) throws ParserException {
        Node node = new Node("MAIN_BLOCK");
        node.parent = parent;

        node.children.add(_begin(node));
        node.children.add(mainCode(node));
        node.children.add(_end(node));

        return node;
    }

    private Node mainCode(Node parent) throws ParserException {
        Node node = new Node("MAIN_CODE");
        node.parent = parent;

        switch (lookahead.type) {
            case VAR:
            case ID:
            case CALL:
            case PRINT:
            case WHILE:
            case IF:
                node.children.add(stmt(node));
                node.children.add(mainCode(node));
                break;
            case PROCEDURE:
                node.children.add(declProcStmt(node));
                node.children.add(mainCode(node));
                break;
            default:
            //nextToken();
        }

        return node;
    }

    private Node block(Node parent) throws ParserException {
        Node node = new Node("BLOCK");
        node.parent = parent;

        node.children.add(_begin(node));
        node.children.add(code(node));
        node.children.add(_end(node));

        return node;
    }

    private Node code(Node parent) throws ParserException {
        Node node = new Node("CODE");
        node.parent = parent;

        switch (lookahead.type) {
            case VAR:
            case ID:
            case CALL:
            case PRINT:
            case WHILE:
            case IF:
                node.children.add(stmt(node));
                node.children.add(code(node));
                break;
            default:
            //nextToken();
        }

        return node;
    }

    private Node stmt(Node parent) throws ParserException {
        Node node = new Node("STMT");
        node.parent = parent;

        switch (lookahead.type) {
            case VAR:
                node.children.add(declVarStmt(node));
                break;
            case ID:
                node.children.add(attrStmt(node));
                break;
            case CALL:
                node.children.add(callStmt(node));
                break;
            case PRINT:
                node.children.add(printStmt(node));
                break;
            case WHILE:
                node.children.add(whileStmt(node));
                break;
            case IF:
                node.children.add(ifStmt(node));
                break;
        }

        return node;
    }

    // statements
    private Node declProcStmt(Node parent) throws ParserException {
        Node node = new Node("DECL_PROC_STMT");
        node.parent = parent;

        node.children.add(_procedure(node));
        node.children.add(_id(node));
        node.children.add(_sc(node));
        node.children.add(block(node));

        return node;
    }

    private Node declVarStmt(Node parent) throws ParserException {
        Node node = new Node("DECL_VAR_STMT");
        node.parent = parent;

        node.children.add(_var(node));
        node.children.add(list(node));
        node.children.add(_sc(node));

        return node;
    }

    private Node list(Node parent) throws ParserException {
        Node node = new Node("LIST");
        node.parent = parent;

        node.children.add(_id(node));
        node.children.add(list_(node));

        return node;
    }

    private Node list_(Node parent) throws ParserException {
        Node node = new Node("LIST'");
        node.parent = parent;

        switch (lookahead.type) {
            case COMMA:
                node.children.add(_comma(node));
                node.children.add(list(node));
                break;
            case ATTR_OP:
                node.children.add(_attrOp(node));
                node.children.add(list__(node));
                break;
            default:
            //nextToken();
        }

        return node;
    }

    private Node list__(Node parent) throws ParserException {
        Node node = new Node("LIST''");
        node.parent = parent;

        node.children.add(intExp(node));
        node.children.add(list___(node));

        return node;
    }

    private Node list___(Node parent) throws ParserException {
        Node node = new Node("LIST'''");
        node.parent = parent;

        switch (lookahead.type) {
            case COMMA:
                node.children.add(_comma(node));
                node.children.add(list(node));
                break;
            default:
            //nextToken();
        }

        return node;
    }

    private Node attrStmt(Node parent) throws ParserException {
        Node node = new Node("ATTR_STMT");
        node.parent = parent;

        node.children.add(_id(node));
        node.children.add(_attrOp(node));
        node.children.add(intExp(node));
        node.children.add(_sc(node));

        return node;
    }

    private Node callStmt(Node parent) throws ParserException {
        Node node = new Node("CALL_STMT");
        node.parent = parent;

        node.children.add(_call(node));
        node.children.add(_id(node));
        node.children.add(_sc(node));

        return node;
    }

    private Node printStmt(Node parent) throws ParserException {
        Node node = new Node("PRINT_STMT");
        node.parent = parent;

        node.children.add(_print(node));
        node.children.add(intExp(node));

        return node;
    }

    private Node whileStmt(Node parent) throws ParserException {
        Node node = new Node("WHILE_STMT");
        node.parent = parent;

        node.children.add(_while(node));
        node.children.add(boolExp(node));
        node.children.add(_do(node));
        node.children.add(block(node));

        return node;
    }

    private Node ifStmt(Node parent) throws ParserException {
        Node node = new Node("IF_STMT");
        node.parent = parent;

        node.children.add(_if(node));
        node.children.add(boolExp(node));
        node.children.add(_then(node));
        node.children.add(block(node));
        node.children.add(elseStmt(node));

        return node;
    }

    private Node elseStmt(Node parent) throws ParserException {
        Node node = new Node("ELSE_STMT");
        node.parent = parent;

        switch (lookahead.type) {
            case ELSE:
                node.children.add(_else(node));
                node.children.add(block(node));
                break;
            default:
            //nextToken();
        }

        return node;
    }

    // integer expression
    private Node intExp(Node parent) throws ParserException {
        Node node = new Node("INT_EXP");
        node.parent = parent;

        node.children.add(term(node));
        node.children.add(intExp_(node));

        return node;
    }

    private Node intExp_(Node parent) throws ParserException {
        Node node = new Node("INT_EXP'");
        node.parent = parent;

        switch (lookahead.type) {
            case SUB_OP:
                node.children.add(_subOp(node));
                node.children.add(term(node));
                node.children.add(intExp_(node));
                break;
            case SUM_OP:
                node.children.add(_sumOp(node));
                node.children.add(term(node));
                node.children.add(intExp_(node));
                break;
            default:
            //nextToken();
        }

        return node;
    }

    private Node term(Node parent) throws ParserException {
        Node node = new Node("TERM");
        node.parent = parent;

        node.children.add(factor(node));
        node.children.add(term_(node));

        return node;
    }

    private Node term_(Node parent) throws ParserException {
        Node node = new Node("TERM'");
        node.parent = parent;

        switch (lookahead.type) {
            case DIV_OP:
                node.children.add(_divOp(node));
                node.children.add(factor(node));
                node.children.add(term_(node));
                break;
            case MULT_OP:
                node.children.add(_multOp(node));
                node.children.add(factor(node));
                node.children.add(term_(node));
                break;
            default:
            //nextToken();
        }

        return node;
    }

    private Node factor(Node parent) throws ParserException {
        Node node = new Node("FACTOR");
        node.parent = parent;

        switch (lookahead.type) {
            case ID:
                node.children.add(_id(node));
                break;
            case INT:
                node.children.add(_int(node));
                break;
            case L_PAR:
                node.children.add(_lPar(node));
                node.children.add(intExp(node));
                node.children.add(_rPar(node));
                break;
        }

        return node;
    }

    // boolean expression
    private Node boolExp(Node parent) throws ParserException {
        Node node = new Node("BOOL_EXP");
        node.parent = parent;

        node.children.add(orFactor(node));
        node.children.add(boolExp_(node));

        return node;
    }

    private Node boolExp_(Node parent) throws ParserException {
        Node node = new Node("BOOL_EXP'");
        node.parent = parent;

        switch (lookahead.type) {
            case OR:
                node.children.add(_or(node));
                node.children.add(orFactor(node));
                node.children.add(boolExp_(node));
                break;
            default:
            //nextToken();
        }

        return node;
    }

    private Node orFactor(Node parent) throws ParserException {
        Node node = new Node("OR_FACTOR");
        node.parent = parent;

        node.children.add(andFactor(node));
        node.children.add(orFactor_(node));

        return node;
    }

    private Node orFactor_(Node parent) throws ParserException {
        Node node = new Node("OR_FACTOR'");
        node.parent = parent;

        switch (lookahead.type) {
            case AND:
                node.children.add(_and(node));
                node.children.add(andFactor(node));
                break;
            default:
            //nextToken();
        }

        return node;
    }

    private Node andFactor(Node parent) throws ParserException {
        Node node = new Node("AND_FACTOR");
        node.parent = parent;

        switch (lookahead.type) {
            case NOT:
                node.children.add(_not(node));
                node.children.add(boolExp(node));
                break;
            default:
                node.children.add(intExp(node));
                node.children.add(compOp(node));
                node.children.add(intExp(node));
        }

        return node;
    }

    private Node compOp(Node parent) throws ParserException {
        Node node = new Node("COMP_OP");
        node.parent = parent;

        switch (lookahead.type) {
            case EQ:
                node.children.add(_eq(node));
                break;
            case DIFF:
                node.children.add(_diff(node));
                break;
            case HT:
                node.children.add(_ht(node));
                break;
            case LT:
                node.children.add(_lt(node));
                break;
            case HET:
                node.children.add(_het(node));
                break;
            case LET:
                node.children.add(_let(node));
                break;
        }

        return node;
    }

    // terminals
    private Node _program(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.PROGRAM) {

            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "program"));
        }

        return node;
    }

    private Node _id(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.ID) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "identificador"));
        }

        return node;
    }

    private Node _sc(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.SC) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", ";"));
        }

        return node;
    }

    private Node _begin(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.BEGIN) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "begin"));
        }

        return node;
    }

    private Node _end(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.END) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "end"));
        }

        return node;
    }

    private Node _procedure(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.PROCEDURE) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "procedure"));
        }

        return node;
    }

    private Node _var(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.VAR) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "var"));
        }

        return node;
    }

    private Node _comma(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.COMMA) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", ","));
        }

        return node;
    }

    private Node _attrOp(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.ATTR_OP) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "="));
        }

        return node;
    }

    private Node _call(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.CALL) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "call"));
        }

        return node;
    }

    private Node _print(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.PRINT) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "print"));
        }

        return node;
    }

    private Node _while(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.WHILE) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "while"));
        }

        return node;
    }

    private Node _do(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.DO) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "do"));
        }

        return node;
    }

    private Node _if(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.IF) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "if"));
        }

        return node;
    }

    private Node _then(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.THEN) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "then"));
        }

        return node;
    }

    private Node _else(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.ELSE) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "else"));
        }

        return node;
    }

    private Node _subOp(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.SUB_OP) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "-"));
        }

        return node;
    }

    private Node _sumOp(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.SUM_OP) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "+"));
        }

        return node;
    }

    private Node _divOp(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.DIV_OP) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "/"));
        }

        return node;
    }

    private Node _multOp(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.MULT_OP) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "*"));
        }

        return node;
    }

    private Node _lPar(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.L_PAR) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "("));
        }

        return node;
    }

    private Node _rPar(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.R_PAR) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", ")"));
        }

        return node;
    }

    private Node _int(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.INT) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "integer"));
        }

        return node;
    }

    private Node _or(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.OR) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "||"));
        }

        return node;
    }

    private Node _and(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.AND) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "&&"));
        }

        return node;
    }

    private Node _not(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.NOT) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "!"));
        }

        return node;
    }

    private Node _eq(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.EQ) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "=="));
        }

        return node;
    }

    private Node _diff(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.DIFF) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "!="));
        }

        return node;
    }

    private Node _ht(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.HT) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", ">"));
        }

        return node;
    }

    private Node _lt(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.LT) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "<"));
        }

        return node;
    }

    private Node _het(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.HET) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", ">="));
        }

        return node;
    }

    private Node _let(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.LET) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "<="));
        }

        return node;
    }

    private Node _period(Node parent) throws ParserException {
        Node node = new Leaf(lookahead.type, lookahead.value);
        node.parent = parent;

        if (lookahead.type == Token.Type.PERIOD) {
            nextToken();
        } else {
            throw new ParserException(String.format("'%s' esperado", "."));
        }

        return node;
    }

    class Node {

        public String id;
        public List<Node> children;
        public Node parent;

        public Node(String id) {
            this.id = id;
            children = new ArrayList<Node>();
        }
    }

    class Leaf extends Node {

        public Token.Type type;
        public String lexval;

        public Leaf(Token.Type type, String lexval) {
            super("");
            this.type = type;
            this.lexval = lexval;
        }
    }
}
