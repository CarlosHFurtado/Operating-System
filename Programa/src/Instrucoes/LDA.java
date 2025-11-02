package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class LDA extends Instrucao {
    
    public LDA() {
        super("LDA", (byte)0x00, "3/4", 3);
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        // 1. Obter PC atual
        int pcAtual = registradores.getValor("PC");
        
        // 2. Determinar formato e ler bytes
        int formato = getFormatoInstrucao(memoria.getBytes(pcAtual, 2));
        byte[] bytesInstrucao = memoria.getBytes(pcAtual, formato);
        
        // 3. Calcular endereço efetivo
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesInstrucao, registradores, pcAtual);
        
        // 4. Obter operando (usa método da classe base)
        int operando = obterOperando(memoria, registradores, enderecoEfetivo);
        
        // 5. Carregar no acumulador A
        registradores.setValor("A", operando); // ✅ MÉTODO CORRETO
        
        // 6. Atualizar PC
        registradores.incrementar("PC", formato);
    
    }
}