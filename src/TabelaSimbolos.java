
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Shu
 */
public class TabelaSimbolos {
    
    public TabelaSimbolos parent;
    public HashMap<String, Token.Type> tabela;
    public int scopo;
    
    public TabelaSimbolos(int scopo) {
        this.scopo = scopo;
        tabela = new HashMap<>();
    }
    
    public boolean lookUp(String nome) {
        if (tabela.containsKey(nome)){
            return true;
        } else if (parent != null){
            return parent.lookUp(nome);
        } else {
            return false;
        }
    }
    
    public void adicionar(String name, Token.Type tipo){
        tabela.put(name, tipo);
    }
    
    public void initializeScope(){
        scopo++;
    }
    
}
