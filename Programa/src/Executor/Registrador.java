package Executor;

import java.util.Arrays;

public class Registrador {

    private final String nome;
    private final int id;
    private final byte[] valor; // sempre 3 bytes (24 bits)

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

    /** 24-bit sem sinal (0..0xFFFFFF). Ideal pra logs/endereços/hex. */
    public int getValorIntUnsigned() {
        int b2 = (valor[0] & 0xFF) << 16;
        int b1 = (valor[1] & 0xFF) << 8;
        int b0 = (valor[2] & 0xFF);
        return (b2 | b1 | b0) & 0x00FFFFFF;
    }

    /** 24-bit com sinal (sign-extend para int 32-bit). Útil pra aritmética/COMP. */
    public int getValorIntSigned() {
        int n = getValorIntUnsigned();
        // sign-extend de 24 bits para 32
        n = (n << (32 - 24)) >> (32 - 24);
        return n;
    }

    /** Define valor garantindo 24 bits. */
    public void setValorInt(int n) {
        n &= 0x00FFFFFF;
        valor[0] = (byte) ((n >>> 16) & 0xFF);
        valor[1] = (byte) ((n >>> 8) & 0xFF);
        valor[2] = (byte) (n & 0xFF);
    }

    /** Retorna cópia (evita alteração externa). */
    public byte[] getValor() {
        return Arrays.copyOf(valor, 3);
    }

    /** Copia até 3 bytes; se vier menor, completa com 0. */
    public void setValor(byte[] novo) {
        Arrays.fill(this.valor, (byte) 0);
        if (novo == null) return;
        int len = Math.min(3, novo.length);
        System.arraycopy(novo, 0, this.valor, 0, len);
    }

    /** Incrementa em aritmética com sinal (como antes), mas mantém 24 bits ao gravar. */
    public void incrementar(int inc) {
        int counter = getValorIntSigned();
        counter += inc;
        setValorInt(counter);
    }

    /** Hex sempre em 6 dígitos (24 bits). Ótimo pra prints. */
    public String getValorHex24() {
        return String.format("%06X", getValorIntUnsigned());
    }
}
