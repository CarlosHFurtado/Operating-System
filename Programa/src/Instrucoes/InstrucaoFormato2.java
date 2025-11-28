package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;

public abstract class InstrucaoFormato2 extends Instrucao {

    public InstrucaoFormato2(String nome, byte opcode) {
        
        super(nome, opcode, "2", 2); 
        
    }
    
    protected int[] obterRegistradores(Memoria memoria, Registradores registradores) {
        
        
        byte[] bytes = memoria.getBytes(2, registradores.getValor("PC"));        
          
        return getRegistradores(bytes);
        
    }
}