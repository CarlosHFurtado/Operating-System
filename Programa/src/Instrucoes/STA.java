package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class STA extends Instrucao {

    public STA() {
        super("STA", (byte)0x0C, "3/4", 3);
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        int pcAtual = registradores.getValor("PC");
        int formato = getFormatoInstrucao(memoria.getBytes(2, pcAtual));
        byte[] bytesInstrucao = memoria.getBytes(formato, pcAtual);
        
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesInstrucao, registradores, pcAtual);
        
        boolean isImediato = getFlags().get("i") && !getFlags().get("n");
        boolean isIndireto = getFlags().get("n") && !getFlags().get("i");
        
        int valorA = registradores.getValor("A");
        
        if (isImediato) {
            PainelLog.logGlobal("ERRO: STA não suporta modo imediato");
        } else if (isIndireto) {
            int enderecoIndireto = memoria.getWord(enderecoEfetivo);
            memoria.setWord(enderecoIndireto, valorA);
        } else {
            memoria.setWord(enderecoEfetivo, valorA);
        }
        
        registradores.incrementar("PC", formato);
    }
}