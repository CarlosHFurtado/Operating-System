package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;
import java.util.Map;

public class STB extends Instrucao {

    public STB() {
        
        super("STB", (byte)0x78, "3/4", 3);
        
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {

        int valorB = registradores.getRegistradorPorNome("B").getValorIntSigned();

        int enderecoCalculado = calcularTA(registradores, memoria);
        
        int enderecoDeEscrita = 0; 

        Map<String, Boolean> flags = getFlags();

        boolean isImediato = flags.get("i") && !flags.get("n"); 
        boolean isIndireto = flags.get("n") && !flags.get("i"); 

        if (isImediato) {
           
            System.err.println("AVISO/ERRO: Tentativa de armazenar no Modo Imediato (STB #const). Operação ignorada.");
            return; 

        } else if (isIndireto) {
            
            enderecoDeEscrita = memoria.getWord(enderecoCalculado);

        } else {
            
            enderecoDeEscrita = enderecoCalculado;
            
        }

        memoria.setWord(enderecoDeEscrita, valorB);
        
    }
}