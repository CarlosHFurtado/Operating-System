package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class STA extends Instrucao {

    public STA() {
        super("STA", (byte) 0x0C, "3", 3); 
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores, byte[] bytesInstrucao, int tamanho) {
        
        int pcAtual = registradores.getValor("PC");
        
        // 1. Define flags
        setFlags(bytesInstrucao);
        
        // 2. Calcula endereço
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesInstrucao, registradores, pcAtual);
        
        // 3. Obtém o valor do registrador A
        int valorA = registradores.getValor("A");
        
        // 4. Executa a operação: Apenas endereçamento direto/indireto é permitido para STA
        boolean n = getFlags().get("n");
        boolean i = getFlags().get("i");
        
        if (i && !n) { // Imediato (ex: STA #VAR) - Ilegal, mas tratamos como direto
            memoria.setWord(enderecoEfetivo, valorA);
        } else if (n && !i) { // Indireto (ex: STA @VAR)
            int enderecoIndireto = memoria.getWord(enderecoEfetivo);
            memoria.setWord(enderecoIndireto, valorA);
        } else { // Direto/Simples
            memoria.setWord(enderecoEfetivo, valorA);
        }
        
        // 5. Atualiza o PC
        registradores.incrementar("PC", tamanho); 
    }
}