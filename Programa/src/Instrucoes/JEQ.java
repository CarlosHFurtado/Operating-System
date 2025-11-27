package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class JEQ extends InstrucaoFormato3ou4 {
    
    // PC <- Endereço Efetivo SE (CC == '=')
    
    public JEQ() {
        
        super("JEQ", (byte) 0x30);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        int pcInicial = registradores.getValor("PC");

        // Determinar formato, incrementar PC e calcular AE
        
        byte[] bytesIniciais = memoria.getBytes(2, pcInicial);
        int formato = getFormatoInstrucao(bytesIniciais);
        byte[] bytesCompletos = memoria.getBytes(formato, pcInicial);
        registradores.incrementar("PC", formato); // PC já avança
        int pcAposBusca = pcInicial + formato;
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesCompletos, registradores, pcAposBusca);
        
        // Obter o endereço de destino
        
        int enderecoDestino;

        if (getFlags().get("n") && !getFlags().get("i")) { 

            enderecoDestino = memoria.getValor3Bytes(enderecoEfetivo);

        } else { 

            enderecoDestino = enderecoEfetivo;
            
        }
        
        // Checar a condição '='
        
        if (registradores.checarCondicao("=")) {
            
            // Se a condição for verdadeira, salta.
            
            registradores.setValor("PC", enderecoDestino);
            
            PainelLog.logGlobal(String.format("JEQ: Condição '=' satisfeita. PC <- 0x%X", enderecoDestino));
            
        } else {
            
            // Se a condição for falsa, o PC permanece no valor incrementado (continua a próxima instrução).
            
            PainelLog.logGlobal(String.format("JEQ: Condição '=' NÃO satisfeita. PC continua em 0x%X", pcAposBusca));
        
        }
    }
}