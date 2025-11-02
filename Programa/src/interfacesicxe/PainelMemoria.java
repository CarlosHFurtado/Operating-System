package interfacesicxe;

import Executor.Executor;
import Executor.Memoria;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.DefaultCellEditor;
import java.awt.*;
import javax.swing.border.TitledBorder;

public class PainelMemoria extends JPanel {

    private static final Color COR_FUNDO_PAINEL = new Color(63, 84, 114);
    private static final Color COR_DETALHE = new Color(6, 8, 11);
    private static final Color COR_TEXTO = Color.WHITE;
    private static final Color COR_CABECALHO = new Color(85, 107, 127);

    private Executor executor;
    private JTable tabelaMemoria;
    private DefaultTableModel modeloTabela;
    private boolean modoEdicao = false;

    public PainelMemoria(Executor executor) {
        this.executor = executor;
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
                return column == 1 && modoEdicao;
            }
        };

        tabelaMemoria = new JTable(modeloTabela);
        tabelaMemoria.setFont(new Font("Consolas", Font.PLAIN, 13));
        tabelaMemoria.setForeground(COR_TEXTO);
        tabelaMemoria.setRowHeight(22);
        tabelaMemoria.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabelaMemoria.getTableHeader().setForeground(COR_TEXTO);
        tabelaMemoria.getTableHeader().setBackground(COR_CABECALHO);
        tabelaMemoria.setBackground(COR_FUNDO_PAINEL);
        tabelaMemoria.setGridColor(new Color(100, 120, 140));

        tabelaMemoria.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setForeground(COR_TEXTO);
                setBackground(COR_FUNDO_PAINEL);
                if (isSelected) {
                    setBackground(new Color(100, 120, 140));
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tabelaMemoria);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COR_FUNDO_PAINEL);
        add(scroll, BorderLayout.CENTER);

        atualizar();
    }

    private void finalizarEdicaoTabela() {
        if (tabelaMemoria.isEditing()) {
            tabelaMemoria.getCellEditor().stopCellEditing();
        }
    }

    public void atualizar() {
        modeloTabela.setRowCount(0);
        Memoria memoria = executor.getMemoria();
        byte[] mem = memoria.getMem();

        for (int i = 0; i < Math.min(64, mem.length); i += 4) {
            int valor = memoria.getWord(i);
            String hexValor = String.format("%06X", valor & 0xFFFFFF);
            modeloTabela.addRow(new Object[]{String.format("%04X", i), hexValor});
        }

        if (modoEdicao) {
            tabelaMemoria.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()) {
                @Override
                public boolean stopCellEditing() {
                    int row = tabelaMemoria.getEditingRow();
                    String valorStr = ((JTextField) getComponent()).getText().trim();
                    try {
                        int valorInt = Integer.parseUnsignedInt(valorStr, 16);
                        String endStr = (String) modeloTabela.getValueAt(row, 0);
                        int endereco = Integer.parseInt(endStr, 16);
                        executor.getMemoria().setWord(endereco, valorInt);
                        return super.stopCellEditing();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(PainelMemoria.this,
                            "Valor inválido. Use hexadecimal (ex: 0000FF).",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            });
        } else {
            tabelaMemoria.setDefaultEditor(Object.class, null);
        }
    }

    public void resetar() {
        executor.limpar();
        atualizar();
    }

    public void alternarModoEdicao() {
        if (modoEdicao) {
            finalizarEdicaoTabela(); // Garante que a edição seja salva antes de sair
        }
        modoEdicao = !modoEdicao;
        atualizar();
    }

    public boolean isModoEdicao() {
        return modoEdicao;
    }
}