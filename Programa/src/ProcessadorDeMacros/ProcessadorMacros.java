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

            if (trim.isEmpty() || trim.startsWith(";") || trim.startsWith(".")) continue;

            if (ehInicioDeMacro(trim)) {
                iniciarMacro(trim);
                continue;
            }

            if (trim.equalsIgnoreCase("MEND")) {
                Macro m = pilhaMacros.pop();
                tabelaMacros.put(m.getNome(), m);
                continue;
            }

            if (!pilhaMacros.isEmpty()) {
                pilhaMacros.peek().adicionarLinha(linha);
                continue;
            }

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

        String label = null;
        String nomeMacro;
        int indiceArgs;

        if (partes.length >= 2 && tabelaMacros.containsKey(partes[1].toUpperCase())) {
            label = partes[0];
            nomeMacro = partes[1].toUpperCase();
            indiceArgs = 2;
        }
        else if (tabelaMacros.containsKey(partes[0].toUpperCase())) {
            nomeMacro = partes[0].toUpperCase();
            indiceArgs = 1;
        }
        else {
            saida.add(linha);
            return;
        }

        Macro macro = tabelaMacros.get(nomeMacro);

        String argsLinha = String.join(" ",
                Arrays.copyOfRange(partes, indiceArgs, partes.length));

        String[] valores = argsLinha.split("[,\\s]+");

        if (valores.length < macro.getParametros().size()) {
            throw new RuntimeException(
                "Macro " + macro.getNome() + " chamada com parâmetros insuficientes.");
        }

        Map<String, String> args = new HashMap<>();
        for (int i = 0; i < macro.getParametros().size(); i++) {
            args.put(macro.getParametros().get(i), valores[i]);
        }


        for (String l : macro.getCorpo()) {

            String expandida = l;
            for (var e : args.entrySet()) {
                expandida = expandida.replace(e.getKey(), e.getValue());
            }

            if (label != null) {
                expandida = String.format("%-8s %s", label, expandida);
                label = null;
            }


            expandirLinha(expandida, saida);
        }
    }
}