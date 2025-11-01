package interfacesicxe;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

public class PainelMemoria extends JPanel {

    private static final Color COR_FUNDO_PAINEL = new Color(63, 84, 114); 
    private static final Color COR_DETALHE = new Color(6,8,11);       
    private static final Color COR_TEXTO = Color.WHITE;
    private static final Color COR_CABECALHO = new Color(85, 107, 127);    

    private JTable tabelaMemoria;
    private DefaultTableModel modeloTabela;

    public PainelMemoria() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COR_DETALHE, 1),
            "Memória (Hexadecimal)",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            COR_DETALHE
        ));
        setBackground(COR_FUNDO_PAINEL);

        modeloTabela = new DefaultTableModel(new Object[]{"Endereço", "Valor"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaMemoria = new JTable(modeloTabela);
        tabelaMemoria.setFont(new Font("Consolas", Font.PLAIN, 13));
        tabelaMemoria.setForeground(COR_TEXTO);
        tabelaMemoria.setRowHeight(22);
        tabelaMemoria.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabelaMemoria.getTableHeader().setForeground(COR_TEXTO);
        tabelaMemoria.getTableHeader().setBackground(COR_CABECALHO); // Cabeçalho em azul médio
        tabelaMemoria.setBackground(COR_FUNDO_PAINEL); // ← Fundo da tabela = fundo do painel
        tabelaMemoria.setGridColor(new Color(100, 120, 140)); // Grade suave

        // Estilizar células
        tabelaMemoria.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setForeground(COR_TEXTO);
                setBackground(COR_FUNDO_PAINEL);
                if (isSelected) {
                    setBackground(new Color(100, 120, 140)); // Seleção em azul mais claro
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tabelaMemoria);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COR_FUNDO_PAINEL); // ← Fundo do scroll
        add(scroll, BorderLayout.CENTER);

        preencherMemoriaFake();
    }

    private void preencherMemoriaFake() {
        for (int i = 0; i < 50; i++) {
            modeloTabela.addRow(new Object[]{String.format("%04X", i * 16), "00"});
        }
    }

    public void resetar() {
        modeloTabela.setRowCount(0);
        preencherMemoriaFake();
    }
}