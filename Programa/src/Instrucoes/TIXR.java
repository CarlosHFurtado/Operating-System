package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class TIXR extends Instrucao {

    public TIXR() {
        
        super("TIXR", (byte)0xB8, "2", 2);
        
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {

        byte[] bytes = memoria.getBytes(registradores.getValorPC(), 2); 
        
        int[] registradoresID = getRegistradores(bytes); 

        int r1ID_Comparacao = registradoresID[0]; 
        
        int valorR1 = registradores.getRegistrador(r1ID_Comparacao).getValorIntSigned(); 

        int valorX_Original = registradores.getRegistradorPorNome("X").getValorIntSigned();
        
        int valorX_Novo = valorX_Original + 1;

        registradores.getRegistradorPorNome("X").setValorInt(valorX_Novo);

        if (valorX_Novo == valorR1) {
            
            registradores.getRegistradorPorNome("SW").setValorInt(0); 
            
        } else if (valorX_Novo < valorR1) {
            
            registradores.getRegistradorPorNome("SW").setValorInt(1); 
            
        } else {
            
            registradores.getRegistradorPorNome("SW").setValorInt(2); 
            
        }
        
        registradores.incrementarPC(getFormato(memoria.getBytes(registradores.getValorPC(), 2)));
        
    }
}