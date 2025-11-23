package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;

public class ADDR extends Instrucao {

    public ADDR() {
        
        super("ADDR", (byte)0x90, "2", 2);
    
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        int pcAtual = registradores.getValor("PC");
        
        byte[] bytesInstrucao = memoria.getBytes(2, pcAtual);
        
        int[] registradoresID = getRegistradores(bytesInstrucao);
        int r1 = registradoresID[0]; // Registrador fonte
        int r2 = registradoresID[1]; // Registrador destino
        
        String nomeR1 = getNomeRegistrador(r1);
        String nomeR2 = getNomeRegistrador(r2);
        
        int valorR1 = registradores.getValor(nomeR1);
        int valorR2 = registradores.getValor(nomeR2);
        
        int resultado = valorR2 + valorR1;
        
        registradores.setValor(nomeR2, resultado);
        
        registradores.incrementar("PC", 2);
        
    }
    
    private String getNomeRegistrador(int id) {
        
        switch(id) {
            
            case 0: return "A";
            case 1: return "X"; 
            case 2: return "L";
            case 3: return "B";
            case 4: return "S";
            case 5: return "T";
            case 6: return "F";
            case 8: return "PC";
            case 9: return "SW";
            default: return "A"; 
            
        }
    }
}