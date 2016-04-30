/* This class is responsible for the syntactic analysis (Token[] -> AST) */

	package parserpl0;

import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.util.TreeSet;

public class Parser {
    List<Token> tokens;
    Token primeiro;
    TreeSet<String> arvore;
    int x;
    
    //pega a lista de tokens do tokenizer
    public void parse(List<Token> tokens){
        this.tokens = tokens;
        primeiro = this.tokens.get(0);
        arvore = new TreeSet<String>();
        program();
	//simbolo de finalizacao eh o $, por enquanto esta epsilon
        if (primeiro.token != Token.CIPHER)
            throw new ParserException("Simbolo esperado %s nao indetificado", primeiro);
    }
    
    //pega o proximo token 
    private void nextToken(){
        tokens.remove(0);
        //retorna um $ pra demonstrar o fim
        if (tokens.isEmpty())
            primeiro = new Token(Token.CIPHER, "$");
        else
            primeiro = tokens.get(0);
    }
    //expressao com termo e operador
     private void expression(){
        
        signedTerm();
        sumOp();
    }
     //programa definido na gramatica
    private void program(){
        if (primeiro.token == Token.PROGRAM){
            nextToken();
            id();
            sc();
            block();
            prd();
            //encontrar um jeito de finalizar o programa aqui
        }
        else{
            throw new ParserException("Esperado a palavra chave program" 
                    +primeiro.sequence + "encontrada no lugar");
          
        }
    }
    //ponto e virgula
    private void sc(){
        if (primeiro.token == Token.SC){
            nextToken();
        }
        else{
            throw new ParserException ("Esperado ; " + primeiro.sequence +
                    " encontrado no lugar");
        }
    }
    //ponto final
    private void prd(){
        if (primeiro.token == Token.PRD){
            nextToken();
        }
        else{
            throw new ParserException ("Esperado . " + primeiro.sequence +
                    " encontrado no lugar");
        }
    }
    //bloco
    private void block(){
        if (primeiro.token == Token.BEGIN){
            nextToken();
            constt();
            var();
            procedures();
            stmt();
            end();
        }
        else{
        }
    }
    
    private void end(){
        if (primeiro.token == Token.END){
            nextToken();
        }
        else{
            throw new ParserException ("Esperado END " + primeiro.sequence +
                    " encontrado no lugar");
        }
    }
    private void stmt(){
        if (primeiro.token == Token.CALL){
            call();
            stmt();
        }
        else if (primeiro.token == Token.WRITE){
            write();
            stmt();
        }
        else if (primeiro.token == Token.READ){
            read();
            stmt();
        }
        else if (primeiro.token == Token.IF){
            ift();
            stmt();
        }
        else if (primeiro.token == Token.WHILE){
            whilet();
            stmt();
        }
        else if (primeiro.token == Token.ATRB){
            atrb();
        }
        else{
        }
    }
        
    private void call(){
        if (primeiro.token == Token.CALL){
            nextToken();
            id();
            sc();
        }
        else{
            throw new ParserException ("Esperado CALL " + primeiro.sequence +
                    " encontrado no lugar");
        }
    }
    
    private void write(){
        if (primeiro.token == Token.WRITE){
            nextToken();
            id();
            sc();
        }
        else{
            throw new ParserException ("Esperado WRITE " + primeiro.sequence +
                    " encontrado no lugar");
        }
    }
    
    private void read(){
        if (primeiro.token == Token.READ){
            nextToken();
            id();
            sc();
        }
        else{
            throw new ParserException ("Esperado READ " + primeiro.sequence +
                    " encontrado no lugar");
        }
    }
    
    private void ift(){
        if (primeiro.token == Token.IF){
            nextToken();
            logic();
            subif();
        }
        else{
            throw new ParserException ("Esperado IF " + primeiro.sequence +
                    " encontrado no lugar");
        }
    }
    
    private void subif(){
        if (primeiro.token == Token.THEN){
            nextToken();
            block();
            if (primeiro.token == Token.SC){
                sc();
            }
            else if (primeiro.token == Token.ELSE){
                elset();
            }
            else{
                throw new ParserException ("Esperado ; ou ELSE " + primeiro.sequence +
                    " encontrado no lugar");
            }
        }
        else{
            throw new ParserException ("Esperado IF " + primeiro.sequence +
                    " encontrado no lugar");
        }
    }
    
    private void elset(){
        if (primeiro.token == Token.ELSE){
            nextToken();
            block();
        }
        else{
            throw new ParserException ("Esperado ELSE " + primeiro.sequence +
                    " encontrado no lugar");
        }
    }
    
    private void whilet(){
        if (primeiro.token == Token.WHILE){
            nextToken();
            logic();
            dot();
            block();
            sc();
        }
        else{
            throw new ParserException ("Esperado WHILE " + primeiro.sequence +
                    " encontrado no lugar");
        }
    }
    
    private void dot(){
        if (primeiro.token == Token.DO){
            nextToken();
        }
        else{
            throw new ParserException ("Esperado DO " + primeiro.sequence +
                    " encontrado no lugar");
        }
    }
    
    
    private void atrb(){
        if (primeiro.token == Token.ATRB){
            id();
            atr();
            expression();
        }
        else{
            throw new ParserException ("Esperado atrb " + primeiro.sequence +
                    " encontrado no lugar");
        }
    }
    
    
    private void atr(){
        if (primeiro.token == Token.ATR){
            nextToken();
        }
        else{
            throw new ParserException ("Esperado aspas " + primeiro.sequence +
                    " encontrado no lugar");
        }
    }
    private void logic(){
        if (primeiro.token == Token.ID){
            id();
            logop();
            if (primeiro.token == Token.ID){
                id();
            }
            else if (primeiro.token == Token.NUMBER){
                number();
            }
        }
        else if(primeiro.token ==Token.NUMBER){
            number();
            logop();
            if (primeiro.token == Token.ID){
                id();
            }
            else if (primeiro.token == Token.NUMBER){
                number();
            }
        }
        else if (primeiro.token == Token.TRUE){
            nextToken();
        }
        else if (primeiro.token == Token.FALSE){
            nextToken();
        }
        else{
            throw new ParserException ("Esperado letra, numero, true ou false, " + primeiro.sequence +
                    " encontrado no lugar");
        }
    }
    
    private void logop(){
        if (primeiro.token == Token.LOGOP){
            nextToken();
        }
        else{
            throw new ParserException ("Esperado ==, !=, <, >, <= ou >=, " + primeiro.sequence +
                    " encontrado no lugar");
        }
    }
    
    
    private void number(){
        if (primeiro.token == Token.NUMBER){
            nextToken();
            numberOp();
        }
        else{
            throw new ParserException ("Esperado numero, " + primeiro.sequence +
                    " encontrado no lugar");
        }
    }
    
    private void numberOp(){
        if (primeiro.token == Token.NUMBER){
            nextToken();
            numberOp();
        }
        else{
        }
    }
    
    private void procedures(){
        if (primeiro.token == Token.PROCEDURE){
            procedure();
            procedures();
        }
        else{
            
        }
    }
    
    private void procedure(){
        if (primeiro.token == Token.PROCEDURE){
            nextToken();
            id();
            sc();
            block();
            sc();
        }
    }
    
    
    private void var(){
        if (primeiro.token == Token.VAR){
            nextToken();
            var_expr();
            sc();
        }
        else{
        }
    }
    
    private void var_expr(){
        if (primeiro.token==Token.ID){
            id();
            var_exprOp();
        }
        else{
            throw new ParserException ("Espera uma letra ou conjunto de letras, "
                + primeiro.sequence+ " encontrado no lugar");
        }
    }
    
    private void var_exprOp(){
        if (primeiro.token == Token.COMMA){
            comma();
            var_expr();
        }
        else{
            
        }
    }
    
    
    //definir as funcoes do bloco
    private void constt(){
        if (primeiro.token==Token.CONST){
            nextToken();
            const_expr();
            sc();
        }
        else{
        }
    }
    
    private void const_expr(){
        if (primeiro.token==Token.ID){
            id();
            atr();
            value();
            const_exprOp();
        }
    }
    
    private void const_exprOp(){
        if (primeiro.token==Token.COMMA){
            comma();
            const_expr();
        }
        else{
            
        }
    }
    
    private void comma(){
        if (primeiro.token == Token.COMMA){
            nextToken();
        }
        else{
            throw new ParserException ("Esperado , " + primeiro.sequence +
                    " encontrado no lugar");
        }
    }
            
    
    
    
    
     //soma ou subtracao
    private void sumOp(){
        if (primeiro.token == Token.OPER){
            nextToken();
            term();
            sumOp();
        }
        else{
        }
    }
    //termo com sinal
    private void signedTerm(){
        if (primeiro.token == Token.OPER){
            nextToken();
            term();
        }
        else{
            term();
        }
    }
    
    //primeiro termo a aparecer
     private void term() {
        factor();
        termOp();
    }
     //termo opcional
    private void termOp() {
        if (primeiro.token == Token.OPERMULTDIV) {
            nextToken();
            signedFactor();
            termOp();
        } else {
        }
    }
    //fator com sinal
    private void signedFactor() {
        if (primeiro.token == Token.OPERMULTDIV) {
            nextToken();
            factor();
        } else {
            factor();
        }
    }
    //fator, pegando variavel, expressao ou constante
    private void factor() {
        if (primeiro.token == Token.ID){
            nextToken();
            id();
        }
        else if (primeiro.token == Token.VALUE) {
            nextToken();
            value();
        }
            
        else if (primeiro.token == Token.LPAR){
            nextToken();
            expression();
            if (primeiro.token == Token.RPAR){
                nextToken();
                term();
            }
            else {
                throw new ParserException("Era esperado um ( porem foi encontrado "
                        + primeiro.sequence + " no lugar");
            }
        }
        else {
            throw new ParserException("Era esperado um valor ou uma expressao porem"
                        +primeiro.sequence + "foi encontrado no lugar");
                    }
    }
    //numero
    private void value() {
        if (primeiro.token == Token.NUMBER) {
            nextToken();
        } else if (primeiro.token == Token.EXPR) {
            nextToken();
            expression();
        } else {
            throw new ParserException(
                    "Simbolo " + primeiro.sequence + " inesperado encontrado");
        }
    }
    //letra
    private void id() {
        if (primeiro.token == Token.LETTER) {
            nextToken();
            idOp();
        } else {
            throw new ParserException(
                    "Simbolo " + primeiro.sequence + " inesperado encontrado");
        }
    }
    //especie de laco que permite um conjunto de letras
    private void idOp() {
        if (primeiro.token == Token.LETTER) {
            nextToken();
            idOp();
        } 
        else {
        }
    }
    
}
