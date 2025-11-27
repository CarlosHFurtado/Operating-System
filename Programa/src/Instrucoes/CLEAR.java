package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class CLEAR extends InstrucaoFormato2 { 

    public CLEAR() {
        
        super("CLEAR", (byte) 0xB4); 
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        int[] regIds = obterRegistradores(memoria, registradores);
        int r1Id = regIds[0]; 
        
        // 2. Incrementa o PC pelo tamanho da instrução
        
        registradores.incrementar("PC", getLength()); 
        
        // Mapeia o ID para o nome 
        
        String r1Nome = Registradores.getNomeRegistradorPorId(r1Id);

        if (r1Nome == null) {
            
            PainelLog.logGlobal(String.format("CLEAR: ERRO - Registrador ID %d inválido.", r1Id));
            return;
            
        }

        // Executa a operação: r1 <- 0
        
        registradores.setValor(r1Nome, 0);
        
        PainelLog.logGlobal(String.format("CLEAR: Registrador %s zerado. Novo valor=0x0", r1Nome));
        
    }
}