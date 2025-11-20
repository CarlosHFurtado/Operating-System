package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class COMP extends Instrucao {

    public COMP() {
        super("COMP", (byte) 0x28, "3/4", 3); // Tamanho: 3
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores, byte[] bytesInstrucao, int tamanho) {
        
        int pcAtual = registradores.getValor("PC");
        char cc; // Condition Code
        
        try {
            int enderecoEfetivo = calcularEnderecoEfetivo(bytesInstrucao, registradores, pcAtual + tamanho);
            int operando = obterOperando(memoria, registradores, enderecoEfetivo);
            
            int valorA = registradores.getValor("A");
            
            // Compara A com o operando e define o Condition Code
            if (valorA < operando) {
                cc = '<';
            } else if (valorA > operando) {
                cc = '>';
            } else {
                cc = '=';
            }
            
            // Define o Condition Code no Registradores
            registradores.setConditionCode(cc);
            
        } catch (IllegalArgumentException e) {
            PainelLog.logGlobal("ERRO COMP: " + e.getMessage());
            // Em caso de erro, apenas avança o PC, mas o CC pode estar indefinido.
        }
        
        // PC avança pelo tamanho lido (3 ou 4)
        registradores.incrementar("PC", tamanho);
    }
}