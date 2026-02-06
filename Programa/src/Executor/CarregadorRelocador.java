package Executor;  

import java.util.*;

public class CarregadorRelocador {
    
    private Memoria memoria;
    private Registradores registradores;
    private List<String> erros;
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
            int enderecoExecucao = 0;
            int enderecoInicio = 0;
            int tamanhoPrograma = 0;
            
            String[] linhas = codigoObjeto.split("\\r?\\n");
                      
            for (String linha : linhas) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;
                
                char tipo = linha.charAt(0);
                
                switch (tipo) {
                    case 'H':
                        enderecoInicio = hexParaInt(linha.substring(7, 13));
                        tamanhoPrograma = hexParaInt(linha.substring(13, 19));
                        break;
                        
                    case 'T':
                        textos.add(parseRegistroTexto(linha));
                        break;
                        
                    case 'M':
                        modificacoes.add(parseRegistroModificacao(linha));
                        break;
                        
                    case 'E':
                        if (linha.length() > 1) {
                            enderecoExecucao = hexParaInt(linha.substring(1, 7));
                        } else {
                            enderecoExecucao = enderecoInicio;
                        }
                        break;
                }
            }
            
       
            for (RegistroTexto t : textos) {
                int enderecoAbsoluto = enderecoCarga + (t.endereco - enderecoInicio);
                carregarTexto(t, enderecoAbsoluto);
            }
            
            
            for (RegistroModificacao m : modificacoes) {
                int enderecoCampo = enderecoCarga + (m.endereco - enderecoInicio);
                aplicarModificacao(m, enderecoCampo, enderecoCarga - enderecoInicio);
            }
            
       
            int enderecoExecucaoAbsoluto = enderecoCarga + (enderecoExecucao - enderecoInicio);
            registradores.setValor("PC", enderecoExecucaoAbsoluto);
            registradores.setValor("SW", 0);
            
            return new ResultadoCarregamento(enderecoExecucaoAbsoluto, erros);
            
        } catch (Exception e) {
            erros.add("Erro no carregamento: " + e.getMessage());
            return new ResultadoCarregamento(0, erros);
        }
    }
    
    private RegistroTexto parseRegistroTexto(String linha) {
        int endereco = hexParaInt(linha.substring(1, 7));
        int tamanho = hexParaInt(linha.substring(7, 9));
        String dados = linha.substring(9).replaceAll("\\s+", "");
        
        byte[] bytes = new byte[dados.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            String byteStr = dados.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) hexParaInt(byteStr);
        }
        
        return new RegistroTexto(endereco, bytes);
    }
    
    private RegistroModificacao parseRegistroModificacao(String linha) {
        int endereco = hexParaInt(linha.substring(1, 7));
        int tamanhoNibbles = hexParaInt(linha.substring(7, 9));
        
 
        if (linha.length() > 9) {
            String resto = linha.substring(9).trim();
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
        int bytesMod = (mod.tamanhoNibbles + 1) / 2;
        long valorAtual = lerCampo(enderecoCampo, bytesMod);
        
        long ajuste = 0;
        if (mod.simbolo == null) {
           
            ajuste = fatorRelocacao;
            if (mod.sinal == '-') ajuste = -ajuste;
        } else {
            
            ajuste = 0; 
        }
        
        long novoValor = (valorAtual + ajuste) & mascaraNibbles(mod.tamanhoNibbles);
        escreverCampo(enderecoCampo, bytesMod, novoValor);
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
        public final List<String> erros;
        
        public ResultadoCarregamento(int enderecoExecucao, List<String> erros) {
            this.enderecoExecucao = enderecoExecucao;
            this.erros = erros;
        }
        
        public boolean sucesso() {
            return erros.isEmpty();
        }
    }
}