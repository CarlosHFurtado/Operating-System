package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class RSUB extends Instrucao {

    public RSUB() {
        // RSUB é Opcode 0x4C, Formato 3, Tamanho 3.
        super("RSUB", (byte) 0x4C, "3", 3); 
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores, byte[] bytesInstrucao, int tamanho) {
        
     // 1. O RSUB deve retornar ao endereço em L
        int enderecoRetorno = registradores.getValor("L");
        
        // 2. PC <-- (L)
        registradores.setValor("PC", enderecoRetorno);
        

    }
}