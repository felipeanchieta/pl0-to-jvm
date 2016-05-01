/* This class is responsible for the semantic analysis (AST -> Annotated AST) */

public class SemanticAnalyser {

    static int scopo = 0;
    static TabelaSimbolos tabelaSimbolo = new TabelaSimbolos(scopo);

    public static void postOrder(Parser.Node no) {

        for (Parser.Node n : no.children) {
            if (n.children != null) {
                postOrder(n);
            }
            if (n.getClass() == Parser.Leaf.class) {
                Parser.Leaf aux = (Parser.Leaf) n;
                switch (aux.type) {
                    case PROGRAM:
                        tabelaSimbolo.adicionar(aux.lexval, Token.Type.PROGRAM);
                        break;
                    case ID:
                        tabelaSimbolo.adicionar(aux.lexval, Token.Type.PROGRAM);
                        break;
                    case VAR:
                        tabelaSimbolo.adicionar(aux.lexval, Token.Type.ID);
                        break;
                }
            }
        }
    }

}
