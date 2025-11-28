package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class AND extends InstrucaoFormato3ou4 {
    
    // A <- (A) AND (m..m+2)
    
    public AND() {
        
        super("AND", (byte) 0x40);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        // Calcular AE e obter o Operando
        
        int operando = calcularEObterOperando(memoria, registradores);
        
        // Obter o valor atual de A 
        
        int valorA = registradores.getValor("A");
        
        // Executar o AND lógico 
        
        int resultado = valorA & operando;
        
        // Armazenar o resultado em A
        
        registradores.setValor("A", resultado);
        
        PainelLog.logGlobal(String.format("AND: A <- (0x%X) AND (0x%X). Novo A=0x%X", 
            valorA, operando, resultado));
        
        logSeparador();
        
    }
}