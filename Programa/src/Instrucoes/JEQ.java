package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class JEQ extends Instrucao {

    public JEQ() {
        super("JEQ", (byte)0x30, "3/4", 3);
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        // 1. Obter PC atual
        int pcAtual = registradores.getValor("PC");
        
        // 2. Determinar formato e ler bytes
        int formato = getFormatoInstrucao(memoria.getBytes(pcAtual, 2));
        byte[] bytesInstrucao = memoria.getBytes(pcAtual, formato);
        
        // 3. Calcular endereço efetivo (destino do salto)
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesInstrucao, registradores, pcAtual);
        
        // 4. Obter código condicional do SW
        int sw = registradores.getValor("SW");
        int codigoCondicional = sw & 0x000003; // Pega apenas bits 0-1 (CC)
        
        // 5. Verificar se deve saltar (igual = código 0)
        if (codigoCondicional == 0) {
            // SALTA: PC recebe o endereço de destino
            registradores.setValor("PC", enderecoEfetivo);
        } else {
            // NÃO SALTA: Avança PC normalmente
            registradores.incrementar("PC", formato);
        }
    }
}