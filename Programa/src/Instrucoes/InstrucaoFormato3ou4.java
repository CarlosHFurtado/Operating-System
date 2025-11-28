package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public abstract class InstrucaoFormato3ou4 extends Instrucao {
    
    public InstrucaoFormato3ou4(String nome, byte opcode) {

        super(nome, opcode, "3/4", 0);
    }

    protected int calcularEObterOperando(Memoria memoria, Registradores registradores) {
        
        int pcInicial = registradores.getValor("PC");

        byte[] bytesIniciais = memoria.getBytes(2, pcInicial); 
        int formato = getFormatoInstrucao(bytesIniciais);

        byte[] bytesCompletos = memoria.getBytes(formato, pcInicial);
        
        registradores.incrementar("PC", formato);

        int pcAposBusca = pcInicial + formato;
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesCompletos, registradores, pcAposBusca); 
        
        PainelLog.logGlobal(String.format("Instrução %s (F%d) em 0x%X. AE=0x%X. Flags: n%d i%d x%d b%d p%d e%d",
            getNome(), formato, pcInicial, enderecoEfetivo,
            getFlags().get("n")?1:0, getFlags().get("i")?1:0, getFlags().get("x")?1:0, 
            getFlags().get("b")?1:0, getFlags().get("p")?1:0, getFlags().get("e")?1:0));

        int operando = obterOperando(memoria, registradores, enderecoEfetivo);
        
        return operando;
    }
    
}