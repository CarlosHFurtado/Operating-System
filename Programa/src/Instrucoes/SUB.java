package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class SUB extends InstrucaoFormato3ou4 {
    
    // A <- (A) - (m..m+2)
    
    public SUB() {
        
        super("SUB", (byte) 0x1C);
        
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        // Calcular AE e obter o Operando
        
        int operando = calcularEObterOperando(memoria, registradores);
        
        // Obter o valor atual de A 
        
        int valorA = registradores.getValorIntSigned("A");
        
        // Executar a subtração
        
        int resultado = valorA - operando;
        
        // Armazenar o resultado em A
        
        registradores.setValor("A", resultado);
        
        PainelLog.logGlobal(String.format("SUB: A <- (%d) - Operando(%d). Novo A=%d (0x%X)", 
            valorA, operando, registradores.getValorIntSigned("A"), registradores.getValor("A")));
        
    }
}