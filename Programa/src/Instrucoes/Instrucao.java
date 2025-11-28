package Instrucoes;

import java.util.HashMap;
import java.util.Map;

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

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

    public int getFormatoInstrucao(byte[] bytes) {
        
        PainelLog.logGlobal("DEBUG: bytes.length = " + (bytes == null ? "null" : bytes.length));
        
        if (bytes == null || bytes.length < 2) {
            
            return 3; 
            
        }

        byte flagsByte = bytes[1];
        
        if ((flagsByte & 0x10) != 0) {
            return 4;
        } else {
            return 3;
        }
    }
    
    protected void setFlags(byte[] bytesInstrucao) {
        if (bytesInstrucao == null || bytesInstrucao.length < 2) {
            flags.clear();
            return;
        }
        
        // Obtém os bytes de flags
        byte opcodeByte = bytesInstrucao[0];
        byte flagsByte = bytesInstrucao[1];

        // n: bit 1 do primeiro byte
        flags.put("n", (opcodeByte & 0x02) != 0);
        // i: bit 0 do primeiro byte
        flags.put("i", (opcodeByte & 0x01) != 0);
        
        // x: bit 5 do segundo byte
        flags.put("x", (flagsByte & 0x80) != 0);
        // b: bit 4 do segundo byte
        flags.put("b", (flagsByte & 0x40) != 0);
        // p: bit 3 do segundo byte
        flags.put("p", (flagsByte & 0x20) != 0);
        // e: bit 2 do segundo byte (Formato 4)
        flags.put("e", (flagsByte & 0x10) != 0);
    }
    
    protected void logSeparador() {
        PainelLog.logGlobal("");
    }
    
    private int getDisp(byte[] bytesInstrucao) {
      
        int disp = bytesInstrucao[1] & 0x0F; 
        disp = (disp << 8) | (bytesInstrucao[2] & 0xFF); 
        
        return disp;
        
    }
    
    private int getAddr(byte[] bytesInstrucao) {
        
        int addr = bytesInstrucao[1] & 0x0F;
        addr = (addr << 8) | (bytesInstrucao[2] & 0xFF); 
        addr = (addr << 8) | (bytesInstrucao[3] & 0xFF); 
        
        return addr;
        
    }
    
    protected int[] getRegistradores(byte[] bytesInstrucao) {
        
        int r1 = (bytesInstrucao[1] & 0xF0) >> 4;
        int r2 = bytesInstrucao[1] & 0x0F;
        
        return new int[]{r1, r2};
        
    }
    
    public int calcularEnderecoEfetivo(byte[] bytesInstrucao, Registradores registradores, int pcAposBusca) {
        
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

            endereco += pcAposBusca; 
            
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
            
            int enderecoIndireto = memoria.getValor3Bytes(enderecoEfetivo);
            
            return memoria.getValor3Bytes(enderecoIndireto);
            
        }

        return memoria.getValor3Bytes(enderecoEfetivo);
        
    }
    
}