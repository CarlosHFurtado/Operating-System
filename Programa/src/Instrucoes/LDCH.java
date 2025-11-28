package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class LDCH extends InstrucaoFormato3ou4 {
    
    // A[byte mais à direita] <- (m)
    
    public LDCH() {
        
        super("LDCH", (byte) 0x50);
        
    }

    @Override
    
    public void executar(Memoria memoria, Registradores registradores) {
        
        int pcInicial = registradores.getValor("PC");
        
        // Determinar o formato, atualizar PC e calcular AE (Formato 3/4)
        
        // Determinar formato e atualizar flags
        
        byte[] bytesIniciais = memoria.getBytes(2, pcInicial); 
        int formato = getFormatoInstrucao(bytesIniciais);

        // Obter todos os bytes
        
        byte[] bytesCompletos = memoria.getBytes(formato, pcInicial);
        
        // Incrementar PC
        
        registradores.incrementar("PC", formato);

        // Calcular AE
        
        int pcAposBusca = pcInicial + formato;
        int enderecoEfetivo = calcularEnderecoEfetivo(bytesCompletos, registradores, pcAposBusca);
        
        // A flag 'i' para LDCH deve ser tratada como indireto.
        
        int valorA = registradores.getValor("A");
        int novoByte;
        
        // Lógica de endereçamento e obtenção do byte
        
        if (getFlags().get("n") && !getFlags().get("i")) {

            int enderecoReal = memoria.getValor3Bytes(enderecoEfetivo);
            
            novoByte = memoria.getByte(enderecoReal) & 0xFF; 
            
        } else {
            
            novoByte = memoria.getByte(enderecoEfetivo) & 0xFF; 
            
        }
               
        valorA = valorA & 0xFFFF00; 
        
        // Setar o novo byte
        
        int novoValorA = valorA | novoByte;
        
        registradores.setValor("A", novoValorA);
        
        PainelLog.logGlobal(String.format("LDCH: A[byte dir] <- Byte (0x%X) de 0x%X. Novo A = 0x%X", 
            novoByte, enderecoEfetivo, novoValorA));
        
        logSeparador();
    }
}