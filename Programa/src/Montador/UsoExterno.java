/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package montador;

/**
 *
 * @author carlo
 */

public class UsoExterno {

    private String simbolo;
    private int endereco;

    public UsoExterno(String simbolo, int endereco) {
        this.simbolo = simbolo;
        this.endereco = endereco;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public int getEndereco() {
        return endereco;
    }
}