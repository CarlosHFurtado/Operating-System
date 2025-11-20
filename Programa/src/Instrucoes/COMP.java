package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class COMP extends Instrucao {

    public COMP() {
        super("COMP", (byte)0x28, "3/4", 3);
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        int pcAtual = registradores.getValor("PC");
        
        int formato = getFormatoInstrucao(memoria.getBytes(2, pcAtual));
        byte[] bytesInstrucao = memoria.getBytes(formato, pcAtual);
        
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesInstrucao, registradores, pcAtual);
        
        int operando = obterOperando(memoria, registradores, enderecoEfetivo);
        
        int valorAcumulador = registradores.getValor("A"); 
        
        int codigoCondicional;
        if (valorAcumulador == operando) {
            codigoCondicional = 0; // Igual
        } else if (valorAcumulador < operando) {
            codigoCondicional = 1; // Menor
        } else {
            codigoCondicional = 2; // Maior
        }
        
        int swAtual = registradores.getValor("SW");
        swAtual = (swAtual & 0xFFFF00) | codigoCondicional; 
        registradores.setValor("SW", swAtual);
        
        registradores.incrementar("PC", formato);
    }
}