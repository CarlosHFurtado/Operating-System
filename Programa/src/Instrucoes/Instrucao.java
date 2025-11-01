package Instrucoes;

import java.util.HashMap;
import java.util.Map;

import Executor.Memoria;
import Executor.Registradores;

public abstract class Instrucao {
    
    private final String nome;
    private final byte opcode;
    private final String formato;
    private final int length;
    private Map<String, Boolean> flags = new HashMap<>();

    Instrucao(String nome, byte opcode, String formato, int length) {
        
        this.nome = nome;
        this.opcode = opcode;
        this.formato = formato;
        this.length = length;
        
    }

    public abstract void executar(Memoria memoria, Registradores registradores);

    public String getNome() { 
        return nome; 
    }
    
    public byte getOpcode() { 
        return opcode; 
    }
    
    public String getFormato() { 
        return formato; 
    }
    
    public int getLength() { 
        return length; 
    }
    
    public Map<String, Boolean> getFlags() { 
        return flags; 
    }

    public void setFlags(byte[] bytes) {
        
        flags.put("n", (bytes[0] & 0b00000010) != 0); 
        flags.put("i", (bytes[0] & 0b00000001) != 0);
        flags.put("x", (bytes[1] & 0b10000000) != 0);
        flags.put("b", (bytes[1] & 0b01000000) != 0);
        flags.put("p", (bytes[1] & 0b00100000) != 0);
        flags.put("e", (bytes[1] & 0b00010000) != 0);
        
    }

    public int getFormatoInstrucao(byte[] bytes) {
        setFlags(bytes);

       
        if(formato.equals("1") || formato.equals("2")) {
            
            return Integer.parseInt(formato);
        
        }
        
        return flags.get("e") ? 4 : 3;
        
    }

    public int[] getRegistradores(byte[] bytes) {
        
        int[] registradores = new int[2];
        
        registradores[0] = (bytes[1] & 0b11110000) >>> 4;
        registradores[1] = (bytes[1] & 0b00001111);
        
        return registradores;
        
    }

    public int getDisp(byte[] bytes) {
        return ((bytes[1] & 0x0F) << 8) | (bytes[2] & 0xFF);
    }

    public int getAddr(byte[] bytes) {
        return ((bytes[1] & 0x0F) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
    }

    public int calcularEnderecoEfetivo(byte[] bytesInstrucao, Registradores registradores, int pcAtual) {
        
        setFlags(bytesInstrucao);
        
        int endereco = 0;
        
        boolean formato4 = flags.get("e");
        
        if (formato4) {
            
            endereco = getAddr(bytesInstrucao);
            
        } else {           
            
            endereco = getDisp(bytesInstrucao);

            if ((endereco & 0x800) != 0) {
                
                endereco |= 0xFFFFF000;
                
            }
        }
        
        if (flags.get("b")) {
            
            endereco += registradores.getValor("B");
            
        } else if (flags.get("p")) {
            
            endereco += pcAtual;
            
        }
        
        if (flags.get("x")) {
            
            endereco += registradores.getValor("X");
        
        }
        
        return endereco;
    }
    
    public int obterOperando(Memoria memoria, Registradores registradores, int enderecoEfetivo) {
        
        if (flags.get("i") && !flags.get("n")) {
            
            return enderecoEfetivo;
            
        }
        
        if (flags.get("n") && !flags.get("i")) {
            
            int enderecoIndireto = memoria.getWord(enderecoEfetivo);
            return memoria.getWord(enderecoIndireto);
            
        }
        
        return memoria.getWord(enderecoEfetivo);
        
    }
}