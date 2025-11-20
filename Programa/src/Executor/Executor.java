package Executor;

import Instrucoes.Instrucao;
import Instrucoes.TabelaOpcodes;
import interfacesicxe.PainelLog;

public class Executor {
    private Memoria memoria;
    private Registradores registradores;
    private TabelaOpcodes instrucoes;
    private int output;
    private boolean stop;

    public Executor() {
        this.memoria = new Memoria(1024);
        this.registradores = new Registradores();
        this.instrucoes = new TabelaOpcodes();
        this.output = -1;
    }
    
    public void limpar() {
        memoria.limpaMem();
        registradores.limpar();
        output = -1;
    }


public void executarPrograma() {
    int pc = registradores.getValor("PC");
    PainelLog.logGlobal("DEBUG: PC inicial = " + String.format("%06X", pc));
    stop = false;
    while (!stop) {
        if (pc < 0 || pc >= memoria.getMem().length - 2) {
            PainelLog.logGlobal("DEBUG: PC fora dos limites: " + String.format("%06X", pc));
            break;
        }

        byte opcode = memoria.getByte(pc);
        PainelLog.logGlobal("DEBUG: Lendo opcode " + String.format("%02X", opcode) + " em PC=" + String.format("%06X", pc));

        if (opcode == (byte) 0xD8) { // READ
            stop = true;
            registradores.incrementar("PC", 1);
            return;
        }
        if (opcode == (byte) 0xDC) { // WRITE
            setOutput(registradores.getValor("A"));
            registradores.incrementar("PC", 1);
        } else {
            // Ajustar o opcode para o opcode base (6 bits) para buscar na TabelaOpcodes
            byte opcodeBase = (byte) (opcode & 0xFC); 
            Instrucao instr = instrucoes.getInstrucao(opcodeBase);
            
            if (instr == null) {
                PainelLog.logGlobal("Opcode desconhecido: " + String.format("%02X", opcode));
                break;
            }
            
            // NOVO: Obter tamanho e bytes para passar para a execução
            int tamanho = instr.getLength();
            byte[] bytesInstrucao = memoria.getBytes(tamanho, pc);
            
            instr.executar(memoria, registradores, bytesInstrucao, tamanho); // NOVO: Chamada atualizada
        }
        pc = registradores.getValor("PC");
    }
}

private PainelLog painelLog;

public void setPainelLog(PainelLog painelLog) {
    this.painelLog = painelLog;
}

private void log(String msg) {
    if (painelLog != null) {
        painelLog.adicionarMensagem(msg);
    }
}

    public boolean executarPasso() {
    int pc = registradores.getValor("PC");
    if (pc < 0 || pc >= memoria.getMem().length - 2) {
        return false;
    }

    byte opcode = memoria.getByte(pc);
    if (opcode == 0 && pc > 0) { // Se for zero e não for o início, pode ser lixo
        PainelLog.logGlobal("PC em endereço vazio: " + String.format("%06X", pc));
        return false;
    }

    if (opcode == (byte) 0xD8) { // READ
        stop = true;
        registradores.incrementar("PC", 1);
        return true;
    }
    if (opcode == (byte) 0xDC) { // WRITE
        setOutput(registradores.getValor("A"));
        registradores.incrementar("PC", 1);
    } else {
        // Ajustar o opcode para o opcode base (6 bits)
        byte opcodeBase = (byte) (opcode & 0xFC);
        Instrucao instr = instrucoes.getInstrucao(opcodeBase);
        
        if (instr == null) {
            log("Opcode inválido: " + String.format("%02X", opcode) + " em PC=" + String.format("%06X", pc));
            return false;
        }
        
        // NOVO: Obter tamanho e bytes para passar para a execução
        int tamanho = instr.getLength();
        byte[] bytesInstrucao = memoria.getBytes(tamanho, pc);
        
        instr.executar(memoria, registradores, bytesInstrucao, tamanho); // NOVO: Chamada atualizada
    }
    return true;
}
    
    public Memoria getMemoria() {
        return memoria;
    }
    
    public Registradores getRegistradores() {
        return registradores;
    }
    
    public TabelaOpcodes getInstrucoes() {
        return instrucoes;
    }

    public void setOutput(int output) {
        this.output = output;
    }

    public int getOutput() {
        return output;
    }
    
    public boolean getStop() {
        return stop;
    }
}