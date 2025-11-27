package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public abstract class InstrucaoFormato3ou4 extends Instrucao {
    
    public InstrucaoFormato3ou4(String nome, byte opcode) {
        // Formato: "3/4", Tamanho: 0 (o tamanho real é determinado pela flag 'e')
        super(nome, opcode, "3/4", 0);
    }

    /**
     * Lógica comum para Formato 3/4: determina o formato, atualiza o PC, 
     * calcula o Endereço Efetivo (AE) e obtém o Operando (valor ou endereço).
     * @param memoria Objeto de memória.
     * @param registradores Objeto de registradores.
     * @return O valor do operando ou o endereço efetivo (depende do modo de endereçamento).
     */
    protected int calcularEObterOperando(Memoria memoria, Registradores registradores) {
        
        int pcInicial = registradores.getValor("PC");

        // 1. Determinar o formato (3 ou 4 bytes) e obter os bytes da instrução.
        // O opcode está em pcInicial.
        byte[] bytesIniciais = memoria.getBytes(2, pcInicial); 
        int formato = getFormatoInstrucao(bytesIniciais); // Isto também seta as flags 'nixbpe'

        // 2. Obter todos os bytes da instrução.
        byte[] bytesCompletos = memoria.getBytes(formato, pcInicial);
        
        // 3. O PC é incrementado para o endereço da próxima instrução.
        registradores.incrementar("PC", formato);

        // 4. Calcular o Endereço Efetivo (AE).
        // Passamos pcInicial + formato, que é o PC após a busca (o valor necessário para PC-relativo).
        int pcAposBusca = pcInicial + formato;
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesCompletos, registradores, pcAposBusca); 
        
        PainelLog.logGlobal(String.format("Instrução %s (F%d) em 0x%X. AE=0x%X. Flags: n%d i%d x%d b%d p%d e%d",
            getNome(), formato, pcInicial, enderecoEfetivo,
            getFlags().get("n")?1:0, getFlags().get("i")?1:0, getFlags().get("x")?1:0, 
            getFlags().get("b")?1:0, getFlags().get("p")?1:0, getFlags().get("e")?1:0));

        // 5. Obter o operando (valor ou endereço)
        int operando = obterOperando(memoria, registradores, enderecoEfetivo);
        
        return operando;
    }
    
    // O método 'executar' deve ser implementado nas classes filhas.
}