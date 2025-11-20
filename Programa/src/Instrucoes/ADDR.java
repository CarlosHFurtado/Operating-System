package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class ADDR extends Instrucao {

    public ADDR() {
        super("ADDR", (byte) 0x90, "2", 2); // Opcode 90, Formato 2, Tamanho 2
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores, byte[] bytesInstrucao, int tamanho) {
        
        // Formato 2: [Opcode (8 bits)][r1 (4 bits)][r2 (4 bits)]
        // O segundo byte (índice 1) contém os IDs dos registradores
        byte regByte = bytesInstrucao[1];
        
        // r1 está nos 4 bits mais significativos
        int r1Id = (regByte >> 4) & 0x0F;
        // r2 está nos 4 bits menos significativos
        int r2Id = regByte & 0x0F;
        
        // Assumindo que você criou o método getNomePorId na classe Registradores (veja a seção 3)
        String r1Nome = registradores.getNomePorId(r1Id);
        String r2Nome = registradores.getNomePorId(r2Id);
        
        if (r1Nome == null || r2Nome == null) {
            PainelLog.logGlobal("ERRO ADDR: Registrador(es) inválido(s).");
            registradores.incrementar("PC", tamanho);
            return;
        }

        int valorR1 = registradores.getValor(r1Nome);
        int valorR2 = registradores.getValor(r2Nome);
        
        // r2 ← (r2) + (r1)
        int resultado = valorR2 + valorR1;
        
        registradores.setValor(r2Nome, resultado);
        
        // Incrementa PC para a próxima instrução
        registradores.incrementar("PC", 3);
    }
}