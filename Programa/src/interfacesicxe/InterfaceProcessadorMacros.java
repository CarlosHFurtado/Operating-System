package interfacesicxe;

import com.formdev.flatlaf.FlatLightLaf;
import ProcessadorDeMacros.ProcessadorDeMacros;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

public class InterfaceProcessadorMacros extends JFrame {

    private JTextField campoArquivoEntrada;
    private JLabel statusLabel;
    private JButton btnProcessar;

    private ProcessadorDeMacros processador;

    public InterfaceProcessadorMacros() {
        this.processador = new ProcessadorDeMacros();
        FlatLightLaf.setup();
        configurarJanela();
        criarComponentes();
    }

    private void configurarJanela() {
        setTitle("Processador de Macros – Macro-Montador SIC/XE");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(720, 260);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(63, 84, 114));
    }

    private void criarComponentes() {
        statusLabel = new JLabel("Selecione um arquivo assembly contendo macros para processar.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel painelArquivo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painelArquivo.setOpaque(false);

        JLabel label = new JLabel("Arquivo de entrada:");
        campoArquivoEntrada = new JTextField(32);
        campoArquivoEntrada.setEditable(false);

        JButton btnSelecionar = new JButton("Selecionar...");
        btnProcessar = new JButton("Processar Macros");
        btnProcessar.setEnabled(false);

        btnSelecionar.addActionListener(this::selecionarArquivo);
        btnProcessar.addActionListener(this::executarProcessamento);

        painelArquivo.add(label);
        painelArquivo.add(campoArquivoEntrada);
        painelArquivo.add(btnSelecionar);
        painelArquivo.add(btnProcessar);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        toolBar.add(btnFechar);

        add(toolBar, BorderLayout.NORTH);
        add(painelArquivo, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void selecionarArquivo(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Escolha o arquivo assembly com macros");
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivos Assembly", "asm", "ASM"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = chooser.getSelectedFile();
            campoArquivoEntrada.setText(arquivo.getAbsolutePath());
            btnProcessar.setEnabled(true);
            statusLabel.setText("Arquivo selecionado. Clique em 'Processar Macros'.");
        }
    }

    private void executarProcessamento(ActionEvent e) {
        String caminhoEntrada = campoArquivoEntrada.getText().trim();
        if (caminhoEntrada.isEmpty()) return;

        statusLabel.setText("Processando macros...");
        btnProcessar.setEnabled(false);

        SwingUtilities.invokeLater(() -> {
            try {
                processador.processar(caminhoEntrada);
                statusLabel.setText("Processamento concluído. Arquivo MASMAPRG.ASM gerado.");
                JOptionPane.showMessageDialog(this,
                        "Macros processadas com sucesso!\nArquivo gerado: MASMAPRG.ASM",
                        "Concluído",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                statusLabel.setText("Erro durante o processamento.");
                JOptionPane.showMessageDialog(this,
                        "Erro: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                btnProcessar.setEnabled(true);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            new InterfaceProcessadorMacros().setVisible(true);
        });
    }
}