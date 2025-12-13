/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ProcessadorDeMacros;

import java.util.*;

/**
 *
 * @author carlo
 */

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
}
