package Instrucoes;

// @author Dienifer Ledebuhr

// ADD -> Soma o valor da memoria com o conteudo do Acumulador e armazena em A

import java.util.Map;

import Executor.Memoria;
import Executor.Registradores;

public class ADD extends Instrucao {
    
    public ADD () {
        
        super ("ADD", (byte)0x18, "3/4", 3);
        
    }
    
    @Override
    
    public void executar (Memoria memoria, Registradores registradores) {
        
        int enderecoOuValorImediato = calcularTA (registradores, memoria);

        int operando = 0; 

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

        int valorAcumulador = registradores.getRegistradorPorNome("A").getValorIntSigned();

        int resultado = valorAcumulador + operando;

        registradores.getRegistradorPorNome("A").setValorInt(resultado);
               
    }        
}

