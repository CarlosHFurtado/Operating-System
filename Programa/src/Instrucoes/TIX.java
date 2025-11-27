package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class TIX extends InstrucaoFormato3ou4 {
    
    // X <- (X) + 1; (X) : (m..m+2) -> Seta CC no SW
    
    public TIX() {
        
        super("TIX", (byte) 0x2C);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        // Calcular AE e obter o Operando 
        
        int operando = calcularEObterOperando(memoria, registradores);
        
        // Incrementar X
        
        registradores.incrementar("X", 1);
        
        // Obter o novo valor de X
        
        int valorX = registradores.getValorIntSigned("X");
        
        // Executar a comparação: (X) : Operando(m)
        
        int novoSW;
        
        // Limpa os bits CC existentes no SW (bits 21-23)
        
        int swAntigo = registradores.getValor("SW") & 0xFF0FFFFF; 
        
        if (valorX < operando) {
            
            novoSW = swAntigo | 0x00800000; 
            
        } else if (valorX > operando) {
            
            novoSW = swAntigo | 0x00400000; 
            
        } else {
            
            novoSW = swAntigo | 0x00200000;
            
        }
        
        // Setar o novo SW
        
        registradores.setValor("SW", novoSW);
        
        PainelLog.logGlobal(String.format("TIX: X <- (X)+1. Compara (X) = %d : Operando(%d). SW_novo=0x%X", 
            valorX, operando, novoSW));
        
    }
}