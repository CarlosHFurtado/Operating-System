package ligador;

import java.util.*;

public final class Ligador {

    public enum Modo {
        LIGADOR,
        LIGADOR_RELOCADOR
    }

   

    public static final class ResultadoLigacao {
        public final String objetoLigado;   
        public final byte[] memoria;        
        public final int enderecoExecucao;  
        public final List<String> erros;

        ResultadoLigacao(String objetoLigado, byte[] memoria, int enderecoExecucao, List<String> erros) {
            this.objetoLigado = objetoLigado;
            this.memoria = memoria;
            this.enderecoExecucao = enderecoExecucao;
            this.erros = erros;
        }

        public String dumpHex(int inicio, int tamanho) {
            if (memoria == null) return "";
            StringBuilder sb = new StringBuilder();
            int fim = Math.min(memoria.length, inicio + tamanho);
            for (int addr = inicio; addr < fim; addr += 16) {
                sb.append(String.format("%06X: ", addr));
                for (int i = 0; i < 16 && addr + i < fim; i++) {
                    sb.append(String.format("%02X ", memoria[addr + i] & 0xFF));
                }
                sb.append("\n");
            }
            return sb.toString().trim();
        }
    }

    

    private static final class Modulo {
        String nome = "";
        int startRel = 0;     
        int length = 0;       
        int csaddr = 0;      
        Integer execRel = null; 

        
        Map<String, Integer> definicoesRel = new LinkedHashMap<>();  
        Set<String> referencias = new LinkedHashSet<>();             

        List<TextRec> textos = new ArrayList<>();
        List<ModRec> mods = new ArrayList<>();
    }

    private static final class TextRec {
        int startRel;
        byte[] bytes;
        TextRec(int startRel, byte[] bytes) { this.startRel = startRel; this.bytes = bytes; }
    }

    private static final class ModRec {
        int addrRel;      
        int nibbles;     
        char sinal;       
        String simbolo;   
        ModRec(int addrRel, int nibbles, char sinal, String simbolo) {
            this.addrRel = addrRel;
            this.nibbles = nibbles;
            this.sinal = sinal;
            this.simbolo = simbolo;
        }
    }

 

    private final Map<String, Integer> estab = new LinkedHashMap<>(); 
    private final List<String> erros = new ArrayList<>();
    private final byte[] memoria;

    public Ligador() {
        this(1024 * 1024);
    }

    public Ligador(int tamanhoMemoriaBytes) {
        this.memoria = new byte[tamanhoMemoriaBytes];
    }

 

    public ResultadoLigacao ligar(List<String> objetos, Modo modo, int enderecoCarga) {
        erros.clear();
        estab.clear();

        List<Modulo> modulos = parseModulos(objetos);
        if (modulos.isEmpty()) {
            return new ResultadoLigacao("", null, 0, List.copyOf(erros));
        }

       
        int csaddr = (modo == Modo.LIGADOR_RELOCADOR) ? enderecoCarga : 0;

        for (Modulo m : modulos) {
            m.csaddr = csaddr;

            
            if (!m.nome.isBlank()) {
                if (estab.containsKey(m.nome)) {
                    erro("Seção duplicada: " + m.nome);
                } else {
                    estab.put(m.nome, m.csaddr);
                }
            }

           
            for (var e : m.definicoesRel.entrySet()) {
                String sym = e.getKey();
                int abs = m.csaddr + e.getValue();
                if (estab.containsKey(sym)) {
                    erro("Símbolo externo duplicado: " + sym);
                } else {
                    estab.put(sym, abs);
                }
            }

            csaddr += m.length;
        }

        
        for (Modulo m : modulos) {
            for (String ref : m.referencias) {
                if (!estab.containsKey(ref)) {
                    erro("Símbolo em R não encontrado no ESTAB: " + ref + " (módulo " + m.nome + ")");
                }
            }
        }

       
        if (modo == Modo.LIGADOR_RELOCADOR) {
            Arrays.fill(memoria, (byte) 0);
            int execAbs = carregarEmMemoriaEAplicarMods(modulos);

            // agora também gera OBJETO ABSOLUTO (sem M) para o CarregadorAbsoluto
            String objAbs = gerarObjetoAbsolutoSemM(modulos, execAbs);

            return new ResultadoLigacao(objAbs, memoria, execAbs, List.copyOf(erros));

        } else {
            String objLigado = gerarObjetoLigadoRelocavel(modulos);
            int execRel = calcularExecucaoRelativa(modulos);
            return new ResultadoLigacao(objLigado, null, execRel, List.copyOf(erros));
        }
    }

    
    public ResultadoLigacao ligar(String objeto) {
        return ligar(List.of(objeto), Modo.LIGADOR_RELOCADOR, 0);
    }

   

    private int carregarEmMemoriaEAplicarMods(List<Modulo> modulos) {
        boolean execSetado = false;
        int execAbs = 0;

        for (Modulo m : modulos) {

       
            for (TextRec t : m.textos) {
                int addrAbs = m.csaddr + t.startRel;
                escreverBytes(addrAbs, t.bytes);
            }

       
            for (ModRec mr : m.mods) {
                int addrCampoAbs = m.csaddr + mr.addrRel;

                long valor = lerCampoNibbles(addrCampoAbs, mr.nibbles);
                long ajuste = 0;

                if (mr.simbolo == null) {
                    
                    ajuste = m.csaddr;
                } else {
                    Integer symAbs = estab.get(mr.simbolo);
                    if (symAbs == null) {
                        erro("Símbolo em M não encontrado no ESTAB: " + mr.simbolo + " (módulo " + m.nome + ")");
                        symAbs = 0;
                    }
                    ajuste = symAbs;
                    if (mr.sinal == '-') ajuste = -ajuste;
                }

                long mask = mascaraNibbles(mr.nibbles);
                long novo = (valor + ajuste) & mask;
                escreverCampoNibbles(addrCampoAbs, mr.nibbles, novo);
            }

           
            if (!execSetado) {
                int eRel = (m.execRel == null) ? 0 : m.execRel;
                execAbs = m.csaddr + eRel;
                execSetado = true;
            }
        }

        return execAbs;
    }

    

    private String gerarObjetoLigadoRelocavel(List<Modulo> modulos) {

       
        String nome = modulos.get(0).nome;
        if (nome == null || nome.isBlank()) nome = "PROG";
        if (nome.length() > 6) nome = nome.substring(0, 6);
        nome = String.format("%-6s", nome);

        int tamanhoTotal = 0;
        for (Modulo m : modulos) tamanhoTotal += m.length;

        byte[] imagem = new byte[Math.max(tamanhoTotal, 1)];
        Arrays.fill(imagem, (byte) 0);

       
        boolean[] bytePresente = new boolean[imagem.length];

     
        for (Modulo m : modulos) {
            for (TextRec t : m.textos) {
                int enderecoBaseZero = m.csaddr + t.startRel; 

                if (enderecoBaseZero < 0 || enderecoBaseZero + t.bytes.length > imagem.length) {
                    erro("Texto fora do tamanho do programa ligado (base0).");
                    continue;
                }

                System.arraycopy(t.bytes, 0, imagem, enderecoBaseZero, t.bytes.length);

                for (int i = 0; i < t.bytes.length; i++) {
                    bytePresente[enderecoBaseZero + i] = true;
                }
            }
        }

   
        List<ModRec> modsFinais = new ArrayList<>();


        for (Modulo m : modulos) {
            for (ModRec mr : m.mods) {
                int addrCampo = m.csaddr + mr.addrRel; 
                if (addrCampo < 0 || addrCampo >= imagem.length) {
                    erro("M aponta para fora do programa ligado (base0).");
                    continue;
                }

                long valor = lerCampoNibblesImagem(imagem, addrCampo, mr.nibbles);

                if (mr.simbolo != null) {
                    Integer symAbsBase0 = estab.get(mr.simbolo); 
                    if (symAbsBase0 == null) {
                        erro("Símbolo em M não encontrado no ESTAB (modo ligador): " + mr.simbolo);
                        symAbsBase0 = 0;
                    }
                    long ajuste = symAbsBase0;
                    if (mr.sinal == '-') ajuste = -ajuste;

                    long mask = mascaraNibbles(mr.nibbles);
                    long novo = (valor + ajuste) & mask;
                    escreverCampoNibblesImagem(imagem, addrCampo, mr.nibbles, novo);
                } else {
                    
                }

                
                modsFinais.add(new ModRec(addrCampo, mr.nibbles, (char)0, null));
            }
        }

        
        StringBuilder out = new StringBuilder();
        out.append(String.format("H%s%06X%06X%n", nome, 0, tamanhoTotal));

       
        int i = 0;
        while (i < imagem.length) {
          
            while (i < imagem.length && !bytePresente[i]) {
                i++;
            }
            if (i >= imagem.length) break;

            int inicio = i;
            int tamanho = 0;

            while (i < imagem.length && tamanho < 30 && bytePresente[i]) {
                i++;
                tamanho++;
            }

            byte[] bloco = Arrays.copyOfRange(imagem, inicio, inicio + tamanho);
            out.append(String.format("T%06X%02X%s%n", inicio, tamanho, bytesParaHex(bloco)));
        }

        modsFinais = compactarMods(modsFinais);

        for (ModRec mr : modsFinais) {
            out.append(String.format("M%06X%02X%n", mr.addrRel, mr.nibbles));
        }

        int execRel = calcularExecucaoRelativa(modulos);
        out.append(String.format("E%06X%n", execRel));

        return out.toString().trim();
    }

    private static List<ModRec> compactarMods(List<ModRec> in) {
        Set<String> seen = new LinkedHashSet<>();
        List<ModRec> out = new ArrayList<>();
        for (ModRec m : in) {
            String key = String.format("%06X%02X", m.addrRel, m.nibbles);
            if (seen.add(key)) out.add(m);
        }
        return out;
    }

    private int calcularExecucaoRelativa(List<Modulo> modulos) {
        
        for (Modulo m : modulos) {
            if (m.execRel != null) return m.csaddr + m.execRel;
        }
        return 0;
    }

    private List<Modulo> parseModulos(List<String> objetos) {
        List<Modulo> out = new ArrayList<>();

        for (String conteudo : objetos) {
            if (conteudo == null || conteudo.isBlank()) continue;

            Modulo m = new Modulo();

            List<String> linhas = Arrays.stream(conteudo.split("\\R"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            for (String l : linhas) {
                char tipo = l.charAt(0);

                switch (tipo) {
                    case 'H' -> {
                        if (l.length() < 19) { erro("H inválido: " + l); continue; }
                        m.nome = l.substring(1, 7).trim();
                        m.startRel = Integer.parseInt(l.substring(7, 13), 16);
                        m.length = Integer.parseInt(l.substring(13, 19), 16);
                    }

                    case 'D' -> {
                        String resto = l.substring(1);
                       
                        for (int i = 0; i + 12 <= resto.length(); i += 12) {
                            String sym = resto.substring(i, i + 6).trim();
                            int addr = Integer.parseInt(resto.substring(i + 6, i + 12), 16);
                            if (!sym.isEmpty()) m.definicoesRel.put(sym, addr);
                        }
                    }

                    case 'R' -> {
                        String resto = l.substring(1);
                     
                        for (int i = 0; i + 6 <= resto.length(); i += 6) {
                            String sym = resto.substring(i, i + 6).trim();
                            if (!sym.isEmpty()) m.referencias.add(sym);
                        }
                    }

                    case 'T' -> {
                        if (l.length() < 9) { erro("T inválido: " + l); continue; }
                        int start = Integer.parseInt(l.substring(1, 7), 16);
                        int len = Integer.parseInt(l.substring(7, 9), 16);
                        String hex = l.substring(9).trim();
                        byte[] bytes = hexParaBytes(hex);

                        
                        if (bytes.length != len) { /* ignora */ }

                        m.textos.add(new TextRec(start, bytes));
                    }

                    case 'M' -> {
                        if (l.length() < 9) { erro("M inválido: " + l); continue; }

                        int addr = Integer.parseInt(l.substring(1, 7), 16);
                        int nibbles = Integer.parseInt(l.substring(7, 9), 16);

                        char sinal = 0;
                        String simbolo = null;

                        if (l.length() > 9) {
                            String resto = l.substring(9).trim();
                            if (!resto.isEmpty()) {
                                sinal = resto.charAt(0);
                                simbolo = resto.substring(1).trim();
                                if (sinal != '+' && sinal != '-') {
                                    // formato sem sinal: assume '+'
                                    sinal = '+';
                                    simbolo = resto.trim();
                                }
                            }
                        }

                        m.mods.add(new ModRec(addr, nibbles, sinal, simbolo));
                    }

                    case 'E' -> {
                        if (l.length() >= 7) m.execRel = Integer.parseInt(l.substring(1, 7), 16);
                        else m.execRel = 0;
                    }

                    default -> {
                        
                    }
                }
            }

            out.add(m);
        }

        return out;
    }    

    private void escreverBytes(int addr, byte[] bytes) {
        if (addr < 0 || addr + bytes.length > memoria.length) {
            erro("Escrita fora da memória em " + String.format("%06X", addr));
            return;
        }
        System.arraycopy(bytes, 0, memoria, addr, bytes.length);
    }

    private long lerCampoNibbles(int addr, int nibbles) {
        int bytes = (nibbles + 1) / 2;
        if (addr < 0 || addr + bytes > memoria.length) {
            erro("Leitura fora da memória em " + String.format("%06X", addr));
            return 0;
        }

        long v = 0;
        for (int i = 0; i < bytes; i++) v = (v << 8) | (memoria[addr + i] & 0xFFL);

        if ((nibbles % 2) == 1) v &= mascaraNibbles(nibbles);
        return v;
    }

    private void escreverCampoNibbles(int addr, int nibbles, long novoValor) {
        int bytes = (nibbles + 1) / 2;
        if (addr < 0 || addr + bytes > memoria.length) {
            erro("Escrita fora da memória em " + String.format("%06X", addr));
            return;
        }

        long atual = 0;
        for (int i = 0; i < bytes; i++) atual = (atual << 8) | (memoria[addr + i] & 0xFFL);

        long mask = mascaraNibbles(nibbles);
        long combinado;

        if ((nibbles % 2) == 1) {
            long preserva = atual & ~mask; 
            combinado = preserva | (novoValor & mask);
        } else {
            combinado = (novoValor & mask);
        }

        for (int i = bytes - 1; i >= 0; i--) {
            memoria[addr + i] = (byte) (combinado & 0xFF);
            combinado >>= 8;
        }
    }

    private static long lerCampoNibblesImagem(byte[] img, int addr, int nibbles) {
        int bytes = (nibbles + 1) / 2;
        if (addr < 0 || addr + bytes > img.length) return 0;

        long v = 0;
        for (int i = 0; i < bytes; i++) v = (v << 8) | (img[addr + i] & 0xFFL);

        if ((nibbles % 2) == 1) v &= mascaraNibbles(nibbles);
        return v;
    }

    private static void escreverCampoNibblesImagem(byte[] img, int addr, int nibbles, long novoValor) {
        int bytes = (nibbles + 1) / 2;
        if (addr < 0 || addr + bytes > img.length) return;

        long atual = 0;
        for (int i = 0; i < bytes; i++) atual = (atual << 8) | (img[addr + i] & 0xFFL);

        long mask = mascaraNibbles(nibbles);
        long combinado;

        if ((nibbles % 2) == 1) {
            long preserva = atual & ~mask;
            combinado = preserva | (novoValor & mask);
        } else {
            combinado = (novoValor & mask);
        }

        for (int i = bytes - 1; i >= 0; i--) {
            img[addr + i] = (byte) (combinado & 0xFF);
            combinado >>= 8;
        }
    }

    private static long mascaraNibbles(int nibbles) {
        if (nibbles >= 16) return -1L;
        return (1L << (4L * nibbles)) - 1;
    }

    private static byte[] hexParaBytes(String hex) {
        String h = hex.replaceAll("\\s+", "");
        if ((h.length() % 2) != 0) h = "0" + h;

        byte[] out = new byte[h.length() / 2];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) Integer.parseInt(h.substring(2*i, 2*i+2), 16);
        }
        return out;
    }

    private static String bytesParaHex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte v : b) sb.append(String.format("%02X", v & 0xFF));
        return sb.toString();
    }

    private void erro(String msg) {
        erros.add(msg);
    }
    
    private String gerarObjetoAbsolutoSemM(List<Modulo> modulos, int execAbs) {

        int inicio = modulos.get(0).csaddr; // no modo relocador = enderecoCarga
        int tamanhoTotal = 0;
        for (Modulo m : modulos) tamanhoTotal += m.length;

        String nome = modulos.get(0).nome;
        if (nome == null || nome.isBlank()) nome = "PROG";
        if (nome.length() > 6) nome = nome.substring(0, 6);
        nome = String.format("%-6s", nome);


        byte[] img = new byte[Math.max(tamanhoTotal, 1)];
        Arrays.fill(img, (byte) 0);


        for (Modulo m : modulos) {
            int baseOffset = m.csaddr - inicio;
            for (TextRec t : m.textos) {
                int off = baseOffset + t.startRel;
                if (off < 0 || off + t.bytes.length > img.length) {
                    erro("Texto fora do programa absoluto (imagem).");
                    continue;
                }
                System.arraycopy(t.bytes, 0, img, off, t.bytes.length);
            }
        }


        for (Modulo m : modulos) {
            int baseOffset = m.csaddr - inicio;

            for (ModRec mr : m.mods) {
                int offCampo = baseOffset + mr.addrRel;

                long valor = lerCampoNibblesImagem(img, offCampo, mr.nibbles);
                long ajuste;

                if (mr.simbolo == null) {

                    ajuste = m.csaddr;
                } else {
                    Integer symAbs = estab.get(mr.simbolo);
                    if (symAbs == null) {
                        erro("Símbolo em M não encontrado no ESTAB: " + mr.simbolo);
                        symAbs = 0;
                    }
                    ajuste = symAbs;
                    if (mr.sinal == '-') ajuste = -ajuste;
                }

                long mask = mascaraNibbles(mr.nibbles);
                long novo = (valor + ajuste) & mask;
                escreverCampoNibblesImagem(img, offCampo, mr.nibbles, novo);
            }
        }


        StringBuilder out = new StringBuilder();
        out.append(String.format("H%s%06X%06X%n", nome, inicio, tamanhoTotal));

        int i = 0;
        while (i < img.length) {
            while (i < img.length && img[i] == 0) i++;
            if (i >= img.length) break;

            int start = i;
            int len = 0;
            while (i < img.length && len < 30) {
                if (len > 0 && img[i] == 0) break;
                i++; len++;
            }

            byte[] bloco = Arrays.copyOfRange(img, start, start + len);
            out.append(String.format("T%06X%02X%s%n", inicio + start, len, bytesParaHex(bloco)));
        }

        out.append(String.format("E%06X%n", execAbs));
        return out.toString().trim();
    }

}
