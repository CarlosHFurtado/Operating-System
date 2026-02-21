package Executor;

import java.util.Arrays;

public class Registrador {

    private final String nome;
    private final int id;
    private final byte[] valor; 

    Registrador(String nome, int id, byte[] valor) {
        this.nome = nome;
        this.id = id;
        this.valor = new byte[3];
        setValor(valor);
    }

    Registrador(String nome, int id) {
        this.nome = nome;
        this.id = id;
        this.valor = new byte[3];
        Arrays.fill(this.valor, (byte) 0);
    }

    public String getNome() {
        return nome;
    }

    public int getId() {
        return id;
    }

    public int getValorIntUnsigned() {
        int b2 = (valor[0] & 0xFF) << 16;
        int b1 = (valor[1] & 0xFF) << 8;
        int b0 = (valor[2] & 0xFF);
        return (b2 | b1 | b0) & 0x00FFFFFF;
    }

    public int getValorIntSigned() {
        int n = getValorIntUnsigned();
        n = (n << (32 - 24)) >> (32 - 24);
        return n;
    }

    public void setValorInt(int n) {
        n &= 0x00FFFFFF;
        valor[0] = (byte) ((n >>> 16) & 0xFF);
        valor[1] = (byte) ((n >>> 8) & 0xFF);
        valor[2] = (byte) (n & 0xFF);
    }

    public byte[] getValor() {
        return Arrays.copyOf(valor, 3);
    }

    public void setValor(byte[] novo) {
        Arrays.fill(this.valor, (byte) 0);
        if (novo == null) return;
        int len = Math.min(3, novo.length);
        System.arraycopy(novo, 0, this.valor, 0, len);
    }

    public void incrementar(int inc) {
        int counter = getValorIntSigned();
        counter += inc;
        setValorInt(counter);
    }

    public String getValorHex24() {
        return String.format("%06X", getValorIntUnsigned());
    }
}
