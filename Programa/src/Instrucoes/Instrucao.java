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
    
    public int[] getRegistradores(byte[] bytes) { // Formato 2
        
        int[] registradores = new int[2];

        registradores[0] = (int)(bytes[1] & 0b11110000) >>> 4; 
        registradores[1] = (int)(bytes[1] & 0b00001111);

        return registradores;
        
    }

    public int getDisp(byte[] bytes) { // Formato 3
        
        int byte1 = (bytes[1] & 0b00001111)<<8;
        int byte2 = bytes[2];
        
        return byte1 | byte2;
        
    }

    public int getDispbpe(byte[] bytes) { 
       
        int byte1 = (bytes[1] & 0b01111111)<<7;
        int byte2 = bytes[2] & 0xFF;

        return byte1 | byte2;
        
    }

    public int getAddr(byte[] bytes) { // Formato 4
       
        int byte1 = (bytes[1] & 0b00001111) << 16;  
        int byte2 = (bytes[2] & 0xFF) << 8;         
        int byte3 = bytes[3] & 0xFF;                

        return byte1 | byte2 | byte3;               
    
    }

    public int calcularTA(Registradores registradores, Memoria memoria) { 
       
        int base = 0;
        int x = 0;
        int m = 0;
        int tamanhom = 0;
        int pc = registradores.getValorPC();
        
        setFlags(memoria.getBytes(pc, 2));

        if(!(flags.get("i") || flags.get("n"))) { 
           
            m = getDispbpe(memoria.getBytes(pc, 3));
            
            tamanhom = 15;
            
        } else if (flags.get("e")) {
           
            m = getAddr(memoria.getBytes(pc, 4)); 
            
            tamanhom = 20;
            
        } else { 
            
            m = getDisp(memoria.getBytes(pc, 3)); 
            
            tamanhom = 12;
            
        }
       
        registradores.incrementarPC(getFormato(memoria.getBytes(registradores.getValorPC(), 2)));
        
        if(flags.get("b")) { 
           
            base += registradores.getRegistradorPorNome("B").getValorIntSigned();
       
        } else if (flags.get("p")) { 
           
            base += registradores.getValorPC();
            
            m = (int) (m << (32 - tamanhom)) >> (32 - tamanhom); 
        
        }
    
        if(flags.get("x")) { 
            
            x = registradores.getRegistradorPorNome("X").getValorIntSigned();
        
        }

        if(flags.get("i") && !flags.get("n")) {             
            
            return m + base;                                        
        
        } else if (flags.get("n") && !flags.get("i")) {     
            
            return m + base;                                        
        
        }

        return m+base+x;   
       
    }
}
