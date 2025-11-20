package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class JEQ extends Instrucao {

    public JEQ() {
        super("JEQ", (byte) 0x30, "3", 3); 
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores, byte[] bytesInstrucao, int tamanho) {
        
        int pcAtual = registradores.getValor("PC");
        
        // 1. Define flags
        setFlags(bytesInstrucao);
        
        // 2. Obtém o Condition Code (CC) do registrador SW
        // O CC é geralmente armazenado nos bits 0-1 do SW (0=EQ, 1=GT, 2=LT)
        int cc = registradores.getValor("SW"); 
        
        if (cc == 0) { // Se for Igual (CC=0), salta
            
            // Calcula o endereço de destino (o operando é o endereço)
            int enderecoEfetivo = calcularEnderecoEfetivo(bytesInstrucao, registradores, pcAtual);
            
            // Salto: o PC recebe o endereço de destino.
            registradores.setValor("PC", enderecoEfetivo);
            
        } else { // Se não for Igual, apenas avança o PC
            
            registradores.incrementar("PC", tamanho);
        }
    }
}