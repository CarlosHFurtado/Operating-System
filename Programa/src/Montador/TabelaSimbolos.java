package montador;

import java.util.HashMap;
import java.util.Map;

public class TabelaSimbolos {
    
    private final Map<String, Simbolo> tabela;

    public TabelaSimbolos() {
        
        this.tabela = new HashMap<>();
        
    }

    public boolean inserir(String nome, int valor, String tipo) {
        
        String nomeUpper = nome.toUpperCase();
        
        if (tabela.containsKey(nomeUpper)) {
            
            System.err.println("[ERRO - PASSO 1] Símbolo duplicado detectado: " + nome);
            return false;
            
        } else {
            
            Simbolo novoSimbolo = new Simbolo(nome, valor, tipo);
            tabela.put(nomeUpper, novoSimbolo);
            return true;
            
        }
    }

    public Simbolo procurar(String nome) {
        
        if (nome == null) return null;
        
        return tabela.get(nome.toUpperCase());
        
    }
  
    public Integer getEndereco(String nome) {
        
        Simbolo simbolo = procurar(nome);
        
        if (simbolo != null) {
            
            return simbolo.getValor(); 
            
        }
        
        return null;
    }
 
    public boolean contem(String nome) {
        return tabela.containsKey(nome.toUpperCase());
    }
    
    public int tamanho() {
        return tabela.size();
    }
    
    public void limpar() {
        tabela.clear();
    }
}