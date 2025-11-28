package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class RSUB extends InstrucaoFormato3ou4 { 
    
    // PC <- (L)
    
    public RSUB() {
        
        super("RSUB", (byte) 0x4C);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        int pcInicial = registradores.getValor("PC");

        // Obtemos o endereço de retorno de L
        
        int enderecoRetorno = registradores.getValor("L");
        
        // RSUB avança o PC em 3 bytes (F3) antes de saltar
        
        registradores.incrementar("PC", 3);
        
        // Atualizar o PC para o endereço de retorno
        
        registradores.setValor("PC", enderecoRetorno);
        
        PainelLog.logGlobal(String.format("RSUB: Retorno de Sub-rotina. PC <- (L)=0x%X", enderecoRetorno));
    
    }
}