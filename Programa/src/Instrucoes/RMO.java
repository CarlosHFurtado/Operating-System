package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class RMO extends InstrucaoFormato2 {
    
    // r2 <- (r1)
    
    public RMO() {
        
        super("RMO", (byte) 0xAC);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        int pcInicial = registradores.getValor("PC");
        
        // Obter R1 e R2
        
        int[] reg = obterRegistradores(memoria, registradores);
        int r1Id = reg[0];
        int r2Id = reg[1];
        
        // Incrementar PC 
        
        registradores.incrementar("PC", 2);

        String r1Nome = Registradores.getNomeRegistradorPorId(r1Id);
        String r2Nome = Registradores.getNomeRegistradorPorId(r2Id);
        
        if (r1Nome == null || r2Nome == null) {
            
            PainelLog.logGlobal("Erro: Registrador inválido para RMO em PC = " + String.format("%06X", pcInicial));
            return;
            
        }

        // Executar a cópia
        
        int valorR1 = registradores.getValor(r1Nome);
        
        // Armazenar o valor de R1 em R2
        
        registradores.setValor(r2Nome, valorR1);
        
        PainelLog.logGlobal(String.format("RMO: %s <- (%s)=%d (0x%X)", 
            r2Nome, r1Nome, valorR1, valorR1));
        
        logSeparador();
        
    }
}