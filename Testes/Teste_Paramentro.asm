PARAM   START   3000

; Macro base
MACRO MOVE &SRC,&DST
LDA &SRC
STA &DST
MEND

; Macro que usa outra macro
COPIA MACRO &A,&B,&C
MOVE &A,&B
MOVE &B,&C
MEND

; Código principal
MAIN    COPIA X Y Z

X       WORD 1
Y       WORD 2
Z       WORD 3

END MAIN
