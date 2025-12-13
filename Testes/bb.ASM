; Arquivo: teste_macro.txt
; Exemplo de código-fonte para testar um Processador de Macros (SIC/XE-like)

BUFFER	EQU	$00
TAM	EQU	100
REG_A	EQU	0
REG_X	EQU	1

; -----------------------------------------------------
; 1. DEFINIÇÃO DAS MACROS
; -----------------------------------------------------

MACRO LEITURA &VAR, &TAM_MAX
; Macro simples: Lê até &TAM_MAX bytes e salva em &VAR
LDA #&TAM_MAX	; Carrega o tamanho máximo para A
TIXR REG_A	; Inicializa o contador de loop (X = &TAM_MAX)
LOOP_LEITURA
TD &VAR		; Testa dispositivo de entrada
JEQ LOOP_LEITURA ; Espera até que o dispositivo esteja pronto
RD &VAR		; Lê o dado do dispositivo de entrada para &VAR
TIXR REG_A	; Decrementa o contador (X = X - 1)
JGT LOOP_LEITURA ; Se X > 0, continua lendo
MEND

MACRO ESCREVA &VAR, &TAM_SAIDA
; Macro complexa: Escreve &VAR, salva e restaura o registrador A
STL SAVEA	; Salva o valor atual de A
LDA #&TAM_SAIDA	; Carrega o tamanho para A (tamanho da saída)
LOOP_ESCREVA
TD &VAR		; Testa dispositivo de saída
JEQ LOOP_ESCREVA ; Espera
WD &VAR		; Escreve o byte em &VAR
TIXR REG_A	; Decrementa o contador
JGT LOOP_ESCREVA ; Se X > 0, continua
LDA SAVEA	; Restaura o valor original de A
MEND

; -----------------------------------------------------
; 2. PROGRAMA PRINCIPAL
; -----------------------------------------------------

.	START	$1000	; Inicia o programa no endereço $1000

MAIN	BASE	$4000	; Define o registrador BASE
	JMP	PROX_BLOCO

PROX_BLOCO
	; Invocação da Macro 1: LEITURA
	LEITURA	DADOS_ENTRADA, 5

	; Invocação da Macro 2: ESCREVA
	ESCREVA	MENSAGEM_SAIDA, 15

	JMP	FIM_PROG

; -----------------------------------------------------
; 3. DADOS E VARIÁVEIS
; -----------------------------------------------------

DADOS_ENTRADA	RESB	100	; Reserva 100 bytes para entrada
MENSAGEM_SAIDA	BYTE	C'TESTE DE MACRO OK' ; Mensagem a ser escrita
SAVEA		RESW	1	; Variável para salvar o registrador A (usada na macro ESCREVA)

FIM_PROG	END	MAIN	; Fim do programa, com início de execução em MAIN