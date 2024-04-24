// TUNNEL CONNEXION #BB
// TODO: handle hidden questions (depends of the class of the user).

import * as data from "../data/data";
import * as params from "../config/params";
import * as animate from "./animate/animate";
import { Modal } from "bootstrap";
import * as ui from "./ui";
import { handlers } from "../app/events";
import * as profileTab from "./tabs/profileTab";
import * as session from "../app/session";

import $ from "jquery";

export function openTutoModal(title, content, buttonLabel) {
  const $div = $("#tuto_modal");

  if (!buttonLabel) buttonLabel = "C'est parti!";
  $("#close_button", $div).html(buttonLabel);
  const myModal = Modal.getOrCreateInstance($div);

  $(".tuto_modal_header", $div)
    .empty()
    .append($(`<h3 class="my-2 myBleuMarine">${title}</h3>`));

  $(".tuto_modal_content", $div)
    .empty()
    .append($(`<p class="m-2 p-2 myBleuMarine">${content}</p>`));
  myModal.show();
}

export function startPreferencesTunnel() {
  beginTunnel("preferences", true);
}

export function startProfileTunnel() {
  beginTunnel("profil", true);
}

/* Show questions from a specific group. */
function showQuestionsFromGroup(group, questions) {
  $(".profile_question").hide();
  const shown = [];
  for (const question of questions) {
    let show = question.group == group;
    if (show && question.id == "spe_classes") {
      show =
        Object.entries(data.getSourceAutoComplete("spe_classes")).length > 0;
    }
    $("#" + question.id + "_div").toggle(show);
    if (show) shown.push(question);
  }
  profileTab.updateQuestionsVisibility(shown);
}

function getGroups() {
  const category = currentCategory;
  const questions = data.getQuestions(category);
  return Array.from(new Set(questions.map((q) => q.group)));
}

export function updateUI(allowExit = null) {
  $("#profile").show();

  const category = currentCategory;
  const current = currentGroupIndex;
  const questions = data.getQuestions(category);

  if (allowExit == null) {
    allowExit = allowTunnelExit(category);
  }

  //selective deactivation of menu items
  $(".profile-tab").prop("disabled", !allowExit);

  const groups = getGroups(category);

  showQuestionsFromGroup(groups[current], questions);

  const HTMLprogressBar = $("#tunnel_connexion_progress_bar_list").empty();

  for (const i in groups) {
    const isCurrent = i == current;
    const group = groups[i];
    const isComplete = i < current;
    const label = params.params.questionGroups[group];
    const HTMLprogressBarElement = $(
      `<a href="#" group_index ="${group}" class="progressbar-step profile_nav d-flex align-items-center flex-column 
      ${isCurrent ? " is-current " : " "}
      ${isComplete ? " is-complete " : " "}"
      >
      <svg class="progressbar-icon">
      <use xlink:href="#checkmark-bold"/>
      </svg>
      <span class="progressbar-step-label">
      ${label}</span>
      </a>`
    );
    HTMLprogressBar.append(HTMLprogressBarElement);
  }

  const question_spe = data
    .getQuestions()
    .filter((q) => q.id == "spe_classes")[0];
  const niveau = data.getProfileValue("niveau");
  if (niveau != "prem" && niveau != "term") {
    $("#q_txt_spe_classes").html(question_spe.txt2);
    $("#q_explain_spe_classes").html(question_spe.explain2);
  } else {
    $("#q_txt_spe_classes").html(question_spe.txt);
    $("#q_explain_spe_classes").html(question_spe.explain);
  }
  profileTab.setUpAutoComplete("spe_classes", 0);

  enableQuestionsNavigation(allowExit);

  animate.scrollTop();
}

function emptyRequiredQuestions(group, questions) {
  const isEmpty = (str) => !str.trim().length;
  const emptyQuestions = [];

  for (const question of questions) {
    if (question.group == group && question.required) {
      if (isEmpty($("#" + question.id).val())) {
        emptyQuestions.push(question);
      }
    }
  }
  return emptyQuestions;
}

function warningRequiredQuestions(group, questions, emptyQuestions) {
  const warningMessage = $(
    `<div id="warning_message" class="profile_question col border mb-5 question rounded p-4 bg-warning">
        Attention, les champs suivants sont requis : ` +
      emptyQuestions.map((q) => q.txt).join(", ") +
      `.
        ` +
      (params.params.questionsExplained[group]
        ? `<br><br>` + params.params.questionsExplained[group]
        : ``) +
      `
      </div>`
  );
  // Remove previous warning message if any.
  $("#warning_message").remove();
  $("#profile-div").prepend(warningMessage);
}

let currentGroupIndex = 0;
let currentCategory = "";

/* Show "Mes préférences" divs one by one when first user connexion. */

function enableQuestionsNavigation(enable) {
  $(".profile_nav").off("click");
  if (enable) {
    $(".profile_nav").css("pointer-events", "auto");
    $(".profile_nav").on("click", (event) => {
      const group_index_str = $(event.currentTarget)
        .closest(".profile_nav")
        .attr("group_index");
      const group_index = parseInt(group_index_str);
      const groups = getGroups(currentCategory);
      for (const i in groups) {
        if (groups[i] == group_index) currentGroupIndex = i;
      }
      updateUI(enable);
      enableQuestionsNavigation(true);
    });
  } else {
    $(".profile_nav").css("pointer-events", "none");
  }
}

function allowTunnelExit(category) {
  const isProfile = category == "profil";
  const isPreferences = category == "preferences";
  const profileDone = session.getProfileCompletenessLevel() >= 1;
  const preferencesDone = session.getProfileCompletenessLevel() >= 2;
  return (isProfile && profileDone) || (isPreferences && preferencesDone);
}

export function beginTunnel(category, skipDialog) {
  //si au moment de rentrer dans le tunnel les questions obligatoires
  //ne sont pas remplies alors on empêche de quitte r le tunnel
  const isProfile = category == "profil";
  const isPreferences = category == "preferences";

  if (isProfile) {
    $("#tunnel-header").html("TON PROFIL");
    ui.showProfileTab();
    if (!skipDialog) {
      openTutoModal(
        "Ton profil",
        `<p>Bonjour, je suis Léa, 
      je vais t'accompagner dans ton exploration des formations du supérieur.
      Mon but: que tu puisses sélectionner dès à présent des formations qui te plaisent
      et te correspondent, afin de préparer
      au mieux ton entrée dans le sup.</p>
      </p>
      <p>
      Tout d’abord tu vas devoir créer ton profil , en renseignant quelques informations !
    </p>
    `
      );
    }
  } else if (isPreferences) {
    $("#tunnel-header").html("TES PREFERENCES");
    ui.showPReferencesTab();

    if (!skipDialog) {
      openTutoModal(
        "Tes Préférences",
        `<p>Maintenant tu vas pouvoir nous donner des éléments qui affineront ton exploration d’orientation, en nous renseignant sur tes préférences. </p>
    `
      );
    }
  } else {
    throw new Error("Tunnel category unknown");
  }

  currentGroupIndex = 0;
  currentCategory = category;

  // Hide elements before the end of the tunnel.
  //$("#myTab").hide();

  const allowExit = allowTunnelExit(category);

  updateUI(allowExit);

  const back_button = $(
    `<button id="back_button" class="btn btn-lg btn-primary rounded p-4">Précédent</button>`
  ).css("visibility", "hidden");
  const next_button = $(
    `<button id="next_button" class="btn btn-lg btn-primary rounded p-4">Suivant</button>`
  );
  const back_next_buttons = $(
    `<div id="back_next_buttons" class="p-4 py-lg-3 mb-5 text-center"></div>`
  )
    .css("display", "flex")
    .css("justify-content", "space-between")
    .append(back_button)
    .append(next_button);

  $("#tunnel_arrows_container").show().empty().append(back_next_buttons);

  back_button.on("click", () => {
    if (currentGroupIndex > 0) {
      currentGroupIndex--;
      back_button.css(
        "visibility",
        currentGroupIndex > 0 ? "visible" : "hidden"
      );
      updateUI(allowExit);
    }
  });
  next_button.on("click", () => {
    const emptyQuestions = emptyRequiredQuestions(
      currentGroupIndex,
      data.getQuestions(currentCategory)
    );

    if (emptyQuestions.length == 0) {
      const nbGroups = getGroups().length;
      if (currentGroupIndex < nbGroups) {
        currentGroupIndex++;
        back_button.css(
          "visibility",
          currentGroupIndex > 0 ? "visible" : "hidden"
        );
        updateUI(allowExit);
      }
      if (currentGroupIndex >= nbGroups) {
        endTunnel();
      }
    } else {
      warningRequiredQuestions(
        currentGroupIndex,
        data.getQuestions(currentCategory),
        emptyQuestions
      );
    }
  });
}

function endTunnel() {
  const isProfile = currentCategory == "profil";
  const isPreferences = currentCategory == "preferences";

  const currentProfileCompleteness = session.getProfileCompletenessLevel();
  if (isProfile)
    session.setProfileCompletenessLevel(
      Math.max(currentProfileCompleteness, 1)
    );
  if (isPreferences)
    session.setProfileCompletenessLevel(
      Math.max(currentProfileCompleteness, 2)
    );
  $(".profile-tab").prop("disabled", false);
  //handlers.finishTunnel(currentCategory);
}
