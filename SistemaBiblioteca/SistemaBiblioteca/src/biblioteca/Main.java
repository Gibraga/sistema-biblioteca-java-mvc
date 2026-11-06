package biblioteca;

import biblioteca.controller.EmprestimoController;
import biblioteca.controller.LivroController;
import biblioteca.controller.UsuarioController;
import biblioteca.view.MainView;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de entrada do sistema. Cria os Controllers e abre a janela principal.
 * Execute esta classe (Run As > Java Application) para iniciar.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignorada) {
                // mantém o visual padrão do Swing
            }

            LivroController livroController = new LivroController();
            UsuarioController usuarioController = new UsuarioController();
            EmprestimoController emprestimoController = new EmprestimoController();

            carregarDadosDeExemplo(livroController, usuarioController);

            new MainView(livroController, usuarioController, emprestimoController).setVisible(true);
        });
    }

    /** Na primeira execução (pasta "dados" vazia), cadastra alguns registros para demonstração. */
    private static void carregarDadosDeExemplo(LivroController livros, UsuarioController usuarios) {
        if (!livros.listar().isEmpty() || !usuarios.listar().isEmpty()) {
            return;
        }
        livros.cadastrar("Dom Casmurro", "Machado de Assis", "1899");
        livros.cadastrar("O Cortiço", "Aluísio Azevedo", "1890");
        livros.cadastrar("Capitães da Areia", "Jorge Amado", "1937");
        livros.cadastrar("Vidas Secas", "Graciliano Ramos", "1938");
        livros.cadastrar("A Hora da Estrela", "Clarice Lispector", "1977");
        livros.cadastrar("Memórias Póstumas de Brás Cubas", "Machado de Assis", "1881");

        usuarios.cadastrar("Ana Souza", "ana.souza@email.com", "(11) 91234-5678");
        usuarios.cadastrar("Carlos Lima", "carlos.lima@email.com", "(11) 98765-4321");
        usuarios.cadastrar("Beatriz Alves", "beatriz.alves@email.com", "(11) 97777-1234");
    }
}
