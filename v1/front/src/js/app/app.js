/*! 

    parcoursup-orientation-front - a client to the oreientation server

    (C) Copyright 2022  
    Ministère de l'Enseignement Supérieur, de la Recherche et de l'Innovation, Hugo Gimbert

    Licensed under the Afferao General Public License (AGPL).  see <http://www.gnu.org/licenses/>

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

    SPDX-License-Identifier: MIT

 */
import $ from "jquery";

import * as server from "../services/services";
import * as session from "./session";
import * as data from "../data/data";
import * as ui from "../ui/ui";
import * as animate from "../ui/animate/animate";
import * as dataload from "../data/load";
import * as tunnel from "../ui/tunnel";
import * as nav from "../app/navigation";

import { toast } from "../ui/animate/toasts";

export {
  disconnect,
  serverErrorHandler,
  disconnectAndShowFeedback,
  deleteUser,
  addGroupMember,
  showToast,
  setSelectedGroup,
  setSelectedStudent,
  commentChanged,
};

/* the new $( document ).ready( handler ), see https://api.jquery.com/ready/ */

export function loginHandler(login, password) {
  if (login === undefined || login === null || login.length == 0) {
    frontErrorHandler({ msg: "Email non renseigné" }, false);
  } else if (
    password === undefined ||
    password === null ||
    password.length == 0
  ) {
    frontErrorHandler({ msg: "Mot de passe non renseigné" }, false);
  } else {
    server.passwordLogin(login, password, async (msg) => {
      storeCredentialsAfterSuccesfulAuth(msg.data.login, password);
      loginServerAnswerHandler(msg.data);
    });
    //fakeLogin();
  }
}

export function oidcLogin(jwt) {
  if (!jwt) {
    frontErrorHandler({ msg: "Jeton JWT erroné" }, false);
  } else {
    server.oidcLogin(jwt, async (msg) => {
      loginServerAnswerHandler(msg.data);
    });
  }
}

export function createAccount(data) {
  //toast("", "Demande de création de compte effectuée.");
  data.cguVersion = __VERSION__;
  server.createAccount(data, async (msg) => {
    storeCredentialsAfterSuccesfulAuth(data.login, data.password);
    loginServerAnswerHandler(msg.data, data.login);
  });
}

export function validateCodeAcces(accounTypen, accessGroupe, handler) {
  server.validateCodeAcces(
    {
      type: accounTypen,
      code: accesGroupe,
    },
    handler
  );
}

export function storeCredentialsAfterSuccesfulAuth(login, password) {
  //https://developer.mozilla.org/en-US/docs/Web/API/CredentialsContainer/store
  // Check if the browser supports password credentials (and the Credential Management API)
  if ("PasswordCredential" in window) {
    let credential = new PasswordCredential({
      id: login,
      name: login, // In case of a login, the name comes from the server.
      password: password,
    });

    navigator.credentials.store(credential).then(
      () => {
        console.info(
          "Credential stored in the user agent's credential manager."
        );
      },
      (err) => {
        console.error("Error while storing the credential: ", err);
      }
    );
  }
}

function loginServerAnswerHandler(data) {
  if (data.validationRequired) {
    ui.showValidationRequiredMessage(data.login, data.validationMessage);
  } else if (data.token !== undefined) {
    session.setToken(data.login, data.token);
    postLoginHandler();
  } else {
    frontErrorHandler("Réponse erronée du serveur", true);
  }
}

/* includes the token in all AJAX requests */
export function postLoginHandler() {
  $(".front-feedback").html("").hide();
  $(".front-error").html("");
  $(".server-error").html("");

  $.ajaxSetup({
    beforeSend: function (xhr) {
      if (!this.crossDomain) {
        // Only include CSRF token for same-origin requests
        xhr.setRequestHeader("X-CSRF-TOKEN", session.getToken());
      }
    },
  });

  dataload.loadZippedData().then(() => {
    server.updateAdminInfos((msg) => {
      setAdminInfos(msg);
      ui.initPostData(session.getLogin(), msg.infos);
      const profileCompleteness = msg.infos.profileCompleteness;
      session.setProfileCompletenessLevel(profileCompleteness);
      server.getProfile((msg) => {
        data.loadProfile(msg.profile);
        //ui.loadProfile();
        startNavigation();
      });
    });
  });

  $(".nav-link").on("click", function (e) {
    logAction("tab click " + e.currentTarget.id);
  });
}

function startNavigation() {
  const screen = session.getScreen();
  if (screen != undefined) {
    nav.setScreen(screen);
  } else {
    if (session.isAdminOrTeacher()) {
      nav.setScreen("groupes");
    } else {
      const profileCompleteness = session.getProfileCompletenessLevel();
      if (profileCompleteness < 2) {
        const category = profileCompleteness == 0 ? "profil" : "preferences";
        nav.setScreen("inscription1");
      } else {
        nav.setScreen("recherche");
      }
    }
  }
}

export async function askFormationsDetails() {
  const profile = data.getAnonymousProfile();
  const msg = await server.getFormationsAffinities(profile);
  const affinites = msg.affinites;
  const keys = [];
  let i = 0;
  for (i = 0; i < 20; i++) {
    //horrible way to do that
    if (i >= affinites.length) break;
    const key = msg.affinites[i].key;
    keys.push(key);
  }

  const msg2 = await server.getExplanations(keys, profile);
  i = 0;
  for (const explanations of msg2.liste) {
    //todo  check key
    if (explanations.key != keys[i]) {
      frontErrorHandler({ msg: "Réponse erronée du serveur" }, true);
      break;
    }
    affinites[i].explanations = explanations;
    i++;
  }

  //explanations
  const msg3 = await server.getDetails(keys, profile);
  i = 0;
  for (const details of msg3.details) {
    //todo  check key
    if (details.key != keys[i]) {
      frontErrorHandler({ msg: "Réponse erronée du serveur" }, true);
      break;
    }
    affinites[i].details = details;
    i++;
  }

  return affinites;
}

export async function doSearch(recherche) {
  const includeFormations = true;
  const includeMetiers = true;
  const pageSize = 20;
  const pageNb = 0;
  const profile = data.getAnonymousProfile();

  return server.search(
    includeFormations,
    includeMetiers,
    pageSize,
    pageNb,
    recherche,
    profile
  );
}

export async function getSelection(recherche) {
  return server.getSelection();
}

export async function doSearchOld() {
  const profile = data.getAnonymousProfile();
  const msg = await server.getFormationsAffinities(profile);
  const affinites = msg.affinites;
  const keys = [];
  let i = 0;
  for (i = 0; i < 20; i++) {
    //horrible way to do that
    if (i >= affinites.length) break;
    const key = msg.affinites[i].key;
    keys.push(key);
  }

  const msg2 = await server.getExplanations(keys, profile);
  i = 0;
  for (const explanations of msg2.liste) {
    //todo  check key
    if (explanations.key != keys[i]) {
      frontErrorHandler({ msg: "Réponse erronée du serveur" }, true);
      break;
    }
    affinites[i].explanations = explanations;
    i++;
  }

  //explanations
  const msg3 = await server.getDetails(keys, profile);
  i = 0;
  for (const details of msg3.details) {
    //todo  check key
    if (details.key != keys[i]) {
      frontErrorHandler({ msg: "Réponse erronée du serveur" }, true);
      break;
    }
    affinites[i].details = details;
    i++;
  }

  return affinites;
}

function askProfileAndSuggestions(handler) {
  server.getProfile((msg) => {
    console.log(JSON.stringify(msg));
    data.loadProfile(msg.profile);
    //ui.loadProfile();
    //ui.hideMainProfileTab();
    server.getSuggestions(data.getAnonymousProfile(), (msg) => {
      loadSuggestions(msg);
      if (handler) handler();
      //ui.showSuggestionsTab();
    });
  });
}

function getSuggestionsLoadThemAndFadeIn() {
  server.getSuggestions(data.getAnonymousProfile(), (msg) => {
    loadSuggestions(msg);
    animate.fadeIn();
  });
}
/**
 *
 * @param {*} fadingOut
 * @param {*} name
 * @param {*} value
 * @param {*} action
 */
export function updateSuggestionsAndReloadUI(fadingOut, suggestions) {
  if (fadingOut) {
    animate.fadeOut(() => {
      server.updateProfile({ suggestions: suggestions }, () => {
        animate.scrollTop();
        ui.loadProfile();
        getSuggestionsLoadThemAndFadeIn();
      });
    });
  } else {
    server.updateProfile({ suggestions: suggestions }, () => {
      getSuggestionsLoadThemAndFadeIn();
    });
  }
}

export function updateProfileAndReloadUI(fadingOut, name, value, action) {
  if (fadingOut) {
    animate.fadeOut(() => {
      server.updateProfile({ name: name, value: value, action: action }, () => {
        animate.scrollTop();
        ui.loadProfile();
        getSuggestionsLoadThemAndFadeIn();
      });
    });
  } else {
    server.updateProfile({ name: name, value: value, action: action }, () => {
      getSuggestionsLoadThemAndFadeIn();
    });
  }
}

export function toTeacher() {
  server.switchRole("pp", postLoginHandler);
}

export function toLyceen() {
  server.switchRole("lyceen", postLoginHandler);
}

function disconnect() {
  $(".front-feedback").html("Vous êtes déconnecté(e).").show();
  $(".front-error").html("");
  $(".server-error").html("");
  session.clear();
  if (session.isLoggedIn()) server.disconnect();
  data.init();
  //ui.showConnectionScreen();
  ui.showLandingScreen();
}

function setAdminInfos(msg) {
  if (msg === undefined || msg.infos === undefined) {
    frontErrorHandler({ msg: "Réponse erronée du serveur" }, true);
  } else {
    session.setRole(msg.infos.role);
    if (
      msg.infos.groups &&
      msg.infos.groups.length > 0 &&
      !session.isAdminOrTeacher()
    ) {
      const myGroup = msg.infos.groups[0];
      console.log("Autoselecting group " + myGroup.name);
      session.setSelectedGroup(myGroup.id);
      session.setSingleGroupFlag(true);
      session.setGroupInfo(myGroup);
    } else {
      session.setSingleGroupFlag(false);
    }
    session.setNoGroupOpenFlag(
      msg.infos.openGroups === undefined || msg.infos.openGroups.length == 0
    );
    session.setAdminInfos(msg.infos);

    ui.loadGroupsInfo();
  }
}

function showSelectGroupTab(msg) {
  setAdminInfos(msg);
  if (!session.isAdmin()) {
    ui.showGroupsTab();
  }
}

function refreshGroupTab(force_update = false) {
  const curGroup = session.getSelectedGroup();
  const curStudent = session.getSelectedStudent();
  if (curGroup !== undefined && curGroup != null && curGroup != "null") {
    if (curStudent !== undefined && curStudent != null) {
      server.getStudentProfile(curGroup, curStudent, processStudentProfileMsg);
    } else {
      server.getSelectedGroupDetails(curGroup, processGroupDetailsMsg);
    }
  } else if (!force_update) {
    showSelectGroupTab({ infos: session.getCachedAdminInfos() });
  } else {
    server.updateAdminInfos(showSelectGroupTab);
  }
}

export function addTransitionToHistory(tabName, data) {
  window.history.pushState({ tabName: tabName, data: data }, tabName, null);
}

export function onBackButtonEvent(e) {
  const state = e.state;
  if (state === undefined || state === null) return false;

  console.log("PopState " + state?.tabName);
  if (state?.tabName) {
    if (state.tabName == "groups") {
      if (state.data) {
        session.setSelectedGroup(state.data.curGroup);
        session.setSelectedStudent(state.data.curStudent);
        ui.showGroupsTab();
        refreshGroupTab(false);
      }
    } else {
      ui.showTabWithId(state.tabName);
    }
  }
  //Devenir référent d'un nouveau groupee.preventDefault();
}

function processGroupDetailsMsg(msg) {
  ui.loadGroupDetails(msg.details);
}

function processStudentProfileMsg(msg) {
  if (msg.profile) {
    data.setMsgs(msg.profile.msgs, true);
    ui.loadStudentProfile(msg.profile, msg.stats);
  } else {
    setSelectedStudent(null);
  }
}

export function joinGroup(group, groupToken) {
  server.joinGroup(group, groupToken, (msg) => {
    setAdminInfos(msg);
    if (session.isAdminOrTeacher()) {
      refreshGroupTab(true);
      showSelectGroupTab(msg);
    } else {
      let msgModal =
        "Ta demande a été prise en compte, le référent du groupe doit la valider.";
      if (
        msg.infos.groups &&
        msg.infos.groups.length == 1 &&
        msg.infos.groups[0].members &&
        msg.infos.groups[0].members.length == 1
      ) {
        msgModal = "Félicitations, tu fais maintenant partie du groupe.";
      }
      tunnel.openTutoModal("", msgModal);
      ui.showAccountTab();
    }
  });
}

function setSelectedGroup(group) {
  addTransitionToHistory("groups", {
    curGroup: group,
    curStudent: null,
  });
  session.setSelectedGroup(group);
  refreshGroupTab(false);
}

function setSelectedStudent(student) {
  addTransitionToHistory("groups", {
    curGroup: session.getSelectedGroup(),
    curStudent: student,
  });
  session.setSelectedStudent(student);
  refreshGroupTab(false);
}

export function setGroupAdmin(group, user, add) {
  server.setGroupAdmin(group, user, add, showAdminTab);
}

export function becomeGroupAdmin(group) {
  server.setGroupAdmin(group, session.getLogin(), true, showSelectGroupTab);
}

function showToast(msg) {
  toast("", msg);
}

function addGroupMember(group, user, add) {
  if (group !== null && user !== null) {
    server.addGroupMember(group, user, add, (msg) => {
      setAdminInfos(msg);
      refreshGroupTab(true);
    });
  } else {
    toast("", "Veuillez sélectionner des données");
  }
}

export function createGroup(lycee, classe) {
  server.createGroup(
    {
      lycee: lycee,
      classe: classe,
      op: "create",
    },
    showAdminTab
  );
}
export function deleteGroup(group) {
  server.createGroup(
    {
      group: group,
      op: "delete",
    },
    showAdminTab
  );
}

function deleteUser(user) {
  server.deleteUser(user, showAdminTab);
}

function loadSuggestions(msg) {
  data.loadSuggestions(msg.suggestions.suggestions);
  ui.updateSuggestionsTab();
}

export function changeGroupStatus(group, status) {
  if (group !== null && status !== undefined) {
    server.changeGroupStatus(group, status, (msg) => {
      setAdminInfos(msg);
      refreshGroupTab(true);
    });
  }
}

export function showDetails(key) {
  const profile = data.getProfile();
  server.getStats(profile.bac, key, (msg) => {
    const stats = msg.stats;
    server.getExplanations(profile, key, (msg_examples) => {
      ui.showDetails(
        key,
        stats,
        msg_examples.explanations,
        msg_examples.exemples
      );
    });
  });
}

export function getFormationsOfInterest(keys, handler) {
  let geo_pref = [];
  if (!session.isAdminOrTeacher()) {
    geo_pref = data.getProfileValue("geo_pref");
  }
  server.getFormationsOfInterest(geo_pref, keys, (msg) => {
    handler(msg.gtas);
  });
}

export function showAdminTab(msg) {
  setAdminInfos(msg);
  refreshGroupTab();
  ui.showAdminTab();
}

export function sendResetPasswordEmail(email) {
  server.sendResetPasswordEmail(email, () => {
    ui.showResetPasswordMessageSent(email);
  });
}

function serverErrorHandler(error) {
  var msg = "";
  if (error.status === 0) {
    msg = "Le serveur n'est pas joignable";
  } else if (error.status == 404) {
    msg = "Page indisponible [404]";
  } else if (error.status == 500) {
    msg = "Erreur interne du serveur [500]" + error.responseText;
  } else {
    msg = JSON.stringify(error);
  }
  console.error(msg);
  disconnect();
  if (error.status === 0) {
    $(".front-feedback")
      .html(
        "Echec: le serveur n'est pas joignable, veuillez SVP vérifier votre connexion internet."
      )
      .show();
  } else {
    ui.displayServerError(msg);
  }
}

export function frontErrorHandler(error, severe) {
  error.login = session.getLogin();
  const msg = JSON.stringify(error);
  console.error(msg);
  disconnect();
  if (severe) {
    ui.displayClientError(msg);
    server.sendError(msg);
  } else {
    $(".front-feedback").html(error).show();
  }
}

export function logUrlOpen(uri) {
  server.trace("front openUrl '" + uri + "'");
}

export function logAction(action) {
  server.trace("front " + action);
}

function disconnectAndShowFeedback(feedback) {
  disconnect();
  $(".front-feedback").html(feedback).show();
}

function commentChanged(topic, comment) {
  //dirty hack, TODO: add a  field `nomAfficheEditeur`
  //comment = `<b>${isMyNote ? nomAfficheEditeur : author}</b>:` + comment;
  if (session.isAdminOrTeacher()) {
    //set by teacher
    const curGroup = session.getSelectedGroup();
    const curStudent = session.getSelectedStudent();
    if (curGroup !== undefined && curStudent !== undefined) {
      server.addMessage(curGroup, curStudent, topic, comment, () => {
        console.log(`Message with topic ${topic} sent to ${curStudent} .`);
        server.getMessages(curStudent, curGroup, topic, commentChangedCallBack);
      });
    }
  } else {
    //set by student
    server.addMessage("", session.getLogin(), topic, comment, () => {
      console.log(`Sent message with topic ${topic}.`);
      server.getMessages(session.getLogin(), "", topic, commentChangedCallBack);
    });
  }
}

function commentChangedCallBack(msg) {
  //console.log(JSON.stringify(msg.msgs));
  data.setMsgs(msg.msgs, false);
  ui.updateNotes();
}

export function newSearchCallback(obj) {
  const str = "searching '" + JSON.stringify(obj) + "'";
  console.log(str);
  server.trace(str);
}

export function finishTunnel(category) {
  const profile = data.getProfile();
  server.updateProfile(profile, () =>
    server.getSuggestions(data.getAnonymousProfile(), () => {
      if (category == "preferences") {
        ui.hideMainProfileTab();
        askProfileAndSuggestions(() => {
          ui.showSuggestionsTab();
          tunnel.openTutoModal(
            "C'est parti pour l'exploration!",
            "Tu as deux étapes à suivre. Dans la première étape tu vas explorer des thématiques et métiers, et dans la seconde partie tu vas explorer des formations ! Des suggestions te sont proposées mais libre à toi de naviguer dans la barre de recherche. "
          );
        });
      } else if (category == "profil") {
        //on va être soit sur du balisé si les préérences ne sont pas encore complètes,
        //soit sinon on repart direct sur suggestions tab
        postLoginHandler();
      }
    })
  );
}
