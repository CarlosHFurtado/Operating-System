package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class SHIFTL extends InstrucaoFormato2 {
    
    // r1 <- (r1) << n (r2)
    
    public SHIFTL() {
        
        super("SHIFTL", (byte) 0xA4);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        int pcInicial = registradores.getValor("PC");
        
        // Obter R1 e n (r2)
        
        int[] reg = obterRegistradores(memoria, registradores);
        int r1Id = reg[0];
        int n = reg[1]; 
        
        // Incrementar PC 
        registradores.incrementar("PC", 2);

        String r1Nome = Registradores.getNomeRegistradorPorId(r1Id);
        
        if (r1Nome == null) {
            
            PainelLog.logGlobal("Erro: Registrador inválido para SHIFTL em PC = " + String.format("%06X", pcInicial));
            return;
        }

        // Executar o shift

        int valorR1 = registradores.getValor(r1Nome);
        
        // Deslocamento para a esquerda 
        
        int resultado = valorR1 << n;
        
        // Armazenar o resultado em R1 

        registradores.setValor(r1Nome, resultado);
        
        PainelLog.logGlobal(String.format("SHIFTL: %s <- (%s) << %d. Resultado: %d (0x%X)", 
            r1Nome, r1Nome, n, resultado, resultado));
        
        logSeparador();
        
    }
}