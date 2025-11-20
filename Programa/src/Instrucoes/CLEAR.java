package Instrucoes;

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public class CLEAR extends Instrucao {

    public CLEAR() {
        super("CLEAR", (byte) 0x04, "2", 2); // Opcode 04, Formato 2, Tamanho 2
    }

    @Override
    public void executar(Memoria memoria, Registradores registradores, byte[] bytesInstrucao, int tamanho) {
        
        // Formato 2: [Opcode (8 bits)][r1 (4 bits)][r2 (4 bits)]
        // CLEAR só usa r1.
        byte regByte = bytesInstrucao[1];
        
        // r1 está nos 4 bits mais significativos
        int r1Id = (regByte >> 4) & 0x0F;
        
        String r1Nome = registradores.getNomePorId(r1Id); // Assumindo método
        
        if (r1Nome == null) {
            PainelLog.logGlobal("ERRO CLEAR: Registrador inválido.");
            // Usando 'tamanho' para avançar o PC, mesmo em caso de erro.
            registradores.incrementar("PC", tamanho); 
            return;
        }

        // r1 ← 0
        registradores.setValor(r1Nome, 0);

// CORREÇÃO: Forçar o PC a avançar 3 bytes para pular o padding
registradores.incrementar("PC", 3); // Onde estava 'tamanho' ou '2'
    }
}