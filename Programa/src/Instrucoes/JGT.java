package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;
import java.util.Map;

public class JGT extends Instrucao {

    public JGT() {
        super("JGT", (byte)0x34, "3/4", 3);
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {

        int enderecoDeDestino = calcularTA(registradores, memoria);
        
        Map<String, Boolean> flags = getFlags();
        
        boolean isIndireto = flags.get("n") && !flags.get("i"); 

        if (isIndireto) {
            
            enderecoDeDestino = memoria.getWord(enderecoDeDestino);
        
        } 
        
        if (registradores.getRegistradorPorNome("SW").getValorIntSigned() == 2) {
            
            registradores.getRegistradorPorNome("PC").setValorInt(enderecoDeDestino);
        
        }
    }
}