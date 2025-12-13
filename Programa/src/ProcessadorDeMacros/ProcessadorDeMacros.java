/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ProcessadorDeMacros;

import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 *
 * @author carlo
 */

public class ProcessadorDeMacros {

    public void processar(String caminhoEntrada) throws IOException {

        List<String> codigoFonte = Files.readAllLines(Paths.get(caminhoEntrada));

        ProcessadorMacros pm = new ProcessadorMacros();
        List<String> expandido = pm.processar(codigoFonte);

        File entrada = new File(caminhoEntrada);
        File saida = new File(entrada.getParent(), "MASMAPRG.ASM");

        Files.write(saida.toPath(), expandido);
    }
}