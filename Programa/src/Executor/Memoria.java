package Executor;

import java.util.Arrays;
public class Memoria {
    private byte[] memoria;
    private int tamanho;

    public Memoria( int tamanho ) {
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
        Arrays.fill( memoria,( byte ) 0 );
    }
    
    public byte getByte( int pos ){
        return ( byte )( ( memoria[pos] ) & 0xFF );
    }
        
    public byte[] getBytes(  int qtd, int pos ) {
        if (qtd < 0) {
            throw new IllegalArgumentException("quantidade deve ser >= 0");
        }
        byte[] bytes = new byte[qtd];

        for( int i = 0; i < qtd && pos+i <= tamanho; i++ ) {
            bytes[i] = getByte( pos+i );
        }

        return bytes;
    }
    
    public byte getOpcode( int pos ) {
        byte byte1 = getByte( pos );

        return ( byte )( ( byte1 & 0b11111100 ) );
    }

    public void setByte( int pos, byte b ) {
        memoria[pos] = b;
    }

    public void setByteInt( int pos, int valor ) {
        memoria[pos] = ( byte )( valor & 0xFF );
    }
    
    public void setWord( int pos, int valor ) {
        setByteInt( pos, valor >>> 16 );
        setByteInt( pos + 1, valor >>> 8 );
        setByteInt( pos + 2, valor );
    }

    public int getWord( int pos ) {
        int MID = getByte( pos + 1 ) << 8; 
        int LSB = getByte( pos + 2 );
        int MSB = getByte( pos ) << 16;

        return MID + LSB + MSB;
    }

}
