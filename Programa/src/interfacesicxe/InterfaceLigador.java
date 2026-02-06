package interfacesicxe;

import com.formdev.flatlaf.FlatLightLaf;
import ligador.Ligador;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class InterfaceLigador extends JFrame {

    private static final Color AZUL_FUNDO = new Color(63, 84, 114);
    private static final Color AZUL_FUNDO_CAIXA = new Color(78, 101, 128);
    private static final Color BRANCO = Color.WHITE;
    private static final Color AZUL_TEXTO = new Color(32, 67, 103);
    private static final Color CINZA_LINHA = new Color(225, 232, 240);

    private final DefaultListModel<String> modeloLista = new DefaultListModel<>();
    private final List<String> conteudosObjetos = new ArrayList<>();

    private JList<String> listaArquivos;
    private JTextArea areaObjetoSelecionado;
    private JTextArea areaSaida;

    private JComboBox<String> comboModo;
    private JTextField campoEnderecoCarga;

    private JLabel statusLabel;

    private final InterfaceSICXE interfacePrincipal;

    private String ultimoObjetoGeradoLimpo = null;

    public InterfaceLigador(InterfaceSICXE interfacePrincipal) {
        
        this.interfacePrincipal = interfacePrincipal;
        FlatLightLaf.setup();
        configurarJanela();
        criarComponentes();
        
    }

    public InterfaceLigador() {
        this(null);
    }

    private void configurarJanela() {
        
        setTitle("Ligador SIC/XE - Duas Passagens");
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

        JButton btnAdicionar = criarBotaoLink("Adicionar");
        JButton btnRemover = criarBotaoLink("Remover");
        JButton btnLimpar = criarBotaoLink("Limpar");
        JButton btnLigar = criarBotaoLink("Ligar");
        JButton btnSalvar = criarBotaoLink("Salvar");
        JButton btnCarregarNoExecutor = criarBotaoLink("Carregar no Executor");

        botoesGrid.add(btnAdicionar);
        botoesGrid.add(btnRemover);
        botoesGrid.add(btnLimpar);
        botoesGrid.add(btnLigar);
        botoesGrid.add(btnSalvar);
        botoesGrid.add(btnCarregarNoExecutor);

        barraBotoes.add(botoesGrid, BorderLayout.CENTER);
     
        JPanel faixaControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        faixaControles.setBackground(BRANCO);
        faixaControles.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, CINZA_LINHA));

        JLabel lblModo = criarLabelTopo("Modo");
        comboModo = new JComboBox<>(new String[]{"LIGADOR", "LIGADOR RELOCADOR"});
        comboModo.setPreferredSize(new Dimension(220, 28));

        JLabel lblEnd = criarLabelTopo("Endereço de Carga:");
        campoEnderecoCarga = new JTextField("000000");
        campoEnderecoCarga.setPreferredSize(new Dimension(120, 28));
     
        faixaControles.add(lblModo);
        faixaControles.add(comboModo);
        faixaControles.add(lblEnd);
        faixaControles.add(campoEnderecoCarga);
       
        JPanel topo = new JPanel(new BorderLayout(0, 0));
        topo.setBackground(BRANCO);
        topo.add(barraBotoes, BorderLayout.NORTH);
        topo.add(faixaControles, BorderLayout.SOUTH);
       
        JPanel centro = new JPanel(new BorderLayout(12, 12));
        centro.setBackground(AZUL_FUNDO);
        centro.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel esquerda = new JPanel(new GridLayout(2, 1, 0, 12));
        esquerda.setBackground(AZUL_FUNDO);

        listaArquivos = new JList<>(modeloLista);
        listaArquivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaArquivos.setFont(new Font("Consolas", Font.PLAIN, 13));
        listaArquivos.setBackground(AZUL_FUNDO_CAIXA);
        listaArquivos.setForeground(BRANCO);
        listaArquivos.setSelectionBackground(new Color(104, 132, 166));
        listaArquivos.setSelectionForeground(BRANCO);

        JScrollPane scrollLista = new JScrollPane(listaArquivos);
        estilizarScrollAzul(scrollLista);
        scrollLista.setBorder(criarBordaTituloBranca("Módulos"));

        areaObjetoSelecionado = new JTextArea();
        areaObjetoSelecionado.setEditable(false);
        areaObjetoSelecionado.setFont(new Font("Consolas", Font.PLAIN, 13));
        areaObjetoSelecionado.setBackground(AZUL_FUNDO_CAIXA);
        areaObjetoSelecionado.setForeground(BRANCO);
        areaObjetoSelecionado.setCaretColor(BRANCO);

        JScrollPane scrollPreview = new JScrollPane(areaObjetoSelecionado);
        estilizarScrollAzul(scrollPreview);
        scrollPreview.setBorder(criarBordaTituloBranca("Preview do módulo"));

        esquerda.add(scrollLista);
        esquerda.add(scrollPreview);

        areaSaida = new JTextArea();
        areaSaida.setEditable(false);
        areaSaida.setFont(new Font("Consolas", Font.PLAIN, 13));
        areaSaida.setBackground(AZUL_FUNDO_CAIXA);
        areaSaida.setForeground(BRANCO);
        areaSaida.setCaretColor(BRANCO);

        JScrollPane scrollSaida = new JScrollPane(areaSaida);
        estilizarScrollAzul(scrollSaida);
        scrollSaida.setBorder(criarBordaTituloBranca("Saída"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, esquerda, scrollSaida);
        split.setDividerLocation(340);
        split.setResizeWeight(0.35);
        split.setBorder(BorderFactory.createLineBorder(BRANCO));
        split.setBackground(AZUL_FUNDO);
        split.setDividerSize(6);

        centro.add(split, BorderLayout.CENTER);
        
        statusLabel = new JLabel("Pronto para ligar.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        statusLabel.setForeground(BRANCO);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 12f));

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(AZUL_FUNDO);
        rodape.add(statusLabel, BorderLayout.WEST);

        root.add(topo, BorderLayout.NORTH);
        root.add(centro, BorderLayout.CENTER);
        root.add(rodape, BorderLayout.SOUTH);

     
        btnAdicionar.addActionListener(this::adicionarArquivos);
        btnRemover.addActionListener(e -> removerSelecionado());
        btnLimpar.addActionListener(e -> limparTudo());
        btnLigar.addActionListener(e -> executarLigacao(false));
        btnSalvar.addActionListener(this::salvarSaida);

        btnCarregarNoExecutor.setEnabled(interfacePrincipal != null);
        btnCarregarNoExecutor.addActionListener(e -> executarLigacao(true));

        listaArquivos.addListSelectionListener(e -> {
            
            int idx = listaArquivos.getSelectedIndex();
            if (idx >= 0 && idx < conteudosObjetos.size()) {
                areaObjetoSelecionado.setText(conteudosObjetos.get(idx));
            } else {
                areaObjetoSelecionado.setText("");
            }
            
        });
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

    private JLabel criarLabelTopo(String texto) {
        
        JLabel l = new JLabel(texto);
        l.setForeground(AZUL_TEXTO);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
        
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

    private void adicionarArquivos(ActionEvent e) {
        
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecionar módulos objeto (SIC/XE)");
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivos Objeto/Texto", "obj", "txt"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var files = chooser.getSelectedFiles();
                for (var f : files) {
                    String conteudo = Files.readString(f.toPath());
                    modeloLista.addElement(f.getName());
                    conteudosObjetos.add(conteudo);
                }
                statusLabel.setText("Módulos carregados: " + conteudosObjetos.size());
                if (!conteudosObjetos.isEmpty() && listaArquivos.getSelectedIndex() == -1) {
                    listaArquivos.setSelectedIndex(0);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao ler arquivo:\n" + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removerSelecionado() {
        
        int idx = listaArquivos.getSelectedIndex();
        if (idx < 0) return;

        modeloLista.remove(idx);
        conteudosObjetos.remove(idx);

        if (!conteudosObjetos.isEmpty()) {
            listaArquivos.setSelectedIndex(Math.min(idx, conteudosObjetos.size() - 1));
        } else {
            areaObjetoSelecionado.setText("");
        }

        statusLabel.setText("Módulos carregados: " + conteudosObjetos.size());
    }

    private void limparTudo() {
        modeloLista.clear();
        conteudosObjetos.clear();
        areaObjetoSelecionado.setText("");
        areaSaida.setText("");
        ultimoObjetoGeradoLimpo = null;
        statusLabel.setText("Pronto para ligar.");
    }

    private void executarLigacao(boolean carregarNoExecutor) {
        if (conteudosObjetos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Adicione pelo menos 1 módulo objeto antes de ligar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ultimoObjetoGeradoLimpo = null;

            String modoTxt = (String) comboModo.getSelectedItem();
            Ligador.Modo modo = Ligador.Modo.valueOf(modoTxt);

            int enderecoCarga = parseHexSeguro(campoEnderecoCarga.getText().trim(), 0);           

            Ligador ligador = new Ligador();
            Ligador.ResultadoLigacao r = ligador.ligar(conteudosObjetos, modo, enderecoCarga);

            StringBuilder sb = new StringBuilder();

            if (r.erros != null && !r.erros.isEmpty()) {
                sb.append("ERROS/AVISOS:\n");
                for (String err : r.erros) sb.append(" - ").append(err).append("\n");
                sb.append("\n");
            }

            sb.append("Endereço de execução: ").append(String.format("%06X", r.enderecoExecucao)).append("\n\n");

            if (modo == Ligador.Modo.LIGADOR) {
                sb.append("OBJETO LIGADO (relocável):\n\n");
                sb.append(r.objetoLigado).append("\n");
                ultimoObjetoGeradoLimpo = (r.objetoLigado == null) ? null : r.objetoLigado.trim();
                statusLabel.setText("Ligação concluída (modo LIGADOR).");
            } else {
                sb.append("DUMP de memória (256 bytes a partir do endereço de carga):\n\n");
                sb.append(r.dumpHex(enderecoCarga, 256)).append("\n");
                statusLabel.setText("Ligação + relocação concluída (modo LIGADOR_RELOCADOR).");
            }

            areaSaida.setText(sb.toString());

            if (carregarNoExecutor) {
                if (interfacePrincipal == null) {
                    JOptionPane.showMessageDialog(this,
                            "Essa janela do Ligador não está conectada ao simulador.",
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (modo == Ligador.Modo.LIGADOR_RELOCADOR) {

                    interfacePrincipal.carregarImagemDeMemoria(r.memoria, r.enderecoExecucao);

                    JOptionPane.showMessageDialog(this,
                            "Programa carregado no Executor!\nPC = " + String.format("%06X", r.enderecoExecucao),
                            "Carregado", JOptionPane.INFORMATION_MESSAGE);

                } else {

                    int enderecoCargaReal = parseHexSeguro(campoEnderecoCarga.getText().trim(), 0);

                    interfacePrincipal.carregarObjetoRelocavelNoExecutor(r.objetoLigado, enderecoCargaReal);
                }
            }

        } catch (Exception ex) {
            areaSaida.setText("ERRO CRÍTICO NO LIGADOR:\n" + ex.getMessage());
            statusLabel.setText("Erro ao ligar.");
            JOptionPane.showMessageDialog(this, "Erro no ligador:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void salvarSaida(ActionEvent e) {
        String conteudo = (ultimoObjetoGeradoLimpo != null && !ultimoObjetoGeradoLimpo.isBlank())
                ? ultimoObjetoGeradoLimpo.trim()
                : areaSaida.getText().trim();

        if (conteudo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há nada para salvar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar saída");
        chooser.setSelectedFile(new java.io.File(ultimoObjetoGeradoLimpo != null ? "objeto_ligado.obj" : "saida_ligador.txt"));
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivos de Texto", "txt", "obj"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String caminho = chooser.getSelectedFile().getAbsolutePath();
                if (!caminho.toLowerCase().endsWith(".txt") && !caminho.toLowerCase().endsWith(".obj")) {
                    caminho += (ultimoObjetoGeradoLimpo != null) ? ".obj" : ".txt";
                }
                Files.writeString(java.nio.file.Paths.get(caminho), conteudo);
                statusLabel.setText("Arquivo salvo: " + caminho);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar:\n" + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int parseHexSeguro(String txt, int padrao) {
        try {
            String t = txt.toUpperCase().trim();
            if (t.startsWith("0X")) t = t.substring(2);
            if (t.isEmpty()) return padrao;
            return Integer.parseUnsignedInt(t, 16);
        } catch (Exception ex) {
            return padrao;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            new InterfaceLigador().setVisible(true);
        });
    }
}
