package ProcessadorDeMacros;

import java.util.*;

// @author carlo
 
public class Macro {

    private String nome;
    private List<String> parametros;
    private List<String> corpo;

    public Macro(String nome, List<String> parametros) {
        this.nome = nome;
        this.parametros = parametros;
        this.corpo = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public List<String> getParametros() {
        return parametros;
    }

    public List<String> getCorpo() {
        return corpo;
    }

    public void adicionarLinha(String linha) {
        corpo.add(linha);
    }
}
