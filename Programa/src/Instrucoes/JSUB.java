package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;
import java.util.Map;

public class JSUB extends Instrucao {

    public JSUB() {
        
        super("JSUB", (byte)0x48, "3/4", 3);
    
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        int enderecoDeDestino = calcularTA(registradores, memoria);
        
        Map<String, Boolean> flags = getFlags();
        
        boolean isIndireto = flags.get("n") && !flags.get("i"); 

        if (isIndireto) {
            
            enderecoDeDestino = memoria.getWord(enderecoDeDestino);
       
        } 
        
        int enderecoRetorno = registradores.getValorPC();
        
        registradores.getRegistradorPorNome("L").setValorInt(enderecoRetorno); 

        registradores.getRegistradorPorNome("PC").setValorInt(enderecoDeDestino);
    
    }
}