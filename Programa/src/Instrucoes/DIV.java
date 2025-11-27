package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class DIV extends InstrucaoFormato3ou4 {
    
    // A <- (A) / (m..m+2)
    
    public DIV() {
        
        super("DIV", (byte) 0x24);
        
    }
    
    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        int pcInicial = registradores.getValor("PC");
        
        // Calcular AE e obter o Operando
        
        int operando = calcularEObterOperando(memoria, registradores);
        
        // Obter o valor atual de A 
        
        int valorA = registradores.getValorIntSigned("A");
        
        // Executar a divisão
        
        if (operando == 0) {
            
            // Tratamento de erro: divisão por zero
            
            PainelLog.logGlobal("ERRO: DIV - Divisão por zero detectada em PC=" + String.format("%06X", pcInicial));
            
            return; 
            
        }

        int resultado = valorA / operando;
        
        // Armazenar o resultado em A
        
        registradores.setValor("A", resultado);
        
        PainelLog.logGlobal(String.format("DIV: A <- (%d) / Operando(%d). Novo A = %d (0x%X)", 
            valorA, operando, registradores.getValorIntSigned("A"), registradores.getValor("A")));
        
    }
}