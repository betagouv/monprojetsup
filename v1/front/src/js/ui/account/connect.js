import $ from "jquery";
import { toast } from "./../animate/toasts";
import { Modal } from "bootstrap";
import * as nav from "../../app/navigation";

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
  /*
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
      }
    });*/

  $("#createAccountButton")
    .off("click")
    .on("click", () => {
      nav.setScreen("inscription1");
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
/*
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
*/
