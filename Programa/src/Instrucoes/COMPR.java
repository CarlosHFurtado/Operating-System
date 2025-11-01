package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class COMPR extends Instrucao {

    public COMPR() {
        
        super("COMPR", (byte)0xA0, "2",2);
    
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        byte[] bytes = memoria.getBytes(registradores.getValorPC(),2); 

        int[] registradoresID = getRegistradores(bytes); 
        
        int r1ID = registradoresID[0];
        int r2ID = registradoresID[1]; 

        int valorR1 = registradores.getRegistrador(r1ID).getValorIntSigned(); 
        int valorR2 = registradores.getRegistrador(r2ID).getValorIntSigned(); 

        if (valorR1 == valorR2) {
            
            registradores.getRegistradorPorNome("SW").setValorInt(0); 
       
        } else if (valorR1 < valorR2) {
            
            registradores.getRegistradorPorNome("SW").setValorInt(1); 
       
        } else {
        
            registradores.getRegistradorPorNome("SW").setValorInt(2);
       
        }

        registradores.incrementarPC(getFormato(memoria.getBytes(registradores.getValorPC(), 2))); 
    
    }    
}