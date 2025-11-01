package Instrucoes;

// @author Dienifer Ledebuhr

import Executor.Memoria;
import Executor.Registradores;
import java.util.Map;

public class AND extends Instrucao {

    public AND() {
        
        super("AND", (byte)0x40, "3/4", 3);
        
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores) {
        
        int enderecoOuValorImediato = calcularTA(registradores, memoria);
        
        int operando = 0;
        
        Map<String, Boolean> flags = getFlags();
        
        boolean isImediato = flags.get("i") && !flags.get("n"); // i=1, n=0
        boolean isIndireto = flags.get("n") && !flags.get("i"); // n=1, i=0
        
        if (isImediato) {
           
            operando = enderecoOuValorImediato;
            
        } else if (isIndireto) {
           
            int enderecoReal = memoria.getWord(enderecoOuValorImediato);           
       
            operando = memoria.getWord(enderecoReal);
            
        } else {
   
            operando = memoria.getWord(enderecoOuValorImediato);
        
        }

        int valorAcumulador = registradores.getRegistradorPorNome("A").getValorIntSigned();

        int resultado = valorAcumulador & operando; 

        registradores.getRegistradorPorNome("A").setValorInt(resultado);
    
    }
}