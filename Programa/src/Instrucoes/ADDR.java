package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class ADDR extends InstrucaoFormato2 {
    
    // r2 <- (r2) + (r1)
    
    public ADDR() {
        
        super("ADDR", (byte)0x90);
        
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
            
            PainelLog.logGlobal("Erro: Registrador inválido para ADDR em PC = " + String.format("%06X", pcInicial));
            return;
            
        }

        // Executar a soma 
        
        int valorR1 = registradores.getValorIntSigned(r1Nome);
        int valorR2 = registradores.getValorIntSigned(r2Nome);
        
        int resultado = valorR1 + valorR2;
        
        // Armazenar o resultado em R2 
        
        registradores.setValor(r2Nome, resultado);
        
        PainelLog.logGlobal(String.format("ADDR: %s <- (%s) + (%s). Resultado em %s: %d (0x%X)", 
            r2Nome, r2Nome, r1Nome, r2Nome, resultado, resultado));
        
        logSeparador();
        
    }
}