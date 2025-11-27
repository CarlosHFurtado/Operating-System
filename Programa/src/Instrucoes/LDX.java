package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class LDX extends InstrucaoFormato3ou4 {
    
    public LDX() {
        
        super("LDX", (byte) 0x08);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        // Calcular AE e obter o Operando
        
        int operando = calcularEObterOperando(memoria, registradores);
        
        // Transferir o operando para o registrador X
        
        registradores.setValor("X", operando);
        
        PainelLog.logGlobal(String.format("LDX: X <- Operando (0x%X). Novo X = 0x%X", 
            operando, registradores.getValor("X")));
        
    }
}