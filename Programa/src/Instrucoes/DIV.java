package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;
import java.util.Map;

public class DIV extends Instrucao {

    public DIV() {
        
        super("DIV", (byte)0x24, "3/4", 3);
    
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        int enderecoOuValorImediato = calcularTA(registradores, memoria);
        
        int operando = 0; // Este será o divisor
       
        Map<String, Boolean> flags = getFlags();
        
        boolean isImediato = flags.get("i") && !flags.get("n"); 
        boolean isIndireto = flags.get("n") && !flags.get("i"); 
        
        if (isImediato) {
            
            operando = enderecoOuValorImediato;
            
        } else if (isIndireto) {
        
            int enderecoReal = memoria.getWord(enderecoOuValorImediato);
           
            operando = memoria.getWord(enderecoReal);
       
        } else {
          
            operando = memoria.getWord(enderecoOuValorImediato);
            
        }
        
        int dividendo = registradores.getRegistradorPorNome("A").getValorIntSigned(); 
      
        if (operando == 0) {
            
            System.err.println("ERRO: Divisão por zero na instrução DIV.");
            return;
            
        }
        
        int quociente = dividendo / operando; 
        
        int resto = dividendo % operando; 

        registradores.getRegistradorPorNome("A").setValorInt(quociente); 
        
        registradores.getRegistradorPorNome("L").setValorInt(resto); 
    
    }
}