/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ProcessadorDeMacros;

import java.util.*;

/**
 *
 * @author carlo
 */

public class ProcessadorMacros {

    private Map<String, Macro> tabelaMacros = new HashMap<>();
    private Stack<Macro> pilhaMacros = new Stack<>();

    public List<String> processar(List<String> codigoFonte) {

        List<String> saida = new ArrayList<>();

        for (int i = 0; i < codigoFonte.size(); i++) {

            String linha = codigoFonte.get(i);
            String trim = linha.trim();

            if (trim.isEmpty() || trim.startsWith(";")) continue;

            // Início de macro (duas sintaxes)
            if (ehInicioDeMacro(trim)) {
                iniciarMacro(trim);
                continue;
            }

            // Fim de macro
            if (trim.equalsIgnoreCase("MEND")) {
                Macro m = pilhaMacros.pop();
                tabelaMacros.put(m.getNome(), m);
                continue;
            }

            // Dentro de definição de macro
            if (!pilhaMacros.isEmpty()) {
                pilhaMacros.peek().adicionarLinha(linha);
                continue;
            }

            // Código normal → expandir
            expandirLinha(linha, saida);
        }

        return saida;
    }

    private boolean ehInicioDeMacro(String linha) {

        String[] t = linha.split("\\s+");
        return t.length >= 2 &&
               (t[0].equalsIgnoreCase("MACRO") ||
                t[1].equalsIgnoreCase("MACRO"));
    }

    private void iniciarMacro(String linha) {

        String[] tokens = linha.split("\\s+", 3);

        String nome;
        String params = "";

        if (tokens[0].equalsIgnoreCase("MACRO")) {
            nome = tokens[1].toUpperCase();
            if (tokens.length == 3) params = tokens[2];
        } else {
            nome = tokens[0].toUpperCase();
            if (tokens.length == 3) params = tokens[2];
        }

        List<String> parametros = new ArrayList<>();
        if (!params.isEmpty()) {
            for (String p : params.split(",")) {
                parametros.add(p.trim());
            }
        }

        pilhaMacros.push(new Macro(nome, parametros));
    }

    private void expandirLinha(String linha, List<String> saida) {

        String[] partes = linha.trim().split("\\s+");
        String nome = partes[0].toUpperCase();

        if (!tabelaMacros.containsKey(nome)) {
            saida.add(linha);
            return;
        }

        Macro macro = tabelaMacros.get(nome);

        Map<String, String> args = new HashMap<>();
        for (int i = 0; i < macro.getParametros().size(); i++) {
            args.put(macro.getParametros().get(i), partes[i + 1]);
        }

        for (String l : macro.getCorpo()) {

            String expandida = l;
            for (var e : args.entrySet()) {
                expandida = expandida.replace(e.getKey(), e.getValue());
            }

            // expansão recursiva (macro dentro de macro)
            expandirLinha(expandida, saida);
        }
    }
}