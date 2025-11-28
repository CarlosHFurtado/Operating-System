package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class LDL extends InstrucaoFormato3ou4 {
    
    public LDL() {
        
        super("LDA", (byte) 0x08);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        // Calcular AE e obter o Operando
        
        int operando = calcularEObterOperando(memoria, registradores);
        
        // Transferir o operando para o registrador A
        
        registradores.setValor("L", operando);
        
        PainelLog.logGlobal(String.format("LDL: L <- Operando (0x%X). Novo L = 0x%X", 
            operando, registradores.getValor("L")));
        
        logSeparador();
    }
}