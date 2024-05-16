import * as ui from "../ui/ui";
import * as session from "./session";
import * as app from "./app";
import * as events from "../app/events";
import $ from "jquery";
import * as data from "./../data/data";
import * as autocomplete from "./../ui/autocomplete/autocomplete";
import { sanitize } from "dompurify";

/** handles navigation between screens  */

const screens = {
  landing: { next: "connect" },
  landing_redir: {},
  connect: {},
  reset_password: {},
  inscription1: { next: "inscription2", back: "landing_redir" },
  inscription2: { next: "inscription_tunnel_statut", back: "inscription1" },
  recherche: {},
  board: {},
  selection: {},
  student_selection: {},
  profil: {},
  student_profile: {},
  inscription_tunnel_statut: {
    next: "inscription_tunnel_scolarite",
    back: "landing_redir",
  },
  inscription_tunnel_scolarite: {
    next: "inscription_tunnel_domaines_pro",
    back: "inscription_tunnel_statut",
  },
  inscription_tunnel_domaines_pro: {
    next: "inscription_tunnel_interests",
    back: "inscription_tunnel_scolarite",
  },
  inscription_tunnel_interests: {
    next: "inscription_tunnel_metiers",
    back: "inscription_tunnel_domaines_pro",
  },
  inscription_tunnel_metiers: {
    next: "inscription_tunnel_etudes",
    back: "inscription_tunnel_interests",
  },
  inscription_tunnel_etudes: {
    next: "inscription_tunnel_formations",
    back: "inscription_tunnel_metiers",
  },
  inscription_tunnel_formations: {
    next: "inscription_tunnel_felicitations",
    back: "inscription_tunnel_etudes",
  },
  inscription_tunnel_felicitations: {},
  inscription_tunnel_felicitations_teacher: {},
  groupes: {},
  profil_teacher: {},
};

const screen_enter_handlers = {
  landing: async () => await displayLandingScreen(),
  landing_redir: async () => {
    await app.disconnect();
    location.reload();
  },
  connect: async () => {
    await ui.showConnectionScreen();
    $("#createAccountButton")
      .off("click")
      .on("click", async () => {
        app.setAnonymousSession(false);
        await initScreen("inscription1");
      });

    $("#anonymousNavButton")
      .off("click")
      .on("click", () => {
        app.setAnonymousSession(true);
        app.loginHandler("anonymous", "anonymous");
      });

    $("#login-button")
      .off("click")
      .on("click", function () {
        const login = $("#champEmail").val();
        const password = $("#password-1144-input").val();
        $("#password-1144-input").val("");
        app.setAnonymousSession(false);
        app.loginHandler(login, password);
      });
    $("#oubli_mdp_button")
      .off("click")
      .on("click", function () {
        $("#oubliMdpButton").trigger("click");
        $("#sendResetPasswordEmail")
          .off()
          .on("click", async (event) => {
            const login = $("#sendResetPasswordEmailInputEmail").val();
            $("#sendResetPasswordEmailInputEmail-messages").empty();
            $("#sendResetPasswordEmailInputEmail-messages2").empty();
            if (login === null || login === undefined || login.length == 0) {
              $("#sendResetPasswordEmailInputEmail-messages").html(
                '<p class="fr-alert fr-alert--error">Préciser le login</p>'
              );
            } else if (!isValidEmail(login)) {
              $("#sendResetPasswordEmailInputEmail-messages").html(
                `<p class="fr-alert fr-alert--error">Préciser une adresse email valide</p>`
              );
            } else {
              app.sendResetPasswordEmail(login);
              //$("#passwordReinitializedButton").trigger("click");
              //$("#resetConfirmationModalEmail").html(login);
              $("#sendResetPasswordEmailInputEmail-messages2").html(
                `<p class="fr-alert fr-alert--success">Si cet identifiant correspond bien à votre compte, un e-mail vous a été envoyé pour vous permettre de réinitialiser votre mot de passe. En cas de difficulté veuillez contacter support@monprojetsup.fr.</p>`
              );
            }
            event.preventDefault();
          });
      });
  },
  recherche: async () => {
    session.setSelectedStudent(null);
    await ui.showRechercheScreen();
    init_main_nav();
    $("#search-784-input").val(session.getCurrentSearch());
    await updateRecherche();
  },
  board: async () => {
    await ui.showBoard();
    const prenom = data.getPrenom();
    $(".ravi-div-prenom").html(prenom);

    const url = getUrlOpinionTally();
    $("#opinionButton").attr("href", url);
    $("#board-opinion-div").toggle(url !== null);

    init_main_nav();
  },
  selection: async () => {
    await ui.showSelection();
    init_main_nav();
    await updateSelection();
  },
  student_selection: async () => {
    const login = session.getSelectedStudent();
    if (login === null || login === undefined) {
      return setScreen("groupes");
    }
    await ui.showSelection();
    const msg = await app.getStudentSelection(login);
    const selection = msg.details;
    const name = session.getSelectedStudentName();
    ui.showFavoris(selection);
    init_main_nav();
    $("#explore-div-entete").hide();
    $("#explore-div-resultats-left-entete-prof").html(
      `La sélection de ${name == null ? login : name}`
    );
    $("#formation-details-header-nav-central-icon").hide();
    await updateStudentRetours(msg.retours);
  },
  inscription1: async () => {
    await ui.showInscriptionScreen1();
    $("#nextButtonAnonymous").hide();

    $("#checkboxes-noaccesscode")
      .off()
      .on("change", function () {
        const isChecked = $(this).is(":checked");
        $("#inputCreateAccountCodeAcces").toggle(!isChecked);
        $("#nextButtonAnonymous").toggle(isChecked);
        $("#nextButton").toggle(!isChecked);
        if (isChecked) {
          $("#inscription1-messages2").html(`<p
        class="fr-alert fr-alert--info"
      >Sans code d'accès vous ne pouvez pas créer de compte mais vous pouvez tout de même tester le site (en mode lycéen).</p>`);
        } else {
          $("#inscription1-messages2").empty();
        }
        if (isChecked) {
          $("#nextButtonAnonymous")
            .off()
            .on("click", async () => {
              app.setAnonymousSession(true);
              app.loginHandler("anonymous", "anonymous");
            });
        }
      });
  },
  inscription2: async () =>
    await ui.showInscriptionScreen2(session.isAdminOrTeacher()),
  profil: async () => {
    await app.getProfile();
    await ui.showProfileScreen();
    profileEditionSetup();
    fillInBin();
    init_main_nav();
  },
  student_profile: async () => {
    const login = session.getSelectedStudent();
    if (login === null || login === undefined) {
      return setScreen("groupes");
    }
    const profile = await app.getStudentProfile(login);
    data.loadProfile(profile);
    await ui.showProfileScreen();
    init_main_nav();
    profileEditionSetup();
    $("#explore-div-entete").show().html(`Le profil de ${name}`);
    $("select").prop("disabled", true);
    $("input").prop("disabled", true);
    $(".fr-input").hide();
    $(".trashItem").hide();
    $(".profile_explanation").hide();
    $(".fr-hint-text").hide();
    $("#range-moyenne-generale-messages").hide();
    $(".multi-options-item").off().addClass("no-pointer");
  },
  profil_teacher: async () => {
    await ui.showTeacherProfileScreen();
    $("#basculer_mode_lyceen")
      .off()
      .on("click", async () => {
        await setScreen(null);
        app.toLyceen();
      });
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
      .off()
      .on("click", async () => {
        await setScreen("board");
      });
  },
  inscription_tunnel_felicitations_teacher: async () => {
    await ui.showTunnelScreen("felicitations_teacher");
    $("#discover_button")
      .off()
      .on("click", async () => {
        await setScreen("groupes");
      });
  },
  groupes: async () => {
    await ui.showGroupesScreen();
    await updateGroupesScreen();
  },
};

const screen_exit_handlers = {
  inscription1: async () => await validateInscription1(),
  inscription2: async () => await validateInscription2(),
  inscription_tunnel_domaines_pro: validateDomainePro,
  inscription_tunnel_interests: validateInterests,
  inscription_tunnel_scolarite: validateEDS,
};

function getUrlOpinionTally() {
  const groupInfo = session.getGroupInfo();
  const group = groupInfo?.expeENSGroup;
  const params = {
    login: session.getLogin(),
    group: group,
    classe: groupInfo?.classe,
    lycee: groupInfo?.lycee,
    role: session.getRole(),
    nb_fav: data.getSuggestionsYes().length,
    nb_bin: data.getSuggestionsRejected().length,
    nb_int: data.getNbCentresInterets(),
    nb_dom_pro: data.getNbDomainesPro(),
  };

  for (const key of ["statut", "niveau", "bac"]) {
    params[key] = data.getProfileValue(key);
  }
  params.projet = params.statut;
  delete params.statut;

  let url = "https://tally.so/r/3xd2aE";
  if (group == "quanti3") url = "https://tally.so/r/3jBYD1";
  if (session.isAdminOrTeacher()) {
    url = group == "quanti3" ? "https://tally.so/r/w8d6Wk" : null;
  }
  if (url === null) return null;
  /*Tests MAI 2024 · Questionnaire lycéen·nes élu·e·s	https://tally.so/r/3xd2aE	Les élu·e·s (lycées agricoles et hors lycées agricoles)	login_elus
    expeENSGroupe "elus"
Tests mai 2024 · Questionnaire lycéen·nes tous lycées	https://tally.so/r/3jBYD1	Tous les autres lycéen·nes des lycées testeurs (N-A, Idf)	login_eleves
  //"expeENSGroupe": "quanti3",
Tests MAI 2024 · Questionnaire équipes pédagogiques	https://tally.so/r/w8d6Wk	Tous les proviseurs, PP et Psy-EN testeurs (N-A, Idf)	login_peda
  //  //"expeENSGroupe": "quanti3",
*/
  let first = true;
  for (const [key, value] of Object.entries(params)) {
    url = `${url}${first ? "?" : "&"}${key}=${encodeURIComponent(value)}`;
    first = false;
  }
  return url;
}

export async function initScreen(screen) {
  session.saveScreen(null);
  return setScreen(screen);
}

async function updateStudentRetours(retoursListe) {
  const retoursByKey = {};
  for (const retour of retoursListe) {
    const key = retour.key;
    let retours = retoursByKey[key];
    if (retours === undefined) retours = {};
    retours[retour.type] = retour.content;
    retoursByKey[key] = retours;
  }
  for (const [key, value] of Object.entries(retoursByKey)) {
    if ("opinion" in value) {
      ui.setTeacherOpinion(key, value.opinion);
    }
    if ("comment" in value) {
      ui.setTeacherComment(key, value.comment);
    }
  }
  if (session.isAdminOrTeacher()) {
    $(".btn-teacher-ok")
      .off()
      .on("click", async function () {
        const key = $(this).attr("key");
        await app.setTeacherOpinion(key, "ok");
        ui.setTeacherOpinion(key, "ok");
      });
    $(".btn-teacher-discuss")
      .off()
      .on("click", async function () {
        const key = $(this).attr("key");
        await app.setTeacherOpinion(key, "discuss");
        ui.setTeacherOpinion(key, "discuss");
      });
    $(".btn-teacher-send-comment")
      .off()
      .on("click", async function () {
        const key = $(this).attr("key");
        const comment = $(`#input-teacher-comment_${key}`).val();
        await app.setTeacherComment(key, comment);
      });
  }
}

export async function setScreen(screen, updateHistory = true) {
  console.log("setScreen", screen);
  if (screen == null) {
    session.saveScreen(null);
  } else if (screen in screens || screen in screen_enter_handlers) {
    let current_screen = session.getScreen();
    screen = await doTransition(current_screen, screen);
    if (screen !== current_screen && updateHistory) {
      app.addTransitionToHistory(screen, {
        curGroup: session.getSelectedGroup(),
        curStudent: session.getSelectedStudent(),
        curSearch: session.getCurrentSearch(),
      });
    }
    init_main_nav();
    ui.hideNiveauInformation(data.getProfileValue("niveau"));
  }
}

function next(current_screen) {
  if (!current_screen in screens) return undefined;
  if (current_screen == "inscription2" && session.isAdminOrTeacher())
    return "inscription_tunnel_felicitations_teacher";
  return screens[current_screen]?.next;
}

function back(current_screen) {
  if (!(current_screen in screens)) return undefined;
  return screens[current_screen].back;
}

async function doTransition(old_screen, new_screen) {
  const result = await exitScreen(old_screen, new_screen);
  let screen = session.getScreen();
  if (result || new_screen === back(old_screen)) {
    await enterScreen(new_screen);
    screen = new_screen;
    app.logAction("doTransition " + old_screen + " --> " + new_screen);
    session.saveScreen(screen);
  }
  const nextScreen = next(screen);
  const backScreen = back(screen);
  ui.displayNextAndBAckButtons(nextScreen, backScreen);
  $("#nextButton")
    .off("click")
    .on("click", async () => {
      await setScreen(nextScreen);
    });
  $("#backButton")
    .off("click")
    .on("click", async () => {
      await setScreen(backScreen);
    });

  ui.setRoleVisibility();
  return screen;
}

async function enterScreen(screen) {
  if (screen in screen_enter_handlers && screen_enter_handlers[screen]) {
    await screen_enter_handlers[screen]();
  } else {
    await displayLandingScreen();
  }
  const loggedIn = session.isLoggedIn();
  $(".visible-only-when-connected").toggle(loggedIn);
  $(".visible-only-when-disconnected").toggle(!loggedIn);
  ui.setRoleVisibility();
  if (loggedIn) {
    $(".hidden-during-inscription").toggle(!screen.includes("inscription"));
  }

  $(".visible-only-when-favoris").toggle(
    loggedIn && session.isStudent() && data.getSuggestionsApproved().length > 0
  );
}

async function exitScreen(screen, new_screen) {
  if (
    screen in screen_exit_handlers &&
    screen in screens &&
    new_screen !== screens[screen].back
  ) {
    if (screen_exit_handlers[screen]) {
      const result = await screen_exit_handlers[screen]();
      return result;
    }
  }
  return true;
  //default behaviour: None
}

export function init_main_nav() {
  let current_screen = session.getScreen();
  $(`.set-screen`).attr("aria-current", false);
  $(`#nav-${current_screen}`).attr("aria-current", true);

  $(".set-screen")
    .off("click")
    .on("click", async function () {
      const screen = $(this).attr("screen");
      //$(this).attr("aria-current", true);
      await setScreen(screen);
    });

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
      if (session.isAdminOrTeacher()) {
        await setScreen("profil_teacher");
      } else {
        await setScreen("profil");
      }
    });
  $(".prenomnom").html(data.getPrenomNom());
}

async function updateRecherche() {
  let str = $("#search-784-input").val();
  session.setCurrentSearch(str);
  if (str === null || str === undefined) str = "";
  ui.showWaitingMessage();
  const msg = await app.doSearch(str);
  const showAffinities = str == "";
  ui.showRechercheData(msg.details, showAffinities);

  $("#search-button").off().on("click", updateRecherche);
  $("#search-784-input")
    .off()
    .on("keypress", function (event) {
      // Check if the key pressed is Enter
      if (event.key === "Enter") {
        updateRecherche();
      }
    });

  $("#formation-details-header-nav-central-icon")
    .off()
    .on("click", function () {
      const id = $(this).attr("data-id");
      events.changeSuggestionStatus(id, data.SUGG_APPROVED, () => {
        ui.updateFav(id);
        init_main_nav();
      });
    });
  $(".add-to-favorites-btn")
    .off()
    .on("click", function () {
      const id = $(this).attr("data-id");

      $(this).html("Ajouté à ma sélection");
      $(this).addClass("activated");
      $(this).addClass("favori");

      events.changeSuggestionStatus(id, data.SUGG_APPROVED, () => {
        ui.updateFav(id);
        init_main_nav();
      });
    });
  $(".add-to-bin-btn")
    .off("click")
    .on("click", function () {
      const id = $(this).attr("data-id");
      events.changeSuggestionStatus(id, data.SUGG_REJECTED, async () => {
        ui.updateFav(id);
        init_main_nav();
        await updateRecherche();
      });
    });
  $(".remove-from-favoris-btn")
    .off("click")
    .on("click", function (event) {
      const id = event.currentTarget.getAttribute("data-id");
      events.changeSuggestionStatus(id, data.SUGG_WAITING, async () => {
        ui.updateFav(id);
        init_main_nav();
        await updateRecherche();
      });
    });
}

async function updateSelection() {
  ui.showWaitingMessage();
  const msg = await app.getSelection();

  ui.showFavoris(msg.details);
  $(".add-to-favorites-btn")
    .off("click")
    .on("click", function () {
      const id = $(this).attr("data-id");

      $(this).html("Ajouté à ma sélection");
      $(this).addClass("activated");
      $(this).addClass("favori");

      events.changeSuggestionStatus(id, data.SUGG_APPROVED, () =>
        ui.updateFav(id)
      );
    });

  $(".add-to-bin-btn")
    .off("click")
    .on("click", function () {
      const id = $(this).attr("data-id");
      events.changeSuggestionStatus(id, data.SUGG_REJECTED, async () => {
        ui.updateFav(id);
        const msg = await app.getSelection();
        ui.showFavoris(msg.details);
      });
    });
  $(".remove-from-favoris-btn")
    .off("click")
    .on("click", function () {
      const id = $(this).attr("data-id");
      events.changeSuggestionStatus(id, data.SUGG_WAITING, async () => {
        ui.updateFav(id);
        const msg = await app.getSelection();
        ui.showFavoris(msg.details);
      });
    });
}

function fillInBin() {
  const $div = $(".bin-container").empty();
  const rejected = data.getSuggestionsRejected();
  for (const sugg of rejected) {
    const label = data.getLabel(sugg.fl);
    const $cell = $(`
      <div class="profile-cell">
        <span class="profile-cell-label"><span>${label}</span>
          <span class="recycleItem fr-icon-recycle-fill" 
            label="${label}"
            title="Recycler"
            >
          </span>
        </span>
      </div>`);
    $(".recycleItem", $cell).on("click", function (event) {
      app.removeFromBin(sugg.fl);
      fillInBin();
      event.preventDefault();
    });
    $div.append($cell);
  }
}

function profileEditionSetup() {
  setUpAutoComplete("spe_classes", 0);
  setUpAutoComplete("metiers", 2);
  setUpAutoComplete("formations", 2);

  const geo_prefs = data.getProfileValue("geo_pref");
  const showVilles = geo_prefs !== undefined && geo_prefs.length > 0;
  if (showVilles) $("#quelques_villes_en_tete").attr("checked", true);
  else $("#aucune_ville_en_tete").attr("checked", true);
  setUpVilles();
  $(".radio-inline-villes").on("change", () => {
    setUpVilles();
  });

  setUpMultiChoices();
  setUpSelects();
  setUpScolarite();
  $("#range-moyenne-generale").val(data.getProfileValue("moygen"));
}

function setUpVilles() {
  const showVilles = $("#quelques_villes_en_tete").is(":checked");
  $("#villes_autocomplete_group").toggle(showVilles);
  if (showVilles) {
    setUpAutoComplete("geo_pref", 2);
  } else {
    events.clearProfileList("geo_pref");
  }
}

function setUpInscription(screen) {
  ui.setupSelects(screen, "#myTabContent");
  profileEditionSetup();
  if (screen == "metiers" || screen == "formations") {
    $(".profile_tab .autocomplete_group").hide();
    $(".metier-toggler")
      .off()
      .on("click", function () {
        $(".profile_tab .autocomplete_group").toggle(
          $("#radio-rich-metier-identifie").is(":checked")
        );
      });
  }
  if (screen == "scolarite") {
    setUpScolarite();
  }
}

function setUpScolarite() {
  updateAutoEvaluationFeedback();
  $("#fr-range--step-moyenne-disable").on("change", () => {
    const checked = $("#fr-range--step-moyenne-disable").is(":checked");
    $("#fr-range--step-moyenne").toggle(!checked);
    $("#range-moyenne-generale-messages").toggle(!checked);
    sendMoyenneDebounced(checked ? "" : null);
  });
  $("#range-moyenne-generale").on("change", () => {
    sendMoyenneDebounced(null);
    updateAutoEvaluationFeedback();
  });
}

function sendMoyenneDebounced(val) {
  debounce(() => sendMoyenne(val), 1000)();
}

function sendMoyenne(val) {
  if (val === null) val = $("#range-moyenne-generale").val();
  console.log("Sending moyenne '" + val + "'");
  events.profileValueChangedHandler2("moygen", val);
}

function debounce(func, delay) {
  let timeoutId;

  return function () {
    const context = this;
    const args = arguments;

    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => {
      func.apply(context, args);
    }, delay);
  };
}

export function updateAutoEvaluationFeedback() {
  const value = $("#range-moyenne-generale").val();
  const id = "range-moyenne-generale-messages";
  if (!value) {
    $(`#${id}`).hide();
    return;
  }
  $(`#${id}`).show();
  const feedback_node = $("#" + id);

  const bac = data.getProfileValue("bac");
  let typeBacStr = "";
  if (bac !== undefined && bac != "") {
    typeBacStr = " de série de Bac '" + data.ppBac(bac) + "'";
  }
  typeBacStr = "";

  if (feedback_node) {
    if (value === undefined || value === "") {
      feedback_node.html("");
    }
    // Feedback (https://rschmacker.github.io/files/JMP.pdf)
    // TODO: load grades distribution.
    else {
      const index40 = Math.max(0, Math.round(2 * value));
      const isMoyGen = true;
      const stats = data.getPercentilesMoyGen40();
      const centile = stats ? stats[index40] : undefined;
      //const percentile = percentilesMoyGen40[index40];
      if (centile !== undefined) {
        const name = isMoyGen ? "moyenne générale" : "moyenne au bac";
        const formatted = format(centile, 0);
        feedback_node.html(
          `
          Cette année, ${formatted} des élèves de terminale ${typeBacStr} admis dans une formation
           sur Parcoursup avaient une ${name} de ${value} ou moins.`
        );
      } else {
        feedback_node.html("");
      }
    }
  }
}

function format(x, nbDigits) {
  return new Intl.NumberFormat("fr-FR", {
    maximumFractionDigits: nbDigits,
    style: "percent",
    roundingMode: "floor",
  }).format(x);
}

async function displayLandingScreen() {
  await ui.showLandingScreen();
  $("#landing-placeholder")
    .off()
    .on("click", async () => {
      setScreen("connect");
    });
}

async function updateGroupesScreen() {
  //ask for the list of groups
  const infos = await app.updateAdminInfos();
  ui.updateGroupsList(infos.groups);
  $(".group_select")
    .off()
    .on("click", async (event) => {
      const group_id = event.currentTarget.getAttribute("group_id");
      app.setSelectedGroup(group_id);
      await showGroup(group_id);
    });

  const url = getUrlOpinionTally();
  $("#opinionButton").attr("href", url);
  $("#board-opinion-div").toggle(url !== null);

  const curGroup = session.getSelectedGroup();
  await showGroup(curGroup);
}

async function showGroup(group_id) {
  if (group_id == null || group_id == undefined) return;
  const details = await app.getGroupDetails(group_id);
  $(".group_select").attr("aria-current", false);
  $(`#group_select_${group_id.replaceAll(" ", "_").replaceAll(".", "_")}`).attr(
    "aria-current",
    "page"
  );
  ui.setStudentDetails(details);
  $(".student_selection_button")
    .off()
    .on("click", async (event) => {
      const login = event.currentTarget.getAttribute("login");
      if (login === undefined || login === null) return;
      const name = event.currentTarget.getAttribute("name");
      session.setSelectedStudent(login);
      session.setSelectedStudentName(name);
      setScreen("student_selection");
    });
  $(".student_profile_button")
    .off()
    .on("click", async (event) => {
      const login = event.currentTarget.getAttribute("login");
      if (login === undefined || login === null) return;
      const name = event.currentTarget.getAttribute("name");
      session.setSelectedStudent(login);
      session.setSelectedStudentName(name);
      setScreen("student_profile");
    });
}

function setUpMultiChoices() {
  //multi-options-item
  $(".multi-options-item")
    .off()
    .on("click", function () {
      const key = $(this).attr("key");
      events.toggleProfileScoreHandler(key);
      const selected = data.isSelected(key);
      $(this).toggleClass("selected", selected);
    });
}

export async function startNavigation() {
  const screen = session.getScreen();
  if (
    screen != undefined &&
    screen != null &&
    screen != "null" &&
    !screen.includes("inscription") &&
    !screen.includes("landing") &&
    !screen.includes("connect")
  ) {
    await setScreen(screen);
  } else {
    if (session.isAdminOrTeacher()) {
      await initScreen("groupes");
    } else {
      const profileCompleteness = session.getProfileCompletenessLevel();
      if (profileCompleteness < 2) {
        await initScreen("inscription_tunnel_statut");
      } else {
        await initScreen("board");
      }
    }
  }
}

function setUpAutoComplete(id, threshold) {
  data.updateAutoComplete();

  const addHandler = (key, label, value) => {
    ui.clearAutoComplete(id);
    events.addElementToProfileListHandler(id, key, label);
  };

  const trashHandler = (label) => {
    ui.clearAutoComplete(id);
    if (id == "metiers" || id == "formations") {
      const sugg = data.getApprovedSuggestionWithLabel(label);
      if (sugg != undefined && sugg != null) {
        events.changeSuggestionStatus(sugg.fl, data.SUGG_REJECTED);
      }
    } else {
      events.removeElementFromProfileList(id, label);
    }
  };

  const feedbackHandler = (resultNb, lookupLength, lookupLengthThreshold) =>
    autoCompleteFeedbackHandler(
      id,
      resultNb,
      lookupLength,
      lookupLengthThreshold
    );

  const updateListHandler = (listeItems) => {
    ui.updateAutoCompleteItemsListe(id, listeItems);
    $(`.autoCompleteItem`).on("click", function () {
      const key = $(this).attr("key");
      const label = $(this).attr("label");
      addHandler(key, label);
      //events.addElementToProfileListHandler(id, key, label);
      $(this).remove();
      autocomplete.updateAutoCompleteListItem(id, trashHandler);
    });
  };

  const show = autocomplete.setUpAutoComplete(
    id,
    addHandler,
    trashHandler,
    feedbackHandler,
    threshold,
    updateListHandler
  );
  if (id === "spe_classes") {
    $("#autocomplete_group_spe_classes").toggle(show);
  }
}

function setUpSelects() {
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
  const radios = {
    statut: "#profile-tab-statut",
  };
  for (const key in radios) {
    const id = radios[key];
    setRadioVal(id, data.getProfileValue(key));
  }
  $(".profile_radio")
    .off()
    .on("change", function () {
      const key = $(this).attr("key");
      const val = $(this).attr("value");
      const ischecked = $(this).is(":checked");
      if (ischecked) {
        events.profileValueChangedHandler2(key, val);
      }
    });
  $(".profile-select")
    .off()
    .on("change", function () {
      const key = $(this).attr("key");
      const val = $(this).val();
      events.profileValueChangedHandler2(key, val);
      if (key == "bac") {
        setUpAutoComplete("spe_classes", 0);
        updateAutoEvaluationFeedback();
      }
      if (key == "niveau") {
        ui.hideNiveauInformation(val);
      }
      ui.updateMultiOptionsItemsStatus(data.getDomainesPro());
      ui.updateMultiOptionsItemsStatus(data.getInterests());
      $(".profile-option")
        .off()
        .on("change", function () {
          const key = $(this).attr("key");
          const val = $(this).val();
          events.profileValueChangedHandler2(key, val);
          if (key == "bac") {
            setUpAutoComplete("spe_classes", 0);
            updateAutoEvaluationFeedback();
          }
          if (key == "niveau") {
            ui.hideNiveauInformation(val);
          }
        });
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
    feedbackDom.html(`<p class="fr-alert fr-alert--error">Aucun résultat</p>`);
  } else if (resultNb == 1) {
    feedbackDom.html(`1 résultat`);
  } else if (resultNb > 1) {
    feedbackDom.html(`${resultNb} résultats`);
  }
}

let accounType = null;
let accessGroupe = null;

async function validateInscription1() {
  $(".fr-messages-group").empty();

  accessGroupe = $("#inputCreateAccountCodeAcces").val();
  const lyceen = $("#radio-create-account-lyceen").is(":checked");
  const pp = $("#radio-create-account-prof").is(":checked");
  accounType = lyceen ? "lyceen" : "pp";
  if (!lyceen && !pp) {
    $("#inscription1-messages").html(
      `<p class="fr-alert fr-alert--error">Veuillez choisir un type de compte</p>`
    );
    return false;
  } else if (accessGroupe == null || accessGroupe.length <= 0) {
    $("#inscription1-messages2").html(
      `<p class="fr-alert fr-alert--error">Veuillez indiquer un code d'accès</p>`
    );
    return false;
  } //récupérer type de compte et mot de passe
  const msg = await app.validateCodeAcces(accounType, accessGroupe);
  if (!msg.ok) {
    $("#inscription1-messages2").html(
      `<p class="fr-alert fr-alert--error">Le code d'accès '${sanitize(
        accessGroupe
      )}' est erroné</p>`
    );
  } else {
    if (pp) session.setRole("TEACHER");
  }
  return msg.ok;
}

function validateDomainePro() {
  const result = data.getNbDomainesPro() > 0;
  if (!result) {
    $("#domaine-pro-messages").html(
      `<p class="fr-alert fr-alert--error">Sélectionne au moins un domaine professionnel pour compléter ton profil!</p>`
    );
  }
  return result;
}
function validateInterests() {
  const result = data.getNbCentresInterets() > 0;
  if (!result) {
    $("#interests-messages").html(
      `<p class="fr-alert fr-alert--error">Sélectionne au moins un centre d'intérêts pour compléter ton profil!</p>`
    );
  }
  return result;
}

function validateEDS() {
  let result = true;
  const niveau = data.getProfileValue("niveau");
  if (niveau == "term" || niveau == "prem") {
    result = data.getNbEDS() >= 2 || data.getNbSpecialites() < 2;
    if (!result) {
      $("#scolarite-messages").html(
        `<p class="fr-alert fr-alert--error">Sélectionne au moins deux spécialités afin de compléter ton profil!</p>`
      );
    }
  }
  return result;
}

const validEmailRegex =
  /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;

function isValidEmail(login) {
  return login.match(validEmailRegex);
}

async function validateInscription2() {
  //const accounType = $("#createAccountSelectType").val();
  const nom = $("#champNom").val();
  const prenom = $("#champPrenom").val();
  const identifiant = $("#champIdentifiant").val();
  const mdp = $("#champMdp").val();
  const mdp2 = $("#champMdp2").val();
  const rgpd = $("#checkbox-rgpd").is(":checked");

  const pattern = /^[a-zA-Z0-9+_.\-@]+$/;

  $("#champPrenom-messages").empty();
  $("#champNom-messages").empty();
  $("#champIdentifiant-messages").empty();
  $("#champMdp-messages").empty();
  $("#champMdp2-messages").empty();
  //champPrenom-messages
  //champNom-messages
  if (nom === undefined || nom === null || nom.length == 0) {
    $("#champNom-messages").html(
      `<p class="fr-alert fr-alert--error">Nom non renseigné</p>`
    );
  } else if (prenom === undefined || prenom === null || prenom.length == 0) {
    $("#champPrenom-messages").html(
      `<p class="fr-alert fr-alert--error">Prénom non renseigné</p>`
    );
  } else if (
    identifiant === undefined ||
    identifiant === null ||
    identifiant.length == 0
  ) {
    $("#champIdentifiant-messages").html(
      `<p class="fr-alert fr-alert--error">Identifiant non renseigné</p>`
    );
  } else if (!session.isAdminOrTeacher() && !pattern.test(identifiant)) {
    $("#champIdentifiant-messages").html(
      `<p class="fr-alert fr-alert--error">Les identifiants autorisés ne comportent que des lettres, des chiffres et les symboles +_.-@</p>`
    );
  } else if (session.isAdminOrTeacher() && !isValidEmail(identifiant)) {
    $("#champIdentifiant-messages").html(
      `<p class="fr-alert fr-alert--error">Veuillez indiquer une adresse email valide.</p>`
    );
  } else if (mdp === undefined || mdp === null || mdp.length == 0) {
    $("#champMdp-messages").html(
      `<p class="fr-alert fr-alert--error">Mot de passe non renseigné</p>`
    );
  } else if (mdp === undefined || mdp === null || mdp.length < 8) {
    $("#champMdp-messages").html(
      `<p class="fr-alert fr-alert--error">Le mot de passe doit contenir au minimum 8 caractères</p>`
    );
  } else if (mdp == accessGroupe) {
    $("#champMdp-messages").html(
      `<p class="fr-alert fr-alert--error">Le mot de passe (privé et personnel) doit être différent du code d'accès au groupe (public).</p>`
    );
  } else if (mdp != mdp2) {
    $("#champMdp2-messages").html(
      `<p class="fr-alert fr-alert--error">Les mots de passe ne sont pas identiques.</p>`
    );
  } else if (!rgpd) {
    $("#checkbox-rgpd-messages").html(
      `<p class="fr-alert fr-alert--error">Vous devez accepter les conditions d'utilisation</p>`
    );
  } else {
    $("#champMdp").val("");
    $("#champMdp2").val("");
    session.saveScreen(null);
    await app.createAccount({
      nom: nom,
      prenom: prenom,
      type: accounType,
      login: identifiant,
      accesGroupe: accessGroupe,
      password: mdp,
    });
    return false;
  }
  return false;
}

function getSelectVal(id) {
  const val = $(id).val();
  if (val === null) return "";
  return val;
}

function setSelectVal(id, val) {
  $(id).val(val);
}

function setRadioVal(id, val) {
  $("#radio_" + id + "_" + val).prop("checked", true);
}
