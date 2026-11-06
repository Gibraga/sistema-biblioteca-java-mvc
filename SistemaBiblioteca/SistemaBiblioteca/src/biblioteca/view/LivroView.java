package biblioteca.view;

import biblioteca.controller.LivroController;
import biblioteca.model.Livro;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * VIEW - tela de cadastro e consulta de livros.
 * Não contém regra de negócio: apenas captura os dados e delega ao Controller.
 */
public class LivroView extends JPanel {

    private static final long serialVersionUID = 1L;

    private final LivroController controller;

    private final JTextField txtTitulo = new JTextField(18);
    private final JTextField txtAutor = new JTextField(16);
    private final JTextField txtAno = new JTextField(5);
    private final JTextField txtBusca = new JTextField(18);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"ID", "Título", "Autor", "Ano", "Situação"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);
    private int idSelecionado = 0;

    public LivroView(LivroController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelBusca(), BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(criarPainelFormulario(), BorderLayout.SOUTH);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(24);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarSelecionado();
            }
        });
        atualizar();
    }

    private JPanel criarPainelBusca() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        JButton btnTodos = new JButton("Mostrar todos");
        btnBuscar.addActionListener(e -> atualizar());
        btnTodos.addActionListener(e -> {
            txtBusca.setText("");
            atualizar();
        });
        painel.add(new JLabel("Buscar por título ou autor:"));
        painel.add(txtBusca);
        painel.add(btnBuscar);
        painel.add(btnTodos);
        return painel;
    }

    private JPanel criarPainelFormulario() {
        JPanel campos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        campos.add(new JLabel("Título:"));
        campos.add(txtTitulo);
        campos.add(new JLabel("Autor:"));
        campos.add(txtAutor);
        campos.add(new JLabel("Ano:"));
        campos.add(txtAno);

        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnRemover = new JButton("Remover");
        JButton btnLimpar = new JButton("Limpar");
        btnCadastrar.addActionListener(e -> cadastrar());
        btnAtualizar.addActionListener(e -> atualizarLivro());
        btnRemover.addActionListener(e -> remover());
        btnLimpar.addActionListener(e -> limparFormulario());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btnCadastrar);
        botoes.add(btnAtualizar);
        botoes.add(btnRemover);
        botoes.add(btnLimpar);

        JPanel painel = new JPanel(new BorderLayout());
        painel.add(campos, BorderLayout.NORTH);
        painel.add(botoes, BorderLayout.SOUTH);
        return painel;
    }

    /** Recarrega a tabela a partir do Controller. */
    public void atualizar() {
        modelo.setRowCount(0);
        String termo = txtBusca.getText().trim();
        List<Livro> livros = termo.isEmpty() ? controller.listar() : controller.buscar(termo);
        for (Livro l : livros) {
            modelo.addRow(new Object[] {l.getId(), l.getTitulo(), l.getAutor(), l.getAno(),
                l.isDisponivel() ? "Disponível" : "Emprestado"});
        }
    }

    private void carregarSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        idSelecionado = (Integer) modelo.getValueAt(linha, 0);
        txtTitulo.setText(String.valueOf(modelo.getValueAt(linha, 1)));
        txtAutor.setText(String.valueOf(modelo.getValueAt(linha, 2)));
        txtAno.setText(String.valueOf(modelo.getValueAt(linha, 3)));
    }

    private void limparFormulario() {
        idSelecionado = 0;
        txtTitulo.setText("");
        txtAutor.setText("");
        txtAno.setText("");
        tabela.clearSelection();
    }

    private void cadastrar() {
        try {
            controller.cadastrar(txtTitulo.getText(), txtAutor.getText(), txtAno.getText());
            limparFormulario();
            atualizar();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Mensagens.erro(this, ex.getMessage());
        }
    }

    private void atualizarLivro() {
        if (idSelecionado == 0) {
            Mensagens.erro(this, "Selecione um livro na tabela para atualizar.");
            return;
        }
        try {
            controller.atualizar(idSelecionado, txtTitulo.getText(), txtAutor.getText(), txtAno.getText());
            limparFormulario();
            atualizar();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Mensagens.erro(this, ex.getMessage());
        }
    }

    private void remover() {
        if (idSelecionado == 0) {
            Mensagens.erro(this, "Selecione um livro na tabela para remover.");
            return;
        }
        if (!Mensagens.confirmar(this, "Deseja realmente remover este livro?")) {
            return;
        }
        try {
            controller.remover(idSelecionado);
            limparFormulario();
            atualizar();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Mensagens.erro(this, ex.getMessage());
        }
    }
}
