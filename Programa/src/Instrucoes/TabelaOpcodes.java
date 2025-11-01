package Instrucoes;

// @author Dienifer Ledebuhr

import java.util.HashMap;
import java.util.Map;
import java.util.Objects; 

public class TabelaOpcodes {
    
    private final Map<Byte, Instrucao> catalogoInstrucoes; 

    public TabelaOpcodes() {
        
        catalogoInstrucoes = new HashMap<>();
        
        catalogoInstrucoes.put((byte)0x18, new ADD  ());
        catalogoInstrucoes.put((byte)0x90, new ADDR ());
   /*   catalogoInstrucoes.put((byte)0x40, new AND  ());
        
        catalogoInstrucoes.put((byte)0x4,  new CLEAR());
        catalogoInstrucoes.put((byte)0x28, new COMP ());
        catalogoInstrucoes.put((byte)0xA0, new COMPR());
        
        catalogoInstrucoes.put((byte)0x24, new DIV  ());
        catalogoInstrucoes.put((byte)0x9C, new DIVR ());
        
        catalogoInstrucoes.put((byte)0x3C, new J    ());
        catalogoInstrucoes.put((byte)0x30, new JEQ  ());
        catalogoInstrucoes.put((byte)0x34, new JGT  ());
        catalogoInstrucoes.put((byte)0x38, new JLT  ());
        catalogoInstrucoes.put((byte)0x48, new JSUB ());
        
        catalogoInstrucoes.put((byte)0x0,  new LDA  ());
        catalogoInstrucoes.put((byte)0x68, new LDB  ());
        catalogoInstrucoes.put((byte)0x50, new LDCH ());
        catalogoInstrucoes.put((byte)0x8,  new LDL  ());
        catalogoInstrucoes.put((byte)0x6C, new LDS  ());
        catalogoInstrucoes.put((byte)0x74, new LDT  ());
        catalogoInstrucoes.put((byte)0x04, new LDX  ());
        
        catalogoInstrucoes.put((byte)0x20, new MUL  ());
        catalogoInstrucoes.put((byte)0x98, new MULR ());
        
        catalogoInstrucoes.put((byte)0x44, new OR   ());
        
        catalogoInstrucoes.put((byte)0xAC, new RMO  ());   
        catalogoInstrucoes.put((byte)0x4C, new RSUB ());
        
        catalogoInstrucoes.put((byte)0xA4, new SHIFTL());
        catalogoInstrucoes.put((byte)0xA8, new SHIFTR());
        catalogoInstrucoes.put((byte)0x0C, new STA   ());
        catalogoInstrucoes.put((byte)0x78, new STB   ());
        catalogoInstrucoes.put((byte)0x54, new STCH  ());
        catalogoInstrucoes.put((byte)0x14, new STL   ());
        catalogoInstrucoes.put((byte)0x7C, new STS   ());
        catalogoInstrucoes.put((byte)0x84, new STT   ());
        catalogoInstrucoes.put((byte)0x10, new STX   ());
        catalogoInstrucoes.put((byte)0x1C, new SUB   ());
        catalogoInstrucoes.put((byte)0x94, new SUBR  ());
        
        catalogoInstrucoes.put((byte)0x2C, new TIX  ());
        catalogoInstrucoes.put((byte)0xB8, new TIXR ());*/
    
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