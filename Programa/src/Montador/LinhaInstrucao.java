package montador;

// Representa uma linha do programa assembly após análise

public class LinhaInstrucao {
    
    private String label;
    private String opcode;
    private String operandos;
    private String linhaBruta;
    private boolean isExtended;
  
    public LinhaInstrucao(String label, String opcode, String operandos, String linhaBruta, boolean isExtended) {
        
        this.label = label;
        this.opcode = opcode;
        this.operandos = operandos;
        this.linhaBruta = linhaBruta;
        this.isExtended = isExtended;
        
    }

    public LinhaInstrucao(String label, String opcode, String operandos, String linhaBruta) {
        
        this(label, opcode, operandos, linhaBruta, false); 
        
    }

    public boolean isExtended() { return isExtended; }
    public String getLabel() { return label; }
    public String getOpcode() { return opcode; }
    public String getOperandos() { return operandos; }
    public String getLinhaBruta() { return linhaBruta; }
    
    public boolean isEmpty() {
        
        return (opcode == null || opcode.isEmpty()) && (label == null || label.isEmpty());
        
    }

    @Override
    public String toString() {
        
        String extendedMarker = isExtended ? "+" : "";
        
        return String.format("L: %-10s | OP: %-6s | ARGS: %-10s", 
            label == null ? "" : label, 
            extendedMarker + (opcode == null ? "" : opcode), 
            operandos == null ? "" : operandos);
        
    }
}