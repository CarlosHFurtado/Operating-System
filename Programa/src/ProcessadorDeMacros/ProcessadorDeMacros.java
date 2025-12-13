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

public class ProcessadorDeMacros {

    private Map<String, Macro> tabelaMacros = new HashMap<>();

    public List<String> processar(List<String> codigoFonte) {

        List<String> codigoExpandido = new ArrayList<>();
        Iterator<String> it = codigoFonte.iterator();

        while (it.hasNext()) {

            String linha = it.next().trim();

            if (linha.equalsIgnoreCase("MACRO")) {
                lerMacro(it);
            } else if (!linha.isEmpty()) {
                expandirLinha(linha, codigoExpandido);
            }
        }

        return codigoExpandido;
    }

    private void lerMacro(Iterator<String> it) {

        String cabecalho = it.next().trim();
        String[] partes = cabecalho.split("\\s+");

        String nome = partes[0].toUpperCase();
        List<String> parametros = new ArrayList<>();

        for (int i = 1; i < partes.length; i++) {
            parametros.add(partes[i]);
        }

        Macro macro = new Macro(nome, parametros);

        while (it.hasNext()) {

            String linha = it.next().trim();
            if (linha.equalsIgnoreCase("MEND")) break;

            macro.getCorpo().add(linha);
        }

        tabelaMacros.put(nome, macro);
    }

    private void expandirLinha(String linha, List<String> saida) {

        String[] partes = linha.split("\\s+");
        String nome = partes[0].toUpperCase();

        if (!tabelaMacros.containsKey(nome)) {
            saida.add(linha);
            return;
        }

        Macro macro = tabelaMacros.get(nome);
        Map<String, String> mapaArgs = new HashMap<>();

        for (int i = 0; i < macro.getParametros().size(); i++) {
            mapaArgs.put(macro.getParametros().get(i), partes[i + 1]);
        }

        for (String linhaCorpo : macro.getCorpo()) {

            String expandida = linhaCorpo;
            for (var entry : mapaArgs.entrySet()) {
                expandida = expandida.replace(entry.getKey(), entry.getValue());
            }
            saida.add(expandida);
        }
    }
}