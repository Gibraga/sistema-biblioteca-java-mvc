package biblioteca.view;

import biblioteca.controller.EmprestimoController;
import biblioteca.controller.LivroController;
import biblioteca.controller.UsuarioController;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * VIEW - janela principal, organizada em abas (Livros, Usuários e Empréstimos).
 */
public class MainView extends JFrame {

    private static final long serialVersionUID = 1L;

    public MainView(LivroController livroController, UsuarioController usuarioController,
                    EmprestimoController emprestimoController) {
        super("Sistema de Biblioteca (MVC)");

        LivroView livros = new LivroView(livroController);
        UsuarioView usuarios = new UsuarioView(usuarioController);
        EmprestimoView emprestimos = new EmprestimoView(emprestimoController, livroController, usuarioController);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Livros", livros);
        abas.addTab("Usuários", usuarios);
        abas.addTab("Empréstimos", emprestimos);
        // Ao trocar de aba, as telas se atualizam com os dados mais recentes.
        abas.addChangeListener(e -> {
            livros.atualizar();
            usuarios.atualizar();
            emprestimos.atualizar();
        });

        setContentPane(abas);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 560);
        setLocationRelativeTo(null);
    }
}
