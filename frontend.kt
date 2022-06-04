import kotlinx.browser.*
import org.w3c.dom.*

@JsName("comeBackHomePage")
fun comeBackHomePage() {
    window.location.href = "/";
}

@JsName("alertInvalidUser")
fun alertInvalidUser() {
    window.alert("Nome de usuário inválido");
    window.location.href = "/login";
}

@JsName("alertInvalidPassword")
fun alertInvalidPassword() {
    window.alert("Senha inválida");
    window.location.href = "/login";
}
@JsName("alertUserCreatedSuccessfully")
fun alertUserCreatedSuccessfully() {
    window.alert("Usuário criado com sucesso!");
    window.location.href = "/login"
}

@JsName("alertUsernameOrEmailAlreadyRegistered")
fun alertUsernameOrEmailAlreadyRegistered() {
    window.alert("Nome de usuário ou email já cadastrado!");
    window.location.href = "/sign-up"
}

@JsName("removeCookies")
fun removeCookies() {
    document.cookie = "user_name=; path=/";
    document.cookie = "is_logined=; path=/";
    window.location.href = "/";
}

@JsName("removeRecipeUser")
fun removeRecipeUser(titleRecipeUser: String) {
    if(document.cookie.replace(" ", "").split(";")[1] === "is_logined=true") {
        window.location.href = "/my-cookbook/" + document.cookie.trim().split(";")[0].split("=")[1] + "/remove-recipe/${titleRecipeUser}"
    } else {
        window.location.href = "/initial"
    }
}

@JsName("redirectToForm")
fun redirectToForm() {
    if(document.cookie.replace(" ", "").split(";")[1] === "is_logined=true") {
        window.location.href = "/my-cookbook/" + document.cookie.trim().split(";")[0].split("=")[1] + "/form-recipe"
    } else {
        window.location.href = "/initial"
    }
}

fun main() {
    println("Front-end acessado!");
}