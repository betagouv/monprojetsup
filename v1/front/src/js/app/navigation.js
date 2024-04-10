import * as ui from "../ui/ui";
import * as session from "./session";
import * as app from "./app";

import $ from "jquery";
/** handles navigation between screens  */

const screens = [
  "landing",
  "connect",
  "reset_password",
  "inscription1",
  "inscription2",
  "recherche",
  "groupes",
];

export function setScreen(screen) {
  console.log("setScreen", screen);
  if (screens.includes(screen)) {
    let current_screen = session.getScreen();
    doTransition(current_screen, screen);
    session.saveScreen(screen);
  }
}

export function next() {
  let current_screen = session.getScreen();
  if (current_screen === "inscription1") {
    validateInscription1();
  } else if (current_screen === "inscription2") {
    validateInscription2();
  }
}
export function back() {
  let current_screen = session.getScreen();
  if (current_screen === "inscription2") {
    setScreen("inscription1");
  }
}

function doTransition(old_screen, new_screen) {
  exitScreen(old_screen);
  enterScreen(new_screen);
}

const screen_enter_handlers = {
  landing: async () => await ui.showLandingScreen(),
  recherche: async () => {
    const details = await app.askFormationsDetails();
    await ui.showRecherche(details);
  },
  inscription1: async () => await ui.showInscriptionScreen1(),
  inscription2: async () => await ui.showInscriptionScreen2(),
  groupes: async () => {
    refreshGroupTab(false);
    ui.showGroupsTab();
  },
};
const screen_exit_handlers = {};

function enterScreen(screen) {
  if (screen in screen_enter_handlers && screen_enter_handlers[screen]) {
    screen_enter_handlers[screen]().then(() => {
      $("#nextButton").off().on("click", next);
      $("#backButton").off().on("click", back);
    });
  } else {
    ui.showLandingScreen();
  }
}
function exitScreen(screen) {
  if (screen in screen_exit_handlers) {
    if (screen_exit_handlers[screen]) screen_exit_handlers[screen]().then();
    return;
  }
  //default behaviour: None
}

let accounType = null;
let accessGroupe = null;

function validateInscription1() {
  accessGroupe = $("#inputCreateAccountCodeAcces").val();
  const lyceen = $("#radio-create-account-lyceen").is(":checked");
  const pp = $("#radio-create-account-prof").is(":checked");
  accounType = lyceen ? "lyceen" : "pp";
  if (!lyceen && !pp) {
    $("#inscription1-messages").html(
      `<p class="fr-alert fr-alert--error">Veuillez choisir un type de compte</p>`
    );
  } else if (accessGroupe == null || accessGroupe.length <= 0) {
    $("#inscription1-messages").html(
      `<p class="fr-alert fr-alert--error">Veuillez indiquer un code d'accès</p>`
    );
  } else {
    //récupérer type de compte et mot de passe
    app.validateCodeAcces(accounType, accessGroupe, () => {
      setScreen("inscription2");
    });
  }
}

function validateInscription2() {
  //const accounType = $("#createAccountSelectType").val();
  const nom = $("#champNom").val();
  const prenom = $("#champPrenom").val();
  const identifiant = $("#champIdentifiant").val();
  const mdp = $("#champMdp").val();
  const mdp2 = $("#champMdp2").val();

  const pattern = /^[a-zA-Z0-9+_.\-@]+$/;

  $("#champIdentifiant-messages").empty();
  $("#champMdp-messages").empty();
  $("#champMdp2-messages").empty();
  if (mdp != mdp2) {
    $("#champMdp2-messages").html(
      `<p class="fr-alert fr-alert--error">Les mots de passe ne sont pas identiques.</p>`
    );
  } else if (mdp == accessGroupe) {
    $("#champMdp-messages").html(
      `<p class="fr-alert fr-alert--error">Le mot de passe (privé et personnel) doit être différent du code d'accès au groupe (public).</p>`
    );
  } else if (
    identifiant === undefined ||
    identifiant === null ||
    identifiant.length == 0
  ) {
    $("#champIdentifiant-messages").html(
      `<p class="fr-alert fr-alert--error">Identifiant non renseigné</p>`
    );
  } else if (!pattern.test(identifiant)) {
    $("#champIdentifiant-messages").html(
      `<p class="fr-alert fr-alert--error">Les identifiants autorisés ne comportent que des lettres, des chiffres et les symboles +_.-@</p>`
    );
  } else if (mdp === undefined || mdp === null || mdp.length == 0) {
    $("#champMdp-messages").html(
      `<p class="fr-alert fr-alert--error">Mot de passe non renseigné</p>`
    );
  } else if (false && validEmailNeeded() && !isValidEmail(login)) {
    toast("", "Renseignez une adresse email valide");
  } else {
    $("#champMdp").val("");
    $("#champMdp2").val("");

    app.createAccount({
      nom: nom,
      prenom: prenom,
      type: accounType,
      login: identifiant,
      accesGroupe: accessGroupe,
      password: mdp,
    });
  }
}
