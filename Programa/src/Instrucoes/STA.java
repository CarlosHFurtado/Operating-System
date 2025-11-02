package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class STA extends Instrucao {

    public STA() {
        super("STA", (byte)0x0C, "3/4", 3);
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        int pcAtual = registradores.getValor("PC");
        int formato = getFormatoInstrucao(memoria.getBytes(pcAtual, 2));
        byte[] bytesInstrucao = memoria.getBytes(pcAtual, formato);
        
        // calcularEnderecoEfetivo já chama setFlags() internamente
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesInstrucao, registradores, pcAtual);
        
        // AGORA as flags estão disponíveis via getFlags()
        boolean isImediato = getFlags().get("i") && !getFlags().get("n");
        boolean isIndireto = getFlags().get("n") && !getFlags().get("i");
        
        int valorA = registradores.getValor("A");
        
        if (isImediato) {
            System.err.println("ERRO: STA não suporta modo imediato");
        } else if (isIndireto) {
            int enderecoIndireto = memoria.getWord(enderecoEfetivo);
            memoria.setWord(enderecoIndireto, valorA);
        } else {
            memoria.setWord(enderecoEfetivo, valorA);
        }
        
        registradores.incrementar("PC", formato);
    }
}