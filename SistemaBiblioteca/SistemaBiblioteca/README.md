# Sistema de Biblioteca (Java + MVC)

Aplicação desktop em Java (Swing) para controlar livros, usuários e empréstimos
de uma biblioteca, com cálculo automático de prazo e multa por atraso.

## Como abrir no Eclipse
1. `File > Import... > General > Existing Projects into Workspace`
2. Marque **Select archive file** e escolha o `.zip` (ou **Select root directory** se já descompactou)
3. Clique em **Finish**
4. Abra `src/biblioteca/Main.java` e use `Run As > Java Application`

> Se o Eclipse reclamar da versão do Java: botão direito no projeto >
> `Properties > Java Build Path > Libraries > JRE System Library > Edit...`
> e escolha o JDK 17 (ou superior) instalado.

## Stack
- Java 17 ou superior (testado com Java 21)
- Eclipse IDE for Java Developers
- Bibliotecas: apenas as do próprio JDK (Swing/AWT, java.time, java.nio.file) - sem dependências externas
- Armazenamento: arquivos CSV (separador `;`) na pasta `dados/`, criada automaticamente

## Estrutura (MVC)
```
src/biblioteca/
  Main.java                 -> inicia o sistema
  model/                    -> MODEL: Livro, Usuario, Emprestimo (regras de prazo e multa)
  dao/                      -> persistência: LivroDAO, UsuarioDAO, EmprestimoDAO, ArquivoCSV
  controller/               -> CONTROLLER: LivroController, UsuarioController, EmprestimoController
  view/                     -> VIEW: MainView, LivroView, UsuarioView, EmprestimoView
dados/                      -> livros.csv, usuarios.csv, emprestimos.csv (gerados ao usar)
```

## Regras de negócio
- Prazo de devolução: 7 dias
- Multa: R$ 1,50 por dia de atraso
- Limite: 3 empréstimos ativos por usuário
- Um livro emprestado não pode ser emprestado de novo nem removido
- Um usuário com empréstimo ativo não pode ser removido

## Dados de exemplo
Na primeira execução (pasta `dados/` vazia) o sistema cadastra 6 livros e 3 usuários
para facilitar a demonstração. Para recomeçar do zero, apague a pasta `dados/`.
