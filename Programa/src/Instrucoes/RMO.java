package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class RMO extends Instrucao {

    public RMO() {
        
        super("RMO", (byte)0xAC, "2", 2);
        
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        byte[] bytes = memoria.getBytes(registradores.getValorPC(), 2); 

        int[] registradoresID = getRegistradores(bytes); 
        
        int r1ID_Destino = registradoresID[0];
        int r2ID_Fonte = registradoresID[1];  
        
        int valorR2 = registradores.getRegistrador(r2ID_Fonte).getValorIntSigned();

        registradores.getRegistrador(r1ID_Destino).setValorInt(valorR2);

        registradores.incrementarPC(getFormato(memoria.getBytes(registradores.getValorPC(), 2)));
        
    }
}