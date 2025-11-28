package interfacesicxe;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.filechooser.FileNameExtensionFilter;

public class InterfaceMontador extends JFrame {

    private JTextArea editorAssembly;
    private JTextArea saidaObjeto;
    private JLabel statusLabel;

    public InterfaceMontador() {
        FlatLightLaf.setup();
        configurarJanela();
        criarComponentes();
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

        // Barra de ferramentas
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(240, 245, 255));

        JButton btnMontar = new JButton("Montar");
        JButton btnCarregar = new JButton("Carregar .asm");
        JButton btnSalvar = new JButton("Salvar .obj");
        JButton btnLimpar = new JButton("Limpar Tudo");
        JButton btnFechar = new JButton("Fechar");

        toolBar.add(btnMontar);
        toolBar.add(btnCarregar);
        toolBar.add(btnSalvar);
        toolBar.addSeparator();
        toolBar.add(btnLimpar);
        toolBar.addSeparator();
        toolBar.add(btnFechar);

        // Ações simuladas (substitua depois pela lógica real)
        btnMontar.addActionListener(e -> simularMontagem());
        btnCarregar.addActionListener(this::carregarArquivoAssembly);
        btnSalvar.addActionListener(this::salvarArquivoObjeto);
        btnLimpar.addActionListener(e -> limparCampos());
        btnFechar.addActionListener(e -> dispose());

        // Layout central
        JSplitPane divisor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollEditor, scrollSaida);
        divisor.setDividerLocation(470);
        divisor.setResizeWeight(0.5);

        add(toolBar, BorderLayout.NORTH);
        add(divisor, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

private void simularMontagem() {
    String codigo = editorAssembly.getText().trim();
    if (codigo.isEmpty()) {
        exibirErro("Código vazio", "Insira código assembly antes de montar.");
        return;
    }

    // Gera uma saída de exemplo sem mostrar nenhuma mensagem de aviso
    saidaObjeto.setText(
        "H^EXEMPLO^001000^00001E\n" +
        "T^001000^0C^0C20154820123C2009\n" +
        "T^00100C^06^4C0000000000\n" +
        "E^001003"
    );
    statusLabel.setText("Montagem concluída (simulada).");
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
        if (conteudo.isEmpty()) {
            exibirErro("Nada para salvar", "Execute a montagem primeiro.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar código-objeto (.obj)");
        chooser.setSelectedFile(new java.io.File("programa.obj"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String caminho = chooser.getSelectedFile().getAbsolutePath();
                if (!caminho.endsWith(".obj")) caminho += ".obj";
                Files.writeString(Paths.get(caminho), conteudo);
                statusLabel.setText("Arquivo salvo: " + caminho);
            } catch (IOException ex) {
                exibirErro("Erro ao salvar", "Não foi possível escrever o arquivo.");
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

    // Para testar isoladamente
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { FlatLightLaf.setup(); } catch (Exception ignored) {}
            new InterfaceMontador().setVisible(true);
        });
    }
}