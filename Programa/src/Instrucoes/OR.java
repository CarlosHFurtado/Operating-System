package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class OR extends InstrucaoFormato3ou4 {
    
    // A <- (A) OR (m..m+2)
    
    public OR() {
        
        super("OR", (byte) 0x44);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        // Calcular AE e obter o Operando 
        
        int operando = calcularEObterOperando(memoria, registradores);
        
        // Obter o valor atual de A 
        
        int valorA = registradores.getValor("A");
        
        // Executar o OR lógico 
        
        int resultado = valorA | operando;
        
        // Armazenar o resultado em A
        
        registradores.setValor("A", resultado);
        
        PainelLog.logGlobal(String.format("OR: A <- (0x%X) OR (0x%X). Novo A = 0x%X", 
            valorA, operando, resultado));
        
        logSeparador();
        
    }
}