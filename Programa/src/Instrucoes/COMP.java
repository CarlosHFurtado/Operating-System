package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class COMP extends InstrucaoFormato3ou4 {
    
    // (A) : (m..m+2) -> Seta CC no SW
    
    public COMP() {
        
        super("COMP", (byte) 0x28);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        // Calcular AE e obter o Operando
        
        int operando = calcularEObterOperando(memoria, registradores);
        
        // Obter o valor atual de A 
        
        int valorA = registradores.getValorIntSigned("A");
        
        // Executar a comparação
        
        int novoSW;
        
        // Limpa os bits CC existentes no SW
        
        int swAntigo = registradores.getValor("SW") & 0xFF0FFFFF; 
        
        if (valorA < operando) {
            
            novoSW = swAntigo | 0x00800000; 
            
        } else if (valorA > operando) {
            
            novoSW = swAntigo | 0x00400000;
            
        } else {
            
            novoSW = swAntigo | 0x00200000; 
            
        }
        
        // 4. Setar o novo SW
        
        registradores.setValor("SW", novoSW);
        
        PainelLog.logGlobal(String.format("COMP: (A)=%d : Operando(%d). SW_novo=0x%X", 
            valorA, operando, novoSW));
        
        logSeparador();
        
    }
}