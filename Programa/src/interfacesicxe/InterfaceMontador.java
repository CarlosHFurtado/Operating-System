package interfacesicxe;

import com.formdev.flatlaf.FlatLightLaf;
import montador.MontadorSICXE;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class InterfaceMontador extends JFrame {

    private static final Color AZUL_FUNDO = new Color(63, 84, 114);
    private static final Color AZUL_FUNDO_CAIXA = new Color(78, 101, 128);
    private static final Color BRANCO = Color.WHITE;
    private static final Color AZUL_TEXTO = new Color(32, 67, 103);

    private JTextArea editorAssembly;
    private JTextArea saidaObjeto;
    private JLabel statusLabel;

    private final InterfaceSICXE interfacePrincipal;

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
        setLayout(new BorderLayout(0, 0));
        setSize(980, 560);
        setLocationRelativeTo(null);
    }

    private void criarComponentes() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(AZUL_FUNDO);
        setContentPane(root);

        JPanel barraBotoes = new JPanel(new BorderLayout());
        barraBotoes.setBackground(BRANCO);
        barraBotoes.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        JPanel botoesGrid = new JPanel(new GridLayout(1, 6, 18, 0));
        botoesGrid.setBackground(BRANCO);

        JButton btnCarregar = criarBotaoLink("Carregar .asm");
        JButton btnMontar = criarBotaoLink("Montar");
        JButton btnSalvar = criarBotaoLink("Salvar .txt");
        JButton btnCarregarNoExecutor = criarBotaoLink("Carregar no Executor");
        JButton btnLimpar = criarBotaoLink("Limpar");
        JButton btnFechar = criarBotaoLink("Fechar");

        botoesGrid.add(btnCarregar);
        botoesGrid.add(btnMontar);
        botoesGrid.add(btnSalvar);
        botoesGrid.add(btnCarregarNoExecutor);
        botoesGrid.add(btnLimpar);
        botoesGrid.add(btnFechar);

        barraBotoes.add(botoesGrid, BorderLayout.CENTER);

        JPanel topo = new JPanel(new BorderLayout(0, 0));
        topo.setBackground(BRANCO);
        topo.add(barraBotoes, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(12, 12));
        centro.setBackground(AZUL_FUNDO);
        centro.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        editorAssembly = new JTextArea();
        editorAssembly.setFont(new Font("Consolas", Font.PLAIN, 13));
        editorAssembly.setBackground(AZUL_FUNDO_CAIXA);
        editorAssembly.setForeground(BRANCO);
        editorAssembly.setCaretColor(BRANCO);
        editorAssembly.setTabSize(4);

        JScrollPane scrollEditor = new JScrollPane(editorAssembly);
        estilizarScrollAzul(scrollEditor);
        scrollEditor.setBorder(criarBordaTituloBranca("Código Assembly (SIC/XE)"));

        saidaObjeto = new JTextArea();
        saidaObjeto.setEditable(false);
        saidaObjeto.setFont(new Font("Consolas", Font.PLAIN, 13));
        saidaObjeto.setBackground(AZUL_FUNDO_CAIXA);
        saidaObjeto.setForeground(BRANCO);
        saidaObjeto.setCaretColor(BRANCO);

        JScrollPane scrollSaida = new JScrollPane(saidaObjeto);
        estilizarScrollAzul(scrollSaida);
        scrollSaida.setBorder(criarBordaTituloBranca("Saída (código-objeto)"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollEditor, scrollSaida);
        split.setDividerLocation(470);
        split.setResizeWeight(0.5);
        split.setBorder(BorderFactory.createLineBorder(BRANCO));
        split.setBackground(AZUL_FUNDO);
        split.setDividerSize(6);

        centro.add(split, BorderLayout.CENTER);

        statusLabel = new JLabel("Pronto para montar.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        statusLabel.setForeground(BRANCO);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 12f));

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(AZUL_FUNDO);
        rodape.add(statusLabel, BorderLayout.WEST);

        root.add(topo, BorderLayout.NORTH);
        root.add(centro, BorderLayout.CENTER);
        root.add(rodape, BorderLayout.SOUTH);

        btnCarregar.addActionListener(this::carregarArquivoAssembly);
        btnMontar.addActionListener(e -> simularMontagem());
        btnSalvar.addActionListener(this::salvarArquivoObjeto);
        btnCarregarNoExecutor.addActionListener(this::carregarDiretoNoExecutor);
        btnLimpar.addActionListener(e -> limparCampos());
        btnFechar.addActionListener(e -> dispose());
    }

    private JButton criarBotaoLink(String texto) {
        JButton b = new JButton(texto);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);

        b.setForeground(AZUL_TEXTO);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setHorizontalAlignment(SwingConstants.CENTER);

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setForeground(new Color(20, 90, 160)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { b.setForeground(AZUL_TEXTO); }
        });

        return b;
    }

    private Border criarBordaTituloBranca(String titulo) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BRANCO),
                titulo,
                0, 0,
                new Font("Segoe UI", Font.BOLD, 12),
                BRANCO
        );
    }

    private void estilizarScrollAzul(JScrollPane sp) {
        sp.getViewport().setBackground(AZUL_FUNDO_CAIXA);
        sp.setBackground(AZUL_FUNDO_CAIXA);
        sp.setBorder(BorderFactory.createLineBorder(BRANCO));
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
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
            exibirErro("Erro de Conexão",
                    "A interface principal do simulador não foi conectada. Use o botão 'Abrir Montador' na interface principal.");
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
        String objectCode = saidaObjeto.getText();
        if (objectCode.trim().isEmpty()) {
            exibirErro("Nada para salvar", "Nenhum código objeto foi gerado ainda.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar código objeto como...");
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivos Texto", "txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.writeString(chooser.getSelectedFile().toPath(), objectCode);
                statusLabel.setText("Arquivo salvo: " + chooser.getSelectedFile().getName());
            } catch (IOException ex) {
                exibirErro("Erro ao salvar", "Não foi possível salvar o arquivo.");
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
        SwingUtilities.invokeLater(() -> new InterfaceMontador().setVisible(true));
    }
}
