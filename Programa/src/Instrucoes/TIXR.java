package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class TIXR extends InstrucaoFormato2 {
    
    // X <- (X) + 1; (X) : (r1) -> Seta CC no SW
    
    public TIXR() {
        
        super("TIXR", (byte) 0xB8);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        int pcInicial = registradores.getValor("PC");
        
        // Obter R1 
        
        int[] reg = obterRegistradores(memoria, registradores);
        int r1Id = reg[0];
        
        // Incrementar PC 
        
        registradores.incrementar("PC", 2);

        String r1Nome = Registradores.getNomeRegistradorPorId(r1Id);
        
        if (r1Nome == null) {
            
            PainelLog.logGlobal("Erro: Registrador inválido para TIXR em PC = " + String.format("%06X", pcInicial));
            return;
            
        }

        // Incrementar X
        
        registradores.incrementar("X", 1);
        
        // Executar a comparação: (X) : (r1)
        
        int valorX = registradores.getValorIntSigned("X");
        int valorR1 = registradores.getValorIntSigned(r1Nome);
        
        int cc; // Código de Condição
        
        if (valorX < valorR1) {
            
            cc = 0b10000000; 
            
        } else if (valorX > valorR1) {
            
            cc = 0b01000000; 
            
        } else {
            
            cc = 0b00100000;
            
        }
        
        // Setar o Código de Condição (CC) no registrador SW
        
        int swAntigo = registradores.getValor("SW") & 0xFF0FFFFF; 
        
        int novoSW = swAntigo;
        
        if (valorX < valorR1) {
            
            novoSW |= 0x00800000; 
            
        } else if (valorX > valorR1) {
            
            novoSW |= 0x00400000; 
            
        } else {
            
            novoSW |= 0x00200000; 
            
        }
        
        registradores.setValor("SW", novoSW);
        
        PainelLog.logGlobal(String.format("TIXR: X <- (X) + 1. Compara (X) = %d : (%s) = %d. SW_novo = 0x%X", 
            valorX, r1Nome, valorR1, novoSW));
        
        logSeparador();
        
    }
}