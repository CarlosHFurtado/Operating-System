package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

// LDA - LDB - LDL - LDS - LDT - LDX -> Mesmo codigo, muda apenas opcode e o registrador

public class LDA extends InstrucaoFormato3ou4 {
    
    // A <- (m..m+2)
    
    public LDA() {
        
        super("LDA", (byte) 0x00);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        // Calcular AE e obter o Operando
        
        int operando = calcularEObterOperando(memoria, registradores);
        
        // Transferir o operando para o registrador A
        
        registradores.setValor("A", operando);
        
        PainelLog.logGlobal(String.format("LDA: A <- Operando (0x%X). Novo A = 0x%X", 
            operando, registradores.getValor("A")));
        
        logSeparador();
        
    }
}