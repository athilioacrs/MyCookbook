import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.request.*
import java.io.File;
import java.util.stream.*

// Classes
// - classe No
class No<T>(
   val info: T,
   val next: No<T>?
);
// - classe Usuario
class User(
   val nameFull: String, 
   val username: String, 
   val email: String,
   val password: String
);
// - classe Receita
class Recipe(
   val title: String,
   val preparationTime: String,
   val ingredients: No<String>?,
   val preparationMode: String
);

// ---------------------------------------------------------------------------------------------------

// Funções para manipular os dados guardados no arquivo users.txt referente aos usuários cadastrados
fun createUser(user: User, refDb: File) {
   // se dados já estão cadastrados - erro
   //se dados ainda não foram cadastrados - cria usuário
    refDb.appendText("${user.nameFull}|${user.username}|${user.email}|${user.password}\n");
}

fun readUsers(listUsers: List<String>): No<User>? {
   if (listUsers.size == 0) {
      return null;
   } else {
      val infosUser = listUsers[0].split("|");
      val list = listUsers.drop(1);
      return No(
         User(infosUser[0], infosUser[1], infosUser[2], infosUser[3]),
         readUsers(list)
      );
   }
}

fun isRegisteredEmail(email: String, no: No<User>?): Boolean {
   if (no == null) {
      return false;
   } else if (no.info.email == email) {
      return true;
   } else {
      return isUser(email, no.next);
   }
}

fun isUser(username: String, no: No<User>?): Boolean {
   if (no == null) {
      return false;
   } else if (no.info.username == username) {
      return true;
   } else {
      return isUser(username, no.next);
   }
}

fun searchUserData(username: String, no: No<User>?): User {
   // só vai ser usada sabendo que o usuário realmente existe
   if (no!!.info.username == username) {
      return no.info;
   } else {
      return searchUserData(username, no.next);
   }
}

// ---------------------------------------------------------------------------------------------------

// Funções para manipular os dados guardados nos arquivos recipes-username.txt referentes as receitas salvas
fun createRecipe(recipe: Recipe, refDb: File) {
   fun listForLine(list: No<String>?): String {
      if(list!!.next == null) {
         return "${list.info}";
      } else {
         return "${list.info}|" + listForLine(list.next);
      }
   }

   refDb.appendText("${recipe.title}\n${recipe.preparationTime}\n${listForLine(recipe.ingredients)}\n${recipe.preparationMode}\n");
}

fun createTableUserRecipes(username: String) {
   val nameFile = "static/db-fake/recipes-${username}.txt";
   val tableFake = File(nameFile);
   if (!tableFake.isFile()) {
      tableFake.createNewFile();
   }
}

fun lineForList(list: List<String>): No<String>? {
   if(list.size == 0) {
      return null;
   } else {
      val line = list[0];
      val newList = list.drop(1);
      return No(line, lineForList(newList));
   }
}

fun readUserRecipes(listRecipes:List<String>): No<Recipe>? {
   if (listRecipes.size == 0) {
      return null;
   } else {
      val infosRecipe = listOf(
         listRecipes[0],
         listRecipes[1],
         listRecipes[2],
         listRecipes[3]
      );
      val list = listRecipes.drop(4);
      return No(
         Recipe(
            infosRecipe[0], 
            infosRecipe[1], 
            lineForList(infosRecipe[2].split("|")), 
            infosRecipe[3]
         ),
         readUserRecipes(list)
      );
   }
}

fun searchRecipe(titleRecipe: String, listRecipes: No<Recipe>?): Recipe {
   // só vai ser usada sabendo que a receita realmente existe
   if (listRecipes!!.info.title == titleRecipe) {
      return listRecipes.info;
   } else {
      return searchRecipe(titleRecipe, listRecipes.next);
   }
}

fun deleteRecipe(titleRecipe: String, refDb: File) {
   fun removeRecipe(titleRecipe: String, no: No<Recipe>?): No<Recipe>? {
      if(no == null) {
         return null;
      } else if(no.info.title == titleRecipe) {
         return removeRecipe(titleRecipe, no.next);
      } else {
         return No(no.info, removeRecipe(titleRecipe, no.next));
      }
   }

   fun createNewRecipesList(no: No<Recipe>?) {
      if(no != null) {
         createRecipe(no.info, refDb);
         createNewRecipesList(no.next);
      }
   }

   val recipeList = readUserRecipes(refDb.readLines());
   refDb.writeText("");
   val newRecipeList = removeRecipe(titleRecipe, recipeList);
   createNewRecipesList(newRecipeList);
}

// ---------------------------------------------------------------------------------------------------

// Funções que retornam código HTML como string
// - head
fun componentHead(title: String, pathStyle: String): String {
   return """
      <!DOCTYPE html>
      <html lang="pt-br">
      <head>
         <meta charset="UTF-8">
         <meta http-equiv="X-UA-Compatible" content="IE=edge">
         <meta name="viewport" content="width=device-width, initial-scale=1.0">

         <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon">

         <link rel="stylesheet" href="${pathStyle}">

         <title>My cookbook | ${title}</title>
      </head>
   """;
}
// -body
fun componentBody(body: String, scriptExtra: String = ""): String {
   return """
      <body>
         ${body}
         <script src="https://cdn.jsdelivr.net/npm/kotlin@1.6.21/kotlin.min.js"></script>
         <script src="/js/main.js"></script>
         <script>
            ${scriptExtra}
         </script>
      </body>
      </html>
   """
}

fun componentHeader(): String {
   return """
      <header>
        <div class="container">
            <h2>My cookbook</h2>
        </div>
        <div class="container">
            sair
        </div>
      </header> 
   """;
}

fun componentHeaderUserPage(): String {
   return """
      <header class="header-main">
        <div class="container">
            <h2>My cookbook</h2>
        </div>
        <div class="container">
            <a class="link-exit" href="main.removeCookies()">sair</a>
        </div>
      </header>
   """
}
// - footer
fun componentFooter(): String {
   return """
      <footer>
        <div class="container">
            <p>
                Projeto desenvolvido em prol de umas das notas da disciplina de Lógica de programação funcional.
            </p>
            <p>
                Athílio Cavalcanti | aacrs@ecomp.poli.br
            </p>
        </div>
      </footer>
   """
}

fun componentBtnComebackHomePage(): String {
   return """
      <div class="btn-comeback" onclick="main.comeBackHomePage()">
        <img src="imgs/icon-home.png" alt="ícone de casa">
      </div>
   """;
}

fun componentFormLogin(): String {
   return """
      <main>
        <h1>My cookbook</h1>
        <form action="/authentication" method="post" id="sign-in">
            <div class="container">
                <label for="username">Nome de usuário</label>
                <input
                    type="text"
                    name="username" 
                    id="username" 
                    autocomplete="off"
                    placeholder="Digite seu nome de usuário"
                    required
                >
            </div>
            <div class="container">
                <label for="password">Senha</label>
                <input 
                    type="password" 
                    name="password" 
                    id="password" 
                    placeholder="Digite sua senha"
                    required
                >
            </div>
            <button form="sign-in" type="submit">entrar</button>
            <p>Ainda não tem uma conta? <span><a href="/sign-up">Cadastre-se aqui!</a></span></p>
        </form>
      </main>
   """;
}

fun componentFormSignUp(): String {
   return """
      <main>
        <h1>My cookbook</h1>
        <form action="/registration" method="post" id="sign-up">
            <div class="container">
                <label for="name">Nome completo</label>
                <input
                    type="text"
                    name="name" 
                    id="name" 
                    pattern="[a-zà-öù-üA-ZÀ-ÄÇ-ÏÑ-ÖÙ-Ü\- ]+$"
                    title="Números e caracteres especiais não são permitidos."
                    autocomplete="off"
                    placeholder="Digite seu nome"
                    required
                >
            </div>
            <div class="container">
                <label for="username">Nome de usuário</label>
                <input
                    type="text"
                    name="username" 
                    id="username" 
                    pattern="[_\.+\-!0-9a-zA-Z]+$"
                    title="Apenas números, letras e os caracteres especiais +, -, ., _ e ! são permitidos."
                    autocomplete="off"
                    placeholder="Digite seu nome de usuário"
                    required
                >
            </div>
            <div class="container">
                <label for="email">Email</label>
                <input
                    type="email"
                    name="email" 
                    id="email" 
                    placeholder="Digite seu email"
                    required
                >
            </div>
            <div class="container">
                <label for="password">Senha</label>
                <input 
                    type="password" 
                    name="password" 
                    id="password" 
                    minlength="6"
                    maxlength="18"
                    placeholder="Digite sua senha"
                    required
                >
            </div>
            <button form="sign-up" type="submit">cadastrar</button>
            <p>Já possui uma conta? <span><a href="/login">Entre aqui!</a></span></p>
        </form>
      </main>
   """
}

fun componentFormRecipe(pathMethod: String): String {
   return ("""
      <div class="form-recipe">
         <h3>Adicione uma nova receita</h3>
         <form action="${pathMethod}/save-recipe" method="post" id="new-recipe">
               <div class="container">
                  <label for="title">Título da receita</label>
                  <input
                     type="text"
                     name="title" 
                     id="title" 
                     autocomplete="off"
                     placeholder="Digite o título da receita"
                     required
                  >
               </div>
               <div class="container">
                  <label for="time">Tempo de preparo</label>
                  <input
                     type="text"
                     name="time" 
                     id="time" 
                     autocomplete="off"
                     placeholder="Digite o tempo de preparo. Ex: '35min'"
                     required
                  >
               </div>
               <div class="container">
                  <label for="ingredients">Ingredientes</label>
                  <input
                     name="ingredients" 
                     id="ingredients" 
                     autocomplete="off"
                     placeholder="Digite os ingredientes separando-os por ';'. Ex: 3 xícaras de farinha de trigo;2 xícaras de açúcar;3 ovos;..."
                     required
                  >
               </div>
               <div class="container">
                  <label for="preparation">Modo de preparo</label>
                  <input
                     name="preparation" 
                     id="preparation" 
                     autocomplete="off"
                     placeholder="Digite o modo de preparo separando cada passo por ';'"
                     required
                  >
               </div>
               <div id="btns-form">
                  <button form="new-recipe" type="submit">salvar</button>
                  <div id="cancel-recipe" onclick="main.comeBackHomePage()">cancelar</div>
               </div>
         </form>
      </div>
   """);
}

fun componentListItens(refDb: File): String {
   val listItens = refDb.readLines().fold(""){acc, info -> acc + ("<li>" + info + "</li>\n")};
   return "<ul>\n" + listItens + "</ul>\n";
}

fun componentBodyUserPage(childComponent: String, scriptExtra: String = ""): String {
   return """
      <header class="header-main">
        <div class="container">
            <h2>My cookbook</h2>
        </div>
        <div class="container">
            <a id="add-recipe" onclick="main.redirectToForm()">+ adicionar receita</a>
            <a class="link-exit" onclick="main.removeCookies()">sair</a>
        </div>
      </header>

      <body>
         <div id="render-area">
            ${childComponent}
         </div>
         <script src="https://cdn.jsdelivr.net/npm/kotlin@1.6.21/kotlin.min.js"></script>
         <script src="/js/main.js"></script>
         <script>
            ${scriptExtra}
         </script>
      </body>

      <footer>
        <div class="container">
            <p>
                Projeto desenvolvido em prol de umas das notas da disciplina de Lógica de programação funcional.
            </p>
            <p>
                Athílio Cavalcanti | aacrs@ecomp.poli.br
            </p>
        </div>
      </footer>
      </html>
   """
}

fun componentListRecipes(username: String, refDb: File): String {
   fun listRecipes(no: No<Recipe>?): String {
      if(no == null) {
         return "";
      } else {
         return (
            """
               <div class="recipe">
                <div class="container">
                    <h3><a href="/my-cookbook/${username}/${no.info.title}">${no.info.title}</a></h3>
                    <p>${no.info.preparationTime}</p>
                </div>
                <div>
                    <img 
                        src="/imgs/icon-trash-can.png" 
                        alt="icone lata de lixo" 
                        title="excluir receita"
                        onclick="main.removeRecipeUser('${no.info.title}')"
                    >
                </div>
            </div>
            """
         ) + listRecipes(no.next);
      }
   }

   if (refDb.isFile() && refDb.readLines().size > 0) {
      val listUserRecipes = readUserRecipes(refDb.readLines());

      return """
         <div class="recipes">
            ${listRecipes(listUserRecipes)}
        </div>
      """
   } else {
      return """
         <div class="recipes">
            <p id="paragraph-alone">Ainda não há receitas cadastradas</p>
        </div>
      """
   }
}

fun componentOpenRecipe(recipe: Recipe): String {

   fun listIngredients(no: No<String>?): String {
      if(no!!.next == null) {
         return "<li>${no.info}</li>";
      } else {
         return "<li>${no.info}</li>\n" + listIngredients(no.next)
      }
   }

   fun listPaces(str: String): String {
      val strList = str.split(";");
      return strList.fold("") { count, pace -> count + "<li><p>${pace}<p></li>" };
   }

   return """
      <div class="recipe-open">
            <header>
                <a class="btn-come-back">
                    <img src="/imgs/back-arrow.png" alt="seta apontando para esquerda" onclick="main.comeBackHomePage()" title="voltar">
                </a>
                <div class="container">
                    <h3>${recipe.title}</h3>
                    <p>Tempo aproximado de preparo: ${recipe.preparationTime}</p>
                </div>
            </header>
            <div class="body-recipe">
                <h4>Ingredientes</h4>
                    <div class="container">
                        <ul>
                           ${listIngredients(recipe.ingredients)}
                        </ul>
                    </div>
                <h4>Modo de preparo</h4>
                <div class="container">
                     <ol class="preparation-mode">
                           ${listPaces(recipe.preparationMode)}
                     </ol>
                </div>
            </div>
        </div>
   """
}

fun componentLoading(): String {
   return """
      <main>
         <img src="imgs/loader-metal.gif">
      </main>
   """
}

// ---------------------------------------------------------------------------------------------------

fun main() {
   // Carregando o aquivo que guarda os dados dos usuários cadatrados
   val users = File("static/db-fake/users.txt");
   
   println("iniciando servidor...")

   embeddedServer(Netty, port = 3000) {
      routing {
         // Rotas
         route("/", HttpMethod.Get) {
            handle {
               val bodyPage = componentLoading();
               val scriptExtra = """
                  if(document.cookie.replace(" ", "").split(";")[1] === "is_logined=true") {
                     window.location.href = "/my-cookbook/" + document.cookie.replace(" ", "").split(";")[0].split("=")[1]
                  } else {
                     window.location.href = "/initial"
                  }
               """
               val page = (
                  componentHead("Carregando...", "/styles/loader-style.css") +
                  componentBody(bodyPage, scriptExtra)
               )
               call.respondText(page, ContentType.Text.Html)
            }
         }

         route("/logined", HttpMethod.Get) {
            handle {
               call.respondRedirect("/my-cookbook")
            }
         }
         
         route("/authentication", HttpMethod.Post) {
            handle {
               val parameters = call.receiveParameters()
               val existingUser = isUser(
                  parameters["username"] as String, 
                  readUsers(users.readLines())
               )
               if (existingUser) {
                  // se usuario for válido
                  val user = searchUserData(parameters["username"] as String, readUsers(users.readLines()));
                  if(user.password == (parameters["password"] as String)) {
                     call.response.cookies.append("user_name", parameters["username"] as String)
                     call.response.cookies.append("is_logined", "true")
                     call.respondRedirect("/")
                  } else {
                     // caso senha errada
                     val scriptExtra = """
                        main.alertInvalidPassword();
                     """
                     val page = componentBody("", scriptExtra)
                     call.respondText(page, ContentType.Text.Html)
                     }  
               } else { 
                  // se usuário inválido
                  val scriptExtra = """
                     main.alertInvalidUser();
                  """
                  val page = componentBody("", scriptExtra)
                  call.respondText(page, ContentType.Text.Html)
               }
            }
         }

         route("/registration", HttpMethod.Post) {
            handle {
               val parameters = call.receiveParameters()
               val existingUser = isUser(
                  parameters["username"] as String, 
                  readUsers(users.readLines())
               )
               val isRegisteredEmail = isRegisteredEmail(
                  parameters["email"] as String, 
                  readUsers(users.readLines())
               )
               if(!existingUser && !isRegisteredEmail) {
                  // se os dados ainda não foram cadastrados
                  val newUser = User(
                     parameters["name"] as String, 
                     parameters["username"] as String, 
                     parameters["email"] as String, 
                     parameters["password"] as String
                  )

                  createUser(newUser,users)

                  createTableUserRecipes(parameters["username"] as String)

                  val scriptExtra = """
                        main.alertUserCreatedSuccessfully();
                  """
                  val page = componentBody("", scriptExtra)
                  call.respondText(page, ContentType.Text.Html)
               }
               else {
                  // se o email ou nome de usuário já forem cadastrados
                  val scriptExtra = """
                        main.alertUsernameOrEmailAlreadyRegistered();
                  """
                  val page = componentBody("", scriptExtra)
                  call.respondText(page, ContentType.Text.Html)
               }
            }
         }

         route("/login", HttpMethod.Get) {
            handle {
               val bodyPage = componentBtnComebackHomePage() + componentFormLogin()
               val page = componentHead("login", "/styles/styles-login-signup.css") + componentBody(bodyPage)
               call.respondText(page, ContentType.Text.Html)
            }
         }

         route("/sign-up", HttpMethod.Get) {
            handle {
               val bodyPage = componentBtnComebackHomePage() + componentFormSignUp()
               val page = componentHead("Cadastro", "/styles/styles-login-signup.css") + componentBody(bodyPage)
               call.respondText(page, ContentType.Text.Html)
            }
         }

         route("/my-cookbook/{user}", HttpMethod.Get) {
            handle {
               val user = call.parameters["user"] as String
      
               val page = (
                  componentHead("My cookbook", "/styles/styles.css") +
                  componentBodyUserPage(
                     componentListRecipes(user,File("static/db-fake/recipes-${user}.txt"))
                  )
               )
               call.respondText(page, ContentType.Text.Html)
            }
         }

         route("/my-cookbook/{user}/{title-recipe}", HttpMethod.Get) {
            handle {
               val user = call.parameters["user"] as String
               val titleRecipe = call.parameters["title-recipe"] as String

               val page = (
                  componentHead("${titleRecipe}", "/styles/styles.css") +
                  componentBodyUserPage(
                     componentOpenRecipe(
                        searchRecipe(
                           "${titleRecipe}",
                           readUserRecipes(File("static/db-fake/recipes-${user}.txt").readLines())
                        )
                     )
                  )
               )
               call.respondText(page, ContentType.Text.Html)
            }
         }

         route("my-cookbook/{user}/form-recipe", HttpMethod.Get) {
            handle {
               val user = call.parameters["user"] as String
               val bodyPage = componentHeaderUserPage() + componentFormRecipe("../"+user) + componentFooter();
               val page = (
                  componentHead("Cadastrar receita", "/styles/styles.css") +
                  componentBody(bodyPage)
               )
               call.respondText(page, ContentType.Text.Html)
            }
         }

         route("my-cookbook/{user}/save-recipe", HttpMethod.Post){
            handle{
               val user = call.parameters["user"] as String
               val parameters = call.receiveParameters()
               val ingredientsList = lineForList((parameters["ingredients"] as String).split(";"))

               val newRecipe = Recipe(
                     parameters["title"] as String, 
                     parameters["time"] as String, 
                     ingredientsList, 
                     parameters["preparation"] as String
               )

               createRecipe(newRecipe, File("static/db-fake/recipes-${user}.txt"))

               //redirecionar
               call.respondRedirect("/my-cookbook/${user}")
            }
         }

         route("my-cookbook/{user}/remove-recipe/{title-recipe}", HttpMethod.Get) {
            handle {
               val user = call.parameters["user"] as String
               val titleRecipe = call.parameters["title-recipe"] as String
               deleteRecipe(titleRecipe, File("static/db-fake/recipes-${user}.txt"))
               call.respondRedirect("/")
            }
         }

         // Rota generica
         route("/*", HttpMethod.Get) {
            handle {
               call.respondRedirect("/404")
            }
         } 

         // Rotas estáticas
         static("/initial") {
            files("static/")
            default("static/index.html")
         }

         static("/404") {
            files("static/")
            default("static/404.html")
         }

         // Rotas estáticas para estilos, js e imagens
         static("/styles") {
            files("static/css")
         }

         static("/js") {
            files("static/js")
         }

         static("/imgs") {
            files("static/images")
         }
      }
   }.start(wait=true)
}
