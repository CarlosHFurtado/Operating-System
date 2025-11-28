package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class MUL extends InstrucaoFormato3ou4 {
    
    // A <- (A) * (m..m+2)
    
    public MUL() {
        
        super("MUL", (byte) 0x20);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        // Calcular AE e obter o Operando (valor com sinal)
        
        int operando = calcularEObterOperando(memoria, registradores);
        
        // Obter o valor atual de A (com sinal)
        
        int valorA = registradores.getValorIntSigned("A");
        
        // Executar a multiplicação
       
        long resultadoLong = (long) valorA * operando;
        
        int resultado = (int) resultadoLong; 

        // Armazenar o resultado em A
        
        registradores.setValor("A", resultado);
        
        PainelLog.logGlobal(String.format("MUL: A <- (%d) * Operando(%d). Novo A=%d (0x%X). Resultado Long: %d", 
            valorA, operando, registradores.getValorIntSigned("A"), registradores.getValor("A"), resultadoLong));
    
    }
}