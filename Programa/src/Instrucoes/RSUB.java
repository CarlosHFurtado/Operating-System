package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class RSUB extends InstrucaoFormato3ou4 {

    // PC <- (L)

    public RSUB() {
        super("RSUB", (byte) 0x4C);
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {

        int pcInicial = registradores.getValor("PC");

        // Determina formato só para avançar corretamente (caso exista +RSUB)
        byte[] bytesIniciais = memoria.getBytes(2, pcInicial);
        int formato = getFormatoInstrucao(bytesIniciais);

        // Endereço de retorno em L
        int enderecoRetorno = registradores.getValor("L");

        // Avança PC do fetch (não afeta o salto final, mas mantém consistência do simulador)
        registradores.incrementar("PC", formato);

        // PC <- L
        registradores.setValor("PC", enderecoRetorno);

        PainelLog.logGlobal(String.format(
                "RSUB: Retorno de Sub-rotina. PC <- (L)=0x%X",
                enderecoRetorno
        ));
    }
}
