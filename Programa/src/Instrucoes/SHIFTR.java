package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class SHIFTR extends Instrucao {

    public SHIFTR() {
        
        super("SHIFTR", (byte)0xA8, "2", 2);
        
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        byte[] bytes = memoria.getBytes(registradores.getValorPC(), 2); 

        int[] registradoresID = getRegistradores(bytes); 
        
        int r1ID_Destino = registradoresID[0]; 
        
        int valorRegistradorR = registradores.getRegistrador(r1ID_Destino).getValorIntSigned();
        
        int n = bytes[1] & 0x0F; 

        int resultado = valorRegistradorR >> n; 
        
        registradores.getRegistrador(r1ID_Destino).setValorInt(resultado);

        registradores.incrementarPC(getFormato(memoria.getBytes(registradores.getValorPC(), 2)));
        
    }
}