package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;

public class CLEAR extends Instrucao {

    public CLEAR() {
        
        super("CLEAR", (byte)0x4, "2",2);
        
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        byte[] bytes = memoria.getBytes(registradores.getValorPC(),2);

        int[] registradoresID = getRegistradores(bytes);

        registradores.getRegistrador(registradoresID[0]).setValorInt(0); 

        registradores.incrementarPC(getFormato(memoria.getBytes(registradores.getValorPC(), 2))); 
    
    }    
}
