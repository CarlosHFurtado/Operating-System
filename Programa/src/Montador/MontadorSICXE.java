// MontadorSICXE.java - simples, funcional e compatível com seu Ligador/Carregadores
package montador;

import java.util.*;

public class MontadorSICXE {

    // =========================
    // Resultado para a Interface
    // =========================
    public static class ResultadoMontagem {
        public final String programaObjeto;
        public final String listagem;
        public final List<String> erros;

        public ResultadoMontagem(String programaObjeto, String listagem, List<String> erros) {
            this.programaObjeto = programaObjeto;
            this.listagem = listagem;
            this.erros = erros;
        }
    }

    // =========================
    // Tabela de Instruções
    // =========================
    private static class InformacaoInstrucao {
        String mnem;
        int opcode;
        int formato; // 1, 2 ou 3 (3 pode virar 4 se tiver '+')
        InformacaoInstrucao(String mnem, int opcode, int formato) {
            this.mnem = mnem;
            this.opcode = opcode;
            this.formato = formato;
        }
    }

    private static final Map<String, InformacaoInstrucao> tabelaInstrucoes = new HashMap<>();
    private static final Map<String, Integer> tabelaRegistradores = new HashMap<>();

    static {
        // Formato 1
        adicionarInstrucao("FIX",   0xC4, 1);
        adicionarInstrucao("FLOAT", 0xC0, 1);
        adicionarInstrucao("HIO",   0xF4, 1);
        adicionarInstrucao("NORM",  0xC8, 1);
        adicionarInstrucao("SIO",   0xF0, 1);
        adicionarInstrucao("TIO",   0xF8, 1);

        // Formato 2
        adicionarInstrucao("ADDR",   0x90, 2);
        adicionarInstrucao("CLEAR",  0xB4, 2);
        adicionarInstrucao("COMPR",  0xA0, 2);
        adicionarInstrucao("DIVR",   0x9C, 2);
        adicionarInstrucao("MULR",   0x98, 2);
        adicionarInstrucao("RMO",    0xAC, 2);
        adicionarInstrucao("SHIFTL", 0xA4, 2);
        adicionarInstrucao("SHIFTR", 0xA8, 2);
        adicionarInstrucao("SUBR",   0x94, 2);
        adicionarInstrucao("SVC",    0xB0, 2);
        adicionarInstrucao("TIXR",   0xB8, 2);

        // Formato 3/4
        adicionarInstrucao("ADD",   0x18, 3);
        adicionarInstrucao("ADDF",  0x58, 3);
        adicionarInstrucao("AND",   0x40, 3);
        adicionarInstrucao("COMP",  0x28, 3);
        adicionarInstrucao("COMPF", 0x88, 3);
        adicionarInstrucao("DIV",   0x24, 3);
        adicionarInstrucao("J",     0x3C, 3);
        adicionarInstrucao("JEQ",   0x30, 3);
        adicionarInstrucao("JGT",   0x34, 3);
        adicionarInstrucao("JLT",   0x38, 3);
        adicionarInstrucao("JSUB",  0x48, 3);
        adicionarInstrucao("LDA",   0x00, 3);
        adicionarInstrucao("LDB",   0x68, 3);
        adicionarInstrucao("LDCH",  0x50, 3);
        adicionarInstrucao("LDF",   0x70, 3);
        adicionarInstrucao("LDL",   0x08, 3);
        adicionarInstrucao("LDS",   0x6C, 3);
        adicionarInstrucao("LDT",   0x74, 3);
        adicionarInstrucao("LDX",   0x04, 3);
        adicionarInstrucao("MUL",   0x20, 3);
        adicionarInstrucao("OR",    0x44, 3);
        adicionarInstrucao("RD",    0xD8, 3);
        adicionarInstrucao("RSUB",  0x4C, 3);
        adicionarInstrucao("STA",   0x0C, 3);
        adicionarInstrucao("STB",   0x78, 3);
        adicionarInstrucao("STCH",  0x54, 3);
        adicionarInstrucao("STF",   0x80, 3);
        adicionarInstrucao("STL",   0x14, 3);
        adicionarInstrucao("STS",   0x7C, 3);
        adicionarInstrucao("STT",   0x84, 3);
        adicionarInstrucao("STX",   0x10, 3);
        adicionarInstrucao("SUB",   0x1C, 3);
        adicionarInstrucao("TD",    0xE0, 3);
        adicionarInstrucao("TIX",   0x2C, 3);
        adicionarInstrucao("WD",    0xDC, 3);

        tabelaRegistradores.put("A", 0);
        tabelaRegistradores.put("X", 1);
        tabelaRegistradores.put("L", 2);
        tabelaRegistradores.put("B", 3);
        tabelaRegistradores.put("S", 4);
        tabelaRegistradores.put("T", 5);
        tabelaRegistradores.put("F", 6);
        tabelaRegistradores.put("PC", 8);
        tabelaRegistradores.put("SW", 9);
    }

    private static void adicionarInstrucao(String mnem, int opcode, int formato) {
        tabelaInstrucoes.put(mnem, new InformacaoInstrucao(mnem, opcode, formato));
    }

    // =========================
    // Diretivas suportadas
    // =========================
    private static final Set<String> diretivas = new HashSet<>(Arrays.asList(
            "START", "END", "BYTE", "WORD", "RESB", "RESW",
            "BASE", "NOBASE", "EXTDEF", "EXTREF"
    ));

    // =========================
    // Estruturas internas
    // =========================
    private static class Simbolo {
        String nome;
        int endereco;
        Simbolo(String nome, int endereco) {
            this.nome = nome;
            this.endereco = endereco;
        }
    }

    private static class LinhaMontagem {
        int numeroLinha;
        String original;
        String rotulo;
        String operacao;
        String operando;
        int endereco;
        String codigoObjeto = "";

        LinhaMontagem(int numeroLinha, String original, int endereco) {
            this.numeroLinha = numeroLinha;
            this.original = original;
            this.endereco = endereco;
        }
    }

    private static class RegistroModificacao {
        int enderecoRelativo; // no objeto
        int quantidadeNibbles; // 05 (formato 4) ou 06 (WORD)
        Character sinal; // '+' ou '-' ou null
        String simbolo; // null quando é relocação simples (somar endereço de carga)
        RegistroModificacao(int enderecoRelativo, int quantidadeNibbles, Character sinal, String simbolo) {
            this.enderecoRelativo = enderecoRelativo;
            this.quantidadeNibbles = quantidadeNibbles;
            this.sinal = sinal;
            this.simbolo = simbolo;
        }
    }

    // =========================
    // Estado do montador
    // =========================
    private Map<String, Simbolo> tabelaSimbolos;
    private List<String> erros;
    private List<LinhaMontagem> linhas;

    private int contadorLocalizacao;
    private int enderecoInicial;
    private int tamanhoPrograma;
    private String nomePrograma;

    private Integer enderecoBase; // BASE
    private final LinkedHashSet<String> definicoesExternas = new LinkedHashSet<>(); // EXTDEF
    private final LinkedHashSet<String> referenciasExternas = new LinkedHashSet<>(); // EXTREF

    private final List<RegistroModificacao> registrosModificacao = new ArrayList<>();

    public MontadorSICXE() {
        reiniciar();
    }

    private void reiniciar() {
        tabelaSimbolos = new LinkedHashMap<>();
        erros = new ArrayList<>();
        linhas = new ArrayList<>();
        contadorLocalizacao = 0;
        enderecoInicial = 0;
        tamanhoPrograma = 0;
        nomePrograma = "";
        enderecoBase = null;
        definicoesExternas.clear();
        referenciasExternas.clear();
        registrosModificacao.clear();
    }

    // =========================
    // API principal
    // =========================
    public ResultadoMontagem montar(List<String> codigoFonte) {
        reiniciar();
        primeiraPassagem(codigoFonte);
        segundaPassagem();
        String objeto = gerarProgramaObjeto();
        String listagem = gerarListagem();
        return new ResultadoMontagem(objeto, listagem, List.copyOf(erros));
    }

    // =========================
    // Primeira passagem
    // =========================
    private void primeiraPassagem(List<String> codigoFonte) {
        boolean encontrouStart = false;
        contadorLocalizacao = 0;

        for (int indice = 0; indice < codigoFonte.size(); indice++) {
            String linhaOriginal = codigoFonte.get(indice);
            String linhaSemComentario = removerComentarios(linhaOriginal);

            LinhaMontagem linha = new LinhaMontagem(indice + 1, linhaOriginal, contadorLocalizacao);
            linhas.add(linha);

            if (linhaSemComentario.isBlank()) continue;

            PartesLinha partes = analisarLinha(linhaSemComentario);
            linha.rotulo = partes.rotulo;
            linha.operacao = partes.operacao;
            linha.operando = partes.operando;

            String operacao = partes.operacao;

            // START
            if ("START".equals(operacao)) {
                if (encontrouStart) {
                    adicionarErro(linha.numeroLinha, "Diretiva START duplicada.");
                    continue;
                }
                encontrouStart = true;
                nomePrograma = (partes.rotulo != null) ? partes.rotulo : "";
                enderecoInicial = interpretarNumero(partes.operando, linha.numeroLinha);
                contadorLocalizacao = enderecoInicial;
                linha.endereco = contadorLocalizacao;
                continue;
            }

            if (!encontrouStart) {
                // se não tem START, assume início 0
                encontrouStart = true;
                enderecoInicial = 0;
                contadorLocalizacao = 0;
                linha.endereco = contadorLocalizacao;
            }

            // rótulo
            if (partes.rotulo != null && !partes.rotulo.isBlank()) {
                if (tabelaSimbolos.containsKey(partes.rotulo)) {
                    adicionarErro(linha.numeroLinha, "Símbolo duplicado: " + partes.rotulo);
                } else {
                    tabelaSimbolos.put(partes.rotulo, new Simbolo(partes.rotulo, contadorLocalizacao));
                }
            }

            // EXTDEF / EXTREF (não consomem memória)
            if ("EXTDEF".equals(operacao)) {
                for (String s : separarListaSimbolos(partes.operando)) definicoesExternas.add(s);
                continue;
            }
            if ("EXTREF".equals(operacao)) {
                for (String s : separarListaSimbolos(partes.operando)) referenciasExternas.add(s);
                continue;
            }

            // diretivas
            if (diretivas.contains(operacao)) {
                contadorLocalizacao += tamanhoDiretiva(operacao, partes.operando, linha.numeroLinha);
                continue;
            }

            // instruções
            if (ehInstrucao(operacao)) {
                boolean estendido = operacao.startsWith("+");
                String base = estendido ? operacao.substring(1) : operacao;
                InformacaoInstrucao info = tabelaInstrucoes.get(base);

                if (info == null) {
                    adicionarErro(linha.numeroLinha, "Instrução inválida: " + operacao);
                    continue;
                }

                if (info.formato == 1) contadorLocalizacao += 1;
                else if (info.formato == 2) contadorLocalizacao += 2;
                else contadorLocalizacao += (estendido ? 4 : 3);

                continue;
            }

            adicionarErro(linha.numeroLinha, "Operação inválida: " + operacao);
        }

        tamanhoPrograma = contadorLocalizacao - enderecoInicial;
    }

    private int tamanhoDiretiva(String operacao, String operando, int numeroLinha) {
        if ("WORD".equals(operacao)) return 3;
        if ("RESW".equals(operacao)) return 3 * Math.max(0, interpretarNumero(operando, numeroLinha));
        if ("RESB".equals(operacao)) return Math.max(0, interpretarNumero(operando, numeroLinha));
        if ("BYTE".equals(operacao)) return tamanhoByte(operando, numeroLinha);

        // BASE / NOBASE / END / EXTDEF / EXTREF: não consomem memória
        return 0;
    }

    // =========================
    // Segunda passagem
    // =========================
    private void segundaPassagem() {
        if (!erros.isEmpty()) return;

        enderecoBase = null;

        for (LinhaMontagem linha : linhas) {
            if (linha.operacao == null) continue;

            String operacao = linha.operacao;
            String operando = (linha.operando == null) ? "" : linha.operando.trim();

            // BASE / NOBASE
            if ("BASE".equals(operacao)) {
                if (tabelaSimbolos.containsKey(operando)) {
                    enderecoBase = tabelaSimbolos.get(operando).endereco;
                } else {
                    adicionarErro(linha.numeroLinha, "BASE exige símbolo definido: " + operando);
                }
                continue;
            }
            if ("NOBASE".equals(operacao)) {
                enderecoBase = null;
                continue;
            }

            // diretivas que geram código
            if ("BYTE".equals(operacao)) {
                linha.codigoObjeto = gerarCodigoByte(operando, linha.numeroLinha);
                continue;
            }
            if ("WORD".equals(operacao)) {
                linha.codigoObjeto = gerarCodigoWord(operando, linha.endereco, linha.numeroLinha);
                continue;
            }

            // diretivas sem código
            if (diretivas.contains(operacao)) {
                // START/END/RESB/RESW/EXTDEF/EXTREF já tratados
                continue;
            }

            // instruções
            if (ehInstrucao(operacao)) {
                linha.codigoObjeto = gerarCodigoInstrucao(operacao, operando, linha.endereco, linha.numeroLinha);
            }
        }
    }

    // =========================
    // Geração de instruções
    // =========================
    private String gerarCodigoInstrucao(String operacao, String operando, int enderecoInstrucao, int numeroLinha) {
        boolean estendido = operacao.startsWith("+");
        String base = estendido ? operacao.substring(1) : operacao;

        InformacaoInstrucao info = tabelaInstrucoes.get(base);
        if (info == null) {
            adicionarErro(numeroLinha, "Instrução inválida: " + operacao);
            return "";
        }

        if (info.formato == 1) {
            return String.format("%02X", info.opcode);
        }

        if (info.formato == 2) {
            return gerarFormato2(info.opcode, operando, numeroLinha);
        }

        // Formato 3/4
        return gerarFormato34(info.opcode, base, estendido, operando, enderecoInstrucao, numeroLinha);
    }

    private String gerarFormato2(int opcode, String operando, int numeroLinha) {
        if (operando == null || operando.isBlank()) return String.format("%02X00", opcode);

        String[] partes = operando.split(",");
        String r1 = partes[0].trim().toUpperCase();
        String r2 = (partes.length > 1) ? partes[1].trim().toUpperCase() : "";

        int codigoR1 = tabelaRegistradores.getOrDefault(r1, -1);
        int codigoR2 = r2.isEmpty() ? 0 : tabelaRegistradores.getOrDefault(r2, -1);

        if (codigoR1 < 0) adicionarErro(numeroLinha, "Registrador inválido: " + r1);
        if (!r2.isEmpty() && codigoR2 < 0) adicionarErro(numeroLinha, "Registrador inválido: " + r2);

        if (codigoR1 < 0) codigoR1 = 0;
        if (codigoR2 < 0) codigoR2 = 0;

        return String.format("%02X%01X%01X", opcode, codigoR1, codigoR2);
    }

    private String gerarFormato34(int opcode, String mnemBase, boolean estendido, String operando, int enderecoInstrucao, int numeroLinha) {
        String oper = (operando == null) ? "" : operando.trim();

        // RSUB
        if ("RSUB".equals(mnemBase)) {
            if (estendido) {
                int byte1 = (opcode & 0xFC) | 0x03; // n=1, i=1
                int byte2 = (1 << 4); // e=1
                return String.format("%02X%02X0000", byte1, byte2);
            } else {
                return String.format("%02X0000", (opcode & 0xFC) | 0x03);
            }
        }

        // Flags n i x
        int n = 1, i = 1, x = 0;

        if (oper.startsWith("#")) {
            n = 0; i = 1;
            oper = oper.substring(1).trim();
        } else if (oper.startsWith("@")) {
            n = 1; i = 0;
            oper = oper.substring(1).trim();
        }

        if (oper.toUpperCase().endsWith(",X")) {
            x = 1;
            oper = oper.substring(0, oper.length() - 2).trim();
        }

        // Se é referência externa -> força formato 4 (mesmo sem '+')
        // Isso evita PC-relative com símbolo externo e evita PC indo para lugar absurdo.
        if (!estendido && referenciasExternas.contains(oper)) {
            estendido = true;
        }

        // Imediato constante (ex: #5)
        Integer numeroImediato = tentarInterpretarNumero(oper);

        if (estendido) {
            // ========== Formato 4 (correto com xbpe) ==========
            int e = 1, b = 0, p = 0;

            int enderecoAlvo = 0;
            boolean ehExterno = referenciasExternas.contains(oper);

            if (numeroImediato != null) {
                enderecoAlvo = numeroImediato;
                ehExterno = false;
            } else if (!ehExterno) {
                Simbolo s = tabelaSimbolos.get(oper);
                if (s == null) {
                    adicionarErro(numeroLinha, "Símbolo não definido: " + oper);
                    enderecoAlvo = 0;
                } else {
                    enderecoAlvo = s.endereco;
                }
            }

            int byte1 = (opcode & 0xFC) | (n << 1) | i;
            int flags = (x << 3) | (b << 2) | (p << 1) | e;

            int endereco20 = enderecoAlvo & 0xFFFFF;
            int byte2 = (flags << 4) | ((endereco20 >> 16) & 0x0F);
            int byte3 = (endereco20 >> 8) & 0xFF;
            int byte4 = endereco20 & 0xFF;

            // Registro M: começa no segundo byte do endereço (enderecoInstrucao + 1)
            // 05 nibbles (20 bits)
            int enderecoRegistroM = enderecoInstrucao + 1;

            if (ehExterno) {
                registrosModificacao.add(new RegistroModificacao(enderecoRegistroM, 0x05, '+', oper));
            } else {
                // relocação simples (somar endereço de carga / csaddr) para programas relocáveis
                registrosModificacao.add(new RegistroModificacao(enderecoRegistroM, 0x05, null, null));
            }

            return String.format("%02X%02X%02X%02X", byte1, byte2, byte3, byte4);
        }

        // ========== Formato 3 ==========
        int e = 0;
        int b = 0;
        int p = 0;

        int byte1 = (opcode & 0xFC) | (n << 1) | i;

        // Imediato constante cabe em 12 bits
        if (numeroImediato != null) {
            int disp = numeroImediato & 0xFFF;
            int flags = (x << 3) | (b << 2) | (p << 1) | e;
            int byte2 = (flags << 4) | ((disp >> 8) & 0x0F);
            int byte3 = disp & 0xFF;
            return String.format("%02X%02X%02X", byte1, byte2, byte3);
        }

        // Símbolo local
        Simbolo simbolo = tabelaSimbolos.get(oper);
        if (simbolo == null) {
            adicionarErro(numeroLinha, "Símbolo não definido: " + oper);
            simbolo = new Simbolo(oper, 0);
        }

        int enderecoAlvo = simbolo.endereco;
        int pc = enderecoInstrucao + 3;

        int deslocamento = enderecoAlvo - pc;

        // PC-relative
        if (deslocamento >= -2048 && deslocamento <= 2047) {
            p = 1;
            int disp = deslocamento & 0xFFF;
            int flags = (x << 3) | (b << 2) | (p << 1) | e;
            int byte2 = (flags << 4) | ((disp >> 8) & 0x0F);
            int byte3 = disp & 0xFF;
            return String.format("%02X%02X%02X", byte1, byte2, byte3);
        }

        // Base-relative
        if (enderecoBase != null) {
            int dispBase = enderecoAlvo - enderecoBase;
            if (dispBase >= 0 && dispBase <= 4095) {
                b = 1; p = 0;
                int disp = dispBase & 0xFFF;
                int flags = (x << 3) | (b << 2) | (p << 1) | e;
                int byte2 = (flags << 4) | ((disp >> 8) & 0x0F);
                int byte3 = disp & 0xFF;
                return String.format("%02X%02X%02X", byte1, byte2, byte3);
            }
        }

        // Fora de alcance -> precisa formato 4
        adicionarErro(numeroLinha, "Endereço fora de alcance (use + para formato 4): " + operando);
        return "";
    }

    // =========================
    // Geração de WORD / BYTE
    // =========================
    private String gerarCodigoWord(String operando, int enderecoPalavra, int numeroLinha) {
        String oper = (operando == null) ? "" : operando.trim();

        Integer numero = tentarInterpretarNumero(oper);
        if (numero != null) {
            return String.format("%06X", numero & 0xFFFFFF);
        }

        // símbolo externo
        if (referenciasExternas.contains(oper)) {
            // valor inicial 000000, corrigido pelo ligador/relocador
            registrosModificacao.add(new RegistroModificacao(enderecoPalavra, 0x06, '+', oper));
            return "000000";
        }

        // símbolo local -> relocação simples
        Simbolo s = tabelaSimbolos.get(oper);
        if (s == null) {
            adicionarErro(numeroLinha, "Símbolo não definido em WORD: " + oper);
            return "000000";
        }

        registrosModificacao.add(new RegistroModificacao(enderecoPalavra, 0x06, null, null));
        return String.format("%06X", s.endereco & 0xFFFFFF);
    }

    private int tamanhoByte(String operando, int numeroLinha) {
        String op = (operando == null) ? "" : operando.trim();
        if (op.startsWith("C'") && op.endsWith("'") && op.length() >= 3) {
            return op.substring(2, op.length() - 1).length();
        }
        if (op.startsWith("X'") && op.endsWith("'") && op.length() >= 3) {
            String hex = op.substring(2, op.length() - 1).replaceAll("\\s+", "");
            return (hex.length() + 1) / 2;
        }
        adicionarErro(numeroLinha, "Operando inválido para BYTE: " + operando);
        return 0;
    }

    private String gerarCodigoByte(String operando, int numeroLinha) {
        String op = (operando == null) ? "" : operando.trim();

        if (op.startsWith("C'") && op.endsWith("'") && op.length() >= 3) {
            String texto = op.substring(2, op.length() - 1);
            StringBuilder sb = new StringBuilder();
            for (char c : texto.toCharArray()) {
                sb.append(String.format("%02X", (int) c));
            }
            return sb.toString();
        }

        if (op.startsWith("X'") && op.endsWith("'") && op.length() >= 3) {
            String hex = op.substring(2, op.length() - 1).replaceAll("\\s+", "").toUpperCase();
            if ((hex.length() % 2) != 0) hex = "0" + hex;
            // valida caracteres
            if (!hex.matches("[0-9A-F]*")) {
                adicionarErro(numeroLinha, "Hex inválido em BYTE: " + operando);
                return "";
            }
            return hex;
        }

        adicionarErro(numeroLinha, "Operando inválido para BYTE: " + operando);
        return "";
    }

    // =========================
    // Geração do programa objeto
    // (H, D, R, T, M, E)
    // Compatível com seu Ligador
    // =========================
    private String gerarProgramaObjeto() {
        if (!erros.isEmpty()) {
            return "ERRO: montagem falhou.\n";
        }

        StringBuilder saida = new StringBuilder();

        // H + nome(6) + inicio(6) + tamanho(6)
        String nome = ajustarParaSeis(nomePrograma);
        saida.append("H").append(nome)
                .append(String.format("%06X", enderecoInicial))
                .append(String.format("%06X", tamanhoPrograma))
                .append("\n");

        // D concatenado: D + (simbolo(6) + endereco(6))*
        if (!definicoesExternas.isEmpty()) {
            StringBuilder registroD = new StringBuilder("D");
            for (String simbolo : definicoesExternas) {
                int endereco = 0;
                Simbolo s = tabelaSimbolos.get(simbolo);
                if (s != null) endereco = s.endereco;
                registroD.append(ajustarParaSeis(simbolo)).append(String.format("%06X", endereco));
            }
            saida.append(registroD).append("\n");
        }

        // R concatenado: R + simbolo(6)*
        if (!referenciasExternas.isEmpty()) {
            StringBuilder registroR = new StringBuilder("R");
            for (String simbolo : referenciasExternas) {
                registroR.append(ajustarParaSeis(simbolo));
            }
            saida.append(registroR).append("\n");
        }

        // T: T + inicio(6) + tamanho(2) + dados
        List<RegistroTexto> registrosTexto = montarRegistrosTexto();
        for (RegistroTexto t : registrosTexto) {
            saida.append(t.paraLinha()).append("\n");
        }

        // M: M + endereco(6) + nibbles(2) [+/-SIMBOLO] opcional
        for (RegistroModificacao m : registrosModificacao) {
            if (m.simbolo == null) {
                saida.append(String.format("M%06X%02X", m.enderecoRelativo, m.quantidadeNibbles)).append("\n");
            } else {
                saida.append(String.format("M%06X%02X%c%s", m.enderecoRelativo, m.quantidadeNibbles, m.sinal, m.simbolo)).append("\n");
            }
        }

        // E + endereco(6)
        saida.append(String.format("E%06X", enderecoInicial));
        return saida.toString();
    }

    private static class RegistroTexto {
        int enderecoInicial;
        StringBuilder dadosHex = new StringBuilder();
        RegistroTexto(int enderecoInicial) { this.enderecoInicial = enderecoInicial; }

        int tamanhoEmBytes() { return dadosHex.length() / 2; }

        boolean cabeMais(int bytesNovos) { return (tamanhoEmBytes() + bytesNovos) <= 30; }

        void adicionar(String codigoHex) { dadosHex.append(codigoHex); }

        String paraLinha() {
            return String.format("T%06X%02X%s", enderecoInicial, tamanhoEmBytes(), dadosHex);
        }
    }

    private List<RegistroTexto> montarRegistrosTexto() {
        List<RegistroTexto> lista = new ArrayList<>();
        RegistroTexto atual = null;

        for (LinhaMontagem linha : linhas) {
            if (linha.codigoObjeto == null || linha.codigoObjeto.isBlank()) continue;

            int bytes = linha.codigoObjeto.length() / 2;

            if (atual == null) {
                atual = new RegistroTexto(linha.endereco);
            }

            // se não for contíguo ou não couber, fecha e abre outro
            int enderecoEsperado = atual.enderecoInicial + atual.tamanhoEmBytes();
            if (linha.endereco != enderecoEsperado || !atual.cabeMais(bytes)) {
                if (atual.tamanhoEmBytes() > 0) lista.add(atual);
                atual = new RegistroTexto(linha.endereco);
            }

            atual.adicionar(linha.codigoObjeto);
        }

        if (atual != null && atual.tamanhoEmBytes() > 0) lista.add(atual);
        return lista;
    }

    // =========================
    // Listagem
    // =========================
    private String gerarListagem() {
        StringBuilder sb = new StringBuilder();

        sb.append("LISTAGEM DO MONTADOR SIC/XE\n");
        sb.append("========================================\n\n");
        sb.append(String.format("Nome do programa: %s\n", nomePrograma));
        sb.append(String.format("Endereço inicial: %06X\n", enderecoInicial));
        sb.append(String.format("Tamanho do programa: %06X bytes\n\n", tamanhoPrograma));

        sb.append("Linha  Endereço  Código Objeto              Instrução\n");
        sb.append("-----  -------  -------------------------  -------------------------------\n");

        for (LinhaMontagem linha : linhas) {
            String codigo = (linha.codigoObjeto == null) ? "" : linha.codigoObjeto;
            sb.append(String.format("%5d  %06X   %-25s  %s\n",
                    linha.numeroLinha, linha.endereco, codigo, linha.original));
        }

        sb.append("\nTABELA DE SÍMBOLOS\n");
        sb.append("-----------------\n");
        for (Simbolo s : tabelaSimbolos.values()) {
            sb.append(String.format("%-10s %06X\n", s.nome, s.endereco));
        }

        if (!erros.isEmpty()) {
            sb.append("\nERROS\n");
            sb.append("-----\n");
            for (String e : erros) sb.append(e).append("\n");
        }

        return sb.toString();
    }

    // =========================
    // Utilidades
    // =========================
    private static boolean ehInstrucao(String operacao) {
        if (operacao == null) return false;
        if (operacao.startsWith("+")) {
            return tabelaInstrucoes.containsKey(operacao.substring(1));
        }
        return tabelaInstrucoes.containsKey(operacao);
    }

    private static class PartesLinha {
        String rotulo;
        String operacao;
        String operando;
    }

    private PartesLinha analisarLinha(String linha) {
        PartesLinha partes = new PartesLinha();

        String[] tokens = linha.trim().split("\\s+");

        if (tokens.length == 1) {
            partes.rotulo = null;
            partes.operacao = tokens[0].toUpperCase();
            partes.operando = "";
            return partes;
        }

        if (tokens.length == 2) {
            if (ehInstrucaoOuDiretiva(tokens[0])) {
                partes.rotulo = null;
                partes.operacao = tokens[0].toUpperCase();
                partes.operando = tokens[1].trim();
            } else {
                partes.rotulo = tokens[0].trim();
                partes.operacao = tokens[1].toUpperCase();
                partes.operando = "";
            }
            return partes;
        }

        // 3 ou mais tokens
        partes.rotulo = tokens[0].trim();
        partes.operacao = tokens[1].toUpperCase();
        int indiceOperando = linha.indexOf(tokens[1]) + tokens[1].length();
        partes.operando = linha.substring(indiceOperando).trim();
        return partes;
    }

    private static boolean ehInstrucaoOuDiretiva(String token) {
        String t = token.toUpperCase();
        return ehInstrucao(t) || diretivas.contains(t);
    }

    private static String removerComentarios(String linhaOriginal) {
        if (linhaOriginal == null) return "";

        String linha = linhaOriginal.trim();
        if (linha.isEmpty()) return "";
        if (linha.startsWith(".")) return "";

        // comentário inline com ponto
        int indice = linha.indexOf('.');
        if (indice >= 0) linha = linha.substring(0, indice).trim();

        return linha.trim();
    }

    private static List<String> separarListaSimbolos(String texto) {
        if (texto == null) return List.of();
        String t = texto.trim();
        if (t.isEmpty()) return List.of();

        String[] itens = t.split("[,\\s]+");
        List<String> saida = new ArrayList<>();
        for (String s : itens) {
            String v = s.trim();
            if (!v.isEmpty()) saida.add(v);
        }
        return saida;
    }

    private int interpretarNumero(String texto, int numeroLinha) {
        Integer v = tentarInterpretarNumero(texto);
        if (v == null) {
            adicionarErro(numeroLinha, "Número inválido: " + texto);
            return 0;
        }
        return v;
    }

    private static Integer tentarInterpretarNumero(String texto) {
        if (texto == null) return null;
        String t = texto.trim().toUpperCase();
        if (t.isEmpty()) return null;

        try {
            if (t.startsWith("0X")) return Integer.parseInt(t.substring(2), 16);
            if (t.endsWith("H")) return Integer.parseInt(t.substring(0, t.length() - 1), 16);

            // se tiver letra A-F, assume hexadecimal (ex: 1A2F)
            if (t.matches(".*[A-F].*")) return Integer.parseInt(t, 16);

            // senão decimal
            return Integer.parseInt(t, 10);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String ajustarParaSeis(String nome) {
        String n = (nome == null) ? "" : nome.trim();
        if (n.length() > 6) n = n.substring(0, 6);
        while (n.length() < 6) n = n + " ";
        return n;
    }

    private void adicionarErro(int numeroLinha, String mensagem) {
        erros.add("Linha " + numeroLinha + ": " + mensagem);
    }
}
