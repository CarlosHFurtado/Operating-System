/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package montador;

/**
 *
 * @author carlo
 */

public class EntradaRelocacao {

    private int endereco;
    private int tamanho; // em bytes (3 ou 4)

    public EntradaRelocacao(int endereco, int tamanho) {
        this.endereco = endereco;
        this.tamanho = tamanho;
    }

    public int getEndereco() {
        return endereco;
    }

    public int getTamanho() {
        return tamanho;
    }
}
