package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;

public class ADDR extends Instrucao {

    public ADDR() {
        
        super("ADDR", (byte)0x90, "2",2);
        
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {

        byte[] bytes = memoria.getBytes(registradores.getValorPC(),2); 

        int[] registradoresID = getRegistradores(bytes); 

        int valorR1 = registradores.getRegistrador(registradoresID[0]).getValorIntSigned(); 
        int valorR2 = registradores.getRegistrador(registradoresID[1]).getValorIntSigned();

        int resultado = valorR1 + valorR2;

        registradores.getRegistrador(registradoresID[1]).setValorInt(resultado);                    

        registradores.incrementarPC(getFormato(memoria.getBytes(registradores.getValorPC(), 2))); 
    
    }  
}