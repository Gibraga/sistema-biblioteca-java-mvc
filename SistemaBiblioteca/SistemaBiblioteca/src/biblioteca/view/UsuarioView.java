package biblioteca.view;

import biblioteca.controller.UsuarioController;
import biblioteca.model.Usuario;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
 * VIEW - tela de cadastro e consulta de usuários.
 */
public class UsuarioView extends JPanel {

    private static final long serialVersionUID = 1L;

    private final UsuarioController controller;

    private final JTextField txtNome = new JTextField(18);
    private final JTextField txtEmail = new JTextField(18);
    private final JTextField txtTelefone = new JTextField(12);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"ID", "Nome", "E-mail", "Telefone"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);
    private int idSelecionado = 0;

    public UsuarioView(UsuarioController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

    private JPanel criarPainelFormulario() {
        JPanel campos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        campos.add(new JLabel("Nome:"));
        campos.add(txtNome);
        campos.add(new JLabel("E-mail:"));
        campos.add(txtEmail);
        campos.add(new JLabel("Telefone:"));
        campos.add(txtTelefone);

        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnRemover = new JButton("Remover");
        JButton btnLimpar = new JButton("Limpar");
        btnCadastrar.addActionListener(e -> cadastrar());
        btnAtualizar.addActionListener(e -> atualizarUsuario());
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
        for (Usuario u : controller.listar()) {
            modelo.addRow(new Object[] {u.getId(), u.getNome(), u.getEmail(), u.getTelefone()});
        }
    }

    private void carregarSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        idSelecionado = (Integer) modelo.getValueAt(linha, 0);
        txtNome.setText(String.valueOf(modelo.getValueAt(linha, 1)));
        txtEmail.setText(String.valueOf(modelo.getValueAt(linha, 2)));
        txtTelefone.setText(String.valueOf(modelo.getValueAt(linha, 3)));
    }

    private void limparFormulario() {
        idSelecionado = 0;
        txtNome.setText("");
        txtEmail.setText("");
        txtTelefone.setText("");
        tabela.clearSelection();
    }

    private void cadastrar() {
        try {
            controller.cadastrar(txtNome.getText(), txtEmail.getText(), txtTelefone.getText());
            limparFormulario();
            atualizar();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Mensagens.erro(this, ex.getMessage());
        }
    }

    private void atualizarUsuario() {
        if (idSelecionado == 0) {
            Mensagens.erro(this, "Selecione um usuário na tabela para atualizar.");
            return;
        }
        try {
            controller.atualizar(idSelecionado, txtNome.getText(), txtEmail.getText(), txtTelefone.getText());
            limparFormulario();
            atualizar();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Mensagens.erro(this, ex.getMessage());
        }
    }

    private void remover() {
        if (idSelecionado == 0) {
            Mensagens.erro(this, "Selecione um usuário na tabela para remover.");
            return;
        }
        if (!Mensagens.confirmar(this, "Deseja realmente remover este usuário?")) {
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
