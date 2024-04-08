/* Copyright 2022 © Ministère de l'Enseignement Supérieur, de la Recherche et de
l'Innovation,
    Hugo Gimbert (hugo.gimbert@enseignementsup.gouv.fr)

    This file is part of orientation-parcoursup.

    orientation-parcoursup is free software: you can redistribute it and/or modify
    it under the terms of the Affero GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    orientation-parcoursup is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    Affero GNU General Public License for more details.

    You should have received a copy of the Affero GNU General Public License
    along withorientation-parcoursup  If not, see <http://www.gnu.org/licenses/>.

 */

import $ from "jquery";
import * as animate from "./animate/animate";
import * as profileTab from "./tabs/profileTab";
import * as group from "./tabs/group";
import * as groupDetails from "./tabs/groupDetails";
import * as studentDetails from "./tabs/studentDetails";
import * as admin from "./tabs/admin";
import * as favoris from "./tabs/favoris";
import * as rejected from "./tabs/bin";
import * as suggestions from "./tabs/exploration";
import * as notes from "./notes/notes";
import * as details from "./details/detailsModal";
import * as data from "./../data/data";
import * as account from "./account/account";
import * as connect from "./account/connect";
import * as bin from "./tabs/bin";
import * as session from "../app/session";

//import * as bsp from bootstrap-show-password;

import { Tab } from "bootstrap";
import { handlers } from "../app/events";
import { isAdmin, isAdminOrTeacher, getRole, getLogin } from "../app/session";
import { Modal } from "bootstrap";

export {
  initOnce,
  loadProfile,
  loadGroupsInfo,
  updateSuggestionsTab,
  displayServerError,
  displayClientError,
  displayProfileTabs,
};

const screens = ["landing", "loading", "connect", "connected"];

const screensHandlersInit = {
  connect: () => connect.init(),
};

async function fetchData(screen) {
  return new Promise((resolve, reject) => {
    $.ajax({
      url: "html/" + screen + ".html",
      method: "GET",
      dataType: "html",
      success: function (html) {
        resolve(html); // Resolve the Promise with the fetched HTML content
      },
      error: function () {
        reject(new Error("Failed to fetch data")); // Reject the Promise with an error
      },
    });
  });
}

async function showScreen(screen, ph = null) {
  if (ph === null) {
    ph = `main-placeholder`;
  }
  $(`#main-placeholder`).hide();
  $(`#landing-placeholder`).hide();
  $(`#main-placeholder`).off();
  $(`#landing-placeholder`).off();
  const $div = $(`#${ph}`);
  $div.show();
  for (const scr of screens) $(`#${scr}`).toggle(scr === screen);

  const html = await fetchData(screen);
  $div.html(html);
  if (screen in screensHandlersInit) {
    screensHandlersInit[screen]();
  }
}

async function showSubScreen(subscreen) {
  await showScreen("main");
  const html = await fetchData(subscreen);
  $(`#sub-placeholder`).html(html);
}

async function showConnectedScreen(subscreen) {
  await showScreen("connected");
  const html = await fetchData(subscreen);
  $(`#myTabContent`).html(html);
}

export function injectHtml() {
  const m = {
    "header.html": "header-placeholder",
    "footer.html": "footer-placeholder",
    "rgpd_content.html": "rgpd-placeholder",
    "modals/oubli_mdp.html": "oubli_mdp-placeholder",
  };
  for (const [file, id] of Object.entries(m)) {
    fetch("html/" + file)
      .then((response) => response.text())
      .then((html) => {
        $(`#${id}`).html(html);
      });
  }
}

export async function showDataLoadScreen() {
  await showScreen("loading");
}

export async function showConnectionScreen() {
  await showScreen("connect", "landing-placeholder");
}

export async function showLandingScreen() {
  //$(".body").addClass("landing");
  await showScreen("landing", "landing-placeholder");
  $("#landing-placeholder")
    .off()
    .on("click", () => {
      showConnectionScreen();
    });
}

export async function showInscriptionScreen1() {
  await showSubScreen("inscription1");
}
export async function showInscriptionScreen2() {
  await showSubScreen("inscription2");
}
export async function showRecherche(affinities) {
  await showConnectedScreen("recherche");
  clearAffinityCards();
  for (let i = 0; i < 20; i++) {
    if (i >= affinities.length) break;
    const aff = affinities[i];
    if (i == 0) displayFormationDetails(aff.key);
    addAffinityCard(aff);
  }
}

function clearAffinityCards() {
  $("#explore-div-resultats-left-liste").empty();
}
function addAffinityCard(aff) {
  const $div = buildAffinityCard(aff);
  $("#explore-div-resultats-left-liste").append($div);
  $div.on("click", () => {
    displayFormationDetails(aff.key);
  });
}

function displayFormationDetails(key) {
  const label = data.getLabel(key);
  $("#formation-details-title").html(label);
  const summary = data.getSummary(key);
  if (summary && summary.length > 0) {
    $("#formation-details-summary").show();
    $("#formation-details-summary").html(summary);
  } else {
    $("#formation-details-summary").hide();
    $("#formation-details-summary").empty();
  }
}
function buildAffinityCard(aff) {
  const label = data.getLabel(aff.key);
  const $div = $(`<div class="formation-card">
          <div class="formation-card-header">
            <div class="formation-card-header-type">FORMATION</div>
            <div class="formation-card-header-sep"></div>
            <div class="formation-card-header-affinity">
              Taux d'affinité ${Math.trunc(100 * aff.affinite)}%
            </div>
          </div>
          <div class="card-formation-title">
            ${label}
          </div>
          <div class="card-geoloc">
            <img src="img/loc.svg" alt="geo" />
            Paris &middot; Bordeaux &middot; Noisy-le-grand &middot; Corté
            &middot; +8
          </div>
          <div class="card-metiers-header">
            Métiers accessibles après cette formation
          </div>
          <div class="card-metiers-list">
            <div class="card-metier">Chef de projet</div>
            <div class="card-metier">Designer graphique</div>
            <div class="card-metier">Graphiste</div>
            <div class="card-metier">+4</div>
          </div>
        </div>`);
  if (aff.exemples.length == 0) {
    $(".card-metiers-header", $div).empty();
    $(".card-metiers-list", $div).empty();
  } else {
    $(".card-metiers-header", $div).html(
      "Métiers accessibles après cette formation"
    );
    $(".card-metiers-list", $div).empty();
    for (let j = 0; j < 5; j++) {
      if (j >= aff.exemples) break;
      const metier = aff.exemples[j];
      const labelMetier = data.getLabel(metier);
      $(".card-metiers-list", $div).append(
        `<div class="card-metier">${labelMetier}</div>`
      );
    }
    if (aff.exemples.length > 5)
      $(".card-metiers-list", $div).append(
        `<div class="card-metier">+${aff.exemples.length - 5}</div>`
      );
  }
  return $div;
}

export const tabs = {
  profile: "nav-profile-tab",
  preferences: "nav-preferences-tab",
  groups: "nav-groups-tab",
  account: "nav-account-tab",
  admin: "nav-admin-tab",
  suggestions: "nav-suggestions-tab",
};

function showConnectedScreenOld() {
  showScreen("connected");
  const is = isAdmin();

  $(`#${tabs.admin}`).toggle(isAdmin());

  $(`#${tabs.groups}`).toggle(isAdminOrTeacher());
}

function displayProfileTabs() {
  $(".profile-tab").show();
}

export function hideProfileTabs() {
  $(".profile-tab").hide();
}

export function showTab(label) {
  const id = tabs[label];
  showTabWithId(id);
}

export function showTabWithId(id) {
  showConnectedScreenOld();
  const someTabTriggerEl = document.querySelector(`#${id}`);
  if (someTabTriggerEl != null) {
    const tab = new Tab(someTabTriggerEl);
    tab.show();
  }
  //handlers.logAction("showTab " + label);
}

export function showProfileTab() {
  $("#profile").show();
  showTab("profile");
}
export function showPReferencesTab() {
  $("#preferences").show();
  showTab("preferences");
}
export function showFavorisTab() {
  $("#profile").hide();
  showTab("favoris");
}
export function showAccountTab() {
  $("#profile").hide();
  showTab("account");
}

export function hideMainProfileTab() {
  $("#profile").hide();
}

export async function showSuggestionsTab() {
  $("#profile").hide();
  showTab(`suggestions`);
}

export function showGroupsTab() {
  $("#profile").hide();
  showTab("groups");
  group.reloadTab();
}
export function showAdminTab() {
  $("#profile").hide();
  showTab("admin");
}

export function initPostData(login, infos) {
  suggestions.reloadTab();

  $("#loginFeedbackStudent").html(`Ton identifiant est <b>'${login}'</b>.`);
  $("#loginFeedbackTeacher").html(
    `Votre identifiant est <b>'${login}' et vous avez le rôle '${infos.apparentType}'</b>.`
  );
  $(".toTeacherButton").toggle(
    infos.type != "lyceen" && infos.type != "demo_lyceen"
  );
}

/*register some static handlers and
  set up initial visibility of elements */
function initOnce() {
  //$("#front-feedback").html("Maintenance en cours, service suspendu.").show();
  $("#front-feedback").html("").hide();

  /* auto scroll upon tab change */
  for (const tabEl of document.querySelectorAll(
    'button[data-bs-toggle="tab"]'
  )) {
    tabEl.addEventListener("shown.bs.tab", (event) => {
      animate.scrollTop();
      //useless ? loadProfile(data.getData().profile);
    });
  }

  const addToHistory = (e) => {
    const id = e.currentTarget.id;
    handlers.addTransitionToHistory(id, {
      curGroup: session.getSelectedGroup(),
      curStudent: session.getSelectedStudent(),
    });
  };

  $("#show-favoris-button")
    .off("click")
    .on("click", function (e) {
      addToHistory(e);
      showFavorisTab();
    });

  $("#nav-profile-tab")
    .off("click")
    .on("click", (e) => {
      addToHistory(e);
      handlers.startProfileTunnel();
    });
  $("#nav-preferences-tab")
    .off("click")
    .on("click", (e) => {
      addToHistory(e);
      const id = e.currentTarget.id;
      handlers.addTransitionToHistory(id, {
        curGroup: session.getSelectedGroup(),
        curStudent: session.getSelectedStudent(),
      });
      handlers.startPreferencesTunnel();
    });
  $("#nav-account-tab")
    .off("click")
    .on("click", (e) => {
      addToHistory(e);
      showAccountTab();
    });
  //deprecated suggestions.initOnce();

  //
}

let loadingProfile = false;

export function updateNotes() {
  const myNotes = data.getNotes("teacher");
  const id =
    isAdminOrTeacher() && lastStudentProfile
      ? lastStudentProfile.prenom + " " + lastStudentProfile.nom
      : null;
  const $notes = notes.getSuggHTMLNotes(
    data.getScreenName(),
    id,
    getLogin(),
    myNotes,
    "teacher",
    "Ta prise de notes...",
    ""
  );
  if (!isAdminOrTeacher()) {
    $("#teacherdialog")
      .html(
        `<p>Ces notes sont partagées avec le(s) référent(s) de ton groupe,
        qui peuvent également t'envoyer des messages.
      </p>
      `
      )
      .append($notes);
  } else if (lastStudentProfile) {
    $(".student_chat").empty().append($notes);
  }
}

function loadProfile() {
  return;
  loadingProfile = true;

  //afficher les tabs nécessaires
  showConnectedScreen(getRole());

  profileTab.reloadTab();

  if (!isAdminOrTeacher()) {
    favoris.reloadTab();
    suggestions.reloadTab();
    rejected.reloadTab();
  }

  updateNotes();

  $(`.bacAttribute`).attr("bac", data.getTypeBacGeneric().index);

  loadingProfile = false;
}

export function showDetails(grpid, stats, explanations, exemples) {
  details.show(grpid, stats, explanations, exemples);
}

let hideFormations = false;

function loadGroupsInfo() {
  const infos = session.getCachedAdminInfos();

  $(".only-students").toggle(!isAdminOrTeacher());
  if (isAdminOrTeacher()) {
    if (isAdmin()) {
      admin.reloadTab(infos);
    }
    group.loadGroupsInfoTeacher(infos);
    displayProfileTabs();
  } else {
    group.loadGroupsInfoStudent(infos.openGroups);
    displayProfileTabs();
  }
  group.reloadTab();
  suggestions.reloadTab();
  $(".lienQuestionnaire").toggle(session.isEvalIndivisible());
  suggestions.hideFormations();
}

export function loadGroupDetails(details) {
  groupDetails.loadGroupDetails(details);
  group.reloadTab();
}

let lastStudentProfile = null;
export function loadStudentProfile(profile, statsGroupes) {
  loadingProfile = true;
  lastStudentProfile = profile;
  studentDetails.loadStudentProfile(profile, statsGroupes);
  group.reloadTab();
  updateNotes(profile);
  loadingProfile = false;
}

function updateSuggestionsTab() {
  suggestions.setupSearchType();
}

export function updateHearts(id, like) {
  if (like) {
    $(`.heart_${id}_icon`).removeClass("bi-heart");
    $(`.heart_${id}_icon`).addClass("bi-heart-fill");
    $(`.heart_${id}_icon`).addClass("red-heart");
  } else {
    $(`.heart_${id}_icon`).removeClass("bi-heart-fill");
    $(`.heart_${id}_icon`).removeClass("red-heart");
    $(`.heart_${id}_icon`).addClass("bi-heart");
    $(`.card-${id}-div`).hide(1000, () => {
      $(`.card-${id}-div`).remove();
    });
  }
}

export function updateFavoris() {
  favoris.reloadTab();
  bin.reloadTab();
}

function displayServerError(msg) {
  const msgHtml = msg
    .replaceAll("\\n", "<br>")
    .replaceAll("\\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
  $("#server-error").html("Erreur serveur: " + msgHtml + "<br><br><br><br>");
}

function displayClientError(msg) {
  const msgHtml = msg
    .replaceAll("\\n", "<br>")
    .replaceAll("\\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
  $("#front-error").html("Erreur client:<br> " + msgHtml);
}

//validationMessage
export function showValidationRequiredMessage(login, message) {
  //show modal
  $("#validationRequiredLogin").html(login);
  $("#validationRequiredMessage").html(message);
  const myModal = new Modal(document.getElementById("validationRequiredModal"));
  myModal.show();
}

export function showResetPasswordMessageSent(email) {
  //show modal
  $("#resetConfirmationModalEmail").html(email);
  const myModal = new Modal(
    document.getElementById("resetPasswordMessageSent")
  );
  myModal.show();
}

export function showEmailResetMessage() {
  //show modal
  const myModal = new Modal(document.getElementById("emailResetModal"));
  myModal.show();
}
