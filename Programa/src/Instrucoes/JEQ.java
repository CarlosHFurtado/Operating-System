package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class JEQ extends Instrucao {

    public JEQ() {
        super("JEQ", (byte)0x30, "3/4", 3);
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        int pcAtual = registradores.getValor("PC");
        
        int formato = getFormatoInstrucao(memoria.getBytes(2, pcAtual));
        byte[] bytesInstrucao = memoria.getBytes(formato, pcAtual);
        
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesInstrucao, registradores, pcAtual);
        
        int sw = registradores.getValor("SW");
        int codigoCondicional = sw & 0x000003; 
        
        if (codigoCondicional == 0) {
            // SALTA: PC recebe o endereço de destino
            registradores.setValor("PC", enderecoEfetivo);
        } else {
            registradores.incrementar("PC", formato);
        }
    }
}