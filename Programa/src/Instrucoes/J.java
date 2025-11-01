package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;
import java.util.Map;

public class J extends Instrucao {

    public J() {
        
        super("J", (byte)0x3C, "3/4", 3);
    
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        int enderecoDeDestino = calcularTA(registradores, memoria);
        
        Map<String, Boolean> flags = getFlags();
        
        boolean isIndireto = flags.get("n") && !flags.get("i");

        if (isIndireto) {
            
            enderecoDeDestino = memoria.getWord(enderecoDeDestino);
        
        } 
        
        registradores.getRegistradorPorNome("PC").setValorInt(enderecoDeDestino); 
    
    }
}