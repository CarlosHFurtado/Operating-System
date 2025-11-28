package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class JSUB extends InstrucaoFormato3ou4 {
    
    // L <- (PC); PC <- Endereço Efetivo
    
    public JSUB() {
        
        super("JSUB", (byte) 0x48);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        int pcInicial = registradores.getValor("PC");

        // Determinar formato e calcular AE
        
        byte[] bytesIniciais = memoria.getBytes(2, pcInicial);
        int formato = getFormatoInstrucao(bytesIniciais);
        byte[] bytesCompletos = memoria.getBytes(formato, pcInicial);
        
        // O valor do PC a ser salvo é o endereço DA PRÓXIMA INSTRUÇÃO
        
        int enderecoRetorno = pcInicial + formato;
        
        // Salvar o endereço de retorno em L
        
        registradores.setValor("L", enderecoRetorno);
        
        // Incrementar PC
        
        registradores.incrementar("PC", formato); 
        
        int pcAposBusca = enderecoRetorno; 
        
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesCompletos, registradores, pcAposBusca);
        
        // Obter o endereço de destino
        
        int enderecoDestino = obterOperando(memoria, registradores, enderecoEfetivo);
        
        // Atualizar o PC para o endereço da sub-rotina
        
        registradores.setValor("PC", enderecoDestino);
        
        PainelLog.logGlobal(String.format("JSUB: L <- 0x%X (Retorno). PC <- 0x%X (Sub-rotina)", 
            enderecoRetorno, enderecoDestino));
        
        logSeparador();
        
    }
}