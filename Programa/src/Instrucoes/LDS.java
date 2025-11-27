package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class LDS extends InstrucaoFormato3ou4 {
    
    public LDS() {
        
        super("LDS", (byte) 0x6C);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        // Calcular AE e obter o Operando
        
        int operando = calcularEObterOperando(memoria, registradores);
        
        // Transferir o operando para o registrador S
        
        registradores.setValor("S", operando);
        
        PainelLog.logGlobal(String.format("LDL: S <- Operando (0x%X). Novo S = 0x%X", 
            operando, registradores.getValor("S")));
        
    }
}