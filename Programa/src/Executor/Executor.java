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
                
                PainelLog.logGlobal("Programa finalizado normalmente.");
                break;
                
            }

            byte opcodeCompleto = memoria.getByte(pc); 

            byte opcodeBase = (byte) (opcodeCompleto & 0xFC); 

            if (opcodeBase == (byte) 0xD8) { 
                
                PainelLog.logGlobal("RD: Leitura de Dispositivo. Interrompendo execução.");
                
                stop = true; 
                
                registradores.incrementar("PC", 3); 
                
                return;
                
            }

            if (opcodeBase == (byte) 0xDC) { 
                
                setOutput(registradores.getValor("A"));
                
                PainelLog.logGlobal(String.format("WD: Escrevendo 0x%X (A) na saída.", registradores.getValor("A")));
                
                registradores.incrementar("PC", 3);
                
            } else { 

                Instrucao instr = instrucoes.getInstrucao(opcodeBase);
                
                if (instr == null) {
                    
                    PainelLog.logGlobal("ERRO: Opcode inválido: " + String.format("%02X", opcodeCompleto) + " (Base: " + String.format("%02X", opcodeBase) + ") em PC=" + String.format("%06X", pc));
                    break;
                    
                }
                
                instr.executar(memoria, registradores);
                
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

        byte opcodeCompleto = memoria.getByte(pc); 

        byte opcodeBase = (byte) (opcodeCompleto & 0xFC);

        if (opcodeCompleto == 0 && pc > 0) { 
            
            PainelLog.logGlobal("PC em endereço vazio: " + String.format("%06X", pc));
            return false;
            
        }

        if (opcodeBase == (byte) 0xD8) {
            
            stop = true; 
            
            registradores.incrementar("PC", 3); 
            
            PainelLog.logGlobal("RD: Leitura de Dispositivo. Execução pausada.");
            
            return true;
            
        }

        if (opcodeBase == (byte) 0xDC) { 
            
            setOutput(registradores.getValor("A"));
            
            registradores.incrementar("PC", 3); 
            
            PainelLog.logGlobal(String.format("WD: Escrevendo 0x%X (A) na saída.", registradores.getValor("A")));
            return true;
            
        } else {
         
            Instrucao instr = instrucoes.getInstrucao(opcodeBase);
           
            if (instr == null) {
                
                log("Opcode inválido: " + String.format("%02X", opcodeCompleto) + " (Base: " + String.format("%02X", opcodeBase) + ") em PC=" + String.format("%06X", pc));
                return false;
                
            }
            
            instr.executar(memoria, registradores);
            
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