package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class AND extends Instrucao {

    public AND() {
        super("AND", (byte) 0x40, "3/4", 3); // Opcode 40, Base 40
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores, byte[] bytesInstrucao, int tamanho) {
        
        // 1. Calcular Endereço Efetivo (E.A.) - Requer método em Instrucao.java
        int pcAtual = registradores.getValor("PC");
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesInstrucao, registradores, pcAtual);
        
        // 2. Obter Operando (valor em 'm') - Requer método em Instrucao.java
        int operando = obterOperando(memoria, registradores, enderecoEfetivo);
        
        // 3. Obter valor de A
        int valorA = registradores.getValor("A");
        
        // 4. A ← (A) & (m) (Bitwise AND)
        int resultado = valorA & operando;
        
        // 5. Salvar resultado em A
        registradores.setValor("A", resultado);
        
        // 6. Atualizar PC para próxima instrução
        registradores.incrementar("PC", tamanho);
    }
}