package montador;

// Representa um símbolo na tabela de símbolos

public class Simbolo {
    
    private String nome;
    private int valor; 
    private String tipo; 

    public Simbolo(String nome, int valor, String tipo) {
        
        this.nome = nome;
        this.valor = valor;
        this.tipo = tipo;
        
    }

    public String getNome() {
        return nome;
    }

    public int getValor() {
        return valor;
    }
    
    
    public String getValorHex() {
        return String.format("%04X", valor);
    }

    public String getTipo() {
        return tipo;
    }
}