package montador;

// Representa uma instrução de máquina (MOT - Machine Operation Table).
// Armazena os dados básicos de uma instrução que sera usada pelo montador.

public class InstrucaoMaquina {
    
    private String mnemônico;
    private String opcodeHex; 
    private int tamanhoBytes;  

    public InstrucaoMaquina(String mnemônico, String opcodeHex, int tamanhoBytes) {
        
        this.mnemônico = mnemônico;
        this.opcodeHex = opcodeHex;
        this.tamanhoBytes = tamanhoBytes;
        
    }

    public String getMnemônico() {
        return mnemônico;
    }

    public String getOpcodeHex() {
        return opcodeHex;
    }

    public int getTamanhoBytes() {
        return tamanhoBytes;
    }
}