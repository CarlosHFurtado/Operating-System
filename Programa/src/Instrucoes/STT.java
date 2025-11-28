package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

// STA - STB - STL - STS - STT - STX -> Mesmo codigo, muda apenas opcode e o registrador

public class STT extends InstrucaoFormato3ou4 {
        
    public STT() {
        
        super("STT", (byte) 0x84);
        
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
        
        // Obter o valor do registrador a ser armazenado
        
        int valorT = registradores.getValor("T"); 

        // Determinar o endereço de destino
        
        int enderecoDestino = enderecoEfetivo;
        
        // Se for Indireto (n=1, i=0), o AE aponta para o endereço real
        
        if (getFlags().get("n") && !getFlags().get("i")) {
            
            // Se o modo for indireto, o AE contém o endereço onde o valor será armazenado
            
            enderecoDestino = memoria.getValor3Bytes(enderecoEfetivo);
            
        }
        
        // Armazenar o valor 
        
        memoria.setValor3Bytes(enderecoDestino, valorT); 
        
        PainelLog.logGlobal(String.format("STT: Armazenando (T) = 0x%X em 0x%X (AE = 0x%X)", 
            valorT, enderecoDestino, enderecoEfetivo));
        
    }
}