TESTE   START   1000

; =========================
; Macro simples (Sintaxe A)
; =========================
MACRO INCR &VAR
LDA &VAR
ADD #1
STA &VAR
MEND

; =========================
; Macro com dois parâmetros (Sintaxe B)
; =========================
SOMA MACRO &A,&B
LDA &A
ADD &B
MEND

; =========================
; Macro que chama outra macro (aninhada)
; =========================
MACRO INCR2 &X
INCR &X
INCR &X
MEND

; =========================
; Código principal
; =========================
MAIN    LDA NUM1
INCR NUM1
SOMA NUM1 NUM2
INCR2 NUM2
STA RESULT

NUM1    WORD 5
NUM2    WORD 10
RESULT  RESW 1

END MAIN
