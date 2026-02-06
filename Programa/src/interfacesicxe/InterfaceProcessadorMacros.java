package interfacesicxe;

import com.formdev.flatlaf.FlatLightLaf;
import ProcessadorDeMacros.ProcessadorDeMacros;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class InterfaceProcessadorMacros extends JFrame {

    private JTextField campoArquivoEntrada;
    private JTextField campoArquivoSaida;

    private JLabel statusLabel;

    private JButton btnProcessar;
    private JButton btnAbrirSaida;

    private File arquivoSaidaAtual;

    private final ProcessadorDeMacros processador;

    public InterfaceProcessadorMacros() {
        this.processador = new ProcessadorDeMacros();
        FlatLightLaf.setup();
        configurarJanela();
        criarComponentes();
    }

    private void configurarJanela() {
        setTitle("Processador de Macros");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(0, 0));

        setSize(720, 205);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(63, 84, 114));
        setResizable(false);
    }

    private void criarComponentes() {

        Color textoBranco = Color.WHITE;

        statusLabel = new JLabel(" ");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // ↓ menor
        statusLabel.setForeground(textoBranco);

        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 15, 0, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        
        // ===== Arquivo de entrada =====
        JLabel lblEntrada = new JLabel("Arquivo de Entrada:");
        lblEntrada.setForeground(textoBranco);

        campoArquivoEntrada = new JTextField();
        campoArquivoEntrada.setEditable(false);

        JButton btnSelecionar = new JButton("Selecionar");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        painelCentral.add(lblEntrada, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        painelCentral.add(campoArquivoEntrada, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        painelCentral.add(btnSelecionar, gbc);

        // ===== Botão Processar =====
        btnProcessar = new JButton("Processar");
        btnProcessar.setEnabled(false);
        btnProcessar.setFont(new Font("Segoe UI", Font.BOLD, 12));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        painelCentral.add(btnProcessar, gbc);
        gbc.gridwidth = 1;

        
        JLabel lblSaida = new JLabel("Arquivo de Saída:");
        lblSaida.setForeground(textoBranco);

        campoArquivoSaida = new JTextField();
        campoArquivoSaida.setEditable(false);
        campoArquivoSaida.setText(""); 

        btnAbrirSaida = new JButton("Abrir");
        btnAbrirSaida.setEnabled(false);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        painelCentral.add(lblSaida, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        painelCentral.add(campoArquivoSaida, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        painelCentral.add(btnAbrirSaida, gbc);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(true);
       

      
        btnSelecionar.addActionListener(this::selecionarArquivo);
        btnProcessar.addActionListener(this::executarProcessamento);
        btnAbrirSaida.addActionListener(e -> abrirSaida());

        add(toolBar, BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);
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

           
            arquivoSaidaAtual = null;
            campoArquivoSaida.setText("");
            btnAbrirSaida.setEnabled(false);
            statusLabel.setText(" ");
        }
    }

    private void executarProcessamento(ActionEvent e) {
        
        String caminhoEntrada = campoArquivoEntrada.getText().trim();
        if (caminhoEntrada.isEmpty()) return;

        statusLabel.setText(" "); 
        btnProcessar.setEnabled(false);

        SwingUtilities.invokeLater(() -> {
            try {
                File entrada = new File(caminhoEntrada);
             
                processador.processar(caminhoEntrada);

                File arquivoSaida = new File(entrada.getParentFile(), "MASMAPRG.ASM");

                if (!arquivoSaida.exists()) {
                    throw new RuntimeException("Arquivo MASMAPRG.ASM não encontrado.");
                }

                arquivoSaidaAtual = arquivoSaida;
                campoArquivoSaida.setText(arquivoSaida.getAbsolutePath());
                btnAbrirSaida.setEnabled(true);

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

    private void abrirSaida() {
        try {
            if (arquivoSaidaAtual == null || !arquivoSaidaAtual.exists()) {
                JOptionPane.showMessageDialog(this,
                        "O arquivo de saída ainda não foi gerado.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Desktop.getDesktop().open(arquivoSaidaAtual);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível abrir o arquivo.\nErro: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            new InterfaceProcessadorMacros().setVisible(true);
        });
    }
}
