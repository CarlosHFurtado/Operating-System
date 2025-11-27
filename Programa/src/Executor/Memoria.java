package Executor;

import interfacesicxe.PainelLog;
import java.util.Arrays;

public class Memoria {
    private byte[] memoria;
    private int tamanho;

    public Memoria(int tamanho) {
        if (tamanho <= 0) {
            throw new IllegalArgumentException("tamanho deve ser > 0");
        }
        this.memoria = new byte[tamanho];
        this.tamanho = tamanho;
    }
    
    public byte[] getMem() {
        return memoria;
    }
    
    public void limpaMem() {
        Arrays.fill(memoria, (byte) 0);
    }
    
    public byte getByte(int pos) {
        if (pos < 0 || pos >= tamanho) {
            return 0;
        }
        return (byte) (memoria[pos] & 0xFF);
    }
        
    public byte[] getBytes(int qtd, int pos) {
        
        PainelLog.logGlobal("DEBUG: getBytes(qtd=" + qtd + ", pos=" + pos + ")");
        
        if (qtd <= 0) {
            
            return new byte[0];
            
        }
        
        byte[] bytes = new byte[qtd];
        
        for (int i = 0; i < qtd; i++) {
            
            if (pos + i < tamanho) {
                
                bytes[i] = getByte(pos + i);
                
            } else {
                
                bytes[i] = 0;
                
            }
            
            PainelLog.logGlobal("  byte[" + i + "] = " + String.format("%02X", bytes[i]));
            
        }
        
        return bytes;
        
    }

    // Obter um valor de 3 bytes

    public int getValor3Bytes(int pos) {
        
        if (pos < 0 || pos + 2 >= tamanho) {
            
            return 0; 
            
        }

        int byte3 = (memoria[pos] & 0xFF) << 16;
        int byte2 = (memoria[pos + 1] & 0xFF) << 8;
        int byte1 = (memoria[pos + 2] & 0xFF);

        return byte3 | byte2 | byte1;
        
    }

    public void setValor3Bytes(int pos, int valor) {
        
        if (pos < 0 || pos + 2 >= tamanho) {

            return; 

        }

        valor &= 0xFFFFFF;

        memoria[pos] = (byte) ((valor >> 16) & 0xFF);

        memoria[pos + 1] = (byte) ((valor >> 8) & 0xFF);

        memoria[pos + 2] = (byte) (valor & 0xFF);

    }
    
    public void setByte(int pos, byte b) {
        if (pos >= 0 && pos < tamanho) {
            memoria[pos] = b;
        }
    }

    public void setByteInt(int pos, int valor) {
        setByte(pos, (byte) (valor & 0xFF));
    }
    
    public void setWord(int pos, int valor) {
        setByteInt(pos, valor >>> 16);
        setByteInt(pos + 1, valor >>> 8);
        setByteInt(pos + 2, valor);
    }

    public int getWord(int pos) {
        if (pos + 2 >= tamanho) return 0;
        int MSB = (getByte(pos) & 0xFF) << 16;
        int MID = (getByte(pos + 1) & 0xFF) << 8;
        int LSB = (getByte(pos + 2) & 0xFF);
        return MSB | MID | LSB;
    }
}