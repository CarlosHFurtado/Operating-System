package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class LDA extends Instrucao {

    public LDA() {
        super("LDA", (byte) 0x00, "3", 3); 
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores, byte[] bytesInstrucao, int tamanho) {
        
        int pcAtual = registradores.getValor("PC");
        
        // 1. Define flags (n, i, x, b, p, e)
        setFlags(bytesInstrucao);
        
        // 2. Calcula endereço
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesInstrucao, registradores, pcAtual);
        
        // 3. Obtém operando (valor, endereço, ou valor indireto)
        int operando = obterOperando(memoria, registradores, enderecoEfetivo);
        
        // 4. Executa a operação
        registradores.setValor("A", operando);
        
        // 5. Atualiza o PC
        registradores.incrementar("PC", tamanho); 
    }
}