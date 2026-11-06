package biblioteca.view;

import biblioteca.controller.EmprestimoController;
import biblioteca.controller.LivroController;
import biblioteca.controller.UsuarioController;
import biblioteca.model.Emprestimo;
import biblioteca.model.Livro;
import biblioteca.model.Usuario;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * VIEW - tela de empréstimos e devoluções.
 */
public class EmprestimoView extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final EmprestimoController controller;
    private final LivroController livroController;
    private final UsuarioController usuarioController;

    private final JComboBox<Livro> cbLivro = new JComboBox<>();
    private final JComboBox<Usuario> cbUsuario = new JComboBox<>();
    private final JCheckBox chkAtivos = new JCheckBox("Mostrar somente empréstimos ativos");

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"ID", "Livro", "Usuário", "Empréstimo", "Prevista", "Devolução",
                "Multa (R$)", "Situação"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    public EmprestimoView(EmprestimoController controller, LivroController livroController,
                          UsuarioController usuarioController) {
        this.controller = controller;
        this.livroController = livroController;
        this.usuarioController = usuarioController;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelNovo(), BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(criarPainelRodape(), BorderLayout.SOUTH);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(24);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        atualizar();
    }

    private JPanel criarPainelNovo() {
        cbLivro.setPreferredSize(new Dimension(280, 26));
        cbUsuario.setPreferredSize(new Dimension(200, 26));
        JButton btnEmprestar = new JButton("Registrar empréstimo");
        btnEmprestar.addActionListener(e -> emprestar());

        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painel.add(new JLabel("Livro:"));
        painel.add(cbLivro);
        painel.add(new JLabel("Usuário:"));
        painel.add(cbUsuario);
        painel.add(btnEmprestar);
        return painel;
    }

    private JPanel criarPainelRodape() {
        JButton btnDevolver = new JButton("Devolver selecionado");
        JButton btnAtualizar = new JButton("Atualizar lista");
        btnDevolver.addActionListener(e -> devolver());
        btnAtualizar.addActionListener(e -> atualizar());
        chkAtivos.addActionListener(e -> atualizar());

        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painel.add(btnDevolver);
        painel.add(btnAtualizar);
        painel.add(chkAtivos);
        return painel;
    }

    /** Recarrega as listas de seleção e a tabela a partir dos Controllers. */
    public void atualizar() {
        cbLivro.removeAllItems();
        for (Livro l : livroController.listarDisponiveis()) {
            cbLivro.addItem(l);
        }
        cbUsuario.removeAllItems();
        for (Usuario u : usuarioController.listar()) {
            cbUsuario.addItem(u);
        }

        modelo.setRowCount(0);
        LocalDate hoje = LocalDate.now();
        for (Emprestimo e : controller.listar(chkAtivos.isSelected())) {
            modelo.addRow(new Object[] {
                e.getId(),
                controller.tituloDoLivro(e.getLivroId()),
                controller.nomeDoUsuario(e.getUsuarioId()),
                e.getDataEmprestimo().format(FORMATO),
                e.getDataPrevista().format(FORMATO),
                e.isAtivo() ? "-" : e.getDataDevolucao().format(FORMATO),
                String.format("%.2f", e.calcularMulta(hoje)),
                situacao(e, hoje)
            });
        }
    }

    private String situacao(Emprestimo e, LocalDate hoje) {
        if (!e.isAtivo()) {
            return "Devolvido";
        }
        return e.diasAtraso(hoje) > 0 ? "Em atraso" : "No prazo";
    }

    private void emprestar() {
        Livro livro = (Livro) cbLivro.getSelectedItem();
        Usuario usuario = (Usuario) cbUsuario.getSelectedItem();
        if (livro == null || usuario == null) {
            Mensagens.erro(this, "Selecione um livro disponível e um usuário.");
            return;
        }
        try {
            Emprestimo e = controller.emprestar(livro.getId(), usuario.getId());
            Mensagens.info(this, "Empréstimo registrado!\nDevolução prevista para "
                    + e.getDataPrevista().format(FORMATO) + ".");
            atualizar();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Mensagens.erro(this, ex.getMessage());
        }
    }

    private void devolver() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            Mensagens.erro(this, "Selecione um empréstimo na tabela.");
            return;
        }
        int id = (Integer) modelo.getValueAt(linha, 0);
        try {
            double multa = controller.devolver(id);
            if (multa > 0) {
                Mensagens.info(this, String.format("Devolução registrada com atraso.\nMulta a pagar: R$ %.2f", multa));
            } else {
                Mensagens.info(this, "Devolução registrada. Sem multa.");
            }
            atualizar();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Mensagens.erro(this, ex.getMessage());
        }
    }
}
