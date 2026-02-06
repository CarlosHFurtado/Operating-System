package interfacesicxe;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.util.List;
import java.util.Arrays;
import montador.MontadorSICXE; 

public class InterfaceMontador extends JFrame {

    private JTextArea editorAssembly;
    private JTextArea saidaObjeto;
    private JLabel statusLabel;
    
    
    private InterfaceSICXE interfacePrincipal; 

    
    public InterfaceMontador(InterfaceSICXE interfacePrincipal) { 
        this.interfacePrincipal = interfacePrincipal;
        FlatLightLaf.setup();
        configurarJanela();
        criarComponentes();
    }
    
    
    public InterfaceMontador() { 
        this(null); 
    }

    private void configurarJanela() {
        setTitle("Montador SIC/XE - Duas Passagens");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(950, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(63, 84, 114));
    }

    private void criarComponentes() {
        statusLabel = new JLabel("Pronto para montar.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(Color.BLACK);

        editorAssembly = new JTextArea();
        editorAssembly.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        editorAssembly.setBorder(BorderFactory.createTitledBorder("Código Assembly (SIC/XE)"));

        saidaObjeto = new JTextArea();
        saidaObjeto.setEditable(false);
        saidaObjeto.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        saidaObjeto.setBorder(BorderFactory.createTitledBorder("Código-Objeto Gerado (Formato SIC)"));

        JScrollPane scrollEditor = new JScrollPane(editorAssembly);
        JScrollPane scrollSaida = new JScrollPane(saidaObjeto);

        
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(240, 245, 255));

        JButton btnMontar = new JButton("Montar");
        JButton btnCarregar = new JButton("Carregar .asm");
        JButton btnSalvar = new JButton("Salvar .txt"); 
        
       
        JButton btnCarregarDireto = new JButton("Carregar no Executor"); 
        
        JButton btnLimpar = new JButton("Limpar Tudo");
        JButton btnFechar = new JButton("Fechar");

        toolBar.add(btnMontar);
        toolBar.add(btnCarregar);
        toolBar.add(btnSalvar);
        
 
        toolBar.add(btnCarregarDireto); 
        
        toolBar.addSeparator();
        toolBar.add(btnLimpar);
        toolBar.addSeparator();
        toolBar.add(btnFechar);

        
        btnMontar.addActionListener(e -> simularMontagem()); 
        btnCarregar.addActionListener(this::carregarArquivoAssembly);
        btnSalvar.addActionListener(this::salvarArquivoObjeto); 
        
        btnCarregarDireto.addActionListener(this::carregarDiretoNoExecutor); 
        
        btnLimpar.addActionListener(e -> limparCampos());
        btnFechar.addActionListener(e -> dispose());

       
        JSplitPane divisor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollEditor, scrollSaida);
        divisor.setDividerLocation(470);
        divisor.setResizeWeight(0.5);

        add(toolBar, BorderLayout.NORTH);
        add(divisor, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    
private String montarEAtualizarSaida() {
    String codigo = editorAssembly.getText(); 
    if (codigo.trim().isEmpty()) {
        exibirErro("Código vazio", "Insira código assembly antes de montar.");
        saidaObjeto.setText("");
        statusLabel.setText("Pronto para montar.");
        return null;
    }

    List<String> codigoSource = Arrays.asList(codigo.split("\\r?\\n"));
    MontadorSICXE montador = new MontadorSICXE();
    
    try {
        
        MontadorSICXE.ResultadoMontagem resultado = montador.montar(codigoSource);
        
        String saidaMontador = resultado.programaObjeto; 
        saidaObjeto.setText(saidaMontador);
        
        if (!resultado.erros.isEmpty()) {
            
            StringBuilder erroText = new StringBuilder();
            erroText.append("ERROS ENCONTRADOS:\n");
            for (String erro : resultado.erros) {
                erroText.append(erro).append("\n");
            }
            erroText.append("\n").append(saidaMontador);
            saidaObjeto.setText(erroText.toString());
            statusLabel.setText("ERRO: Montagem com erros. Verifique a saída.");
            return null;
        }

        statusLabel.setText("Montagem concluída! Código-objeto gerado com sucesso.");
        return saidaMontador;
        
    } catch (Exception ex) {
        String erroMsg = "ERRO CRÍTICO NA MONTAGEM: " + ex.getMessage() + "\n";
        saidaObjeto.setText(erroMsg);
        statusLabel.setText("ERRO: Montagem falhou. Verifique a saída.");
        exibirErro("Erro de Montagem", "Ocorreu um erro crítico durante o processo: " + ex.getMessage());
        return null;
    }
}

    private void simularMontagem() {
        montarEAtualizarSaida(); 
    }
    
  
    private void carregarDiretoNoExecutor(ActionEvent e) {
        if (interfacePrincipal == null) {
            exibirErro("Erro de Conexão", "A interface principal do simulador não foi conectada. Use o botão 'Abrir Montador' na interface principal.");
            return;
        }
        
        
        String objectCode = montarEAtualizarSaida(); 
        
        if (objectCode == null || objectCode.contains("ERRO")) { 
            return; 
        }
        
        try {
            
            interfacePrincipal.carregarProgramaMontado(objectCode); 
            
            JOptionPane.showMessageDialog(this, 
                "Código objeto carregado no simulador principal com sucesso!", 
                "Carregamento Concluído", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            exibirErro("Erro ao Carregar", "Não foi possível carregar o programa no Executor. Detalhes: " + ex.getMessage());
        }
    }

    private void carregarArquivoAssembly(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Carregar arquivo assembly (.asm)");
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivos Assembly", "asm", "txt"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String conteudo = Files.readString(chooser.getSelectedFile().toPath());
                editorAssembly.setText(conteudo);
                statusLabel.setText("Arquivo carregado: " + chooser.getSelectedFile().getName());
            } catch (IOException ex) {
                exibirErro("Erro ao carregar", "Não foi possível ler o arquivo.");
            }
        }
    }

    private void salvarArquivoObjeto(ActionEvent e) {
        String conteudo = saidaObjeto.getText().trim();
        if (conteudo.isEmpty() || conteudo.contains("ERRO")) {
            exibirErro("Nada para salvar", "Execute a montagem primeiro ou corrija os erros.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar código-objeto (.txt)"); 
        chooser.setSelectedFile(new java.io.File("programa.txt"));
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivos de Texto (.txt)", "txt")); // Filtro para TXT
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String caminho = chooser.getSelectedFile().getAbsolutePath();
                if (!caminho.toLowerCase().endsWith(".txt")) caminho += ".txt"; // Garante a extensão .txt
                Files.writeString(Paths.get(caminho), conteudo);
                statusLabel.setText("Arquivo salvo: " + caminho);
            } catch (IOException ex) {
                exibirErro("Erro ao salvar", "Não foi possível escrever o arquivo: " + ex.getMessage());
            }
        }
    }

    private void limparCampos() {
        editorAssembly.setText("");
        saidaObjeto.setText("");
        statusLabel.setText("Pronto para montar.");
    }

    private void exibirErro(String titulo, String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, titulo, JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { FlatLightLaf.setup(); } catch (Exception ignored) {}
            
            new InterfaceMontador().setVisible(true); 
        });
    }
}