package montador;


import java.util.List;
import java.util.List;
import java.util.ArrayList;
import ProcessadorDeMacros.ProcessadorDeMacros;

public class Montador {
    
    private TabelaSimbolos tabelaSimbolos;
    private int locationCounter; 
    private Integer baseRegisterValue = null; 
    private StringBuilder objectCodeBuilder; 
    private List<String> instructionObjectCodes; 
    
    private int enderecoInicial;
    private int tamanhoPrograma;
    private String nomePrograma;
    
    public Montador() {
        
        this.tabelaSimbolos = new TabelaSimbolos();
        this.locationCounter = 0; 
        this.objectCodeBuilder = new StringBuilder(); 
        this.instructionObjectCodes = new ArrayList<>(); 
        
    }

    public String montar(List<String> codigoSource) {
        
        ProcessadorDeMacros macroProcessor = new ProcessadorDeMacros();
        List<String> codigoExpandido = macroProcessor.processar(codigoSource);

        objectCodeBuilder.setLength(0);
        instructionObjectCodes.clear();

        passo1(codigoExpandido);
        passo2(codigoExpandido);

        return String.join("\n", instructionObjectCodes);
        
    }
    
    public void passo1(List<String> codigoFonte) {
        
        for (String linhaBruta : codigoFonte) {
            
            LinhaInstrucao linha = Parser.parseLine(linhaBruta);

            if (linha.isEmpty()) continue; 

            String label = linha.getLabel();
            String opcode = linha.getOpcode().toUpperCase(); 
            String operandos = linha.getOperandos();

            if (opcode.equals("START")) {
                
                nomePrograma = (label != null ? label : "PROG"); 
                
                try {
                    
                    int startAddress = Integer.parseInt(operandos, 16); 
                    locationCounter = startAddress;
                    enderecoInicial = startAddress;
                    
                } catch (Exception e) {
                    
                    locationCounter = 0;
                    enderecoInicial = 0;
                    
                }
                
                continue; 
                
            }

            if (label != null) {
                
                tabelaSimbolos.inserir(label, locationCounter, "LABEL");
                
            }
            
            int tamanhoInstrucao = 0;
            
            if (TabelaInstrucoesMaquina.isMachineOp(opcode)) {
                
                if (linha.isExtended()) { 
                    
                    tamanhoInstrucao = 4;
                    
                } else {
                    
                    InstrucaoMaquina inst = TabelaInstrucoesMaquina.getMachineOp(opcode);
                    tamanhoInstrucao = inst.getTamanhoBytes();
                    
                }
                
            } else if (TabelaPseudoInstrucoes.isPseudoOp(opcode)) {
                
                tamanhoInstrucao = calcularTamanhoPseudoOp(opcode, linha.getOperandos());
                
                if (opcode.equals("END")) {
                    
                    tamanhoPrograma = locationCounter - enderecoInicial;
                    break; 
                    
                }
                
            } 
            
            locationCounter += tamanhoInstrucao; 
            
        }
        
        if (tamanhoPrograma == 0) {
            
              tamanhoPrograma = locationCounter - enderecoInicial;
              
        }
    }

    private int calcularTamanhoPseudoOp(String opcode, String operandos) {
        
        String args = operandos == null ? "" : operandos.trim();
        
        switch (opcode) {
            
            case "WORD": return 3;
            
            case "BYTE":
                
                if (args.startsWith("C'") && args.endsWith("'")) {
                    
                    return args.substring(2, args.length() - 1).length();
                    
                } else if (args.startsWith("X'") && args.endsWith("'")) {
                    
                    String hexContent = args.substring(2, args.length() - 1);
                    return (hexContent.length() + 1) / 2;
                    
                }
                
                return 0;
                
            case "RESW":
                
                try {
                    
                    return Integer.parseInt(args) * 3;
                    
                } catch (NumberFormatException e) { return 0; }
                
            case "RESB":
                
                try {
                    
                    return Integer.parseInt(args);
                    
                } catch (NumberFormatException e) { return 0; }
                
            case "BASE": 
                
                return 0;
                
            default:
                
                return 0;
                
        }
    }

    public void passo2(List<String> codigoFonte) {
        
        String registroT = ""; 
        
        int lcInicioT = this.enderecoInicial; 
        
        this.locationCounter = this.enderecoInicial;
    
        for (String linhaBruta : codigoFonte) {
            
            LinhaInstrucao linha = Parser.parseLine(linhaBruta);
            
            if (linha.isEmpty()) continue;
            
            String opcode = linha.getOpcode().toUpperCase();

            String operandos = linha.getOperandos();
            
            if (opcode.equals("START")) continue; 
            
            int tamanho = 0; 
            
            String codigoObjeto = ""; 
            
            if (TabelaInstrucoesMaquina.isMachineOp(opcode)) {
                
                InstrucaoMaquina inst = TabelaInstrucoesMaquina.getMachineOp(opcode);
                
                if (linha.isExtended()) { 
                    
                    tamanho = 4;
                    codigoObjeto = montarFormato4(inst, operandos); 
                    
                } else {
                    
                    tamanho = inst.getTamanhoBytes();
                    codigoObjeto = montarInstrucao(inst, operandos, this.locationCounter);
                    
                }

            } else if (TabelaPseudoInstrucoes.isPseudoOp(opcode)) {
                
                tamanho = calcularTamanhoPseudoOp(opcode, operandos); 
                
                switch (opcode) {
                    
                    case "WORD":
                        
                        codigoObjeto = montarWord(operandos);
                        break;
                        
                    case "BYTE":
                        
                        codigoObjeto = montarByte(operandos);
                        break;
                        
                    case "BASE":
                        
                        if (operandos != null) {
                            
                            baseRegisterValue = tabelaSimbolos.getEndereco(operandos.trim());
                            
                        }
                        
                    case "RESW":
                    case "RESB":
                        
                        if (!registroT.isEmpty()) {
                            
                            objectCodeBuilder.append(gerarRegistroT(lcInicioT, registroT)).append("\n");
                            registroT = "";
                            lcInicioT = this.locationCounter + tamanho; 
                            
                        }
                        
                        break;
                        
                    case "END":
                        
                        if (!registroT.isEmpty()) {
                            
                            objectCodeBuilder.append(gerarRegistroT(lcInicioT, registroT)).append("\n");
                        
                        }
                        
                        objectCodeBuilder.append(gerarRegistroE(enderecoInicial));
                        
                        return;
                        
                }
            }
            
            if (!codigoObjeto.isEmpty()) {
                
                instructionObjectCodes.add(codigoObjeto); 

                if (registroT.length() + codigoObjeto.length() > 60) {
                    
                    objectCodeBuilder.append(gerarRegistroT(lcInicioT, registroT)).append("\n");
                    registroT = codigoObjeto;
                    lcInicioT = this.locationCounter;
                    
                } else {
                    
                    registroT += codigoObjeto;
                    
                }
            }

            this.locationCounter += tamanho;
            
        }
    }

    private String montarInstrucao(InstrucaoMaquina inst, String operandos, int lc) {
        
        if (inst.getTamanhoBytes() == 2) {
            
            return montarFormato2(inst, operandos);
            
        }
        
        if (inst.getTamanhoBytes() == 3) {
            
            return montarFormato3(inst, operandos, lc);
            
        }
        
        return inst.getOpcodeHex(); 
        
    }

    private String montarFormato2(InstrucaoMaquina inst, String operandos) {
        
        String[] regs = operandos.split(",");
        String r1 = traduzirRegistrador(regs[0].trim());
        String r2 = (regs.length > 1) ? traduzirRegistrador(regs[1].trim()) : "0"; 

        return inst.getOpcodeHex() + r1 + r2;
        
    }

    private String traduzirRegistrador(String reg) {
        
           switch (reg.toUpperCase()) {
             case "A": return "0";
             case "X": return "1";
             case "L": return "2";
             case "B": return "3";
             case "S": return "4";
             case "T": return "5";
             case "F": return "6";
             case "PC": return "8";
             case "SW": return "9";
             default: return "0";
             
         }
    }


    private String montarFormato3(InstrucaoMaquina inst, String operandos, int lc) {
        
        int n = 1, i = 1, x = 0, b = 0, p = 0;
        String simboloOuValor;
        
        if (operandos == null || operandos.trim().isEmpty()) {
            
            simboloOuValor = "0"; 
            n = 1; i = 1;
            
        } else if (operandos.startsWith("#")) {
            
            n = 0; i = 1; 
            simboloOuValor = operandos.substring(1).trim();
            
        } else if (operandos.startsWith("@")) {
            
            n = 1; i = 0; 
            simboloOuValor = operandos.substring(1).trim();
            
        } else {
            
            if (operandos.toUpperCase().contains(",X")) {
                
                x = 1; 
                
                simboloOuValor = operandos.split(",")[0].trim();
                
            } else {
                
                simboloOuValor = operandos.trim();
                
            }
        }

        int opcodeBase = Integer.parseInt(inst.getOpcodeHex(), 16);
        int disp;
              
        if (n == 0 && i == 1 && simboloOuValor.matches("^-?[0-9A-Fa-f]+$")) {
            
            try {
                
                disp = Integer.parseInt(simboloOuValor, 16); 
                
            } catch (NumberFormatException e) {
                
                disp = 0;
                
            }
        } else {
            
            Integer endSimbolo = tabelaSimbolos.getEndereco(simboloOuValor);
            int TA; 
            
            if (endSimbolo == null) {
                
                disp = 0; 
                
            } else {
                
                TA = endSimbolo;
                
                int PC = lc + 3; 
                
                disp = TA - PC;
                
                if (disp >= -2048 && disp <= 2047) {
                    
                    b = 0; p = 1; 
                    
                } else if (baseRegisterValue != null) {
                    
                    disp = TA - baseRegisterValue;
                    
                    if (disp >= 0 && disp <= 4095) {
                        
                        b = 1; p = 0; 
                        
                    } else {
                        
                        disp = 0; b = 0; p = 0; 
                        
                    }
                } else {
                    
                    disp = 0; b = 0; p = 0; 
                    
                }
            }
        }
        
        disp = disp & 0xFFF; 

        int byte1 = (opcodeBase & 0xFC) | (n << 1) | i;
        int xbp = (x << 3) | (b << 2) | (p << 1) | 0; 
        int dispHigh = (disp >> 8) & 0x0F;
        int byte2 = (xbp << 4) | dispHigh;
        int byte3 = disp & 0xFF;
        
        return String.format("%02X%02X%02X", byte1, byte2, byte3);
        
    }
    
    private String montarFormato4(InstrucaoMaquina inst, String operandos) {
        
        int n, i, x;  
        String simboloOuValor;

        n = 1; i = 1; x = 0; 
        int e = 1; 

        if (operandos == null || operandos.trim().isEmpty()) { 
            
            simboloOuValor = "0"; n = 1; i = 1; 
        
        } else if (operandos.startsWith("#")) { 
            
            n = 0; i = 1; simboloOuValor = operandos.substring(1).trim(); 
        
        } else if (operandos.startsWith("@")) { 
            
            n = 1; i = 0; simboloOuValor = operandos.substring(1).trim(); 
        
        } else {
            
            if (operandos.toUpperCase().contains(",X")) {
                
                x = 1; simboloOuValor = operandos.split(",")[0].trim();
                
            } else { 
                
                simboloOuValor = operandos.trim(); 
                
            }
        }

        int opcodeBase = Integer.parseInt(inst.getOpcodeHex(), 16);
        long enderecoAbsoluto;
        
        if (simboloOuValor.matches("^-?[0-9A-Fa-f]+$")) {
            
            try {
                
                enderecoAbsoluto = Long.parseLong(simboloOuValor, 16) & 0xFFFFF; 
                
            } catch (NumberFormatException ex) { 
                
                enderecoAbsoluto = 0; 
            
            }
            
        } else {
            
            Integer endSimbolo = tabelaSimbolos.getEndereco(simboloOuValor);
            
            enderecoAbsoluto = (endSimbolo != null) ? (endSimbolo & 0xFFFFF) : 0;
            
        }

        
        int byte1 = (opcodeBase & 0xFC) | (n << 1) | i;
      
        int xbp_e = (x << 7) | (0 << 6) | (0 << 5) | (e << 4);
        int endHigh = (int) ((enderecoAbsoluto >> 16) & 0x0F);
        
        int byte2 = xbp_e | endHigh; 
        
        int byte3 = (int) ((enderecoAbsoluto >> 8) & 0xFF);
       
        int byte4 = (int) (enderecoAbsoluto & 0xFF);

        return String.format("%02X%02X%02X%02X", byte1, byte2, byte3, byte4);
        
    }

    private String montarWord(String operandos) {
        
        try {
            
            String args = operandos.trim();
            int valor;
            
            if (args.startsWith("X'") && args.endsWith("'")) {
                
                valor = Integer.parseInt(args.substring(2, args.length() - 1), 16);
                
            } else {
                
                valor = Integer.parseInt(args, 10);
                
            }
            
            return String.format("%06X", valor & 0xFFFFFF); 
            
        } catch (NumberFormatException e) {
            
            return "000000";
            
        }
    }

    private String montarByte(String operandos) {
        
        String args = operandos.trim();
        
        if (args.startsWith("C'") && args.endsWith("'")) {
            
            String content = args.substring(2, args.length() - 1);
            StringBuilder hex = new StringBuilder();
            
            for (char c : content.toCharArray()) {
                
                hex.append(String.format("%02X", (int) c));
                
            }
            
            return hex.toString();
            
        } else if (args.startsWith("X'") && args.endsWith("'")) {
            
            String hexContent = args.substring(2, args.length() - 1);
            
            return hexContent.toUpperCase();
            
        }
        
        return "";
        
    }

    private String gerarRegistroT(int enderecoInicial, String codigoObjeto) {
        
        int tamanhoBytes = codigoObjeto.length() / 2;
        
        return String.format("T%06X%02X%s", enderecoInicial, tamanhoBytes, codigoObjeto);
        
    }

    private String gerarRegistroE(int enderecoExecucao) {
        
        return String.format("E%06X", enderecoExecucao);
        
    }
}
