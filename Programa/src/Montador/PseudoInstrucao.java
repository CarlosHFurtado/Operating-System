package montador;

// Representa uma pseudo-instrução do montador (POT).
 
public class PseudoInstrucao {
    
    private String mnemônico;

    public PseudoInstrucao(String mnemônico) {
        this.mnemônico = mnemônico;
    }

    public String getMnemônico() {
        return mnemônico;
    }

}