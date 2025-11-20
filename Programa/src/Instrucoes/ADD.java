package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class ADD extends Instrucao {

    public ADD() {
        super("ADD", (byte) 0x18, "3", 3); 
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores, byte[] bytesInstrucao, int tamanho) {
        
        int pcAtual = registradores.getValor("PC");
        
        setFlags(bytesInstrucao);
        
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesInstrucao, registradores, pcAtual);
        
        int operando = obterOperando(memoria, registradores, enderecoEfetivo);
        
        int valorA = registradores.getValor("A");
        
        // Executa a soma
        registradores.setValor("A", valorA + operando);
        
        // Atualiza o PC
        registradores.incrementar("PC", tamanho); 
    }
}