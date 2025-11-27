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
        
        // PC e SW de 24 bits
        
        registradores.put("PC", new Registrador("PC", 8));
        registradores.put("SW", new Registrador("SW", 9));
    }

    public Registrador get(String nome) { 
        
        return registradores.get(nome.toUpperCase());
        
    }

    public int getValor(String nome) { 
        
        return registradores.get(nome.toUpperCase()).getValorIntUnsigned();
        
    }
    
    public int getValorIntSigned(String nome) { 

        return registradores.get(nome.toUpperCase()).getValorIntSigned();
    
    }

    public void setValor(String nome, int valor) { 
        
        registradores.get(nome.toUpperCase()).setValorInt(valor);
        
    }

    public void incrementar(String nome, int valor) {
        
        registradores.get(nome.toUpperCase()).incrementar(valor);
        
    }


    public void limpar() { 
        
        for (Registrador r : registradores.values()) {
            
            if (r.getValor().length == 3) {
                
                r.setValor(new byte[3]);
                
            } else {
                
                r.setValor(new byte[r.getValor().length]);
                
            }
        }
    }
    
    // Por causa dos registradores
    
    public static String getNomeRegistradorPorId(int id) {
        
        switch (id) {
            
            case 0: return "A";
            case 1: return "X";
            case 2: return "L";
            case 3: return "B";
            case 4: return "S";
            case 5: return "T";
            case 8: return "PC";
            case 9: return "SW";
            default: return null;
            
        }
    }
    
    // Por causa das Instruções condicionais 
    
    public boolean checarCondicao(String condicao) {
        
        int valorSW = getValor("SW");
        int mascara;

        switch (condicao) {
            
            case "=":
                mascara = 0x00200000; 
                break;
                
            case ">":
                mascara = 0x00400000; 
                break;
                
            case "<":
                mascara = 0x00800000; 
                break;
                
            default:
               
                return false;
        }

        return (valorSW & mascara) != 0;
        
    }
}