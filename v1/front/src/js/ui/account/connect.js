import $ from "jquery";
import { toast } from "./../animate/toasts";
import { Modal } from "bootstrap";

import * as app from "../../app/app";

function errorHtml(id, errorMsg) {
  if (errorMsg === undefined || errorMsg === null || errorMsg.length == 0) {
    $(`#id`).empty();
  } else {
    $(`#id`).html(
      `<p class="fr-message fr-message--error" id="radio-error-message-error">
    ${errorMsg}
          </p>`
    );
  }
}
export function init() {
  updateAccounCreationMenu();

  $("#sendResetPasswordEmail")
    .off("click")
    .on("click", () => {
      const email = $("#sendResetPasswordEmailInputEmail").val();
      if (!isValidEmail(email)) {
        //
        errorHtml(
          "sendResetPasswordEmailInputEmail-messages",
          "Veuillez renseigner une adresse email valide"
        );
        //toast("", "Veuillez renseigner une adresse email valide");
      } else {
        errorHtml("sendResetPasswordEmailInputEmail-messages", "");
        app.sendResetPasswordEmail(email);
        /*
        const myModal = new Modal(document.getElementById("oublimdpModal"));
        myModal.hide();*/
      }
    });

  $("#doCreateAccount")
    .off("click")
    .on("click", () => {
      //login normal, infos supprimées à la fin du parcours
      errorHtml("createAccount-profil-messages", "");
      errorHtml("createAccount-code-acces-messages", "");

      if (
        $("#radio-create-account-lyceen").unchecked &&
        $("#radio-create-account-prof").unchecked
      ) {
        errorHtml(
          "createAccount-profil-messages",
          "Veuillez sélectionner un profil"
        );
        return;
      }
      if ($("#radio-create-account-lyceen").unchecked) {
        accounType = "lyceen";
      } else {
        accounType = "pp";
      }

      const accessGroupe = $("#inputCreateAccountCodeAcces").val();
      if (
        accessGroupe === undefined ||
        accessGroupe === null ||
        accessGroupe.length == 0
      ) {
        errorHtml(
          "createAccount-code-acces-messages",
          "Veuillez renseigner le code d'accès à votre groupe"
        );
        return;
      }
      //todo ecran suivant

      //const accounType = $("#createAccountSelectType").val();
      const login = $("#inputEmailCgu").val();

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

  $("#createAccountButton")
    .off("click")
    .on("click", () => {
      //call to server then open modal
      updateAccounCreationMenu();

      const myModalEl = document.querySelector("#createAccountModal");
      const myModal = Modal.getOrCreateInstance(myModalEl);
      myModal.show(document.getElementById("login-modal-contained"));
    });

  $("#login-button")
    .off("click")
    .on("click", function () {
      const login = $("#champEmail").val();
      const password = $("#champMotDePasse").val();
      $("#champMotDePasse").val("");
      app.loginHandler(login, password);
    });
}

//called on account change type

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
  $("#inputAccesGroupe").toggle(showMenu);
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
