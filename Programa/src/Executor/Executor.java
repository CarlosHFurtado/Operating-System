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

    public enum MotivoParada {
        NENHUM,
        PARADO_MANUAL,
        AGUARDANDO_RD,
        FIM_PROGRAMA,
        PC_FORA_FAIXA,
        PC_FORA_MEMORIA,
        AREA_NAO_INICIALIZADA,
        LIMITE_PASSOS,
        OPCODE_INVALIDO,
        ERRO
    }

    private MotivoParada motivoParada = MotivoParada.NENHUM;

    private CarregadorAbsoluto carregadorAbsoluto;
    private CarregadorRelocador carregadorRelocador;
    private PainelLog painelLog;

    private boolean programaCarregado = false;
    private int inicioPrograma = -1;
    private int fimPrograma = -1;

    public Executor() {
        this.memoria = new Memoria(1024 * 1024); 
        this.registradores = new Registradores();
        this.instrucoes = new TabelaOpcodes();
        this.output = -1;

        this.carregadorAbsoluto = new CarregadorAbsoluto(memoria, registradores);
        this.carregadorRelocador = new CarregadorRelocador(memoria, registradores);
    }

    public boolean carregarProgramaAbsoluto(String codigoObjeto) {
        limpar();

        CarregadorAbsoluto.ResultadoCarregamento resultado =
                carregadorAbsoluto.carregar(codigoObjeto);

        if (!resultado.erros.isEmpty()) {
            for (String erro : resultado.erros) {
                PainelLog.logGlobal("ERRO CARREGADOR ABSOLUTO: " + erro);
            }
            programaCarregado = false;
            inicioPrograma = -1;
            fimPrograma = -1;
            return false;
        }

        registradores.setValor("L", 0);

        inicioPrograma = resultado.inicioPrograma;
        fimPrograma = resultado.inicioPrograma + resultado.tamanhoPrograma;
        programaCarregado = true;
        motivoParada = MotivoParada.NENHUM;

        PainelLog.logGlobal("Programa carregado (absoluto) - PC = " +
                String.format("%06X", resultado.enderecoExecucao) +
                " - faixa=[" + String.format("%06X", inicioPrograma) +
                "," + String.format("%06X", fimPrograma) + ")");

        return true;
    }

    public boolean carregarProgramaRelocavel(String codigoObjeto, int enderecoCarga) {
        limpar();

        carregadorRelocador.setEnderecoCarga(enderecoCarga);
        CarregadorRelocador.ResultadoCarregamento resultado =
                carregadorRelocador.carregar(codigoObjeto);

        if (!resultado.erros.isEmpty()) {
            for (String erro : resultado.erros) {
                PainelLog.logGlobal("ERRO CARREGADOR RELOCADOR: " + erro);
            }
            programaCarregado = false;
            inicioPrograma = -1;
            fimPrograma = -1;
            return false;
        }

        registradores.setValor("L", 0);

        inicioPrograma = resultado.inicioPrograma;
        fimPrograma = resultado.inicioPrograma + resultado.tamanhoPrograma;
        programaCarregado = true;
        motivoParada = MotivoParada.NENHUM;

        PainelLog.logGlobal("Programa carregado (relocável) - PC = " +
                String.format("%06X", resultado.enderecoExecucao) +
                " - Carga em: " + String.format("%06X", enderecoCarga) +
                " - faixa=[" + String.format("%06X", inicioPrograma) +
                "," + String.format("%06X", fimPrograma) + ")");

        return true;
    }

    public void limpar() {
        memoria.limpaMem();
        registradores.limpar();
        output = -1;
        stop = false;

        motivoParada = MotivoParada.NENHUM;

        programaCarregado = false;
        inicioPrograma = -1;
        fimPrograma = -1;
    }

    public void setPainelLog(PainelLog painelLog) {
        this.painelLog = painelLog;
    }

    private void log(String msg) {
        PainelLog.logGlobal(msg);
    }

    private boolean pcForaDaFaixaDoPrograma(int pc) {
        if (!programaCarregado) return false;
        return pc < inicioPrograma || pc >= fimPrograma;
    }

    private int tamanhoInstrucaoF3F4(byte b1) {
        return ((b1 & 0x10) != 0) ? 4 : 3;
    }

    private void setCC(String condicao) {
        int sw = registradores.getValor("SW");
        sw &= ~0x00E00000; 
        switch (condicao) {
            case "=" -> sw |= 0x00200000;
            case ">" -> sw |= 0x00400000;
            case "<" -> sw |= 0x00800000;
            default -> { /* ignora */ }
        }
        registradores.setValor("SW", sw);
    }

    public void executarPrograma() {
        int pcInicial = registradores.getValor("PC");
        log("DEBUG: PC inicial = " + String.format("%06X", pcInicial));
        stop = false;
        motivoParada = MotivoParada.NENHUM;

        final int LIMITE_PASSOS = 5_000_000;
        int passos = 0;

        while (!stop) {

            int pc = registradores.getValor("PC");

            if (pcForaDaFaixaDoPrograma(pc)) {
                log("Programa finalizado: PC saiu da faixa do programa. PC=" +
                        String.format("%06X", pc) +
                        " faixa=[" + String.format("%06X", inicioPrograma) +
                        "," + String.format("%06X", fimPrograma) + ")");
                motivoParada = MotivoParada.PC_FORA_FAIXA;
                break;
            }

            if (pc < 0 || pc >= memoria.getMem().length - 3) {
                log("Programa finalizado: PC fora da memória (" + String.format("%06X", pc) + ").");
                motivoParada = MotivoParada.PC_FORA_MEMORIA;
                break;
            }

            if (++passos > LIMITE_PASSOS) {
                log("Programa interrompido: limite de passos atingido (provável loop infinito).");
                motivoParada = MotivoParada.LIMITE_PASSOS;
                break;
            }

            byte b0 = memoria.getByte(pc);
            byte b1 = memoria.getByte(pc + 1);
            byte b2 = memoria.getByte(pc + 2);

            if (b0 == 0 && b1 == 0 && b2 == 0) {
                log("Execução encerrada: área não inicializada (00 00 00) em PC=" + String.format("%06X", pc));
                motivoParada = MotivoParada.AREA_NAO_INICIALIZADA;
                break;
            }

            byte opcodeCompleto = b0;
            byte opcodeBase = (byte) (opcodeCompleto & 0xFC);

            if (opcodeBase == (byte) 0xE0) { // TD
                int tam = tamanhoInstrucaoF3F4(b1);
                registradores.incrementar("PC", tam);
                setCC("<");
                log("TD: dispositivo pronto (simulado). CC = <");
                continue;

            } else if (opcodeBase == (byte) 0xD8) { // RD
                int tam = tamanhoInstrucaoF3F4(b1);
                log("RD: Leitura de Dispositivo. Execução pausada (aguardando entrada simulada).");
                stop = true;
                motivoParada = MotivoParada.AGUARDANDO_RD;
                registradores.incrementar("PC", tam); 
                break;

            } else if (opcodeBase == (byte) 0xDC) { // WD
                int tam = tamanhoInstrucaoF3F4(b1);
                int a = registradores.getValor("A") & 0xFFFFFF;
                setOutput(a & 0xFF); 
                log(String.format("WD: Escrevendo 0x%02X (byte baixo de A) na saída.", (a & 0xFF)));
                registradores.incrementar("PC", tam);

            } else if (opcodeBase == (byte) 0x4C) { // RSUB
                int lAntes = registradores.getValor("L");

                Instrucao instr = instrucoes.getInstrucao(opcodeBase);
                if (instr != null) {
                    instr.executar(memoria, registradores);
                } else {
                    log("ERRO: RSUB sem implementação na tabela de instruções.");
                    motivoParada = MotivoParada.ERRO;
                    break;
                }

                if (lAntes == 0) {
                    log("RSUB: retorno para 000000 (fim do programa).");
                    stop = true;
                    motivoParada = MotivoParada.FIM_PROGRAMA;
                    break;
                } else {
                    log("RSUB: retorno de subrotina (continua).");
                }

            } else {
                Instrucao instr = instrucoes.getInstrucao(opcodeBase);
                if (instr == null) {
                    log("ERRO: Opcode inválido: " + String.format("%02X", opcodeCompleto) +
                            " (Base: " + String.format("%02X", opcodeBase) + ") em PC=" + String.format("%06X", pc));
                    motivoParada = MotivoParada.OPCODE_INVALIDO;
                    break;
                }
                instr.executar(memoria, registradores);
            }
        }
    }

    public boolean executarPasso() {
        int pc = registradores.getValor("PC");

        if (pcForaDaFaixaDoPrograma(pc)) {
            log("Parou: PC saiu da faixa do programa. PC=" +
                    String.format("%06X", pc) +
                    " faixa=[" + String.format("%06X", inicioPrograma) +
                    "," + String.format("%06X", fimPrograma) + ")");
            motivoParada = MotivoParada.PC_FORA_FAIXA;
            return false;
        }

        if (pc < 0 || pc >= memoria.getMem().length - 3) {
            log("Parou: PC fora da memória (" + String.format("%06X", pc) + ").");
            motivoParada = MotivoParada.PC_FORA_MEMORIA;
            return false;
        }

        byte b0 = memoria.getByte(pc);
        byte b1 = memoria.getByte(pc + 1);
        byte b2 = memoria.getByte(pc + 2);

        if (b0 == 0 && b1 == 0 && b2 == 0) {
            log("Parou: área não inicializada (00 00 00) em PC=" + String.format("%06X", pc));
            motivoParada = MotivoParada.AREA_NAO_INICIALIZADA;
            return false;
        }

        byte opcodeCompleto = b0;
        byte opcodeBase = (byte) (opcodeCompleto & 0xFC);

        if (opcodeBase == (byte) 0xE0) { // TD
            int tam = tamanhoInstrucaoF3F4(b1);
            registradores.incrementar("PC", tam);
            setCC("<");
            log("TD: dispositivo pronto (simulado). CC = <");
            return true;

        } else if (opcodeBase == (byte) 0xD8) { // RD
            int tam = tamanhoInstrucaoF3F4(b1);
            stop = true;
            registradores.incrementar("PC", tam);
            motivoParada = MotivoParada.AGUARDANDO_RD;
            log("RD: Leitura de Dispositivo. Execução pausada (aguardando entrada simulada).");
            return false;

        } else if (opcodeBase == (byte) 0xDC) { // WD
            int tam = tamanhoInstrucaoF3F4(b1);
            int a = registradores.getValor("A") & 0xFFFFFF;
            setOutput(a & 0xFF);
            registradores.incrementar("PC", tam);
            log(String.format("WD: Escrevendo 0x%02X (byte baixo de A) na saída.", (a & 0xFF)));
            return true;

        } else if (opcodeBase == (byte) 0x4C) { // RSUB
            int lAntes = registradores.getValor("L");

            Instrucao instr = instrucoes.getInstrucao(opcodeBase);
            if (instr != null) {
                instr.executar(memoria, registradores);
            } else {
                log("ERRO: RSUB sem implementação na tabela de instruções.");
                motivoParada = MotivoParada.ERRO;
                return false;
            }

            if (lAntes == 0) {
                log("RSUB: retorno para 000000 (fim do programa).");
                stop = true;
                motivoParada = MotivoParada.FIM_PROGRAMA;
                return false;
            }

            log("RSUB: retorno de subrotina (continua).");
            return true;

        } else {
            Instrucao instr = instrucoes.getInstrucao(opcodeBase);
            if (instr == null) {
                log("Opcode inválido: " + String.format("%02X", opcodeCompleto) +
                        " (Base: " + String.format("%02X", opcodeBase) + ") em PC=" + String.format("%06X", pc));
                motivoParada = MotivoParada.OPCODE_INVALIDO;
                return false;
            }
            instr.executar(memoria, registradores);
            return true;
        }
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

    public MotivoParada getMotivoParada() {
        return motivoParada;
    }

    public boolean estaAguardandoEntradaRD() {
        return motivoParada == MotivoParada.AGUARDANDO_RD;
    }

    public void fornecerEntradaRD(int byteLido) {
        int v = byteLido & 0xFF;
        int a = registradores.getValor("A") & 0xFFFFFF;

        int novoA = (a & 0xFFFF00) | v;
        registradores.setValor("A", novoA);

        stop = false;
        motivoParada = MotivoParada.NENHUM;

        log(String.format("RD: entrada simulada recebida (0x%02X) -> A(low)=0x%02X", v, v));
    }

    public void parar() {
        stop = true;
        if (motivoParada == MotivoParada.NENHUM) {
            motivoParada = MotivoParada.PARADO_MANUAL;
        }
    }
}
