package montador;

import java.util.Arrays;
import montador.TabelaInstrucoesMaquina; 
import montador.TabelaPseudoInstrucoes;

// Responsável por analisar cada linha do código assembly e convertê-la em um objeto LinhaInstrucao estruturado

public class Parser {

    public static LinhaInstrucao parseLine(String linhaBruta) {
     
        String linhaSemComentario = linhaBruta.split(";")[0].trim();

        if (linhaSemComentario.isEmpty()) {
            
            return new LinhaInstrucao(null, null, null, linhaBruta); 
            
        }
        
        String[] partes = linhaSemComentario.split("\\s+");
        
        String label = null;
        String opcodeBruto;
        String opcodeBase;
        String operandos = null;
        boolean isExtended = false;

        String primeiraParte = partes[0].toUpperCase();

        if (primeiraParte.startsWith("+")) {
            
            isExtended = true;
            opcodeBase = primeiraParte.substring(1); 
            opcodeBruto = primeiraParte;
            
        } else {
            
            opcodeBase = primeiraParte;
            opcodeBruto = primeiraParte;
            
        }
    
        boolean primeiraPartePodeSerLabel = partes.length >= 2 && 
                                           !TabelaInstrucoesMaquina.isMachineOp(opcodeBase) && 
                                           !TabelaPseudoInstrucoes.isPseudoOp(opcodeBase);
        
        if (primeiraPartePodeSerLabel) {
          
            label = primeiraParte;
            opcodeBruto = partes[1].toUpperCase();
            isExtended = opcodeBruto.startsWith("+");
            opcodeBase = isExtended ? opcodeBruto.substring(1) : opcodeBruto;
            
            if (partes.length >= 3) {
               
                operandos = String.join(" ", java.util.Arrays.copyOfRange(partes, 2, partes.length));
            
            }
            
        } else if (partes.length >= 1) {
            
            if (partes.length >= 2) {
               
                operandos = String.join(" ", java.util.Arrays.copyOfRange(partes, 1, partes.length));
            
            }
            
        } else {
           
             return new LinhaInstrucao(null, null, null, linhaBruta); 
        
        }       
       
        return new LinhaInstrucao(label, opcodeBase, operandos, linhaBruta, isExtended);
    
    }
}