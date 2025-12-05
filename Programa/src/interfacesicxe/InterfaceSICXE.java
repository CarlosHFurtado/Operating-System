package interfacesicxe;

import Executor.Executor;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;

public class InterfaceSICXE extends JFrame {

    private Executor executor;
    private PainelControle painelControle;
    private PainelLog painelLog;
    private PainelMemoria painelMemoria;
    private PainelRegistradores painelRegistradores;

    public InterfaceSICXE() {
        super("Simulador SIC/XE - Interface Visual");
        
        FlatLightLaf.setup();
        configurarJanela();
        criarComponentes();
    }

    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(1100, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(63, 84, 114));
    }

    private void criarComponentes() {
        painelLog = new PainelLog();
        // Inicializa o Executor e o conecta ao PainelLog
        this.executor = new Executor();
        this.executor.setPainelLog(painelLog);
        
        painelControle = new PainelControle(executor, this);
        painelMemoria = new PainelMemoria(executor);
        painelRegistradores = new PainelRegistradores(executor);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(240, 245, 255));

        JButton btnCarregar = new JButton("Carregar Programa");
        JButton btnResetar = new JButton("Resetar");
        JButton btnLimparLog = new JButton("Limpar Log");
        JButton btnEditar = new JButton("Editar Manualmente");
        JButton btnAbrirMontador = new JButton("Abrir Montador");
        JButton btnSair = new JButton("Sair");


        toolBar.add(btnCarregar);
        toolBar.add(btnResetar);
        toolBar.addSeparator();
        toolBar.add(btnLimparLog);
        toolBar.addSeparator();
        toolBar.add(btnEditar);
        toolBar.addSeparator();
        toolBar.add(btnAbrirMontador);
        toolBar.addSeparator();
        toolBar.add(btnSair);
        
        // AÇÃO MODIFICADA: Passa a referência desta interface (this) para o Montador
        btnAbrirMontador.addActionListener(e -> new InterfaceMontador(this).setVisible(true));

        // AÇÃO ORIGINAL DE CARREGAMENTO DE ARQUIVO (MANTIDA)
        btnCarregar.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Selecione o arquivo de programa (.txt com hex)");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    List<String> lines = Files.readAllLines(chooser.getSelectedFile().toPath());
                    executor.limpar();

                    int endereco = 0;
                    int memoriaTamanho = executor.getMemoria().getMem().length;

                    for (String linha : lines) {
                        linha = linha.trim().toUpperCase();
                        if (linha.isEmpty() || linha.startsWith(";")) continue;

                        if (linha.length() % 2 != 0) {
                            JOptionPane.showMessageDialog(this, "Linha inválida: " + linha);
                            return;
                        }

                        for (int i = 0; i < linha.length(); i += 2) {
                            if (endereco >= memoriaTamanho) {
                                JOptionPane.showMessageDialog(this, 
                                    "Memória cheia! Programa truncado a partir do byte " + endereco + ".");
                                break;
                            }

                            String hexByte = linha.substring(i, i + 2);
                            int valor = Integer.parseUnsignedInt(hexByte, 16);
                            executor.getMemoria().setByte(endereco++, (byte) valor);
                        }
                    }

                    // Forçar PC para 0 e limpar outros registradores
                    executor.getRegistradores().setValor("PC", 0);
                    executor.getRegistradores().setValor("A", 0);
                    executor.getRegistradores().setValor("SW", 0);

                    atualizarTodosPaineis();
                    painelLog.adicionarMensagem("Programa carregado com sucesso.");
                    painelLog.adicionarMensagem("Bytes escritos: " + endereco);
                    painelLog.adicionarMensagem("PC definido para 0.");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao carregar: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        btnResetar.addActionListener(e -> {
            executor.limpar();
            atualizarTodosPaineis();
            painelLog.adicionarMensagem("Simulador resetado.");
        });
        
        btnLimparLog.addActionListener(e -> {
            painelLog.limpar();
        });

        btnEditar.addActionListener(e -> {
            painelMemoria.alternarModoEdicao();
            painelRegistradores.alternarModoEdicao();
            boolean modoEdicao = painelMemoria.isModoEdicao();
            btnEditar.setText(modoEdicao ? "✅ Salvar Edições" : "✏️ Editar Manualmente");
        });

        btnSair.addActionListener(e -> System.exit(0));

        JPanel painelCentral = new JPanel(new GridLayout(1, 2, 10, 10));
        painelCentral.setBorder(BorderFactory.createTitledBorder("Área de Trabalho"));
        painelCentral.add(painelMemoria);
        painelCentral.add(painelLog);

        JPanel painelLateral = new JPanel(new BorderLayout(10, 10));
        painelLateral.setBorder(BorderFactory.createTitledBorder("Controles"));
        painelLateral.add(painelRegistradores, BorderLayout.CENTER);
        painelLateral.add(painelControle, BorderLayout.SOUTH);

        add(toolBar, BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);
        add(painelLateral, BorderLayout.EAST);
    }
    
    /**
     * NOVO MÉTODO: Carrega o programa diretamente a partir de uma string de código objeto.
     * Este método é chamado pela InterfaceMontador ao clicar em "Carregar no Executor".
     *
     * @param objectCode A string contendo o código objeto em hexadecimal.
     */
    public void carregarProgramaMontado(String objectCode) {
        if (objectCode == null || objectCode.trim().isEmpty() || objectCode.contains("ERRO")) {
            painelLog.adicionarMensagem("ERRO: Código objeto vazio ou inválido. Carregamento cancelado.");
            return;
        }

        try {
            executor.limpar();
            // Remove espaços e novas linhas, assumindo que objectCode é uma sequência de bytes hex
            String hexCode = objectCode.replaceAll("\\s+", ""); 
            
            int endereco = 0;
            int memoriaTamanho = executor.getMemoria().getMem().length;

            if (hexCode.length() % 2 != 0) {
                painelLog.adicionarMensagem("ERRO: Código objeto tem um número ímpar de caracteres hexadecimais.");
                return;
            }

            for (int i = 0; i < hexCode.length(); i += 2) {
                if (endereco >= memoriaTamanho) {
                    painelLog.adicionarMensagem("Memória cheia! Programa truncado a partir do byte " + endereco + ".");
                    break;
                }

                String hexByte = hexCode.substring(i, i + 2);
                int valor = Integer.parseUnsignedInt(hexByte, 16);
                executor.getMemoria().setByte(endereco++, (byte) valor);
            }
            
            // Forçar PC para 0 e limpar outros registradores
            executor.getRegistradores().setValor("PC", 0);
            executor.getRegistradores().setValor("A", 0);
            executor.getRegistradores().setValor("SW", 0);


            atualizarTodosPaineis();
            painelLog.adicionarMensagem("🚀 Programa carregado com sucesso pelo Montador.");
            painelLog.adicionarMensagem("Bytes escritos: " + endereco);
            painelLog.adicionarMensagem("PC definido para 0.");

        } catch (Exception ex) {
            painelLog.adicionarMensagem("ERRO CRÍTICO ao carregar o código objeto. Detalhes: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void atualizarTodosPaineis() {
        painelMemoria.atualizar();
        painelRegistradores.atualizar();
    }
}