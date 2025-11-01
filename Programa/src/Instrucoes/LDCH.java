package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;
import java.util.Map;

public class LDCH extends Instrucao {

    public LDCH() {
        
        super("LDCH", (byte)0x50, "3/4", 3);
        
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {

        int enderecoOuValorImediato = calcularTA(registradores, memoria);
        int operandoByte = 0;
        
        Map<String, Boolean> flags = getFlags();
        
        boolean isImediato = flags.get("i") && !flags.get("n"); 
        boolean isIndireto = flags.get("n") && !flags.get("i"); 

        if (isImediato) {
            
            operandoByte = enderecoOuValorImediato & 0xFF; 
            
        } else if (isIndireto) {
            
            int enderecoReal = memoria.getWord(enderecoOuValorImediato);
            
            operandoByte = memoria.getByte(enderecoReal);
            
        } else {
            
            operandoByte = memoria.getByte(enderecoOuValorImediato);
        
        }
        
        int valorA = registradores.getRegistradorPorNome("A").getValorIntSigned();
        
        int valorA_semUltimoByte = valorA & 0xFFFFFF00; 
        
        int novoValorA = valorA_semUltimoByte | (operandoByte & 0xFF); 

        registradores.getRegistradorPorNome("A").setValorInt(novoValorA);
        
    }
}