package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;
import java.util.Map;

public class LDB extends Instrucao {
    public LDB() {
        
        super("LDB", (byte)0x68, "3/4", 3);
        
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        int enderecoOuValorImediato = calcularTA(registradores, memoria);
        int operando = 0; 
        
        Map<String, Boolean> flags = getFlags();
        
        boolean isImediato = flags.get("i") && !flags.get("n"); 
        boolean isIndireto = flags.get("n") && !flags.get("i"); 
        
        if (isImediato) {
           
            operando = enderecoOuValorImediato;
            
        } else if (isIndireto) {
                       
            int enderecoReal = memoria.getWord(enderecoOuValorImediato);
            
            operando = memoria.getWord(enderecoReal);
            
        } else {
           
            operando = memoria.getWord(enderecoOuValorImediato);
            
        }
       
        registradores.getRegistradorPorNome("B").setValorInt(operando);
        
    }
}