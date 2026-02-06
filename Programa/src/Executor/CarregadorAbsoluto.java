
package Executor;  

import java.util.*;

public class CarregadorAbsoluto {
    
    private Memoria memoria;
    private Registradores registradores;
    private List<String> erros;
    
    public CarregadorAbsoluto(Memoria memoria, Registradores registradores) {
        this.memoria = memoria;
        this.registradores = registradores;
        this.erros = new ArrayList<>();
    }
    
    public ResultadoCarregamento carregar(String codigoObjeto) {
        erros.clear();
        
        try {
            String[] linhas = codigoObjeto.split("\\r?\\n");
            int enderecoExecucao = 0;
            boolean temHeader = false;
            
            for (String linha : linhas) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;
                
                char tipo = linha.charAt(0);
                
                switch (tipo) {
                    case 'H':
                        processarHeader(linha);
                        temHeader = true;
                        break;
                        
                    case 'T':
                        if (!temHeader) {
                            erros.add("Registro T sem Header");
                            return new ResultadoCarregamento(0, erros);
                        }
                        processarTexto(linha);
                        break;
                        
                    case 'E':
                        if (linha.length() > 1) {
                            enderecoExecucao = hexParaInt(linha.substring(1, 7));
                        }
                        break;
                        
                    case 'M':
                        
                        erros.add("Registro M encontrado em carregamento absoluto");
                        break;
                        
                    default:
                        erros.add("Tipo desconhecido: " + tipo);
                }
            }
            
       
            registradores.setValor("PC", enderecoExecucao);
            registradores.setValor("SW", 0);
            
            return new ResultadoCarregamento(enderecoExecucao, erros);
            
        } catch (Exception e) {
            erros.add("Erro no carregamento: " + e.getMessage());
            return new ResultadoCarregamento(0, erros);
        }
    }
    
    private void processarHeader(String linha) {
       
        if (linha.length() < 19) {
            erros.add("Header inválido: " + linha);
            return;
        }
        
       
        int inicio = hexParaInt(linha.substring(7, 13));
        int tamanho = hexParaInt(linha.substring(13, 19));
        
        if (inicio + tamanho > memoria.getMem().length) {
            erros.add("Programa excede memória disponível");
        }
    }
    
    private void processarTexto(String linha) {
       
        if (linha.length() < 9) {
            erros.add("Texto inválido: " + linha);
            return;
        }
        
        int inicio = hexParaInt(linha.substring(1, 7));
        int tamanho = hexParaInt(linha.substring(7, 9));
        String dados = linha.substring(9).replaceAll("\\s+", "");
        
        
        for (int i = 0; i < dados.length(); i += 2) {
            String byteStr = dados.substring(i, i + 2);
            int valor = hexParaInt(byteStr);
            memoria.setByte(inicio + (i / 2), (byte) valor);
        }
    }
    
    private int hexParaInt(String hex) {
        try {
            return Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            erros.add("Hexadecimal inválido: " + hex);
            return 0;
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