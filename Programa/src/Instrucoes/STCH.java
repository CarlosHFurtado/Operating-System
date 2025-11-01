package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;
import java.util.Map;

public class STCH extends Instrucao {

    public STCH() {
        
        super("STCH", (byte)0x54, "3/4", 3);
    
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {

        int valorA_int = registradores.getRegistradorPorNome("A").getValorIntSigned();

        byte valorA_byte = (byte) (valorA_int & 0xFF); 

        int enderecoCalculado = calcularTA(registradores, memoria);
        
        int enderecoDeEscrita = 0; 

        Map<String, Boolean> flags = getFlags();

        boolean isImediato = flags.get("i") && !flags.get("n"); 
        boolean isIndireto = flags.get("n") && !flags.get("i"); 

        if (isImediato) {
            
            System.err.println("AVISO/ERRO: Tentativa de armazenar no Modo Imediato (STCH #const). Operação ignorada.");
            return; 

        } else if (isIndireto) {
            
            enderecoDeEscrita = memoria.getWord(enderecoCalculado);

        } else {
            
            enderecoDeEscrita = enderecoCalculado;
            
        }

        memoria.setByte(enderecoDeEscrita, valorA_byte);
        
    }
}