package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;

public abstract class InstrucaoFormato2 extends Instrucao {

    public InstrucaoFormato2(String nome, byte opcode) {
        
        super(nome, opcode, "2", 2); 
        
    }
    
    protected int[] obterRegistradores(Memoria memoria, Registradores registradores) {
        
        // O PC já aponta para o byte do opcode, precisamos dos próximos 2 bytes 
        // No caso, apenas 1 byte após o opcode, pois o formato 2 tem 2 bytes no total
        
        byte[] bytes = memoria.getBytes(2, registradores.getValor("PC"));        
          
        return getRegistradores(bytes);
        
    }
}