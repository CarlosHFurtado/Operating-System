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
        executor = new Executor();
        executor.setPainelLog(painelLog);
        
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
        JButton btnProcessadorMacros = new JButton("Processador de Macros");
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
        toolBar.add(btnProcessadorMacros);
        toolBar.addSeparator();
        toolBar.add(btnSair);

        btnAbrirMontador.addActionListener(e -> new InterfaceMontador(this).setVisible(true));
        btnProcessadorMacros.addActionListener(e -> new InterfaceProcessadorMacros().setVisible(true));

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

    public void atualizarTodosPaineis() {
        painelMemoria.atualizar();
        painelRegistradores.atualizar();
    }


    public void carregarProgramaMontado(String codigoObjeto) {
        if (codigoObjeto == null || codigoObjeto.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Código-objeto está vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            executor.limpar();
            String[] linhas = codigoObjeto.split("\\r?\\n");
            int enderecoInicio = 0;
            boolean pcDefinido = false;

            for (String linha : linhas) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;

                String[] partes = linha.split("\\^");
                if (partes.length < 2) continue;

                switch (partes[0]) {
                    case "H":
                        if (partes.length >= 3) {
                            try {
                                enderecoInicio = Integer.parseInt(partes[2], 16);
                                pcDefinido = true;
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(this,
                                    "Endereço inválido no registro H: " + partes[2],
                                    "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        break;

                    case "T":
                        if (partes.length >= 3) {
                            int endereco;
                            try {
                                endereco = Integer.parseInt(partes[1], 16);
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(this,
                                    "Endereço inválido no registro T: " + partes[1],
                                    "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                                continue;
                            }
                            String dadosHex = partes[2].replaceAll("\\^", "").toUpperCase();

                            for (int i = 0; i < dadosHex.length(); i += 2) {
                                if (i + 1 >= dadosHex.length()) {
                                    JOptionPane.showMessageDialog(this,
                                        "Quantidade ímpar de dígitos hex no registro T.",
                                        "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                                String byteStr = dadosHex.substring(i, i + 2);
                                if (!byteStr.matches("[0-9A-F]{2}")) {
                                    JOptionPane.showMessageDialog(this,
                                        "Caractere inválido no código-objeto: " + byteStr,
                                        "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                                int valor = Integer.parseUnsignedInt(byteStr, 16);
                                if (endereco < executor.getMemoria().getMem().length) {
                                    executor.getMemoria().setByte(endereco, (byte) valor);
                                    endereco++;
                                } else {
                                    JOptionPane.showMessageDialog(this,
                                        "Memória excedida ao carregar código-objeto!",
                                        "Erro", JOptionPane.WARNING_MESSAGE);
                                    break;
                                }
                            }
                        }
                        break;

                    case "E":
                        if (partes.length >= 2 && !partes[1].isEmpty()) {
                            try {
                                int pc = Integer.parseInt(partes[1], 16);
                                executor.getRegistradores().setValor("PC", pc);
                            } catch (NumberFormatException ignored) {}
                        } else if (pcDefinido) {
                            executor.getRegistradores().setValor("PC", enderecoInicio);
                        }
                        break;
                }
            }

            if (!pcDefinido) {
                executor.getRegistradores().setValor("PC", 0);
            }

            executor.getRegistradores().setValor("A", 0);
            executor.getRegistradores().setValor("X", 0);
            executor.getRegistradores().setValor("L", 0);
            executor.getRegistradores().setValor("SW", 0);

            atualizarTodosPaineis();
            painelLog.adicionarMensagem("Programa carregado via código-objeto.");
            painelLog.adicionarMensagem("PC = 0x" + String.format("%06X", executor.getRegistradores().getValor("PC")));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao interpretar código-objeto:\n" + ex.getMessage(),
                "Erro de Carregamento", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}