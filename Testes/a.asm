; PROGRAMA: TESTE_FINALIZACAO
; DESCRIÇÃO: Código que garante a inicialização do Registrador Base (B)
;            e usa o RSUB para encerrar o programa de forma controlada.

PROGRAMA START 1000               ; Início em 1000h
         BASE    DADOS             ; Diretiva para o montador (Base Register = Endereço de DADOS)

; --- INICIALIZAÇÃO OBRIGATÓRIA DA BASE ---
         LDB     #DADOS            ; **CORREÇÃO:** Carrega o endereço literal de DADOS no Registrador B
                                   ; (Usa endereçamento Imediato)
         
; --- EXECUÇÃO ---
         
         LDA     #5                ; 1. A = 5
         STA     VAR1              ; 2. Armazena A em VAR1 (VAR1 = 5)
         
         LDB     #10               ; 3. B = 10
         ADDR    B,A               ; 4. B = B + A (B = 15)
         
         LDX     #1                ; 5. X = 1 (Índice)
         
         ; Endereçamento Indexado (Teste de leitura de dados):
         LDA     DADOS,X           ; 6. A = Memoria[DADOS + 1]. Carrega '0B' em A.
         
         LDS     #100              ; 7. S = 100 (Novo valor para comparação)
         
         COMPR   B,S               ; 8. Compara B (15) com S (100). CC é setado para MENOR (<)
         
         JLT     FIM               ; 9. Salta para FIM se B < S (PC-relativo) -> O salto ocorre!
         
         JEQ     LOOP_SE_ERRO      ; 10. Esta instrução será ignorada devido ao salto JLT

LOOP_SE_ERRO COMPR A,X             ; Se o loop ainda ocorrer, o problema está no RSUB ou Tabela de Opcodes.
             RSUB                  ; Sai se o JLT falhar

FIM      RSUB                      ; 11. Retorna ao SO (Encerra a execução do programa)

; --- SEÇÃO DE DADOS ---
DADOS    BYTE    X'0A'             ; Endereço base (1 byte)
         BYTE    X'0B'             ; Byte para Indexação
VALOR    WORD    256               ; 000100h (3 bytes)
VAR1     RESW    1                 ; Reserva 1 Palavra (3 bytes)
VAR2     RESB    10                ; Reserva 10 Bytes
         
         END     PROGRAMA