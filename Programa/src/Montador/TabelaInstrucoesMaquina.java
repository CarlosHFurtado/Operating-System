package montador;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class TabelaInstrucoesMaquina {
    
    private static final Map<String, InstrucaoMaquina> MOT_SICXE;

    static {
        
        Map<String, InstrucaoMaquina> sicxeMap = new HashMap<>();
        
    
        sicxeMap.put("ADDR", new InstrucaoMaquina("ADDR", "90", 2));
        sicxeMap.put("CLEAR", new InstrucaoMaquina("CLEAR", "B4", 2));
        sicxeMap.put("COMPR", new InstrucaoMaquina("COMPR", "A0", 2));
        sicxeMap.put("DIVR", new InstrucaoMaquina("DIVR", "9C", 2));
        sicxeMap.put("MULR", new InstrucaoMaquina("MULR", "98", 2));
        sicxeMap.put("RMO", new InstrucaoMaquina("RMO", "AC", 2));
        sicxeMap.put("SHIFTL", new InstrucaoMaquina("SHIFTL", "A4", 2));
        sicxeMap.put("SHIFTR", new InstrucaoMaquina("SHIFTR", "A8", 2));
        sicxeMap.put("SUBR", new InstrucaoMaquina("SUBR", "94", 2));
        sicxeMap.put("TIXR", new InstrucaoMaquina("TIXR", "B8", 2));
            
      
        sicxeMap.put("ADD", new InstrucaoMaquina("ADD", "18", 3));
        sicxeMap.put("AND", new InstrucaoMaquina("AND", "40", 3));
        sicxeMap.put("COMP", new InstrucaoMaquina("COMP", "28", 3));
        sicxeMap.put("DIV", new InstrucaoMaquina("DIV", "24", 3));
        sicxeMap.put("J", new InstrucaoMaquina("J", "3C", 3));
        sicxeMap.put("JEQ", new InstrucaoMaquina("JEQ", "30", 3));
        sicxeMap.put("JGT", new InstrucaoMaquina("JGT", "34", 3));
        sicxeMap.put("JLT", new InstrucaoMaquina("JLT", "38", 3));
        sicxeMap.put("JSUB", new InstrucaoMaquina("JSUB", "48", 3));
        sicxeMap.put("LDA", new InstrucaoMaquina("LDA", "00", 3));
        sicxeMap.put("LDB", new InstrucaoMaquina("LDB", "68", 3));
        sicxeMap.put("LDCH", new InstrucaoMaquina("LDCH", "50", 3));
        sicxeMap.put("LDL", new InstrucaoMaquina("LDL", "08", 3));
        sicxeMap.put("LDS", new InstrucaoMaquina("LDS", "6C", 3));
        sicxeMap.put("LDT", new InstrucaoMaquina("LDT", "74", 3));
        sicxeMap.put("LDX", new InstrucaoMaquina("LDX", "04", 3));
        sicxeMap.put("MUL", new InstrucaoMaquina("MUL", "20", 3));
        sicxeMap.put("OR", new InstrucaoMaquina("OR", "44", 3));
        sicxeMap.put("RSUB", new InstrucaoMaquina("RSUB", "4C", 3));
        sicxeMap.put("STA", new InstrucaoMaquina("STA", "0C", 3));
        sicxeMap.put("STB", new InstrucaoMaquina("STB", "78", 3));
        sicxeMap.put("STCH", new InstrucaoMaquina("STCH", "54", 3));
        sicxeMap.put("STL", new InstrucaoMaquina("STL", "14", 3));
        sicxeMap.put("STS", new InstrucaoMaquina("STS", "7C", 3));
        sicxeMap.put("STT", new InstrucaoMaquina("STT", "84", 3));
        sicxeMap.put("STX", new InstrucaoMaquina("STX", "10", 3));
        sicxeMap.put("SUB", new InstrucaoMaquina("SUB", "1C", 3));
        sicxeMap.put("TIX", new InstrucaoMaquina("TIX", "2C", 3));
        sicxeMap.put("RD", new InstrucaoMaquina("RD", "D8", 3));
        sicxeMap.put("WD", new InstrucaoMaquina("WD", "DC", 3));
        sicxeMap.put("SSK", new InstrucaoMaquina("SSK", "EC", 3));

        MOT_SICXE = Collections.unmodifiableMap(sicxeMap);
        
    }
    
 
    public static boolean isMachineOp(String mnemônico) {
        
        if (mnemônico == null) return false;
        
        String mnemônicoUpper = mnemônico.toUpperCase();
        return MOT_SICXE.containsKey(mnemônicoUpper);
        
    }


    public static InstrucaoMaquina getMachineOp(String mnemônico) {
        
        if (mnemônico == null) return null;
        
        String mnemônicoUpper = mnemônico.toUpperCase();
        return MOT_SICXE.get(mnemônicoUpper);
        
    }
}