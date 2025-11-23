package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class LDA extends Instrucao {
    
    public LDA() {
        super("LDA", (byte)0x00, "3/4", 3);
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        int pcAtual = registradores.getValor("PC");
        
        int formato = getFormatoInstrucao(memoria.getBytes(2, pcAtual));
        byte[] bytesInstrucao = memoria.getBytes(formato, pcAtual);
        
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesInstrucao, registradores, pcAtual);
        
        int operando = obterOperando(memoria, registradores, enderecoEfetivo);
        
        registradores.setValor("A", operando); 
        
        registradores.incrementar("PC", formato);
    
    }
}