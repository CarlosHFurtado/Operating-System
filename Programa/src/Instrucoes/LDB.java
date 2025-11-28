package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class LDB extends InstrucaoFormato3ou4 {
    
    // B <- (m..m+2)
    
    public LDB() {
        
        super("LDB", (byte) 0x68);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        // Calcular AE e obter o Operando
        
        int operando = calcularEObterOperando(memoria, registradores);
        
        // 2. Transferir o operando para o registrador B
        
        registradores.setValor("B", operando);
        
        PainelLog.logGlobal(String.format("LDB: B <- Operando (0x%X). Novo B = 0x%X", 
            operando, registradores.getValor("B")));
        
        logSeparador();
    }
}