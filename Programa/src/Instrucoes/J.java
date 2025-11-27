package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class J extends InstrucaoFormato3ou4 {
    
    // PC <- Endereço Efetivo
    
    public J() {
        
        super("J", (byte) 0x3C);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        int pcInicial = registradores.getValor("PC");

        // Determinar formato, incrementar PC e calcular AE
        
        byte[] bytesIniciais = memoria.getBytes(2, pcInicial);
        int formato = getFormatoInstrucao(bytesIniciais);
        byte[] bytesCompletos = memoria.getBytes(formato, pcInicial);
        registradores.incrementar("PC", formato);
        int pcAposBusca = pcInicial + formato;
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesCompletos, registradores, pcAposBusca);
        
        // O operando (m) é o endereço de destino (AE, Imediato, ou Indireto)

        int enderecoDestino = obterOperando(memoria, registradores, enderecoEfetivo);
        
        // Atualizar o PC
        
        registradores.setValor("PC", enderecoDestino);
        
        PainelLog.logGlobal(String.format("J: Salto Incondicional. PC <- 0x%X", enderecoDestino));
        
    }
}