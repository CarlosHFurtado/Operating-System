package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class STCH extends InstrucaoFormato3ou4 {
    
    // m <- (A) [byte mais à direita]
    
    public STCH() {
        
        super("STCH", (byte) 0x54);
        
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
        
        // Obter o valor do byte de A
        
        int valorA = registradores.getValor("A");
        byte byteA = (byte) (valorA & 0xFF); 
        
        // Determinar o endereço de destino
        
        int enderecoDestino = enderecoEfetivo;
        
        // Se for Indireto (n=1, i=0), o AE aponta para o endereço real.

        if (getFlags().get("n") && !getFlags().get("i")) {
            
            // O valor em AE é o endereço real do byte
            
            enderecoDestino = memoria.getValor3Bytes(enderecoEfetivo);
            
        }
        
        // Armazenar o valor
        
        memoria.setByte(enderecoDestino, byteA); 

        PainelLog.logGlobal(String.format("STCH: Armazenando Byte (A)=0x%X em 0x%X (AE=0x%X)", 
            byteA, enderecoDestino, enderecoEfetivo));
        
        logSeparador();
        
    }
}