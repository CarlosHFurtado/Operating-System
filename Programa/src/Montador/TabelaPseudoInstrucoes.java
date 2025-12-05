package montador;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class TabelaPseudoInstrucoes {
    
  
    private static final Map<String, PseudoInstrucao> POT;
    
    static {
        Map<String, PseudoInstrucao> aMap = new HashMap<>();
        
       
        aMap.put("START", new PseudoInstrucao("START"));
        aMap.put("END", new PseudoInstrucao("END"));
        aMap.put("ORG", new PseudoInstrucao("ORG")); 
        aMap.put("EQU", new PseudoInstrucao("EQU")); 
        
     
        aMap.put("RESW", new PseudoInstrucao("RESW")); 
        aMap.put("RESB", new PseudoInstrucao("RESB")); 
        aMap.put("WORD", new PseudoInstrucao("WORD")); 
        aMap.put("BYTE", new PseudoInstrucao("BYTE")); 
        aMap.put("BASE", new PseudoInstrucao("BASE"));
       

        POT = Collections.unmodifiableMap(aMap);
    }
    

    public static boolean isPseudoOp(String mnemônico) {
        if (mnemônico == null) return false;
        return POT.containsKey(mnemônico.toUpperCase());
    }

 
    public static PseudoInstrucao getPseudoOp(String mnemônico) {
        if (mnemônico == null) return null;
        return POT.get(mnemônico.toUpperCase());
    }
}