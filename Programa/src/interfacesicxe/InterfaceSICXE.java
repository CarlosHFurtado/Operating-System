package interfacesicxe;

import Executor.Executor;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.util.List;

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

        executor = new Executor();
        executor.setPainelLog(painelLog);

        painelControle = new PainelControle(executor, this);
        painelMemoria = new PainelMemoria(executor);
        painelRegistradores = new PainelRegistradores(executor);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(240, 245, 255));

        JButton btnCarregarHex = new JButton("Carregar HEX");
        JButton btnResetar = new JButton("Resetar");
        JButton btnLimparLog = new JButton("Limpar Log");
        JButton btnEditar = new JButton("Editar Manualmente");
        JButton btnAbrirMontador = new JButton("Abrir Montador");
        JButton btnProcessadorMacros = new JButton("Processador de Macros");
        JButton btnLigador = new JButton("Ligador");
        JButton btnSair = new JButton("Sair");

        toolBar.add(btnCarregarHex);
        toolBar.addSeparator();
        toolBar.add(btnResetar);
        toolBar.addSeparator();
        toolBar.add(btnLimparLog);
        toolBar.addSeparator();
        toolBar.add(btnEditar);
        toolBar.addSeparator();
        toolBar.add(btnAbrirMontador);
        toolBar.addSeparator();
        toolBar.add(btnProcessadorMacros);
        toolBar.addSeparator();
        toolBar.add(btnLigador);
        toolBar.addSeparator();
        toolBar.add(btnSair);

        // Abre janelas
        btnAbrirMontador.addActionListener(e -> new InterfaceMontador(this).setVisible(true));
        btnProcessadorMacros.addActionListener(e -> new InterfaceProcessadorMacros().setVisible(true));
        btnLigador.addActionListener(e -> new InterfaceLigador(this).setVisible(true));

        // Carregar HEX bruto (não é OBJ SIC/XE)
        btnCarregarHex.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Selecione um arquivo (.txt) com bytes HEX (sem H/T/M/E)");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    List<String> lines = Files.readAllLines(chooser.getSelectedFile().toPath());
                    executor.limpar();

                    int endereco = 0;
                    int memoriaTamanho = executor.getMemoria().getMem().length;

                    for (String linha : lines) {
                        linha = linha.trim().toUpperCase();
                        if (linha.isEmpty() || linha.startsWith(";")) continue;

                        linha = linha.replaceAll("\\s+", ""); // aceita com espaços
                        if (linha.length() % 2 != 0) {
                            JOptionPane.showMessageDialog(this, "Linha inválida (quantidade ímpar de hex): " + linha);
                            return;
                        }

                        for (int i = 0; i < linha.length(); i += 2) {
                            if (endereco >= memoriaTamanho) {
                                JOptionPane.showMessageDialog(this, "Memória cheia! Programa truncado no byte " + endereco + ".");
                                break;
                            }
                            int valor = Integer.parseUnsignedInt(linha.substring(i, i + 2), 16);
                            executor.getMemoria().setByte(endereco++, (byte) valor);
                        }
                    }

                    executor.getRegistradores().setValor("PC", 0);
                    executor.getRegistradores().setValor("A", 0);
                    executor.getRegistradores().setValor("X", 0);
                    executor.getRegistradores().setValor("L", 0);
                    executor.getRegistradores().setValor("SW", 0);

                    atualizarTodosPaineis();
                    painelLog.adicionarMensagem("HEX bruto carregado com sucesso.");
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

        btnLimparLog.addActionListener(e -> painelLog.limpar());

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

    public void atualizarTodosPaineis() {
        painelMemoria.atualizar();
        painelRegistradores.atualizar();
    }

    /**
     * Esse é o "Carregador Absoluto" do seu simulador:
     * Recebe a imagem final de memória (vinda do ligador-relocador) e o PC.
     */
    public void carregarImagemDeMemoria(byte[] imagem, int pc) {
        if (imagem == null) {
            JOptionPane.showMessageDialog(this, "Imagem de memória nula.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        executor.limpar();

        byte[] memExec = executor.getMemoria().getMem();
        int n = Math.min(imagem.length, memExec.length);
        System.arraycopy(imagem, 0, memExec, 0, n);

        executor.getRegistradores().setValor("PC", pc);
        executor.getRegistradores().setValor("A", 0);
        executor.getRegistradores().setValor("X", 0);
        executor.getRegistradores().setValor("L", 0);
        executor.getRegistradores().setValor("SW", 0);

        atualizarTodosPaineis();
        painelLog.adicionarMensagem("Imagem de memória carregada no Executor.");
        painelLog.adicionarMensagem("PC = 0x" + String.format("%06X", pc));
    }

    public void carregarObjetoRelocavelNoExecutor(String objetoLigadoRelocavel, int enderecoCarga) {
    if (objetoLigadoRelocavel == null || objetoLigadoRelocavel.isBlank()) {
        JOptionPane.showMessageDialog(this, "Objeto relocável vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
    }

    executor.limpar();

    boolean ok = executor.carregarProgramaRelocavel(objetoLigadoRelocavel, enderecoCarga);
    if (!ok) {
        JOptionPane.showMessageDialog(this,
                "Falha ao carregar objeto relocável. Veja o log para detalhes.",
                "Erro", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Atualiza a interface
    painelMemoria.atualizar();
    painelRegistradores.atualizar();

    JOptionPane.showMessageDialog(this,
            "Programa carregado (relocável) no Executor!\nPC = " +
                    String.format("%06X", executor.getRegistradores().getValor("PC")),
            "Carregado", JOptionPane.INFORMATION_MESSAGE);
}
    public void carregarProgramaMontado(String codigoObjeto) {
        if (codigoObjeto == null || codigoObjeto.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Código-objeto está vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ligador.Ligador lig = new ligador.Ligador();
            ligador.Ligador.ResultadoLigacao r = lig.ligar(
                    java.util.List.of(codigoObjeto),
                    ligador.Ligador.Modo.LIGADOR_RELOCADOR,
                    0x000000
            );

            if (r != null && r.erros != null && !r.erros.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Erros/avisos no ligador:\n- " + String.join("\n- ", r.erros),
                        "Ligador", JOptionPane.WARNING_MESSAGE);
            }

            carregarImagemDeMemoria(r.memoria, r.enderecoExecucao);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar via Ligador:\n" + ex.getMessage(),
                    "Erro de Carregamento", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
