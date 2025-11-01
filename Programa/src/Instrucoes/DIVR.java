package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class DIVR extends Instrucao {

    public DIVR() {
        
        super("DIVR", (byte)0x9C, "2", 2);
    
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {

        byte[] bytes = memoria.getBytes(registradores.getValorPC(), 2);

        int[] registradoresID = getRegistradores(bytes);
        
        int r1ID = registradoresID[0]; 
        int r2ID = registradoresID[1]; 

        int valorR1 = registradores.getRegistrador(r1ID).getValorIntSigned(); 
        int valorR2 = registradores.getRegistrador(r2ID).getValorIntSigned(); 
        
        if (valorR1 == 0) {
            
            System.err.println("ERRO: Divisão por zero na instrução DIVR.");
            return;
            
        }

        int quociente = valorR2 / valorR1; 
        int resto = valorR2 % valorR1;     

        registradores.getRegistrador(r2ID).setValorInt(quociente);

        registradores.getRegistradorPorNome("L").setValorInt(resto);

        registradores.incrementarPC(getFormato(memoria.getBytes(registradores.getValorPC(), 2)));
    
    }
}