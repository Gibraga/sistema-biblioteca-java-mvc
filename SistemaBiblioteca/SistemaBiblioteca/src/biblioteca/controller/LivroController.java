package biblioteca.controller;

import biblioteca.dao.LivroDAO;
import biblioteca.model.Livro;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CONTROLLER de Livro - valida os dados vindos da View,
 * aplica as regras de negócio e chama o DAO.
 */
public class LivroController {

    private final LivroDAO dao = new LivroDAO();

    public void cadastrar(String titulo, String autor, String anoTexto) {
        Livro livro = new Livro(0, validarTexto(titulo, "título"), validarTexto(autor, "autor"),
                validarAno(anoTexto), true);
        dao.salvar(livro);
    }

    public void atualizar(int id, String titulo, String autor, String anoTexto) {
        Livro livro = dao.buscarPorId(id);
        if (livro == null) {
            throw new IllegalArgumentException("Livro não encontrado.");
        }
        livro.setTitulo(validarTexto(titulo, "título"));
        livro.setAutor(validarTexto(autor, "autor"));
        livro.setAno(validarAno(anoTexto));
        dao.salvar(livro);
    }

    public void remover(int id) {
        Livro livro = dao.buscarPorId(id);
        if (livro == null) {
            throw new IllegalArgumentException("Livro não encontrado.");
        }
        if (!livro.isDisponivel()) {
            throw new IllegalStateException("Não é possível remover: o livro está emprestado.");
        }
        dao.remover(id);
    }

    public List<Livro> listar() {
        return dao.listar();
    }

    public List<Livro> listarDisponiveis() {
        List<Livro> resultado = new ArrayList<>();
        for (Livro l : dao.listar()) {
            if (l.isDisponivel()) {
                resultado.add(l);
            }
        }
        return resultado;
    }

    /** Busca por parte do título ou do autor (sem diferenciar maiúsculas). */
    public List<Livro> buscar(String termo) {
        String t = termo.trim().toLowerCase();
        List<Livro> resultado = new ArrayList<>();
        for (Livro l : dao.listar()) {
            if (l.getTitulo().toLowerCase().contains(t) || l.getAutor().toLowerCase().contains(t)) {
                resultado.add(l);
            }
        }
        return resultado;
    }

    private String validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o " + campo + " do livro.");
        }
        return valor.trim();
    }

    private int validarAno(String texto) {
        int ano;
        try {
            ano = Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O ano deve ser um número.");
        }
        if (ano < 1000 || ano > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("Informe um ano entre 1000 e " + LocalDate.now().getYear() + ".");
        }
        return ano;
    }
}
