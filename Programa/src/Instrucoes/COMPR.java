package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class COMPR extends InstrucaoFormato2 {
    
    // (r1) : (r2) -> Seta CC no SW
    
    public COMPR() {
        
        super("COMPR", (byte) 0xA0);
        
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
            
            PainelLog.logGlobal("Erro: Registrador inválido para COMPR em PC = " + String.format("%06X", pcInicial));
            return;
            
        }

        // Executar a comparação 
        
        int valorR1 = registradores.getValorIntSigned(r1Nome);
        int valorR2 = registradores.getValorIntSigned(r2Nome);
        
        int cc; // Código de Condição
        
        if (valorR1 < valorR2) {
            
            cc = 0b10000000;
            
        } else if (valorR1 > valorR2) {
            
            cc = 0b01000000; 
            
        } else {
            
            cc = 0b00100000; 
            
        }
              
        // Limpa os bits CC existentes no SW
        
        int swAntigo = registradores.getValor("SW") & 0xFF0FFFFF; // 0xFF0FFFFF limpa os 3 bits CC (bits 20-22)
        
        // Converte o CC para o valor de 24 bits do SW
            
        int novoSW = swAntigo;
        
        if (valorR1 < valorR2) {
            
            novoSW |= 0x00800000; 
            
        } else if (valorR1 > valorR2) {
            
            novoSW |= 0x00400000; 
            
        } else {
            
            novoSW |= 0x00200000; 
            
        }
        
        registradores.setValor("SW", novoSW);
        
        PainelLog.logGlobal(String.format("COMPR: (%s) = %d : (%s) = %d. SW_novo = 0x%X", 
            r1Nome, valorR1, r2Nome, valorR2, novoSW));
        
    }
}