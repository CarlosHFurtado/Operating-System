package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class JSUB extends InstrucaoFormato3ou4 {

    // L <- (PC); PC <- Endereço Efetivo (ou indireto se @)

    public JSUB() {
        super("JSUB", (byte) 0x48);
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {

        int pcInicial = registradores.getValor("PC");

        // Determinar formato e ler instrução completa
        byte[] bytesIniciais = memoria.getBytes(2, pcInicial);
        int formato = getFormatoInstrucao(bytesIniciais);
        byte[] bytesCompletos = memoria.getBytes(formato, pcInicial);

        // Endereço de retorno é a próxima instrução
        int enderecoRetorno = pcInicial + formato;

        // Salvar retorno em L
        registradores.setValor("L", enderecoRetorno);

        // Avança PC (fetch)
        registradores.incrementar("PC", formato);

        int pcAposBusca = enderecoRetorno;

        // Calcula AE (isso também seta as flags n,i,x,b,p,e internamente)
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesCompletos, registradores, pcAposBusca);

        // ✅ Para JSUB, o destino é o AE (ou MEM[AE] se indireto).
        int enderecoDestino;

        boolean n = getFlags().get("n");
        boolean i = getFlags().get("i");

        if (n && !i) {
            // Indireto (@): PC <- MEM[AE]
            enderecoDestino = memoria.getValor3Bytes(enderecoEfetivo);
        } else {
            // Simples / Imediato: PC <- AE
            enderecoDestino = enderecoEfetivo;
        }

        // PC <- destino
        registradores.setValor("PC", enderecoDestino);

        PainelLog.logGlobal(String.format(
                "JSUB: L <- 0x%X (Retorno). PC <- 0x%X (Sub-rotina)",
                enderecoRetorno, enderecoDestino
        ));
    }
}
