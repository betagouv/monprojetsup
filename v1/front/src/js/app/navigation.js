import * as ui from "../ui/ui";
import * as session from "./session";
import * as app from "./app";
import * as events from "../app/events";
import $ from "jquery";
import * as data from "./../data/data";
import * as autocomplete from "./../ui/autocomplete/autocomplete";

/** handles navigation between screens  */

const screens = [
  "landing",
  "connect",
  "reset_password",
  "inscription1",
  "inscription2",
  "recherche",
  "board",
  "selection",
  "groupes",
  "profil",
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

function init_main_nav() {
  $(".set-screen").on("click", async function () {
    const screen = $(this).attr("screen");
    setScreen(screen);
  });
  $(".disconnect").show();
  $(".disconnect").on("click", async function () {
    app.disconnect();
    $(".disconnect").hide();
  });
}

async function updateRecherche() {
  let str = $("#search-784-input").val();
  if (str === null || str === undefined) str = "";
  ui.showWaitingMessage();
  const msg = await app.doSearch(str);
  ui.showRechercheData(msg.details);
  $("#search-button").on("click", updateRecherche);
  $("#search-784-input").on("keypress", function (event) {
    // Check if the key pressed is Enter
    if (event.key === "Enter") {
      updateRecherche();
    }
  });

  $("#add-to-favorites-btn").on("click", function () {
    const id = $(this).attr("data-id");
    events.changeSuggestionStatus(id, data.SUGG_APPROVED, () =>
      ui.updateFav(id)
    );
  });
  $("#formation-details-header-nav-central-icon").on("click", function () {
    const id = $(this).attr("data-id");
    events.changeSuggestionStatus(id, data.SUGG_APPROVED, () =>
      ui.updateFav(id)
    );
  });
  $("#add-to-bin-btn").on("click", function () {
    const id = $(this).attr("data-id");
    events.changeSuggestionStatus(id, data.SUGG_REJECTED, () =>
      ui.updateFav(id)
    );
  });
}
async function updateSelection() {
  ui.showWaitingMessage();
  const msg = await app.getSelection();
  ui.showFavoris(msg.details);
  $("#add-to-bin-btn").on("click", function () {
    const id = $(this).attr("data-id");
    events.changeSuggestionStatus(id, data.SUGG_REJECTED, async () => {
      ui.updateFav(id);
      const msg = await app.getSelection();
      ui.showFavoris(msg.details);
    });
  });
}

const screen_enter_handlers = {
  landing: async () => await ui.showLandingScreen(),
  recherche: async () => {
    await ui.showRechercheScreen();
    init_main_nav();
    $("#search-784-input").val("");
    await updateRecherche();
  },
  board: async () => {
    await ui.showBoard();
    const prenom = data.getPrenom();
    $(".ravi-div-prenom").html(prenom);
    init_main_nav();
  },
  selection: async () => {
    await ui.showSelection();
    init_main_nav();
    await updateSelection();
  },
  inscription1: async () => await ui.showInscriptionScreen1(),
  inscription2: async () => await ui.showInscriptionScreen2(),
  groupes: async () => {
    refreshGroupTab(false);
    ui.showGroupsTab();
  },
  profil: async () => {
    await app.getProfile();
    await ui.showProfileScreen();
    setUpAutoComplete("spe_classes", 0);
    setUpAutoComplete("geo_pref", 2);
    setUpMultiChoices();
    //todo register save handlers
    updateProfile();
    init_main_nav();
  },
};
const screen_exit_handlers = {};

function setUpMultiChoices() {
  //multi-options-item
  $(".multi-options-item")
    .off("click")
    .on("click", function () {
      const key = $(this).attr("key");
      events.toggleProfileScoreHandler(key);
      const selected = data.isSelected(key);
      $(this).toggleClass("selected", selected);
    });
}

function setUpAutoComplete(id, threshold) {
  data.updateAutoComplete();
  const show = autocomplete.setUpAutoComplete(
    id,
    (key, label, value) => {
      events.addElementToProfileListHandler(id, label);
    },
    (key, label, value) => {
      events.removeElementFromProfileList(id, value);
    },
    (resultNb, lookupLength, lookupLengthThreshold) => {
      autoCompleteFeedbackHandler(
        id,
        resultNb,
        lookupLength,
        lookupLengthThreshold
      );
    },
    threshold
  );
  if (id === "spe_classes") {
    $("#autocomplete_group_spe_classes").toggle(show);
  }
}

function autoCompleteFeedbackHandler(
  id,
  resultNb,
  lookupLength,
  lookupLengthThreshold
) {
  const feedbackDom = $(`#${id}_autocomplete_feedback`);
  feedbackDom.removeClass("text-success");
  feedbackDom.removeClass("text-warning");
  feedbackDom.removeClass("text-danger");
  if (resultNb == 0 && lookupLength == 0) {
    feedbackDom.html("&nbsp;");
  } else if (resultNb == 0 && lookupLength < lookupLengthThreshold) {
    feedbackDom.addClass("text-warning");
    feedbackDom.html("Mot trop court");
  } else if (resultNb == 0 && lookupLength >= lookupLengthThreshold) {
    feedbackDom.addClass("text-danger");
    feedbackDom.html("Aucun résultat");
  } else if (resultNb == 1) {
    feedbackDom.html(`1 résultat`);
  } else if (resultNb > 1) {
    feedbackDom.html(`${resultNb} résultats`);
  }
}

function enterScreen(screen) {
  if (screen in screen_enter_handlers && screen_enter_handlers[screen]) {
    screen_enter_handlers[screen]()
      .catch((error) => {
        console.error("Error in enterScreen", screen, error);
      })
      .then(() => {
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

function getSelectVal(id) {
  const val = $(id).val();
  if (val === null) return "";
  return val;
}
function setSelectVal(id, val) {
  $(id).val(val);
}

function updateProfile() {
  const selects = {
    niveau: "#profile-tab-scolarite-classe-select",
    bac: "#profile-tab-scolarite-bac-select",
    duree: "#profile-tab-etudes-duree-select",
    apprentissage: "#profile-tab-etudes-app-select",
  };
  for (const key in selects) {
    const id = selects[key];
    setSelectVal(id, data.getProfileValue(key));
  }
  $(".profile-select").on("change", function () {
    const key = $(this).attr("key");
    const val = $(this).val();
    events.profileValueChangedHandler2(key, val);
    if (key == "bac") {
      setUpAutoComplete("spe_classes", 0);
    }
  });
  /*
  $(".save-profile-button").on("click", function () {
    const pf = {};
    const category = $(this).attr("category");
    if (category === "scolarite") {
      const classe = getSelectVal("#profile-tab-scolarite-classe-select");
      const bac = getSelectVal("#profile-tab-scolarite-bac-select");
      events.profileValueChangedHandler2("niveau", classe);
      events.profileValueChangedHandler2("bac", bac);
    }
    if (category === "etudes") {
      const duree = getSelectVal("#profile-tab-etudes-duree-select");
      const app = getSelectVal("#profile-tab-etudes-app-select");
      events.profileValueChangedHandler2("duree", duree);
      events.profileValueChangedHandler2("apprentissage", app);
    }
  });*/
}
