package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class LDT extends InstrucaoFormato3ou4 {
    
    public LDT() {
        
        super("LDT", (byte) 0x08);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        // Calcular AE e obter o Operando
        
        int operando = calcularEObterOperando(memoria, registradores);
        
        // Transferir o operando para o registrador T
        
        registradores.setValor("T", operando);
        
        PainelLog.logGlobal(String.format("LDT: T <- Operando (0x%X). Novo T = 0x%X", 
            operando, registradores.getValor("T")));
        
    }
}