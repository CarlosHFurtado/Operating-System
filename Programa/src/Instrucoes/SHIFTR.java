package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class SHIFTR extends InstrucaoFormato2 {
    
    // r1 <- (r1) >> n (r2)
    
    public SHIFTR() {
        
        super("SHIFTR", (byte) 0xA8);
        
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
            
            PainelLog.logGlobal("Erro: Registrador inválido para SHIFTR em PC = " + String.format("%06X", pcInicial));
            return;
            
        }

        // Executar o shift
     
        int valorR1Signed = registradores.getValorIntSigned(r1Nome);
        
        // Deslocamento para a direita aritmético 
        
        int resultado = valorR1Signed >> n;
        
        // Armazenar o resultado em R1 
        
        registradores.setValor(r1Nome, resultado);
        
        PainelLog.logGlobal(String.format("SHIFTR: %s <- (%s) >> %d (Aritmético). Resultado: %d (0x%X)", 
            r1Nome, r1Nome, n, resultado, resultado));
        
        logSeparador();
        
    }
}