package biblioteca.controller;

import biblioteca.dao.EmprestimoDAO;
import biblioteca.dao.UsuarioDAO;
import biblioteca.model.Emprestimo;
import biblioteca.model.Usuario;
import java.util.List;

/**
 * CONTROLLER de Usuario - valida os dados, aplica regras e chama o DAO.
 */
public class UsuarioController {

    private final UsuarioDAO dao = new UsuarioDAO();
    private final EmprestimoDAO emprestimoDAO = new EmprestimoDAO();

    public void cadastrar(String nome, String email, String telefone) {
        Usuario usuario = new Usuario(0, validarNome(nome), validarEmail(email),
                telefone == null ? "" : telefone.trim());
        dao.salvar(usuario);
    }

    public void atualizar(int id, String nome, String email, String telefone) {
        Usuario usuario = dao.buscarPorId(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        usuario.setNome(validarNome(nome));
        usuario.setEmail(validarEmail(email));
        usuario.setTelefone(telefone == null ? "" : telefone.trim());
        dao.salvar(usuario);
    }

    public void remover(int id) {
        for (Emprestimo e : emprestimoDAO.listar()) {
            if (e.getUsuarioId() == id && e.isAtivo()) {
                throw new IllegalStateException("Não é possível remover: o usuário tem empréstimo ativo.");
            }
        }
        dao.remover(id);
    }

    public List<Usuario> listar() {
        return dao.listar();
    }

    private String validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome do usuário.");
        }
        return nome.trim();
    }

    private String validarEmail(String email) {
        if (email == null || !email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }
        return email.trim();
    }
}
