package Executor;

import java.util.*;

public class CarregadorRelocador {

    private final Memoria memoria;
    private final Registradores registradores;
    private final List<String> erros;
    private int enderecoCarga;

    public CarregadorRelocador(Memoria memoria, Registradores registradores) {
        
        this.memoria = memoria;
        this.registradores = registradores;
        this.erros = new ArrayList<>();
        this.enderecoCarga = 0;
        
    }

    public void setEnderecoCarga(int enderecoCarga) {
        
        this.enderecoCarga = enderecoCarga;
        
    }

    public ResultadoCarregamento carregar(String codigoObjeto) {
        
        erros.clear();

        try {
            
            List<RegistroTexto> textos = new ArrayList<>();
            List<RegistroModificacao> modificacoes = new ArrayList<>();

            boolean headerLido = false;
            int enderecoExecucao = -1;
            int enderecoInicio = 0;
            int tamanhoPrograma = 0;

            String[] linhas = codigoObjeto.split("\\r?\\n");

            for (String linha : linhas) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;

                char tipo = linha.charAt(0);

                switch (tipo) {
                    
                    case 'H':
                        
                        if (linha.length() < 19) {
                            
                            erros.add("Header inválido: " + linha);
                            break;
                            
                        }
                        
                        enderecoInicio = hexParaInt(linha.substring(7, 13));
                        tamanhoPrograma = hexParaInt(linha.substring(13, 19));
                        headerLido = true;
                        break;

                    case 'T':
                        
                        textos.add(parseRegistroTexto(linha));
                        break;
                        

                    case 'M':
                        
                        modificacoes.add(parseRegistroModificacao(linha));
                        break;

                    case 'E':
                        
                        if (linha.length() >= 7) {
                            enderecoExecucao = hexParaInt(linha.substring(1, 7));
                        } else {
                            enderecoExecucao = enderecoInicio;
                        }
                        break;

                    default:
                        
                        erros.add("Tipo desconhecido: " + tipo);
                        break;
                        
                }
            }

            if (!headerLido) {
                
                erros.add("Header (H) não encontrado no código objeto.");
                
            }

            if (enderecoExecucao < 0) {
               
                enderecoExecucao = enderecoInicio;
                
            }

            long fimAbsoluto = (long) enderecoCarga + (long) tamanhoPrograma;
            
            if (enderecoCarga < 0 || fimAbsoluto > memoria.getMem().length) {
                
                erros.add("Programa relocável excede memória: carga=" + String.format("%06X", enderecoCarga) +
                        " fim=" + String.format("%06X", (int) fimAbsoluto));
                
            }

            if (!erros.isEmpty()) {
                
                return new ResultadoCarregamento(0, 0, 0, erros);
                
            }

            for (RegistroTexto t : textos) {
                
                int enderecoAbsoluto = enderecoCarga + (t.endereco - enderecoInicio);
                carregarTexto(t, enderecoAbsoluto);
                
            }

            int fatorRelocacao = enderecoCarga - enderecoInicio;
            
            for (RegistroModificacao m : modificacoes) {
                
                int enderecoCampo = enderecoCarga + (m.endereco - enderecoInicio);
                aplicarModificacao(m, enderecoCampo, fatorRelocacao);
                
            }

            int enderecoExecucaoAbsoluto = enderecoCarga + (enderecoExecucao - enderecoInicio);
            
            registradores.setValor("PC", enderecoExecucaoAbsoluto);
            registradores.setValor("SW", 0);

            int inicioProgramaAbsoluto = enderecoCarga;

            return new ResultadoCarregamento(enderecoExecucaoAbsoluto, inicioProgramaAbsoluto, tamanhoPrograma, erros);

        } catch (Exception e) {
            
            erros.add("Erro no carregamento: " + e.getMessage());
            return new ResultadoCarregamento(0, 0, 0, erros);
            
        }
    }

    private RegistroTexto parseRegistroTexto(String linha) {
        if (linha.length() < 9) {
            erros.add("Registro T inválido: " + linha);
            return new RegistroTexto(0, new byte[0]);
        }

        int endereco = hexParaInt(linha.substring(1, 7));
        int tamanho = hexParaInt(linha.substring(7, 9));
        String dados = linha.substring(9).replaceAll("\\s+", "");

        int bytesDisponiveis = dados.length() / 2;
        int bytesParaLer = Math.min(tamanho, bytesDisponiveis);

        byte[] bytes = new byte[bytesParaLer];
        for (int i = 0; i < bytesParaLer; i++) {
            String byteStr = dados.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) hexParaInt(byteStr);
        }

        return new RegistroTexto(endereco, bytes);
    }

    private RegistroModificacao parseRegistroModificacao(String linha) {
        if (linha.length() < 9) {
            erros.add("Registro M inválido: " + linha);
            return new RegistroModificacao(0, 0, '+', null);
        }

        int endereco = hexParaInt(linha.substring(1, 7));
        int tamanhoNibbles = hexParaInt(linha.substring(7, 9));

        if (linha.length() > 9) {
            String resto = linha.substring(9).trim();
            if (resto.isEmpty()) {
                return new RegistroModificacao(endereco, tamanhoNibbles, '+', null);
            }
            char sinal = resto.charAt(0);
            String simbolo = resto.substring(1).trim();
            return new RegistroModificacao(endereco, tamanhoNibbles, sinal, simbolo);
        } else {
            return new RegistroModificacao(endereco, tamanhoNibbles, '+', null);
        }
    }

    private void carregarTexto(RegistroTexto texto, int enderecoAbsoluto) {
        for (int i = 0; i < texto.bytes.length; i++) {
            memoria.setByte(enderecoAbsoluto + i, texto.bytes[i]);
        }
    }

    private void aplicarModificacao(RegistroModificacao mod, int enderecoCampo, int fatorRelocacao) {

        int nibbles = mod.tamanhoNibbles;
        int bytesMod = (nibbles + 1) / 2; // ceil
        boolean odd = (nibbles % 2) == 1;

        long ajuste;
        if (mod.simbolo == null) {
            ajuste = fatorRelocacao;
            if (mod.sinal == '-') ajuste = -ajuste;
        } else {            
            ajuste = 0;
        }

        long mascara = mascaraNibbles(nibbles);

        if (odd) {
            
            int primeiro = memoria.getByte(enderecoCampo) & 0xFF;
            int highNibblePreservado = primeiro & 0xF0;

            long raw = lerCampo(enderecoCampo, bytesMod);
            long valorCampo = raw & mascara;

            long novoCampo = (valorCampo + ajuste) & mascara;

            byte[] out = new byte[bytesMod];
            long temp = novoCampo;
            for (int i = bytesMod - 1; i >= 0; i--) {
                out[i] = (byte) (temp & 0xFF);
                temp >>= 8;
            }

            out[0] = (byte) ((out[0] & 0x0F) | highNibblePreservado);

            for (int i = 0; i < bytesMod; i++) {
                memoria.setByte(enderecoCampo + i, out[i]);
            }

        } else {
            long valorAtual = lerCampo(enderecoCampo, bytesMod);
            long novoValor = (valorAtual + ajuste) & mascara;
            escreverCampo(enderecoCampo, bytesMod, novoValor);
        }
    }

    private long lerCampo(int endereco, int bytes) {
        long valor = 0;
        for (int i = 0; i < bytes; i++) {
            valor = (valor << 8) | (memoria.getByte(endereco + i) & 0xFFL);
        }
        return valor;
    }

    private void escreverCampo(int endereco, int bytes, long valor) {
        for (int i = bytes - 1; i >= 0; i--) {
            memoria.setByte(endereco + i, (byte) (valor & 0xFF));
            valor >>= 8;
        }
    }

    private long mascaraNibbles(int nibbles) {
        if (nibbles >= 16) return 0xFFFFFFFFL;
        return (1L << (4L * nibbles)) - 1;
    }

    private int hexParaInt(String hex) {
        try {
            return Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            erros.add("Hexadecimal inválido: " + hex);
            return 0;
        }
    }

    private static class RegistroTexto {
        int endereco;
        byte[] bytes;

        RegistroTexto(int endereco, byte[] bytes) {
            this.endereco = endereco;
            this.bytes = bytes;
        }
    }

    private static class RegistroModificacao {
        int endereco;
        int tamanhoNibbles;
        char sinal;
        String simbolo;

        RegistroModificacao(int endereco, int tamanhoNibbles, char sinal, String simbolo) {
            this.endereco = endereco;
            this.tamanhoNibbles = tamanhoNibbles;
            this.sinal = sinal;
            this.simbolo = simbolo;
        }
    }

    public static class ResultadoCarregamento {
        public final int enderecoExecucao;
        public final int inicioPrograma;
        public final int tamanhoPrograma;
        public final List<String> erros;

        public ResultadoCarregamento(int enderecoExecucao, int inicioPrograma, int tamanhoPrograma, List<String> erros) {
            this.enderecoExecucao = enderecoExecucao;
            this.inicioPrograma = inicioPrograma;
            this.tamanhoPrograma = tamanhoPrograma;
            this.erros = erros;
        }

        public boolean sucesso() {
            return erros.isEmpty();
        }
    }
}
