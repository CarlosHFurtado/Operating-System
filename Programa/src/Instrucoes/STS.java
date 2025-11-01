package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;
import java.util.Map;

public class STS extends Instrucao {

    public STS() {
        
        super("STS", (byte)0x7C, "3/4", 3);
        
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {

        int valorS = registradores.getRegistradorPorNome("S").getValorIntSigned();

        int enderecoCalculado = calcularTA(registradores, memoria);
        
        int enderecoDeEscrita = 0; 

        Map<String, Boolean> flags = getFlags();

        boolean isImediato = flags.get("i") && !flags.get("n"); 
        boolean isIndireto = flags.get("n") && !flags.get("i"); 

        if (isImediato) {
           
            System.err.println("AVISO/ERRO: Tentativa de armazenar no Modo Imediato (STS #const). Operação ignorada.");
            return; 

        } else if (isIndireto) {
            
            enderecoDeEscrita = memoria.getWord(enderecoCalculado);

        } else {
            
            enderecoDeEscrita = enderecoCalculado;
             
        }

         memoria.setWord(enderecoDeEscrita, valorS);
         
    }
}