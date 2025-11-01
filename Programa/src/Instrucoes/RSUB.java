package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class RSUB extends Instrucao {
    
    public RSUB() {
        
        super("RSUB", (byte)0x4C, "3/4", 3);
        
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        int enderecoRetorno = registradores.getRegistradorPorNome("L").getValorIntSigned(); 

        registradores.getRegistradorPorNome("PC").setValorInt(enderecoRetorno); 

    }
}