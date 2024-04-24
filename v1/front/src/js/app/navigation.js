import * as ui from "../ui/ui";
import * as session from "./session";
import * as app from "./app";
import * as events from "../app/events";
import $ from "jquery";
import * as data from "./../data/data";
import * as autocomplete from "./../ui/autocomplete/autocomplete";

/** handles navigation between screens  */

const screens = {
  landing: {},
  connect: {},
  reset_password: {},
  inscription1: { next: "inscription2", back: "landing" },
  inscription2: { next: "inscription_tunnel_statut", back: "inscription1" },
  recherche: {},
  board: {},
  selection: {},
  groupes: {},
  profil: {},
  inscription_tunnel_statut: { next: "inscription_tunnel_scolarite" },
  inscription_tunnel_scolarite: {
    next: "inscription_tunnel_domaines_pro",
    back: "inscription_tunnel_statut",
  },
  inscription_tunnel_domaines_pro: {
    next: "inscription_tunnel_interests",
    back: "inscription_tunnel_scolarite",
  },
  inscription_tunnel_interests: {
    next: "inscription_tunnel_etudes", //inscription_tunnel_metiers
    back: "inscription_tunnel_domaines_pro",
  },
  inscription_tunnel_metiers: {
    next: "inscription_tunnel_etudes",
    back: "inscription_tunnel_interests",
  },
  inscription_tunnel_etudes: {
    next: "inscription_tunnel_felicitations", //"inscription_formations",
    back: "inscription_tunnel_interests", //"inscription_tunnel_metiers",
  },
  inscription_tunnel_formations: {
    next: "inscription_tunnel_felicitations",
    back: "inscription_tunnel_etudes",
  },
  inscription_tunnel_felicitations: {},
};

export async function setScreen(screen) {
  console.log("setScreen", screen);
  if (screen in screens) {
    let current_screen = session.getScreen();
    await doTransition(current_screen, screen);
    session.saveScreen(screen);
    $(`#nav-${screen}`).attr("aria-current", true);
    init_main_nav();
  }
}

function next(current_screen) {
  if (!current_screen in screens) return undefined;
  return screens[current_screen]?.next;
}
function back(current_screen) {
  if (!current_screen in screens) return undefined;
  return screens[current_screen]?.back;
}

async function doTransition(old_screen, new_screen) {
  await exitScreen(old_screen);
  await enterScreen(new_screen);
}

export function init_main_nav() {
  $(".set-screen")
    .off()
    .on("click", async function () {
      const screen = $(this).attr("screen");
      $(this).attr("aria-current", true);
      await setScreen(screen);
    });
  $(".visible-only-when-connected").show();
  $(".visible-only-when-disconnected").hide();

  const screen = session.getScreen();
  if (screen) {
    $(".hidden-during-inscription").toggle(!screen.includes("inscription"));
  } else {
    $(".hidden-during-inscription").show();
  }

  $(".disconnect")
    .off()
    .on("click", async function () {
      app.disconnect();
      $(".visible-only-when-connected").hide();
      $(".visible-only-when-disconnected").show();
    });
  $(".recherche")
    .off()
    .on("click", async function () {
      await setScreen("recherche");
    });
  $(".monespace")
    .off()
    .on("click", async function () {
      await setScreen("profil");
    });
  $(".prenomnom").html(data.getPrenomNom());
}

async function updateRecherche() {
  let str = $("#search-784-input").val();
  if (str === null || str === undefined) str = "";
  ui.showWaitingMessage();
  const msg = await app.doSearch(str);
  ui.showRechercheData(msg.details);
  $("#search-button").off("click").on("click", updateRecherche);
  $("#search-784-input").on("keypress", function (event) {
    // Check if the key pressed is Enter
    if (event.key === "Enter") {
      updateRecherche();
    }
  });

  $("#add-to-favorites-btn")
    .off("click")
    .on("click", function () {
      const id = $(this).attr("data-id");
      events.changeSuggestionStatus(id, data.SUGG_APPROVED, () =>
        ui.updateFav(id)
      );
    });
  $("#formation-details-header-nav-central-icon")
    .off("click")
    .on("click", function () {
      const id = $(this).attr("data-id");
      events.changeSuggestionStatus(id, data.SUGG_APPROVED, () =>
        ui.updateFav(id)
      );
    });
  $("#add-to-bin-btn")
    .off("click")
    .on("click", function () {
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
  $("#add-to-bin-btn")
    .off("click")
    .on("click", function () {
      const id = $(this).attr("data-id");
      events.changeSuggestionStatus(id, data.SUGG_REJECTED, async () => {
        ui.updateFav(id);
        const msg = await app.getSelection();
        ui.showFavoris(msg.details);
      });
    });
}

function profileEditionSetup() {
  setUpAutoComplete("spe_classes", 0);
  setUpAutoComplete("geo_pref", 2);
  setUpMultiChoices();
  updateProfile();
}

function setUpInscription(screen) {
  ui.setupSelects(screen, "#myTabContent");
  profileEditionSetup();
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
    profileEditionSetup();
    init_main_nav();
  },
  inscription_tunnel_statut: async () => {
    await ui.showTunnelScreen("statut");
    setUpInscription("statut");
  },
  inscription_tunnel_scolarite: async () => {
    await ui.showTunnelScreen("scolarite");
    setUpInscription("scolarite");
  },
  inscription_tunnel_domaines_pro: async () => {
    await ui.showTunnelScreen("domaines_pro");
    setUpInscription("domaines_pro");
  },
  inscription_tunnel_interests: async () => {
    await ui.showTunnelScreen("interests");
    setUpInscription("interests");
  },
  inscription_tunnel_metiers: async () => {
    await ui.showTunnelScreen("metiers");
    setUpInscription("metiers");
  },
  inscription_tunnel_etudes: async () => {
    await ui.showTunnelScreen("etudes");
    setUpInscription("etudes");
  },
  inscription_tunnel_formations: async () => {
    await ui.showTunnelScreen("formations");
    setUpInscription("formations");
  },
  inscription_tunnel_felicitations: async () => {
    await ui.showTunnelScreen("felicitations");
    $("#discover_button")
      .off("click")
      .on("click", () => {
        setScreen("board");
      });
  },
};
const screen_exit_handlers = {
  inscription1: () => validateInscription1(),
  inscription2: async () => await validateInscription2(),
};

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

async function enterScreen(screen) {
  if (screen in screen_enter_handlers && screen_enter_handlers[screen]) {
    await screen_enter_handlers[screen]();
    const nextScreen = next(screen);
    const backScreen = back(screen);
    $("#nextButton").toggle(nextScreen !== undefined);
    $("#backButton").toggle(backScreen !== undefined);
    if (nextScreen)
      $("#nextButton")
        .off("click")
        .on("click", async () => {
          await setScreen(nextScreen);
        });
    if (backScreen)
      $("#backButton")
        .off("click")
        .on("click", async () => {
          await setScreen(backScreen);
        });
  } else {
    ui.showLandingScreen();
  }
}
async function exitScreen(screen) {
  if (screen in screen_exit_handlers) {
    if (screen_exit_handlers[screen]) await screen_exit_handlers[screen]();
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
    app.validateCodeAcces(accounType, accessGroupe, async () => {
      await setScreen("inscription2");
    });
  }
}

async function validateInscription2() {
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

    await app.createAccount({
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
