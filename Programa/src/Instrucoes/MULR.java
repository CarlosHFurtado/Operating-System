package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class MULR extends Instrucao {

    public MULR() {
        
        super("MULR", (byte)0x98, "2", 2);
        
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        byte[] bytes = memoria.getBytes(registradores.getValorPC(), 2);

        int[] registradoresID = getRegistradores(bytes);
        
        int r1ID = registradoresID[0]; 
        int r2ID = registradoresID[1];        

        long valorR1_long = registradores.getRegistrador(r1ID).getValorIntSigned();
        long valorR2_long = registradores.getRegistrador(r2ID).getValorIntSigned();
        
        long resultado_long = valorR2_long * valorR1_long; // r2 * r1

        int msw = (int) (resultado_long >> 24); 
        
        int lsw = (int) (resultado_long & 0xFFFFFF); 

        registradores.getRegistradorPorNome("A").setValorInt(msw); 
        
        registradores.getRegistradorPorNome("L").setValorInt(lsw); 
        
        registradores.incrementarPC(getFormato(memoria.getBytes(registradores.getValorPC(), 2)));
    
    }
}