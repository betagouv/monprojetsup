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
const connected_screens = ["recherche", "board", "selection"];

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

export async function showConnectedScreen(subscreen) {
  await showScreen("connected");
  const html = await fetchData(subscreen);
  $("#header-navigation a").removeAttr("aria-current");
  $(`#nav-${subscreen}`).attr("aria-current", true);
  $(`#myTabContent`).html(html);
}

function injectInSelect($select, data) {
  for (const [key, value] of Object.entries(data)) {
    $select.append(`<option value="${key}">${value}</option>`);
  }
}

function injectInMultiOptions($accordions_group, menus) {
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
    for (const item of menu.items) {
      let key = item.key;
      if (key === undefined) key = item.id;
      if (data.isSelected(key)) {
        expand = true;
      }
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
            ${menu.label}
            <span class="multi-options-group-emojis">${emoji2}</span>
          </button>
        </h3>
        <div class="fr-collapse muti-options-group-content" id="accordion-${menu.key}">
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
        `<div class="multi-options-item multi-options-item_${key}" key="${key}"><span class="multi-options-item-emoji">${emoji2}</span>${item.label}</div>`
      );
    }
    $accordions_group.append($menu);
  }
}

async function injectProfileTab(tabName) {
  console.log("Injecting profile tab " + tabName);
  const html = await fetchData("profil/" + tabName);
  console.log("Fetched " + tabName);
  const $div = $(`#tab-${tabName}-panel`);
  if ($div.length == 0) {
    throw Error(`no div #tab-${tabName}-panel`);
  }
  $div.html(html);
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
  console.log("Intitialized profile tab " + tabName);
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
  $("#main-placeholder").css({
    "background-image": 'url("../img/bg.svg")',
  });
  $(".disconnect").hide();
  $("#landing-placeholder")
    .off()
    .on("click", () => {
      showConnectionScreen();
    });
}

export async function showInscriptionScreen1() {
  return showSubScreen("inscription1");
}
export async function showInscriptionScreen2() {
  return showSubScreen("inscription2");
}
export async function showBoard() {
  $("#main-placeholder").css({
    "background-image": "none",
  });

  return showConnectedScreen("board");
}
export async function showSelection() {
  return showConnectedScreen("selection");
}
export async function showRechercheScreen() {
  await showConnectedScreen("recherche");
}
export async function showProfileScreen() {
  $("#main-placeholder").css({
    "background-image": "none",
  });
  await showConnectedScreen("profile");
  //inject profile data
  await injectProfileTab("scolarite");
  await injectProfileTab("etudes");
  await injectProfileTab("domaines_pro");
  await injectProfileTab("interests");
  $(".profile-div-prenomnom").html(data.getPrenomNom());
  $(".profile-div-email").html(session.getLogin());
}

export function showWaitingMessage() {
  $("#explore-div-wait").show();
  $("#explore-div-resultats").hide();
}
export function showRechercheData(data) {
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
  updateFav(dat.key);
  $("#explore-div-resultats-left-liste").append($div);
  $div.on("click", () => {
    displayItemDetails(dat, nodetails);
  });
}

let currentKeyDisplayed = "";

function displayItemDetails(dat, nodetails) {
  const key = dat.key;
  const isMetier = data.isMetier(key);
  if (isMetier) {
    $(".explore-specific-formation").hide();
    $(".explore-specific-metier").show();
    displayMetierDetails(dat, nodetails);
  } else {
    $(".explore-specific-formation").show();
    $(".explore-specific-metier").hide();
    displayFormationDetails(dat, nodetails);
  }
  $("#add-to-favorites-btn").attr("data-id", key);
  $("#add-to-bin-btn").attr("data-id", key);
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

export function updateFav(key) {
  const fav = data.isFavoris(key);
  if (key == currentKeyDisplayed) {
    if (fav) {
      $("#add-to-favorites-btn").html("Ajouté à ma sélection");
      $("#add-to-favorites-btn").addClass("activated");
      $("#add-to-favorites-btn").addClass("favori");
      $("#add-to-bin-btn").html("Plus intéressé");
      $(`#formation-details-header-nav-central-icon`)
        .removeClass("fr-icon-heart-line")
        .addClass("fr-icon-heart-fill");
    } else {
      $("#add-to-favorites-btn").html("Ajouter à ma sélection");
      $("#add-to-bin-btn").html("Pas intéressé");
      $("#add-to-favorites-btn").removeClass("activated");
      $("#add-to-favorites-btn").removeClass("favori");
      $(`#formation-details-header-nav-central-icon`)
        .removeClass("fr-icon-heart-fill")
        .addClass("fr-icon-heart-line");
    }
  }
  $(`.icon-favorite_${key}`).toggle(fav);
}

function displayFormationDetails(dat) {
  $("#explore-div-resultats-right").show();

  updateFav(dat.key);

  const key = dat.key;

  //title
  const label = data.getLabel(key);
  $(".formation-details-title").html(label);
  //summary
  displaySummary(key);
  //links
  displayUrls(key, dat.fois);
  //stats
  displayStats(dat.stats.stats);
  //Explication
  const devMode = false;
  displayExplanations(dat.explanations, devMode);
  //exemples
  displayMetiers(dat.examples);
  //attendus
  displayAttendus(key);
}

function displaySummary(key) {
  const summary = data.getSummary(key);
  if (summary && summary.length > 0) {
    const cleanup = summary.replaceAll("h3", "p").replaceAll("h2", "p");

    $(".formation-details-summary").html(cleanup);
    $(".formation-details-summary").show();
  } else {
    $(".formation-details-summary").hide();
    $(".formation-details-summary").empty();
  }
}

function displayUrls(subkey, forsOfInterest) {
  const urls = [...data.getUrls(subkey, true)];
  const label = data.getExtendedLabel(subkey);
  let found = urls.length > 0;
  if (data.isFiliere(subkey)) {
    let uri = data.getParcoursupSearchAdress([subkey], label, forsOfInterest);
    uri = uri.replace("'", " "); //this caracter is ok in uris but not in html
    urls.push(uri);
    found = true;
  }

  $(".formation-details-links").empty();
  for (const url of urls) {
    $(".formation-details-links").append(
      `<div class="formation-details-link">
          <a href="${url}"
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
    return "Plus d'infos sur Onisep";
  }
  if (url.includes("pole")) {
    return "Plus d'infos sur France Travail";
  }
  if (url.includes("parcoursup")) {
    return "Plus d'infos sur Parcoursup";
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

function displayStats(stats) {
  if (stats == null || stats == undefined || !(data.tousBacs in stats)) {
    $(".formation-details-stats-bloc").hide();
  } else {
    $(".formation-details-stats-bloc").show();
    const nbAdmis = [];
    let total = -1;
    for (const o of Object.entries(stats)) {
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
      $(".formation-details-stats-bloc").show();
    } else {
      $(".formation-details-stats-bloc").hide();
    }

    /*
    <div class="formation-details-stats-bacs">
          Répartition par filières
          <div class="formation-details-stats-bac-badge">Générale 89%</div>
          <div class="formation-details-stats-bac-badge">Pro 3.7%</div>
          <div class="formation-details-stats-bac-badge">STMG 3.3%</div>
          <div class="formation-details-stats-bac-badge">STI2D 1%</div>
        </div>
    */
  }
}

function addExplanation(msg, icon = "fr-icon-success-line") {
  $(".formation-details-reasons").append(
    `
    <div class="formation-details-reason ">
    <span class="formation-details-reason-icon ${icon}" aria-hidden="true"></span>${msg}</div>`
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
    let msg = `Une ou plusieurs formations de ce type sont situées à proximité de `;
    for (const [city, dist] of Object.entries(villeToDist)) {
      msg = msg + ` ${city} (${dist < 1 ? "moins de 1 " : dist}km)`;
    }
    msg = msg + ".";
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
    msgs.push(`<div class="formation-details-tags">`);
    for (const label of labels) {
      msgs.push(
        `<div  class="formation-details-exemple-metier">${label}</div>`
      );
    }
    msgs.push("</div>");
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
      $(".formation-details-exemples-metiers-container").append(
        `<div class="formation-details-exemple-metier">${labelMetier}</div>`
      );
    }
  }
}

function displayAttendus(key) {
  const d = data.getEDSData(key);
  $("#accordion-attendus").empty();
  $("#accordion-eds").empty();
  if (d) {
    const label = d.label;
    const eds = d.eds;
    if (eds.attendus) {
      $("#accordion-attendus").html(eds.attendus);
    }
    if (eds.recoEDS) {
      $("#accordion-eds").html(eds.recoEDS);
    }
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
          <div class="card-geoloc">
            <img src="img/loc.svg" alt="geo" />
          </div>
          <div class="card-metiers-header">
            Exemples de métiers accessibles après cette formation
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
  if (fav) {
    $(".icon-favorite", $div).show();
  } else {
    $(".icon-favorite", $div).hide();
  }
  if (cities.length == 0) {
    $(".card-geoloc", $div).empty();
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
  }

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
  if (fav) {
    $(".icon-favorite", $div).show();
  } else {
    $(".icon-favorite", $div).hide();
  }

  if (formations.length == 0) {
    $(".card-metiers-header", $div).empty();
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
  $(".server-error").html("Erreur serveur: " + msgHtml + "<br><br><br><br>");
}

function displayClientError(msg) {
  const msgHtml = msg
    .replaceAll("\\n", "<br>")
    .replaceAll("\\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
  $(".front-error").html("Erreur client:<br> " + msgHtml);
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
