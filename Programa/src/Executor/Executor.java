/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Executor;

import Instrucoes.TabelaOpcodes;
/**
 *
 * @author carlos
 */

public class Executor {
    private Memoria memoria;
    private Registradores registradores;
    private TabelaOpcodes instrucoes;
    private int output;
    private boolean stop;

    public Executor() {
        this.memoria = new Memoria(1024); // 1KB de memória
        this.registradores = new Registradores();
        this.instrucoes = new TabelaOpcodes();
        this.output = -1;
    }
    
    public void limpar() {
        memoria.limpaMem();
        registradores.limpar();
        output = -1;
    }

    public void setPrograma(String programaObjeto) {
        memoria.limpaMem();
        registradores.limpar();

        int posMem = 0;

        StringBuilder hexString = new StringBuilder();

        String[] lines = programaObjeto.split("\\r?\\n");

        for (String l : lines) {
            hexString.append(l.trim());
        }
    
        for (int i = 0; i < hexString.length(); i += 2) {
            String pedaco = hexString.substring(i, Math.min(i + 2, hexString.length()));

            byte pedacoByte = (byte) Integer.parseInt(pedaco, 16);

            memoria.setByte(posMem++, pedacoByte);
        }
    }

    
    public void executarPrograma()
    {
        int pc = registradores.get("PC").getValorIntSigned();
        stop = false;

        while (memoria.getWord(pc) != 0) // para de executar se a proxima palavra for vazia
        {

            byte opcode = memoria.getOpcode(pc);
            if (opcode == (byte)0xD8){ // Read
                stop = true;
                registradores.incrementar("PC",1);
                return;
            }
            
            if (opcode == (byte)0xDC) { // Write
                setOutput(registradores.get("A").getValorIntSigned());
                registradores.incrementar("PC",1);
            } else {
                instrucoes.getInstrucao(opcode).executar(memoria, registradores);
            }
            
            pc = this.registradores.get("PC").getValorIntSigned();
        }   
    }

    public boolean executarPasso()
    {
        int pc = this.registradores.get("PC").getValorIntSigned();

        if (memoria.getWord(pc) == 0) // para de executar se a proxima palavra for vazia
        {
            return false;
        }
        
        byte opcode = memoria.getOpcode(pc);
        stop = false;
        
        if (opcode == (byte)0xD8)
        {
            stop = true;
            registradores.incrementar("PC",1);
            return true;
        }

        if (opcode == (byte)0xDC) {
            setOutput(registradores.get("A").getValorIntSigned());
            registradores.incrementar("PC",1);
        } else {
            instrucoes.getInstrucao(opcode).executar(memoria, registradores);
        }

        pc = this.registradores.get("PC").getValorIntSigned();

        return true;
    }
    
    public Memoria getMemoria() {
        return memoria;
    }
    
    public Registradores getRegistradores() {
        return registradores;
    }
    
    public TabelaOpcodes getInstrucoes() {
        return instrucoes;
    }

    public void setOutput(int output) {
        this.output = output;
    }

    public int getOutput() {
        return output;
    }
    
    public boolean getStop(){
        return stop;
    }
}
