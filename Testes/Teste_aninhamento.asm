ANINHA  START   2000

; Macro simples
MACRO INC &X
LDA &X
ADD #1
STA &X
MEND

; Macro intermediária (outra sintaxe)
DUPLA MACRO &Y
INC &Y
INC &Y
MEND

; Macro de alto nível
MACRO QUAD &Z
DUPLA &Z
DUPLA &Z
MEND

; Código principal
MAIN    QUAD VAR
        INC VAR

VAR     WORD 0

END MAIN
