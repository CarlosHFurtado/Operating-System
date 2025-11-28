package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class ADD extends InstrucaoFormato3ou4 {
    
    // A <- (A) + (m..m+2)
    
    public ADD() {
        
        super("ADD", (byte) 0x18);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        // Calcular AE e obter o Operando 
        
        int operando = calcularEObterOperando(memoria, registradores);
        
        // Obter o valor atual de A 
        
        int valorA = registradores.getValorIntSigned("A");
        
        // Executar a soma
       
        int resultado = valorA + operando;
        
        // Armazenar o resultado em A.
       
        registradores.setValor("A", resultado);
        
        PainelLog.logGlobal(String.format("ADD: A <- (%d) + Operando(%d). Novo A = %d (0x%X)", 
            valorA, operando, registradores.getValorIntSigned("A"), registradores.getValor("A")));
        
    }
}