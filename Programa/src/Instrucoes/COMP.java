package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class COMP extends Instrucao {

    public COMP() {
        super("COMP", (byte)0x28, "3/4", 3);
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        // 1. Obter PC atual
        int pcAtual = registradores.getValor("PC");
        
        // 2. Determinar formato e ler bytes
        int formato = getFormatoInstrucao(memoria.getBytes(pcAtual, 2));
        byte[] bytesInstrucao = memoria.getBytes(pcAtual, formato);
        
        // 3. Calcular endereço efetivo
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesInstrucao, registradores, pcAtual);
        
        // 4. Obter operando (usa método da classe base)
        int operando = obterOperando(memoria, registradores, enderecoEfetivo);
        
        // 5. Obter valor do acumulador
        int valorAcumulador = registradores.getValor("A"); // ✅ MÉTODO CORRETO
        
        // 6. Realizar comparação e atualizar SW (Status Word)
        int codigoCondicional;
        if (valorAcumulador == operando) {
            codigoCondicional = 0; // Igual
        } else if (valorAcumulador < operando) {
            codigoCondicional = 1; // Menor
        } else {
            codigoCondicional = 2; // Maior
        }
        
        // 7. Atualizar código condicional no registrador SW
        // O SW tem vários campos, assumindo que CC são os bits menos significativos
        int swAtual = registradores.getValor("SW");
        swAtual = (swAtual & 0xFFFF00) | codigoCondicional; // Preserva outros bits, atualiza CC
        registradores.setValor("SW", swAtual);
        
        // 8. Atualizar PC
        registradores.incrementar("PC", formato);
    }
}