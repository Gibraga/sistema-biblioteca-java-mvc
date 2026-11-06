package biblioteca.dao;

import biblioteca.model.Usuario;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (acesso a dados) de Usuario - persiste em dados/usuarios.csv.
 */
public class UsuarioDAO {

    private final Path arquivo = Paths.get("dados", "usuarios.csv");

    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        for (String[] c : ArquivoCSV.ler(arquivo)) {
            lista.add(new Usuario(Integer.parseInt(c[0]), c[1], c[2], c[3]));
        }
        return lista;
    }

    public Usuario buscarPorId(int id) {
        for (Usuario u : listar()) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    /** Insere (id = 0) ou atualiza um usuário. */
    public void salvar(Usuario usuario) {
        List<Usuario> lista = listar();
        if (usuario.getId() == 0) {
            usuario.setId(proximoId(lista));
            lista.add(usuario);
        } else {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getId() == usuario.getId()) {
                    lista.set(i, usuario);
                }
            }
        }
        gravar(lista);
    }

    public void remover(int id) {
        List<Usuario> lista = listar();
        lista.removeIf(u -> u.getId() == id);
        gravar(lista);
    }

    private int proximoId(List<Usuario> lista) {
        int maior = 0;
        for (Usuario u : lista) {
            maior = Math.max(maior, u.getId());
        }
        return maior + 1;
    }

    private void gravar(List<Usuario> lista) {
        List<String[]> linhas = new ArrayList<>();
        for (Usuario u : lista) {
            linhas.add(new String[] {
                String.valueOf(u.getId()),
                ArquivoCSV.limpar(u.getNome()),
                ArquivoCSV.limpar(u.getEmail()),
                ArquivoCSV.limpar(u.getTelefone())
            });
        }
        ArquivoCSV.escrever(arquivo, linhas);
    }
}
