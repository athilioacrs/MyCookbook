if (typeof kotlin === 'undefined') {
  throw new Error("Error loading module 'main'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'main'.");
}
var main = function (_, Kotlin) {
  'use strict';
  var replace = Kotlin.kotlin.text.replace_680rmw$;
  var split = Kotlin.kotlin.text.split_ip8yn$;
  var println = Kotlin.kotlin.io.println_s8jyv4$;
  var throwCCE = Kotlin.throwCCE;
  var trim = Kotlin.kotlin.text.trim_gw00vp$;
  function comeBackHomePage() {
    window.location.href = '/';
  }
  function alertInvalidUser() {
    window.alert('Nome de usu\xE1rio inv\xE1lido');
    window.location.href = '/login';
  }
  function alertInvalidPassword() {
    window.alert('Senha inv\xE1lida');
    window.location.href = '/login';
  }
  function alertUserCreatedSuccessfully() {
    window.alert('Usu\xE1rio criado com sucesso!');
    window.location.href = '/login';
  }
  function alertUsernameOrEmailAlreadyRegistered() {
    window.alert('Nome de usu\xE1rio ou email j\xE1 cadastrado!');
    window.location.href = '/sign-up';
  }
  function removeCookies() {
    document.cookie = 'user_name=; path=/';
    document.cookie = 'is_logined=; path=/';
    window.location.href = '/';
  }
  function removeRecipeUser(titleRecipeUser) {
    if (split(replace(document.cookie, ' ', ''), [';']).get_za3lpa$(1) === 'is_logined=true') {
      var tmp$ = window.location;
      var $receiver = document.cookie;
      var tmp$_0;
      tmp$.href = '/my-cookbook/' + split(split(trim(Kotlin.isCharSequence(tmp$_0 = $receiver) ? tmp$_0 : throwCCE()).toString(), [';']).get_za3lpa$(0), ['=']).get_za3lpa$(1) + ('/remove-recipe/' + titleRecipeUser);
    } else {
      window.location.href = '/initial';
    }
  }
  function redirectToForm() {
    if (split(replace(document.cookie, ' ', ''), [';']).get_za3lpa$(1) === 'is_logined=true') {
      var tmp$ = window.location;
      var $receiver = document.cookie;
      var tmp$_0;
      tmp$.href = '/my-cookbook/' + split(split(trim(Kotlin.isCharSequence(tmp$_0 = $receiver) ? tmp$_0 : throwCCE()).toString(), [';']).get_za3lpa$(0), ['=']).get_za3lpa$(1) + '/form-recipe';
    } else {
      window.location.href = '/initial';
    }
  }
  function redirectInitialPage() {
    window.location.href = '/';
  }
  function main() {
    println('Front-end acessado!');
  }
  _.comeBackHomePage = comeBackHomePage;
  _.alertInvalidUser = alertInvalidUser;
  _.alertInvalidPassword = alertInvalidPassword;
  _.alertUserCreatedSuccessfully = alertUserCreatedSuccessfully;
  _.alertUsernameOrEmailAlreadyRegistered = alertUsernameOrEmailAlreadyRegistered;
  _.removeCookies = removeCookies;
  _.removeRecipeUser = removeRecipeUser;
  _.redirectToForm = redirectToForm;
  _.redirectInitialPage = redirectInitialPage;
  _.main = main;
  main();
  Kotlin.defineModule('main', _);
  return _;
}(typeof main === 'undefined' ? {} : main, kotlin);
