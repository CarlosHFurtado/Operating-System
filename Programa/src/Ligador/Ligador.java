/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ligador;

import montador.*;
import java.util.*;

/**
 *
 * @author carlo
 */
public class Ligador {

    private List<Montador> programas = new ArrayList<>();
    private Map<String, Integer> tabelaGlobal = new HashMap<>();

    public void adicionarPrograma(Montador m) {
        programas.add(m);
    }

    public String ligar() {
        primeiraPassagem();
        return segundaPassagem();
    }

    // ============ PASSAGEM 1 ============
    private void primeiraPassagem() {

        int enderecoBase = 0;

        for (Montador m : programas) {

            for (Simbolo s : m.getDEFTAB()) {
                tabelaGlobal.put(
                    s.getNome(),
                    s.getValor() + enderecoBase
                );
            }

            m.setEnderecoBase(enderecoBase);
            enderecoBase += m.getTamanhoPrograma();
        }
    }

    // ============ PASSAGEM 2 ============
    private String segundaPassagem() {

        StringBuilder codigoFinal = new StringBuilder();

        for (Montador m : programas) {

            List<String> codigo = m.getCodigoObjeto();

            for (String linha : codigo) {
                codigoFinal.append(linha).append("\n");
            }
        }

        return codigoFinal.toString();
    }
}
