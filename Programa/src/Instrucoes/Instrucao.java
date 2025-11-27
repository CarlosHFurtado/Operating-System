package Instrucoes;

import java.util.HashMap;
import java.util.Map;

import Executor.Memoria;
import Executor.Registradores;
import interfacesicxe.PainelLog;

public abstract class Instrucao {
    
    private final String nome;
    private final byte opcode;
    private final String formato;
    private final int length;
    private Map<String, Boolean> flags = new HashMap<>();

    Instrucao(String nome, byte opcode, String formato, int length) {
        
        this.nome = nome;
        this.opcode = opcode;
        this.formato = formato;
        this.length = length;
        
    }

    public abstract void executar(Memoria memoria, Registradores registradores);

    public String getNome() { 
        return nome; 
    }
    
    public byte getOpcode() { 
        return opcode; 
    }
    
    public String getFormato() { 
        return formato; 
    }
    
    public int getLength() { 
        return length; 
    }
    
    public Map<String, Boolean> getFlags() { 
        return flags; 
    }

    public int getFormatoInstrucao(byte[] bytes) {
        PainelLog.logGlobal("DEBUG: bytes.length = " + (bytes == null ? "null" : bytes.length));
        if (bytes == null || bytes.length < 2) {
            return 3; // Padrão se não conseguir ler
        }

        // Obtém o byte de flags/niXbPe
        byte flagsByte = bytes[1];
        
        // Determina Formato 4 se a flag 'e' (bit 4) estiver setada
        if ((flagsByte & 0x10) != 0) {
            return 4;
        } else {
            return 3;
        }
    }
    
    protected void setFlags(byte[] bytesInstrucao) {
        if (bytesInstrucao == null || bytesInstrucao.length < 2) {
            flags.clear();
            return;
        }
        
        // Obtém os bytes de flags
        byte opcodeByte = bytesInstrucao[0];
        byte flagsByte = bytesInstrucao[1];

        // n: bit 1 do primeiro byte
        flags.put("n", (opcodeByte & 0x02) != 0);
        // i: bit 0 do primeiro byte
        flags.put("i", (opcodeByte & 0x01) != 0);
        
        // x: bit 5 do segundo byte
        flags.put("x", (flagsByte & 0x80) != 0);
        // b: bit 4 do segundo byte
        flags.put("b", (flagsByte & 0x40) != 0);
        // p: bit 3 do segundo byte
        flags.put("p", (flagsByte & 0x20) != 0);
        // e: bit 2 do segundo byte (Formato 4)
        flags.put("e", (flagsByte & 0x10) != 0);
    }
    
    private int getDisp(byte[] bytesInstrucao) {
        // Obter os 12 bits de deslocamento (disp)
        // O disp é composto pelos 4 bits mais baixos do segundo byte
        // e todos os 8 bits do terceiro byte (bytesInstrucao[2])
        
        int disp = bytesInstrucao[1] & 0x0F; // 4 bits do byte 1
        disp = (disp << 8) | (bytesInstrucao[2] & 0xFF); // 8 bits do byte 2
        
        return disp;
    }
    
    private int getAddr(byte[] bytesInstrucao) {
        // Obter os 20 bits de endereço (addr) para Formato 4
        
        int addr = bytesInstrucao[1] & 0x0F; // 4 bits do byte 1
        addr = (addr << 8) | (bytesInstrucao[2] & 0xFF); // 8 bits do byte 2
        addr = (addr << 8) | (bytesInstrucao[3] & 0xFF); // 8 bits do byte 3
        
        return addr;
    }
    
    protected int[] getRegistradores(byte[] bytesInstrucao) {
        // Para Formato 2: r1 e r2 estão no segundo byte (bytesInstrucao[1])
        // r1: 4 bits mais altos (bits 7-4)
        // r2: 4 bits mais baixos (bits 3-0)
        
        int r1 = (bytesInstrucao[1] & 0xF0) >> 4;
        int r2 = bytesInstrucao[1] & 0x0F;
        
        return new int[]{r1, r2};
    }
    
    /**
     * Calcula o endereço efetivo (AE) para instruções Formato 3/4.
     * @param bytesInstrucao Os bytes da instrução completa (3 ou 4 bytes).
     * @param registradores O objeto Registradores.
     * @param pcAposBusca O valor do registrador PC *após* a instrução ter sido buscada (PC = PC_antigo + tamanho_instrucao).
     * @return O Endereço Efetivo (AE).
     */
    public int calcularEnderecoEfetivo(byte[] bytesInstrucao, Registradores registradores, int pcAposBusca) {
        
        setFlags(bytesInstrucao);
        
        int endereco = 0;
        
        boolean formato4 = flags.get("e");
        
        if (formato4) {
            
            endereco = getAddr(bytesInstrucao);
            
        } else {           
            
            endereco = getDisp(bytesInstrucao);

            // Extensão de sinal para 12-bit disp
            if ((endereco & 0x800) != 0) {
                
                endereco |= 0xFFFFF000;
                
            }
        }
        
        // Lógica de endereçamento Base/PC-Relativo e Direto:
        // A arquitetura SIC/XE é estrita: b=1 e p=1 é inválido. O seu código original
        // usa 'if/else if' que implementa a prioridade: Se b=1, usa Base; se não, e p=1, usa PC.
        // Vou manter a estrutura de prioridade do seu código, mas usando o PC correto.
        
        if (flags.get("b")) {
            
            // Base Relativo: AE = (B) + disp
            endereco += registradores.getValor("B");
            
        } else if (flags.get("p")) {
            
            // PC Relativo: AE = (PC) + disp
            // **CORREÇÃO AQUI:** pcAposBusca é o PC já incrementado.
            endereco += pcAposBusca; 
            
        }
        // Se b=0 e p=0, é endereçamento direto (AE = disp/addr), 
        // ou seja, o 'endereco' não é modificado.
        
        
        // Endereçamento Indexado (Indexado x=1)
        if (flags.get("x")) {
            
            endereco += registradores.getValor("X");
        
        }
        
        return endereco;
    }
    
    public int obterOperando(Memoria memoria, Registradores registradores, int enderecoEfetivo) {
        
        // Modo Imediato: #m (n=0, i=1)
        if (flags.get("i") && !flags.get("n")) {
            
            // O operando é o próprio endereço efetivo
            return enderecoEfetivo;
            
        }
        
        // Modo Indireto: @m (n=1, i=0)
        if (flags.get("n") && !flags.get("i")) {
            
            // O endereço efetivo é o endereço do operando (ponteiro)
            // Lemos o valor de 3 bytes (endereço) na memória no AE, e usamos esse novo valor como endereço final
            int enderecoIndireto = memoria.getValor3Bytes(enderecoEfetivo);
            
            // Lemos o valor final (3 bytes) no endereço indireto
            return memoria.getValor3Bytes(enderecoIndireto);
            
        }
        
        // Modo Direto/Simples: m (n=0, i=0) ou (n=1, i=1) - SIC/XE padrão usa Direto
        // O valor é lido na memória no endereço efetivo
        return memoria.getValor3Bytes(enderecoEfetivo);
        
    }
    
    // Método auxiliar para ler 3 bytes (24 bits) da memória, útil para indireto e direto
    // NOTA: Para este método funcionar, você precisa adicionar getValor3Bytes(int pos) na sua classe Memoria.
    // **AÇÃO NECESSÁRIA EM Memoria.java**
    // public int getValor3Bytes(int pos) { ... }
}