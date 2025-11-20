package Instrucoes;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects; 

public class TabelaOpcodes {
    
    private final Map<Byte, Instrucao> catalogoInstrucoes; 

    public TabelaOpcodes() {
        
        catalogoInstrucoes = new HashMap<>();
        
catalogoInstrucoes.put((byte)0x18, new ADD  ());
        catalogoInstrucoes.put((byte)0x90, new ADDR ());
        catalogoInstrucoes.put((byte)0x28, new COMP ());
        catalogoInstrucoes.put((byte)0x30, new JEQ  ());
        catalogoInstrucoes.put((byte)0x00, new LDA  ());
        catalogoInstrucoes.put((byte)0x0C, new STA   ());
        
        // NOVAS INSTRUÇÕES NECESSÁRIAS (Base Opcodes)
        catalogoInstrucoes.put((byte)0x40, new AND  ());   
        catalogoInstrucoes.put((byte)0x04, new CLEAR());
    }

    public Instrucao getInstrucao(byte opcode) { 
       
        return catalogoInstrucoes.get(opcode);
        
    }

    public Instrucao getInstrucaoPorNome(String nome) {
        
        for (Instrucao instrucao : catalogoInstrucoes.values()) {
            
            if (instrucao.getNome().equals(nome))
                
                return instrucao;
            
        }
        
        return null;
        
    }
}