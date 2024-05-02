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
import * as connect from "./account/connect";
import * as bin from "./tabs/bin";
import * as session from "../app/session";
import * as params from "../config/params";

//import * as bsp from bootstrap-show-password;

import { Tab } from "bootstrap";
import { handlers } from "../app/events";
import { isAdmin, isAdminOrTeacher, getRole, getLogin } from "../app/session";
import { Modal } from "bootstrap";
import { setScreen } from "../app/navigation";
import { trace } from "../services/services";

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
  $("#main-placeholder").css({
    "background-image": "none",
  });
}

async function showSubScreen(subscreen) {
  await showScreen("main");
  const html = await fetchData(subscreen);
  $(`#sub-placeholder`).html(html);
  if (subscreen == "inscription_tunnel") {
    $("#main-placeholder").css({
      "background-image": 'url("../img/bg.svg")',
    });
  }
}

export async function showConnectedScreen(subscreen) {
  await showScreen("connected");
  const html = await fetchData(subscreen);
  $("#header-navigation a").removeAttr("aria-current");
  $(`#nav-${subscreen}`).attr("aria-current", true);
  const $div = $(`#myTabContent`);
  if ($div.length == 0) throw Error("no myTabContent");
  $div.html(html);
}

const tunnelScreens = [
  "statut",
  "scolarite",
  "domaines_pro",
  "interests",
  "metiers",
  "etudes",
  "formations",
];
const tunnelScreensTitles = [
  "Mon projet supérieur",
  "Ma scolarité",
  "Mes domaines professionnels",
  "Mes centres d'intérêt",
  "Les métiers qui m’inspirent",
  "Mes études supérieures",
  "Les formations post-bac",
  "Inscripion terminée",
];
export async function showTunnelScreen(subscreen) {
  await showSubScreen("inscription_tunnel");
  const fromProfile = ["domaines_pro", "interests", "etudes", "scolarite"];
  const prefix = fromProfile.includes(subscreen) ? "profil/" : "inscription/";
  const html = await fetchData(prefix + subscreen);
  const $div = $(`#myTabContent`);
  if ($div.length == 0) {
    throw Error("no myTabContent");
  }
  const changes = {
    scolarite: [[".profile_tab_title", "Dis-nous en plus sur ta scolarité"]],
    domaines_pro: [
      [".profile_tab_title", "Les domaines professionnels qui t'attirent"],
    ],
    interests: [[".profile_tab_title", "Plus tard, je voudrais ..."]],
    etudes: [[".profile_tab_title", "À propos des études supérieures"]],
  };
  $div.html(html);
  if (subscreen in changes) {
    for (const [selector, text] of changes[subscreen]) {
      $(selector, $div).html(text);
    }
  }
  const nbSteps = tunnelScreens.length;
  $(".inscription_progress_steps").empty();
  $(".inscription_progress_bar").toggle(subscreen != "felicitations");
  $("#steps_total").html(nbSteps);
  for (const idx in tunnelScreens) {
    const screen = tunnelScreens[idx];
    const active = screen == subscreen;
    if (active) {
      const nextIdx = 1 + parseInt(idx);
      $("#steps_idx").html(nextIdx);
      $("#step_title").html(tunnelScreensTitles[idx]);
      $("#next_step_title").html(tunnelScreensTitles[nextIdx]);
    }
    $(".inscription_progress_steps").append(
      `<div class="progress_step ${active ? "active" : ""}"></div>`
    );
    //
  }
  if (subscreen === "statut") {
    const icons = [
      ["egg", "poussin", "poussin2"],
      ["monocle", "openstreetmap", "dart"],
      ["poussin", "openstreetmap", "dart"],
    ];
    const sec = new Date().getSeconds();
    const j = Math.round(sec % icons.length);
    for (const i of [0, 1, 2]) {
      $(`#icon-img-idea${i}`).attr("src", `img/${icons[j][i]}.png`);
    }
  }
}

export function displayNextAndBAckButtons(nextScreen, backScreen) {
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
}
export function hideNiveauInformation(niveau) {
  if (niveau === undefined || niveau === null || niveau === "") {
    niveau = "sec";
  }
  $(".premiere-only").toggle(niveau === "term" || niveau === "prem");
  $(".terminale-only").toggle(niveau === "term");
}
function injectInSelect($select, data) {
  for (const [key, value] of Object.entries(data)) {
    $select.append(`<option value="${key}">${value}</option>`);
  }
}

function removeJeVeux(label) {
  if (label.startsWith("Je veux ") || label.startsWith("je veux ")) {
    const answer = label.substring(8);
    if (answer == "") return "";
    return answer.charAt(0).toUpperCase() + answer.slice(1);
  } else {
    return label;
  }
}

function compareAlphanumeric(a, b) {
  // Convert both values to strings
  let strA = String(a.label);
  let strB = String(b.label);

  // Use localeCompare to perform alphanumeric comparison
  return strA.localeCompare(strB, undefined, { numeric: true });
}

function injectInMultiOptions($accordions_group, menus) {
  menus = menus.sort(compareAlphanumeric);
  let menuid = Math.round(10000 * Math.random());
  for (const menu of menus) {
    let emoji1 = menu.emoji;
    let emoji2 = "";
    if (emoji1 === undefined || emoji1 == "") {
      emoji1 = "";
      for (const item of menu.items) {
        if (item.emoji) {
          emoji2 = emoji2 + "&nbsp;&nbsp;" + item.emoji;
        }
      }
    }
    let expand = false;
    const items = menu.items.sort(compareAlphanumeric);

    for (const item of items) {
      let key = item.key;
      if (key === undefined) key = item.id;
      if (data.isSelected(key)) {
        expand = true;
      }
    }
    if (menu.key === undefined) {
      menu.key = menuid++; //this should not be necessary because already loaded in postLoad... fishy...
    }
    const $menu = $(`      
      <section class="fr-accordion multi-options-group">
        <h3 class="fr-accordion__title muti-options-group-header">
          <button
            class="fr-accordion__btn "
            aria-expanded="${expand}"
            aria-controls="accordion-${menu.key}"
          >
            <span class="multi-options-group-emojis">${emoji1}</span>
            ${removeJeVeux(menu.label)}
            <span class="multi-options-group-emojis">${emoji2}</span>
          </button>
        </h3>
        <div class="fr-collapse muti-options-group-content" id="accordion-${
          menu.key
        }">
        <div class"multi-options-instructions">Sélectionne la ou les catégories qui t'intéressent</div>
        <div class="multi-options-group-list">
        </div>
        </div>
      </section>`);
    const $ul = $(`.multi-options-group-list`, $menu);
    for (const item of menu.items) {
      const emoji2 = item.emoji ? item.emoji : "";
      let key = item.key;
      if (key === undefined) key = item.id;
      if (key === undefined) continue;
      $ul.append(
        `<div class="multi-options-item multi-options-item_${key}" key="${key}"><span class="multi-options-item-emoji">${emoji2}</span>${removeJeVeux(
          item.label
        )}</div>`
      );
    }
    $accordions_group.append($menu);
  }
}

export function setupSelects(tabName, divName) {
  let $div = $(divName);
  if (tabName === "scolarite") {
    injectInSelect(
      $("#profile-tab-scolarite-classe-select", $div),
      params.classes
    );
    injectInSelect($("#profile-tab-scolarite-bac-select", $div), params.bacs);
  }
  if (tabName === "etudes") {
    injectInSelect($("#profile-tab-etudes-duree-select", $div), params.durees);
    injectInSelect($("#profile-tab-etudes-app-select", $div), params.apps);
  }
  if (tabName === "domaines_pro") {
    injectInMultiOptions(
      $("#profile-items-domaines-pro", $div),
      data.getDomainesPro()
    );
    updateMultiOptionsItemsStatus();
  }
  if (tabName === "interests") {
    injectInMultiOptions(
      $("#profile-items-interests", $div),
      data.getInterests()
    );
    updateMultiOptionsItemsStatus();
  }
}

async function injectProfileTabs(tabNames) {
  console.log("Injecting profile tab " + tabNames);
  const htmls = {};
  for (const tabName of tabNames) {
    htmls[tabName] = await fetchData("profil/" + tabName);
  }

  for (const tabName of tabNames) {
    //console.log("Fetched " + tabName);
    let $div = $(`#tab-${tabName}-panel`);
    /*while ($div.length == 0) {
    $div = $(`#tab-${tabName}-panel`);
    //throw Error(`no div #tab-${tabName}-panel`);
  }*/
    $div.html(htmls[tabName]);
    setupSelects(tabName, `#tab-${tabName}-panel`);
  }
  //console.log("Intitialized profile tab " + tabName);
}

export function updateMultiOptionsItemsStatus() {
  //for every key in the profile, update the status of the corresponding item
  //multi-options-item_${key}
  const keys = data.getProfileKeys();
  $(`.multi-options-item`).removeClass("selected");
  for (const key of keys) {
    const $divs = $(`.multi-options-item_${key}`);
    if ($divs.length > 0) {
      $divs.addClass("selected");
    }
  }
}

export function injectHtml() {
  const m = {
    "header.html": "header-placeholder",
    "footer.html": "footer-placeholder",
    "modals/oubli_mdp.html": "oubli_mdp-placeholder",
    "modals/validate_account.html": "validate_account-placeholder",
    "modals/error.html": "error-placeholder",
    "modals/metier.html": "metier-placeholder",
    "modals/rgpd.html": "rgpd-modal-placeholder",
    "rgpd_content.html": "rgpd-placeholder",
  };
  for (const [file, id] of Object.entries(m)) {
    fetch("html/" + file)
      .then((response) => response.text())
      .then((html) => {
        $(`#${id}`).html(html);
        $(".hidden-modal-button").hide();
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
  $(".visible-only-when-connected").hide();
  $(".visible-only-when-disconnected").show();

  $("#landing-placeholder")
    .off()
    .on("click", () => {
      showConnectionScreen();
    });
}

export async function showInscriptionScreen1() {
  showSubScreen("inscription1");
}
export async function showInscriptionScreen2() {
  showSubScreen("inscription2");
}
export async function showBoard() {
  return showConnectedScreen("board");
}
export async function showGroupesScreen() {
  return showConnectedScreen("groupes");
}
export async function showSelection() {
  return showConnectedScreen("selection");
}
export async function showRechercheScreen() {
  await showConnectedScreen("recherche");
}
export async function showProfileScreen() {
  await showConnectedScreen("profile");
  //inject profile data
  await injectProfileTabs(["scolarite", "etudes", "domaines_pro", "interests"]);
  $(".profile-div-prenomnom").html(data.getPrenomNom());
  $(".prenomnom").html(data.getPrenomNom());
  $(".profile-div-email").html(session.getLogin());
}
export async function showTeacherProfileScreen() {
  await showConnectedScreen("profile_teacher");
  $(".profile-div-prenomnom").html(data.getPrenomNom());
  $(".prenomnom").html(data.getPrenomNom());
  $(".profile-div-email").html(session.getLogin());
}

export function showWaitingMessage() {
  $("#explore-div-wait").show();
  $("#explore-div-resultats").hide();
}
export function showRechercheData(data, showAffinities) {
  clearAffinityCards();
  $("#explore-div-wait").hide();
  $("#explore-div-resultats").show();
  const nbResults = data.length;
  $("#search-results-nb").html(nbResults);
  for (let i = 0; i < nbResults; i++) {
    const dat = data[i];
    if (i == 0) {
      displayItemDetails(dat, false);
    }
    addAffinityCard(dat, false);
  }
  $(".formation-card-header-affinity").toggle(showAffinities);
  //like and dislike handlers
}

export function showFavoris(data) {
  clearAffinityCards();
  $("#explore-div-wait").hide();
  $("#explore-div-resultats").show();

  const nbResults = data.length;
  $("#search-results-nb").html(nbResults);
  for (let i = 0; i < nbResults; i++) {
    const dat = data[i];
    if (i == 0) {
      displayItemDetails(dat, true);
    }
    addAffinityCard(dat, true);
  }
  //like and dislike handlers
}

export function showGroupsTab2() {
  /*
  $("#profile").hide();
  showTab("groups");
  group.reloadTab();*/
}

export function clearAffinityCards() {
  $("#explore-div-resultats-left-liste").empty();
  $("#explore-div-resultats-right").hide();
  $("#explore-div-resultats-left-noresult").show();
  $("#explore-div-resultats-left-entete").hide();
}
function addAffinityCard(dat, nodetails) {
  $("#explore-div-resultats-left-noresult").hide();
  $("#explore-div-resultats-left-entete").show();

  const $div = buildAffinityCard(
    dat.key,
    dat.fav,
    dat.type,
    dat.affinity,
    dat.cities,
    dat.examples,
    nodetails
  );
  if ($div !== null) {
    updateFav(dat.key);
    $("#explore-div-resultats-left-liste").append($div);
    $div.on("click", () => {
      displayItemDetails(dat, nodetails);
    });
  } else {
    console.log("null formation " + dat.key);
  }
}

let currentKeyDisplayed = "";

function displayItemDetails(dat, nodetails) {
  const key = dat.key;
  const isMetier = data.isMetier(key);
  if (isMetier) {
    displayMetierDetails(dat, nodetails);
    $(".explore-specific-formation").hide();
    $(".explore-specific-metier").show();
  } else {
    displayFormationDetails(dat, nodetails);
    $(".explore-specific-formation").show();
    $(".explore-specific-metier").hide();
  }
  $(".formation-details-actions .add-to-favorites-btn").attr("data-id", key);
  $(".formation-details-actions .add-to-bin-btn").attr("data-id", key);
  $("#formation-details-header-nav-central-icon").attr("data-id", key);
  currentKeyDisplayed = key;
  updateFav(dat.key);
}

function displayMetierDetails(dat) {
  $("#explore-div-resultats-right").show();
  const key = dat.key;
  //title
  const label = data.getLabel(key);
  $(".formation-details-title").html(label);
  //summary
  displaySummary(key);
  //Explication
  const devMode = false;
  displayExplanations(dat.explanations, devMode);
  //exemples
  displayMetiers(dat.examples);
}

export function updateGroupsList(groups) {
  const $div = $(".nav_teacher #header-navigation .fr-nav__list").empty();
  for (const group of groups) {
    addGroupEntry(group, $div);
  }
}
function addGroupEntry(group, $div) {
  const li = `
        <li class="fr-nav__item">
          <a
            class="fr-nav__link group_select"
            href="#"
            target="_self"
            aria-current="page"
            group_id="${group.id}"
            >${group.name}</a
          >
        </li>
    `;
  $div.append(li);
}

export function setStudentDetails(details) {
  $("#nb_students_connected").html(details.nb_students_connected);
  const $div = $(".eleves_tiles_container").empty();
  let nb = 0;
  let nb_students_connected = 0;
  let nb_students_completed = 0;
  let nb_students_selected = 0;

  const niveau = details.niveau;

  for (const student of details.students) {
    addStudentTile(student, $div);
    nb++;
    nb_students_connected++;
    if (student.profileCompletenessPercent == "100") nb_students_completed++;
    if (
      (niveau == "seconde" &&
        parseInt(student.nbFormationsFavoris) +
          parseInt(student.nbMetiersFavoris) >
          0) ||
      student.nbFormationsFavoris > 3
    )
      nb_students_selected++;
  }
  if (nb == 0) nb = 1;
  $("#nb_students_connected").html(nb_students_connected);
  $("#pct_students_connected").html(
    Math.round((100 * nb_students_connected) / nb) + "%"
  );
  $("#nb_students_completed").html(nb_students_completed);
  $("#pct_students_completed").html(
    Math.round((100 * nb_students_completed) / nb) + "%"
  );
  $("#nb_students_selected").html(nb_students_selected);
  $("#pct_students_selected").html(
    Math.round((100 * nb_students_selected) / nb) + "%"
  );
  //objectif_annee

  if (niveau === "seconde") {
    $("#objectif_annee").html(
      "Un objectif en classe de seconde est la sélection d’au moins une formation ou un métier"
    );
    $("#nb_students_selected_objectif").html("une formation ou un métier");
    $("#en_quelques_chiffres_stat3").show();
  } else if (niveau === "premiere") {
    $("#objectif_annee").html(
      "Un objectif en classe de première est la sélection d’au moins 3 formations, et une réflexion sur les attendus des formations"
    );
    $("#nb_students_selected_objectif").html("trois formations");
    $("#en_quelques_chiffres_stat3").show();
  } else if (niveau === "terminale") {
    $("#objectif_annee").html(
      "Un objectif en classe de terminale est la sélection d’au moins 3 formations, et la sélection de voeux Parcoursup associés"
    );
    $("#nb_students_selected_objectif").html("trois formations");
    $("#en_quelques_chiffres_stat3").show();
  } else {
    $("#objectif_annee").html();
    $("#en_quelques_chiffres_stat3").hide();
  }
}
function addStudentTile(student, $div) {
  let statut = "A quelques pistes au sujet de son orientation";
  if (student.reflexionStatus !== undefined) {
    if (student.reflexionStatus == "0") {
      statut = "N'a pas encore d'idée au sujet de son orientation"; //Je n'ai pas encore d’idée
    }
    if (student.reflexionStatus == "1") {
      statut = "A quelques pistes au sujet de son orientation"; //Je n'ai pas encore d’idée
    }
    if (student.reflexionStatus == "2") {
      statut = "A un projet précis"; //Je n'ai pas encore d’idée
    }
  }
  let nbFormations = "";
  if (student.nbFormationsFavoris == "0") {
    nbFormations = `<span class="eleve_tile_nb_formations">Aucune formation</span>
            sélectionnée`;
  } else if (student.nbFormationsFavoris == "1") {
    nbFormations = `<span class="eleve_tile_nb_formations">1 formation</span>
            sélectionnée`;
  } else {
    nbFormations = `<span class="eleve_tile_nb_formations">${student.nbFormationsFavoris} formations</span>
            sélectionnées`;
  }
  let nbMetiers = "";
  if (student.nbMetiersFavoris == "0") {
    nbMetiers = `<span class="eleve_tile_nb_metiers">Aucun métier</span>
            sélectionné`;
  } else if (student.nbMetiersFavoris == "1") {
    nbMetiers = `<span class="eleve_tile_nb_metiers">1 métier</span>
            sélectionné`;
  } else {
    nbMetiers = `<span class="eleve_tile_nb_metiers">${student.nbMetiersFavoris} métiers</span>
            sélectionnés`;
  }
  const $tile = $(`
        <div class="eleves_tile">
          <div class="eleves_tile_name">${student.name}</div>
          <div class="eleves_tile_percent_senetence">
            Profil complété à <span class="eleve_tile_percent">${student.profileCompletenessPercent}%</span>
          </div>
          <div class="eleves_tile_pistes">
            ${statut}
          </div>
          <div class="eleves_tile_formations">
            <span class="fr-icon-award-line"></span>
            ${nbFormations}
          </div>
          <div class="eleves_tile_formations">
            <span class="fr-icon-briefcase-line"></span>
            ${nbMetiers}
          </div>
          <div class="eleves_tile_buttons">
            <button login="${student.login}" name="${student.name}" class="fr-btn student_selection_button" title="sa sélection">Sa sélection</button>
            <div class="sep"></div>
            <button login="${student.login}" name="${student.name}" class="fr-btn fr-btn--secondary student_profile_button" title="son profil">
              Son profil
            </button>
          </div>
        </div>`);
  const nb =
    parseInt(student.nbFormationsFavoris) + parseInt(student.nbMetiersFavoris);
  if (nb == 0) $(".student_selection_button", $tile).hide();
  $div.append($tile);
}

function updateFavDiv(fav, $div) {
  if (fav) {
    $(".add-to-favorites-btn", $div).html("Ajouté à ma sélection");
    $(".add-to-favorites-btn", $div).addClass("activated");
    $(".add-to-favorites-btn", $div).addClass("favori");
    $(".add-to-bin-btn", $div).html("Plus intéressé");
    $(".add-to-bin-btn", $div).show();
  } else {
    $(".add-to-favorites-btn", $div).html("Ajouter à ma sélection");
    $(".add-to-favorites-btn", $div).removeClass("activated");
    $(".add-to-favorites-btn", $div).removeClass("favori");
    $(".add-to-bin-btn", $div).html("Pas intéressé");
    $(".add-to-bin-btn", $div).hide();
  }
}

export function updateFav(key) {
  if (session.isAdminOrTeacher()) {
  } else {
    const fav = data.isFavoris(key);
    if (key == currentKeyDisplayed) {
      updateFavDiv(fav, $("#explore-div-resultats-right"));
      if (fav) {
        $(`#formation-details-header-nav-central-icon`)
          .removeClass("fr-icon-heart-line")
          .addClass("fr-icon-heart-fill");
      } else {
        $(`#formation-details-header-nav-central-icon`)
          .removeClass("fr-icon-heart-fill")
          .addClass("fr-icon-heart-line");
      }
    }
    $(`.icon-favorite_${key}`).toggle(fav);
  }
}

function displayFormationDetails(dat) {
  const $div = $("#explore-div-resultats-right");
  $div.show();

  $(".formation-details-actions").toggle(session.isStudent());
  $(".formation-details-header-nav").toggle(session.isStudent());

  updateFav(dat.key);

  const key = dat.key;

  //title
  const label = data.getLabel(key);
  $(".formation-details-title").html(label);
  //cities
  addGeoloctoDiv(dat.cities, $div);
  //links
  displayUrls(key, dat.fois);
  //summary
  displaySummary(key);
  //diplome
  displayDiplome(key);
  //stats
  displayStats(dat.stats.stats);
  //Explication
  const devMode = false;
  displayExplanations(dat.explanations, devMode);
  //exemples
  displayMetiers(dat.examples);
  //attendus
  displayAttendus(key);
  //grille analyse
  displayGrilleAnalyseCandidatures(key);
}

function displaySummary(key) {
  const summary = data.getSummary(key);
  if (summary && summary.length > 0) {
    const cleanup = summary.replaceAll("h3", "p").replaceAll("h2", "p");
    $(".formation-details-summary").show().html(cleanup);
  } else {
    $(".formation-details-summary").empty().hide();
  }
}

function displayDiplome(key) {
  const diplome = data.getSummaryFormation(key);
  if (diplome && diplome.length > 0) {
    const cleanup = diplome.replaceAll("h3", "p").replaceAll("h2", "p");
    $(".formation-details-diplome").show().html(cleanup);
    $("#tabpanel-le-diplome").show();
  } else {
    $(".formation-details-diplome").empty().hide();
    $("#tabpanel-le-diplome").hide();
  }
}

function displayUrls(subkey, forsOfInterest) {
  const urls = [...data.getUrls(subkey, true)];
  const label = data.getExtendedLabel(subkey);
  if (data.isFiliere(subkey)) {
    let uri = data.getParcoursupSearchAdress([subkey], label, forsOfInterest);
    uri = uri.replace("'", " "); //this caracter is ok in uris but not in html
    $("#formation-details-link-psup")
      .empty()
      .append(
        `<div class="formation-details-link">
          <a href="${uri}" target="_psup"
            >${getUrlLabel(uri)}<img
              src="img/link-dsfr.svg"
              alt="lien vers le site"
          /></a>
        </div>
        `
      );
  }

  $(".formation-details-links-other").empty();
  for (const url of urls) {
    $(".formation-details-links-other").append(
      `<div class="formation-details-link">
          <a href="${url}" target="_onisep"
            >${getUrlLabel(url)}<img
              src="img/link-dsfr.svg"
              alt="lien vers le site"
          /></a>
        </div>
        `
    );
  }
}

function getUrlLabel(url) {
  if (url.includes("onisep") || url.includes("terminales")) {
    return "Lire la suite";
  }
  if (url.includes("pole")) {
    return "Plus de détails";
  }
  if (url.includes("parcoursup")) {
    return "Plus de détails sur Parcoursup";
  }
  return "Plus d'infos";
}

function format(x, nbDigits) {
  return new Intl.NumberFormat("fr-FR", {
    maximumFractionDigits: nbDigits,
    style: "percent",
    roundingMode: "floor",
  })
    .format(x)
    .replace(",", ".");
}

function displayStats(statsAll) {
  if (
    statsAll == null ||
    statsAll == undefined ||
    !(data.tousBacs in statsAll)
  ) {
    $(".formation-details-stats-moyenne").hide();
    $("#formation-details-stats-bloc-nb-admis-div").hide();
  } else {
    $(".formation-details-stats-moyenne").show();
    const nbAdmis = [];
    let total = -1;
    for (const o of Object.entries(statsAll)) {
      const nb = o[1].nbAdmis;
      let bac = o[0];
      if (bac == data.tousBacs) {
        total = nb;
      } else if (nb > 0) {
        nbAdmis.push(o);
      }
    }

    if (total > 0 && nbAdmis.length > 0) {
      $(".formation-details-stats-bloc-nb-admis").html(total);
      $(".formation-details-stats-bacs-repartition").empty();
      nbAdmis.sort((x, y) => {
        return y[1].nbAdmis - x[1].nbAdmis;
      });

      /*<ol class="progressbar mt-3">
    <span class="stats-bar-step out" style=";width:${per5}%"> 
      <span class="stats-bar-step-label rounded-pill py-1 px-2 stats">${note5}</span>
    </span>*/

      let lastpct = 0;
      for (const d of nbAdmis) {
        let bac = data.ppBac(d[0]);
        const nb = d[1].nbAdmis;
        const pct = (100 * nb) / total;
        if (pct < 1) break;
        $(".formation-details-stats-bacs-repartition").append(
          `
          <div class="formation-details-stats-bac-badge">${bac} ${format(
            pct / 100,
            0
          )}</div>
          `
        );
      }
      $(".formation-details-stats-moyenne").show();
    } else {
      $("#formation-details-stats-bloc-nb-admis-div").hide();
      $(".formation-details-stats-moyenne").hide();
    }
  }
  const bac = data.getProfileValue("bac");
  const niveau = data.getProfileValue("niveau");
  const showNotes = niveau == "prem" || niveau == "term";

  const o = data.getStats(statsAll, bac);
  const stats = o.stats;
  const statsBac = o.statsBac;
  const statsTousBacs = o.statsTousBacs;
  if ((showNotes && statsTousBacs) || statsBac) {
    const $div = getStatsScolDiv(bac, statsTousBacs, statsBac);
    $(".formation-details-stats-moy-admis").empty().append($div);
  }
}

function notGood(stats) {
  const result =
    stats == null ||
    stats === undefined ||
    ((stats.statsScol === undefined ||
      stats.statsScol[data.moyGenIndex()] === undefined) &&
      (stats.nbAdmis === undefined || stats.nbAdmis < 10));
  return result;
}

function getStatsScolDiv(bac, statsTousBacs, statsBac) {
  const highlight = false;
  let stats = statsBac;
  let serie = data.ppBac(bac);
  let qualifSerieBac = "";
  if (notGood(stats)) {
    stats = statsTousBacs;
    if (notGood(stats)) {
      return $("");
    } else if (bac != "") {
      serie = "Toutes";
      qualifSerieBac = " (toutes séries de bacs confondues) ";
    }
  } else {
    qualifSerieBac =
      serie != ""
        ? " (série '" + serie + "')"
        : " (toutes séries de bacs confondues)";
  }
  const stat = stats.statsScol[data.moyGenIndex()];

  $(".stats_serie_bac").html(qualifSerieBac);
  const $div = getStatsScolLine(stat);

  return $div;
}

function getStatsScolLine(stat) {
  const note5 = stat.rangEch5 / data.statNotesDivider();
  const note25 = stat.rangEch25 / data.statNotesDivider();
  const note75 = stat.rangEch75 / data.statNotesDivider();
  const note95 = stat.rangEch95 / data.statNotesDivider();
  const ratio = 2 + (note95 - note5);
  const lower = note5 - 1;
  const per5 = (100 * (note5 - lower)) / ratio;
  const per25 = (100 * (note25 - lower)) / ratio;
  const per75 = (100 * (note75 - lower)) / ratio;
  const per95 = (100 * (note95 - lower)) / ratio;

  return $(`
    <ol class="stats-bar mt-3">
    <span class="stats-bar-step out" style=";width:${per5}%"> 
      <span class="stats-bar-step-label stats"><span class="note">${note5}</span><span class="survingt">/20</span></span>
    </span>

    <span class="stats-bar-step" style="width:${per25 - per5}%"> 
    <span style="position: relative; top:8px" class="mt-2 stats-bar-step-pct"><b>20%</b></span> 
    <span class="stats-bar-step-label stats"><span class="note">${note25}</span><span class="survingt">/20</span></span>
    </span>

    <span class="stats-bar-step is-mediane" style="width:${per75 - per25}%">  
    <span style="position: relative; top:8px" class="mt-2 stats-bar-step-pct-mediane"><b>50%</b></span> 
    <span class="stats-bar-step-label stats"><span class="note">${note75}</span><span class="survingt">/20</span></span>
    </span>

        <span class="stats-bar-step " style="width:${per95 - per75}%">  
    <span style="position: relative; top:8px" class="mt-2 stats-bar-step-pct"><b>20%</b></span> 
    <span class="stats-bar-step-label stats"><span class="note">${note95}</span><span class="survingt">/20</span></span>
    </span>

    <span class="stats-bar-step out"  style="width:${100 - per95}%">
    </span>

    </ol>
    </li>`);
}

function addExplanation(msg, icon = "fr-icon-hashtag") {
  $(".formation-details-reasons").append(
    `
    <div class="formation-details-reason ">
    <div class="formation-details-reason-icon ${icon}" aria-hidden="true">
    </div><div>${msg}</div></div>`
  );
}

function getHTMLMiddle50(moy) {
  const intLow = moy.middle50.rangEch25 / data.statNotesDivider();
  const intHigh = moy.middle50.rangEch75 / data.statNotesDivider();
  if (intHigh == intLow) {
    //un cas très rare
    return `[${intLow}, ${intLow + 0.5}[`;
  } else {
    return `[${intLow},${intHigh}]`;
  }
}
function displayExplanations(explications, detailed) {
  if (
    explications == null ||
    explications === undefined ||
    explications.length == 0
  ) {
    $(".formation-details-pourquoi").hide();
    return;
  }
  $(".formation-details-pourquoi").show();
  $(".formation-details-reasons").empty();

  const geos = explications.filter((expl) => expl.geo);
  if (geos.length > 0) {
    const villeToDist = {};
    for (const expl of geos) {
      for (const d of expl.geo) {
        let dist = d.distance;
        const city = d.city;
        if (city in villeToDist) {
          dist = Math.min(dist, villeToDist[city]);
        }
        villeToDist[city] = dist;
      }
      for (const { geo } of geos) {
        for (const { city, distance, form } of geo) {
          let dist = distance;
          if (city in villeToDist) {
            dist = Math.min(dist, villeToDist[city]);
          }
          villeToDist[city] = dist;
        }
      }
    }
    let msg = "";

    for (const [city, dist] of Object.entries(villeToDist)) {
      msg =
        msg + `A proximité de ${city} (${dist < 1 ? "moins de 1 " : dist}km).`;
    }
    addExplanation(msg, "fr-icon-map-pin-2-line");
  }

  let simis = explications
    .filter((expl) => expl.simi && expl.simi.fl)
    .map((expl) => data.getExtendedLabel(expl.simi.fl))
    .filter((l) => l);
  simis = Array.from(new Set(simis));
  if (simis.length == 1) {
    addExplanation(
      `Cette formation est similaire à <em>&quot;${simis[0]}&quot;</em>.`,
      "fr-icon-arrow-right-up-line"
    );
  } else if (simis.length > 1) {
    addExplanation(
      `Cette formation est similaire à: <em>&quot;${simis.join(
        "&quot;,&quot;"
      )}</em>.</p>`,
      "fr-icon-arrow-right-up-line"
    );
  }
  /*  } else if (expl.simi && expl.simi.fl) {
   */

  for (const expl of explications) {
    addExplanation2(expl);
  }
}

export function clearAutoComplete(id) {
  $(`#${id}_autocomplete`).val("");
  const $container = $(`#autoCompleteItemsContainer_${id}`);
  $container.empty();
}

export function updateAutoCompleteItemsListe(id, listeItems) {
  console.log("");
  const $container = $(`#autoCompleteItemsContainer_${id}`);
  $container.empty();
  const liste = data.getLabelsListFromProfile(id);
  if (liste === undefined) return;
  for (const elt of listeItems) {
    if (!liste.includes(elt.label)) {
      $container.append(
        `<div class="autoCompleteItem" key="${elt.key}" label="${elt.label}"><span class="autoCompleteItemLabel">${elt.label}</span></div>`
      );
    }
  }
}

function addExplanation2(expl) {
  const giveDetailsFlag = false; //keep it for later
  let str = [];
  if (expl.dur && expl.dur.option == "court") {
    addExplanation(
      "Tu as une préférence pour les études courtes.",
      "fr-icon-pie-chart-box-line"
    );
  } else if (expl.dur && expl.dur.option == "long") {
    addExplanation(
      "Tu as une préférence pour les études longues.",
      "fr-icon-pie-chart-box-line"
    );
  } else if (expl.app) {
    addExplanation("Cette formation existe en apprentissage.");
  } else if (expl.tag || expl.tags) {
    const labels = [];
    if (giveDetailsFlag && expl.tag && expl.tag.pathes) {
      //al details
      for (const path of expl.tag.pathes) {
        const nodes = path.nodes;
        let strs = [];
        if (nodes) {
          for (const node of nodes) {
            let label = data.getExtendedLabel(node) + " (" + node + ")";
            if (!label) label = node;
            strs.push(label);
          }
        }
        strs.pop();
        const weight = path.weight;
        strs.push(`[score ${Math.round(1000 * weight)}]`);
        labels.push(strs.join(" - "));
      }
    } else {
      //no details
      const s = new Set();
      if (expl?.tags?.ns) {
        for (const node of expl.tags.ns) {
          //let label = data.getExtendedLabel(node);
          let label = data.getExtendedLabel(node);
          s.add(label);
        }
      }
      if (expl?.tag?.pathes) {
        for (const path of expl.tag.pathes) {
          const nodes = path.nodes;
          if (nodes && nodes.length > 0) {
            const node = nodes[0];
            let label = data.getExtendedLabel(node);
            s.add(label);
          }
        }
      }
      for (const l of s) {
        let label = l;
        const firstIndexOcc = label.indexOf("(");
        if (firstIndexOcc > 0) {
          label = label.substring(0, firstIndexOcc - 1);
        }
        labels.push(label);
      }
    }
    const msgs = [];
    //msgs.push(`<p>En lien avec tes choix et ta sélection:</p>`);
    msgs.push(`<span class="formation-details-tags">`);
    let first = true;
    for (const label of labels) {
      if (!first) msgs.push(", ");
      msgs.push(`<b class="formation-details-lien-choix">${label}</b>`);
      first = false;
    }
    msgs.push(".");
    msgs.push("</span>");
    addExplanation("En lien avec tes choix " + msgs.join(""));
  } else if (expl.bac) {
    addExplanation(
      "Tu as auto-évalué ta " +
        " moyenne générale à <b>" +
        +expl.bac.moy +
        "</b>. " +
        "Parmi les lycéennes et lycéens " +
        (expl.bac.bacUtilise == ""
          ? ""
          : "de série '" + expl.bac.bacUtilise + "' ") +
        "admis dans ce type de formation en 2023, la moitié avait une moyenne au bac dans l'intervalle <b>" +
        getHTMLMiddle50(expl.bac) +
        "</b>."
    );
  } else if (expl.tbac) {
    addExplanation("Idéal si tu as un bac série '" + expl.tbac.bac + "'.</p>");
  } else if (expl.perso) {
    return; // "<p>Tu as toi-même ajouté cette formation à ta sélection.</p>";
  } else if (expl.spec) {
    const stats = expl.spec.stats;
    let result = "";
    for (const [spe, pourcentage] of Object.entries(expl.spec.stats)) {
      result +=
        "<p>La spécialité '" +
        spe +
        "' a été choisie par " +
        Math.round(100 * pourcentage) +
        "% des candidats admis dans ce type de formation en 2023.</p>";
    }
    addExplanation(result);
  } else if (expl.interets) {
    const tags = expl.interets.tags;
    let result =
      "<p>Tu as demandé à consulter les formations correspondant aux mots-clés '" +
      tags +
      "'.</p>";
    addExplanation(result);
  }
}

function displayMetiers(metiers) {
  if (metiers == null || metiers === undefined || metiers.length == 0) {
    $(".formation-details-exemples-metiers").hide();
  } else {
    $(".formation-details-exemples-metiers").show();
    $(".formation-details-exemples-metiers-container").empty();
    for (let j = 0; j < 5; j++) {
      if (j >= metiers.length) break;
      const metier = metiers[j];
      const labelMetier = data.getLabel(metier);
      const $button = $(
        `<button key="${metier}" class="formation-details-exemple-metier">${labelMetier}</button>`
      );
      $(".formation-details-exemples-metiers-container").append($button);
      $button.on("click", () => {
        showMetierDetails(metier);
      });
      //showMetierDetails(label, descriptif, url)
    }
  }
}

function displayAttendus(key) {
  const d = data.getEDSData(key);

  if (d) {
    const eds = d.eds;
    if (eds.attendus !== undefined && eds.attendus != "") {
      $("#formation-details-attendus").show();
      $("#formation-details-attendus-details").html(eds.attendus);
    } else {
      $("#formation-details-attendus").hide();
    }
    if (eds.recoEDS !== undefined && eds.recoEDS != "") {
      $("#tabpanel-les-conseils-li").show();
      $("#accordion-eds").html(eds.recoEDS);
    } else {
      $("#tabpanel-les-conseils-li").hide();
    }
  } else {
    $("#formation-details-attendus").hide();
    $("#tabpanel-les-conseils-li").hide();
  }
}

function displayGrilleAnalyseCandidatures(key) {
  let d = data.getGrilleAnalyseCandidatures(key);
  if (d === undefined || d === null) d = {};
  const l = data.getGrilleAnalyseCandidaturesLabels();
  const liste = [];
  for (const [key, value] of Object.entries(d)) {
    if (value >= 10) {
      const label = l[key];
      if (label) {
        liste.push([label, value]);
      }
    }
  }
  liste.sort((x, y) => y[1] - x[1]);
  if (liste.length > 0) {
    $(".formation-details-grille-analyse").show();
    $(".formation-details-grille-analyse-list").empty();
    for (const [label, value] of liste) {
      $(".formation-details-grille-analyse-list").append(
        `<span class="fr-icon-check-line formation-details-grille-analyse-item">${label} (${value}%)</span>`
      );
    }
  } else {
    $(".formation-details-grille-analyse").hide();
  }
}

function buildAffinityCard(
  key,
  fav,
  type,
  affinite,
  cities,
  metiers,
  nodetails
) {
  if (type == "formation")
    return buildFormationAffinityCard(
      key,
      fav,
      affinite,
      cities,
      metiers,
      nodetails
    );
  else return buildMetierAffinityCard(key, fav, metiers, nodetails);
}

function buildFormationAffinityCard(
  key,
  fav,
  affinite,
  cities,
  metiers,
  nodetails
) {
  const label = data.getLabel(key);
  if (label === undefined || label === null) return null;
  const $div = $(`<div class="formation-card">
          <div class="formation-card-header formation-card-header-detail">
            <div class="formation-card-header-affinity">
              Taux d'affinité ${Math.trunc(100 * affinite)}%
            </div>
            <div class="formation-card-header-sep"></div>
            <div class="formation-card-header-type fr-icon-heart-fill icon-favorite icon-favorite_${key}"></div>
          </div>
          <div class="formation-card-header formation-card-header-nodetail">
            <div class="formation-card-header-type">FORMATION</div>
            <div class="formation-card-header-sep"></div>
            <div class="formation-card-header-type fr-icon-heart-fill icon-favorite icon-favorite_${key}"></div>
          </div>
          <div class="card-formation-title">
            ${label}
          </div>
          <span class="card-geoloc fr-icon-map-pin-2-fill">            
          </span>
          <div class="card-metiers-header">
            Parmi les métiers accessibles après cette formation
          </div>
          <div class="card-metiers-list">
          </div>
        </div>`);
  if (nodetails) {
    $(".formation-card-header-affinity", $div).hide();
    $(".card-geoloc", $div).hide();
    $(".card-metiers-header", $div).hide();
    $(".card-metiers-list", $div).hide();
    $(".formation-card-header-detail", $div).hide();
  } else {
    $(".formation-card-header-nodetail", $div).hide();
  }
  if (fav && session.isStudent()) {
    $(".icon-favorite", $div).show();
  } else {
    $(".icon-favorite", $div).hide();
  }
  addGeoloctoDiv(cities, $div);

  if (metiers.length > 0) {
    $(".card-metiers-list", $div).empty();
    for (let j = 0; j < 5; j++) {
      if (j >= metiers.length) break;
      const metier = metiers[j];
      const labelMetier = data.getLabel(metier);
      $(".card-metiers-list", $div).append(
        `<div class="card-metier">${labelMetier}</div>`
      );
    }
    if (metiers.length > 5)
      $(".card-metiers-list", $div).append(
        `<div class="card-metier">+${metiers.length - 5}</div>`
      );
  } else {
    $(".card-metiers-header", $div).empty();
  }
  return $div;
}
function buildMetierAffinityCard(key, fav, formations, nodetails) {
  const label = data.getLabel(key);
  const $div = $(`        <div class="formation-card">
          <div class="formation-card-header">
            <div class="metier-card-header-type">METIER</div>
            <div class="formation-card-header-sep"></div>
            <div class="metier-card-header-type fr-icon-heart-fill icon-favorite"></div>
          </div>
          <div class="card-metier-title">${label}</div>
          <!--
          <div class="card-studies-level">
            <img src="img/arrow-dsfr.svg" alt="arrow" />
            A partir de Bac + ?
          </div>-->
          <div class="card-metiers-header">
            ${formations.length} formations pour apprendre le métier
          </div>
        </div>
`);
  if (nodetails) {
    $(".card-metiers-header", $div).hide();
  }
  if (fav && session.isStudent()) {
    $(".icon-favorite", $div).show();
  } else {
    $(".icon-favorite", $div).hide();
  }

  if (formations.length == 0) {
    $(".card-metiers-header", $div).empty();
  }
  return $div;
}

function addGeoloctoDiv(cities, $div) {
  $(".card-geoloc", $div).empty();
  if (cities.length == 0) {
    $(".card-geoloc", $div).empty();
    $(".card-geoloc", $div).hide();
  } else {
    for (let j = 0; j < 5; j++) {
      if (j >= cities.length) break;
      const city = cities[j];
      $(".card-geoloc", $div).append(
        (j == 0 ? "" : "&nbsp;&middot;&nbsp;") + city
      );
    }
    if (cities.length > 5) {
      $(".card-geoloc", $div).append(
        `&nbsp;&middot;&nbsp;+${cities.length - 5}`
      );
    }
    $(".card-geoloc", $div).show();
  }
}
export const tabs = {
  profile: "nav-profil-tab",
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
  //$(".front-feedback").html("Maintenance en cours, service suspendu.").show();
  $(".front-feedback").html("").hide();

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

  $(".visible-only-when-connected").hide();
  $(".visible-only-when-disconnected").show();

  $("#show-favoris-button")
    .off("click")
    .on("click", function (e) {
      addToHistory(e);
      showFavorisTab();
    });

  $("#nav-profil-tab")
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
  $(".hidden-modal-button").hide();
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

export function setRoleVisibility() {
  $(".student-only").toggle(session.isStudent());
  $(".teacher-only").toggle(session.isAdminOrTeacher());
  $(".admin-only").toggle(session.isAdmin());
}

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
  showErrorMessage("Erreur", msgHtml);
  //$(".server-error").html("Erreur serveur: " + msgHtml + "<br><br><br><br>");
}

function displayClientError(msg) {
  const msgHtml = msg
    .replaceAll("\\n", "<br>")
    .replaceAll("\\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
  showErrorMessage("Erreur", msgHtml);
}

//validationMessage
export function showValidationRequiredMessage(login, message) {
  //show modal
  $("#validationRequiredLogin").html(login);
  $("#validationRequiredMessage").html(message);

  $("#validationRequiredModalButton").trigger("click");
}

export function showMetierDetails(metier) {
  const label = data.getLabel(metier);
  const descriptif = data.getSummary(metier);
  const urls = data.getUrls(metier);
  const fav = data.isFavoris(metier);

  //show modal
  $("#metierLabel").html(label);
  if (descriptif !== null) {
    $("#metierDescriptif").html(descriptif);
  } else {
    $("#metierDescriptif").empty();
  }
  $("#metierUrl").hide();
  for (const url of urls) {
    $("#metierUrl").show().attr("href", url);
  }
  $("#metierModal .add-to-favorites-btn").attr("data-id", metier);
  $("#metierModal .add-to-bin-btn").attr("data-id", metier);

  updateFavDiv(fav, $("#metierModal"));
  $("#metierModalButton").trigger("click");
}

export function showErrorMessage(title, message) {
  //show modal
  $("#errorTitle").html(title);
  $("#errorMessage").html(message);
  $("#errorModalButton").trigger("click");
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
