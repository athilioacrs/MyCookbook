# My cookbook
Projeto de Aplicação Web usando Ktor, desenvolvido em prol de uma das notas da disciplina de Linguagem de Programação Funcional do curso de engenharia da computação da Escola Politécnica de Pernambuco.

O projeto se trata de um site que permite o cadastro de usuários, e após o registro eles poderão salvar receitas culinárias para consutarem sempre que quiserem.

Os arquivos e pastas deste projeto:

* __servidor.kt__: Código que executa o servidor Ktor além de contém classes e funções que auxiliam na manipulação de dados, mais algumas funções que ajudam a construir páginas que são enviadas como resposta para certas requisições
* __frontend.kt__: Código que roda no navagador (FrontEnd)
* __static__: Diretório de conteúdo estático
* __static/index.html__: Página HTML que é carregada ao acessar a rota raiz da aplicação
* __static/404.html__: Página HTML que é enviada caso o usuário tente acessar uma rota inexistente
* __static/css/404.css__: Arquivo CSS que estiliza a página 404.html
* __static/css/loader-login-signup.css__: Arquivo CSS que estiliza as páginas de login e cadastro que são geradas pelo backend
* __static/css/styles.css__: Arquivo CSS que estiliza a página index.html e as demais páginas que são geradas pelo backend
* __static/db-fake__: Diretório que contém arquivos .txt que auxiliam na persistência de informações
* __static/images__: Diretório que contém as imagens usadas na aplicação
* __static/js__: Diretório reservado para arquivos .js
* __static/js/main.js__: Código de frontend.kt compilador para Javascript


Para compilar o projeto utilize os seguintes comandos:

Compila o servidor / página BackEnd:
```
kotlinc -cp ktor.jar:. servidor.kt
```

Compila o código do FrontEnd:
```
kotlinc-js FrontEnd.kt -output static/FrontEnd.js
```

Executa o servidor Web:
```
kotlin -cp ktor.jar:. ServidorKt
```
Observações: 
1) Provavelmente no windows o ":" deve ser substituido por ";"
2) Importar o arquivo "jar" pode gerar um warning de conflito de versões. Este warning pode ser ignorado.

