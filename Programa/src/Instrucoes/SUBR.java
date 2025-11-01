package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class SUBR extends Instrucao {

    public SUBR() {
        
        super("SUBR", (byte)0x94, "2", 2);
        
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        byte[] bytes = memoria.getBytes(registradores.getValorPC(), 2); 

        int[] registradoresID = getRegistradores(bytes);
        
        int r1ID_Subtraendo = registradoresID[0];
        int r2ID_Destino = registradoresID[1];   
        
        int valorR1 = registradores.getRegistrador(r1ID_Subtraendo).getValorIntSigned(); 
        int valorR2 = registradores.getRegistrador(r2ID_Destino).getValorIntSigned(); 

        int resultado = valorR2 - valorR1;

        registradores.getRegistrador(r2ID_Destino).setValorInt(resultado);

        registradores.incrementarPC(getFormato(memoria.getBytes(registradores.getValorPC(), 2)));
        
    }
}