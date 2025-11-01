package interfacesicxe;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class InterfaceSICXE extends JFrame {

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
    // Criar painéis
    painelControle = new PainelControle();
    painelLog = new PainelLog();
    painelMemoria = new PainelMemoria();
    painelRegistradores = new PainelRegistradores();

    // Barra de ferramentas (opcional)
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    toolBar.setBackground(new Color(240, 245, 255));

    JButton btnCarregar = new JButton("📁 Carregar Programa");
    JButton btnResetar = new JButton("🔄 Resetar");
    JButton btnSair = new JButton("🚪 Sair");

    toolBar.add(btnCarregar);
    toolBar.add(btnResetar);
    toolBar.addSeparator();
    toolBar.add(btnSair);

    // Eventos (exemplo simples)
    btnCarregar.addActionListener(e -> JOptionPane.showMessageDialog(this, "Função de carregar programa ainda não implementada."));
    btnResetar.addActionListener(e -> {
        // Aqui você chama métodos de reset dos painéis
        painelMemoria.resetar();
        painelRegistradores.resetar();
        painelLog.limpar();
        JOptionPane.showMessageDialog(this, "Simulador resetado.");
    });
    btnSair.addActionListener(e -> System.exit(0));

    // Painel central: memória + log
    JPanel painelCentral = new JPanel(new GridLayout(1, 2, 10, 10));
    painelCentral.setBorder(BorderFactory.createTitledBorder("Área de Trabalho"));
    painelCentral.add(painelMemoria);
    painelCentral.add(painelLog);

    // Painel lateral: registradores + controle
    JPanel painelLateral = new JPanel(new BorderLayout(10, 10));
    painelLateral.setBorder(BorderFactory.createTitledBorder("Controles"));

    painelLateral.add(painelRegistradores, BorderLayout.CENTER);
    painelLateral.add(painelControle, BorderLayout.SOUTH);

    // Adicionar tudo à janela
    add(toolBar, BorderLayout.NORTH);
    add(painelCentral, BorderLayout.CENTER);
    add(painelLateral, BorderLayout.EAST);
}
}
