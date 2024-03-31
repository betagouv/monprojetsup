import $ from "jquery";
import * as params from "./../../config/params";
import { toast } from "./../animate/toasts";
import { Modal } from "bootstrap";

import * as app from "../../app/app";

function showLyceesNeeded() {
  const selected = $("#createAccountSelectType").find(":selected").val();
  return false;
  /*
  return (
    //selected === "lyceen" ||
    //selected === "pp" ||
    selected === "proviseur" || selected === "psyen"
  );*/
}

function showClassesNeeded() {
  return false;
  /*
  const selected = $("#createAccountSelectType").find(":selected").val();
  return selected === "lyceen" || selected === "pp";
  */
}

function showAccesGroupeNeeded() {
  const selected = $("#createAccountSelectType").find(":selected").val();
  return true;
  //return selected === "lyceen" || selected === "pp";
}

function validEmailNeeded() {
  const selected = $("#createAccountSelectType").find(":selected").val();
  return selected !== "lyceen";
}

const validEmailRegex =
  /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;

function isValidEmail(login) {
  return login.match(validEmailRegex);
}

function updateAccounCreationMenu() {
  const selected = $("#createAccountSelectType").find(":selected").val();
  let text = "";
  let text2 = "";
  switch (selected) {
    case "lyceen":
      text = `Renseigne le code d'accès à ta classe.
      `;
      text2 = `Indique ton email (ou à défaut un pseudonyme) et le
                    mot de passe de ton choix. 
      Renseigne de préférence ton email plutôt qu'un pseudonyme, sauf si tu ne possèdes pas d'adresse email,
      cela te permettra de retrouver facilement tes données en cas de perte de ton mot de passe.
      `;
      break;
    case "pp":
    case "psyen":
    case "proviseur":
      text = `Veuillez fournir le code d'accès référent à une des classes de votre lycée dont vous êtes responsables. Vous pourrez
      par la suite ajouter d'autres classes si nécessaire.`;
      text2 = `Veuillez fournir un email valide, de préférence votre email professionnel.`;
      break;
    case "demo_lyceen":
    case "demo_referent":
      text =
        "Après confirmation de votre adresse email, votre demande de création de compte sera examinée par un modérateur.";
      break;
    default:
      text = "";
      break;
  }
  $("#texteSpecificAccount").html(text);
  $("#texteSpecificAccount2").html(text2);

  const showMenu = selected != "";
  $("#texteSpecificAccount").toggle(showMenu);
  $("#texteSpecificAccount2").toggle(showMenu);
  $("#inputAccesGroupe").toggle(showAccesGroupeNeeded() && showMenu);
  $("#inputEmailCgu").toggle(showMenu);
  $("#inputPasswordGroup").toggle(showMenu);

  const placeholderEmail =
    selected == "lyceen" ? "Ton email ou pseudonyme..." : "Votre email...";
  const placeholderPAssword =
    selected == "lyceen"
      ? "Choisis ton mot de passe..."
      : "Choisissez votre mot de passe...";
  const placeholderCodeAcces =
    selected == "lyceen"
      ? "Le code d'accès à ta classe..."
      : "Le code d'accès référent...";
  $("#inputEmailCgu").attr("placeholder", placeholderEmail);
  $("#inputPasswordCgu").attr("placeholder", placeholderPAssword);
  $("#inputAccesGroupe").attr("placeholder", placeholderCodeAcces);
}

export function init() {
  updateAccounCreationMenu();

  $("#login-button")
    .off("click")
    .on("click", function () {
      const login = $("#inputEmail").val();
      const password = $("#inputPassword").val();
      //$("#inputEmail").val("");
      $("#inputPassword").val("");
      app.loginHandler(login, password);
    });

  $(".disconnectButton")
    .off("click")
    .on("click", () => {
      app.disconnect();
    });
  $(".toLyceenButton")
    .off("click")
    .on("click", () => {
      app.toLyceen();
    });
  $(".toTeacherButton")
    .off("click")
    .on("click", () => {
      app.toTeacher();
    });

  /******************* CREATE ACCOUNT *******************  */

  const typeAccountSel = $("#createAccountSelectType");
  typeAccountSel.off("change").on("change", updateAccounCreationMenu);

  $("#doCreateAccount")
    .off("click")
    .on("click", () => {
      //login normal, infos supprimées à la fin du parcours
      const accounType = $("#createAccountSelectType").val();
      const login = $("#inputEmailCgu").val();
      const accessGroupe = $("#inputAccesGroupe").val();
      const password = $("#inputPasswordCgu").val();
      const pattern = /^[a-zA-Z0-9+_.\-@]+$/;

      if (password == accessGroupe) {
        toast(
          "",
          "Le mot de passe (privé et personnel) doit être différent du code d'accès au groupe (public)"
        );
      } else if (login === undefined || login === null || login.length == 0) {
        toast("", "Email non renseigné");
      } else if (!pattern.test(login)) {
        toast(
          "",
          "Les logins autorisés ne comportent que des lettres, des chiffres et les symboles +_.-@"
        );
      } else if (
        password === undefined ||
        password === null ||
        password.length == 0
      ) {
        toast("", "Mot de passe non renseigné");
      } else if (password.length < 8) {
        toast("", "Mot de passe trop court (8 caractères minimum)");
      } else if (validEmailNeeded() && !isValidEmail(login)) {
        toast("", "Renseignez une adresse email valide");
      } else {
        //make modal disappaer
        const myModalEl = document.getElementById("createAccountModal");
        const modal = Modal.getInstance(myModalEl); // Returns a Bootstrap modal instance
        modal.hide();

        $("#inputPasswordCgu").val("");

        app.createAccount({
          type: accounType,
          login: login,
          accesGroupe: accessGroupe,
          password: password,
        });
      }
    });
  $("#sendResetPasswordEmail")
    .off("click")
    .on("click", () => {
      const email = $("#sendResetPasswordEmailInputEmail").val();
      if (!isValidEmail(email)) {
        toast("", "Veuillez renseigner une adresse email valide");
      } else {
        app.sendResetPasswordEmail(email);
        /*
        const myModal = new Modal(document.getElementById("oublimdpModal"));
        myModal.hide();*/
      }
    });

  $(".inputPasswordEyeIcon")
    .off("mouseover")
    .on("mouseover", function () {
      $(".inputPasswordEyeIcon").removeClass("bi-eye-slash");
      $(".inputPasswordEyeIcon").addClass("bi-eye");
      for (const ipe of $(".inputPassword")) ipe.type = "text";
    });
  $(".inputPasswordEyeIcon")
    .off("mouseout")
    .on("mouseout", function () {
      $(".inputPasswordEyeIcon").removeClass("bi-eye");
      $(".inputPasswordEyeIcon").addClass("bi-eye-slash");
      for (const ipe of $(".inputPassword")) ipe.type = "password";
    });

  $("#createAccountButton")
    .off("click")
    .on("click", () => {
      //call to server then open modal
      updateAccounCreationMenu();

      const myModalEl = document.querySelector("#createAccountModal");
      const myModal = Modal.getOrCreateInstance(myModalEl);
      myModal.show(document.getElementById("login-modal-contained"));
    });
}
