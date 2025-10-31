package Instrucoes;

// @author Dienifer Ledebuhr

import java.util.HashMap;
import java.util.Map;

import Executor.Memoria;
import Executor.Registradores;

public abstract class Instrucao {
    
    // --- ATRIBUTOS ---
    
    private final String nome;
    private final byte opcode;
    private final String formato;
    private final int comprimento;
    
    Map<String, Boolean> flags = new HashMap<>();
    
    // --- CONSTRUTOR ---
    
    Instrucao(String nome, byte opcode, String formato, int comprimento) {
        this.nome = nome;
        this.opcode = opcode;
        this.formato = formato;
        this.comprimento = comprimento;
    }
    
    // --- MÉTODO PRINCIPAL ---
    
    public abstract void executar(Memoria memoria, Registradores registradores);

    // --- MÉTODOS GETTERS ---
    
    public String getNome() { 
        return nome; 
    }
    public byte getOpcode() { 
        return opcode; 
    }
    public String getFormato() { 
        return formato; 
    }
    public int getComprimento() { 
        return comprimento; 
    }
    public Map<String,Boolean> getFlags() { 
        return this.flags; 
    }

    public int getFormato(byte[] bytes) {
        
        setFlags(bytes);

        if(formato.contains("3/4")) { // Se 3/4
            
            if (flags.get("e")) { // Se e = 1
                
                return 4; // Formato = 4
                
            } else { // Se e = 0
                 
                return 3; // Formato = 3
            }
        }

        return Integer.parseInt(formato);  // Formato = 1 ou 2
        
    }
    
    public void setFlags(byte[] bytes) {
        
        // Bytes[0] -> Opcode (6 bits) + n + i (2 bits)
        
        flags.put("n", (bytes[0] & 0b00000010) != 0); // Bit 'n'
        flags.put("i", (bytes[0] & 0b00000001) != 0); // Bit 'i'
        
        // Bytes[1] -> x + b + p + e (4 bits) + parte do disp/addr (4 bits)
        
        flags.put("x", (bytes[1] & 0b10000000) != 0); // Bit 'x' 
        flags.put("b", (bytes[1] & 0b01000000) != 0); // Bit 'b' 
        flags.put("p", (bytes[1] & 0b00100000) != 0); // Bit 'p' 
        flags.put("e", (bytes[1] & 0b00010000) != 0); // Bit 'e' 
    
    }

}
