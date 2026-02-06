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
    
    // Novos carregadores
    private CarregadorAbsoluto carregadorAbsoluto;
    private CarregadorRelocador carregadorRelocador;
    
    public Executor() {
        this.memoria = new Memoria(1024 * 1024); // 1MB
        this.registradores = new Registradores();
        this.instrucoes = new TabelaOpcodes();
        this.output = -1;
        
        // Inicializa carregadores
        this.carregadorAbsoluto = new CarregadorAbsoluto(memoria, registradores);
        this.carregadorRelocador = new CarregadorRelocador(memoria, registradores);
    }
    
    public boolean carregarProgramaAbsoluto(String codigoObjeto) {
        CarregadorAbsoluto.ResultadoCarregamento resultado = 
            carregadorAbsoluto.carregar(codigoObjeto);
        
        if (!resultado.erros.isEmpty()) {
            for (String erro : resultado.erros) {
                PainelLog.logGlobal("ERRO CARREGADOR ABSOLUTO: " + erro);
            }
            return false;
        }
        
        PainelLog.logGlobal("Programa carregado (absoluto) - PC = " + 
            String.format("%06X", resultado.enderecoExecucao));
        return true;
    }
    
    public boolean carregarProgramaRelocavel(String codigoObjeto, int enderecoCarga) {
        carregadorRelocador.setEnderecoCarga(enderecoCarga);
        CarregadorRelocador.ResultadoCarregamento resultado = 
            carregadorRelocador.carregar(codigoObjeto);
        
        if (!resultado.erros.isEmpty()) {
            for (String erro : resultado.erros) {
                PainelLog.logGlobal("ERRO CARREGADOR RELOCADOR: " + erro);
            }
            return false;
        }
        
        PainelLog.logGlobal("Programa carregado (relocável) - PC = " + 
            String.format("%06X", resultado.enderecoExecucao) + 
            " - Carga em: " + String.format("%06X", enderecoCarga));
        return true;
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
                PainelLog.logGlobal("Programa finalizado por limite de memória.");
                break;
            }

            byte opcodeCompleto = memoria.getByte(pc);
            byte opcodeBase = (byte) (opcodeCompleto & 0xFC);

            if (opcodeBase == (byte) 0xD8) { // RD
                PainelLog.logGlobal("RD: Leitura de Dispositivo. Interrompendo execução.");
                stop = true;
                registradores.incrementar("PC", 3);
                return;
            } else if (opcodeBase == (byte) 0xDC) { // WD
                setOutput(registradores.getValor("A"));
                PainelLog.logGlobal(String.format("WD: Escrevendo 0x%X (A) na saída.", registradores.getValor("A")));
                registradores.incrementar("PC", 3);
            } else if (opcodeBase == (byte) 0x4C) { // RSUB
                Instrucao instr = instrucoes.getInstrucao(opcodeBase);
                if (instr != null) instr.executar(memoria, registradores);
                PainelLog.logGlobal("RSUB (4C): Programa principal finalizado com sucesso.");
                stop = true;
                break;
            } else {
                Instrucao instr = instrucoes.getInstrucao(opcodeBase);
                if (instr == null) {
                    PainelLog.logGlobal("ERRO: Opcode inválido: " + String.format("%02X", opcodeCompleto) +
                            " (Base: " + String.format("%02X", opcodeBase) + ") em PC=" + String.format("%06X", pc));
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

        if (opcodeBase == (byte) 0xD8) { // RD
            stop = true;
            registradores.incrementar("PC", 3);
            PainelLog.logGlobal("RD: Leitura de Dispositivo. Execução pausada.");
            return true;
        } else if (opcodeBase == (byte) 0xDC) { // WD
            setOutput(registradores.getValor("A"));
            registradores.incrementar("PC", 3);
            PainelLog.logGlobal(String.format("WD: Escrevendo 0x%X (A) na saída.", registradores.getValor("A")));
            return true;
        } else if (opcodeBase == (byte) 0x4C) { // RSUB
            Instrucao instr = instrucoes.getInstrucao(opcodeBase);
            if (instr != null) instr.executar(memoria, registradores);
            PainelLog.logGlobal("RSUB (4C): Retorno de Subrotina. Execução finalizada.");
            return false;
        } else {
            Instrucao instr = instrucoes.getInstrucao(opcodeBase);
            if (instr == null) {
                log("Opcode inválido: " + String.format("%02X", opcodeCompleto) +
                        " (Base: " + String.format("%02X", opcodeBase) + ") em PC=" + String.format("%06X", pc));
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