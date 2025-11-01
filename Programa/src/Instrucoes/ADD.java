package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class ADD extends Instrucao {
    
    public ADD() {
        
        super("ADD", (byte)0x18, "3/4", 3);
        
    }
    
    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        int pcAtual = registradores.getValor("PC");

        int formato = getFormatoInstrucao(memoria.getBytes(pcAtual, 2));
        
        byte[] bytesInstrucao = memoria.getBytes(pcAtual, formato);
           
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesInstrucao, registradores, pcAtual);
        
        int operando = obterOperando(memoria, registradores, enderecoEfetivo);
        
        int acumulador = registradores.getValor("A"); 
        
        int resultado = acumulador + operando;
        
        registradores.setValor("A", resultado); 
         
        registradores.incrementar("PC", formato); 
    
    }        
}