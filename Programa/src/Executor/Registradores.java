package Executor;

import java.util.HashMap;
import java.util.Map;

public class Registradores {

    private Map<String, Registrador> registradores;

    public Registradores() {
        registradores = new HashMap<>();

        //Registradores de 24 bits
        registradores.put("A", new Registrador("A", 0));
        registradores.put("X", new Registrador("X", 1));
        registradores.put("L", new Registrador("L", 2));
        registradores.put("B", new Registrador("B", 3));
        registradores.put("S", new Registrador("S", 4));
        registradores.put("T", new Registrador("T", 5));
        // F de 48 bits
        registradores.put("F", new Registrador("F", 6, new byte[6]));
        // PC e SW de 24 bits
        registradores.put("PC", new Registrador("PC", 8));
        registradores.put("SW", new Registrador("SW", 9));
    }


    public Registrador get(String nome) { //Retorna registrador por nome
        return registradores.get(nome.toUpperCase());
    }

    public int getValor(String nome) { //Retorna int (unsigned)
        return registradores.get(nome.toUpperCase()).getValorIntUnsigned();
    }

    public void setValor(String nome, int valor) { // Define valor int
        registradores.get(nome.toUpperCase()).setValorInt(valor);
    }

    public void incrementar(String nome, int valor) {
        registradores.get(nome.toUpperCase()).incrementar(valor);
    }


    public void limpar() { //Limpa os registradores
        for (Registrador r : registradores.values()) {
            if (r.getValor().length == 3) {
                r.setValor(new byte[3]);
            } else {
                r.setValor(new byte[r.getValor().length]); // F tem 6 bytes
            }
        }
    }
}