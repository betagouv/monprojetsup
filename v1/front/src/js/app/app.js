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
import * as dataload from "../data/load";
import * as nav from "../app/navigation";
import("./../../dsfr.module.min.js");

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

export async function loginHandler(login, password) {
  if (login === undefined || login === null || login.length == 0) {
    await frontErrorHandler({ msg: "Email non renseigné" }, false);
  } else if (
    password === undefined ||
    password === null ||
    password.length == 0
  ) {
    await frontErrorHandler({ msg: "Mot de passe non renseigné" }, false);
  } else {
    server.passwordLogin(login, password, async (msg) => {
      storeCredentialsAfterSuccesfulAuth(msg.data.login, password);
      await loginServerAnswerHandler(msg.data);
    });
    //fakeLogin();
  }
}

export async function oidcLogin(jwt) {
  if (!jwt) {
    await frontErrorHandler({ msg: "Jeton JWT erroné" }, false);
  } else {
    server.oidcLogin(jwt, async (msg) => {
      loginServerAnswerHandler(msg.data);
    });
  }
}

export async function createAccount(data) {
  //toast("", "Demande de création de compte effectuée.");
  data.cguVersion = __VERSION__;
  const msg = await server.createAccountAsync(data);
  storeCredentialsAfterSuccesfulAuth(data.login, data.password);
  return await loginServerAnswerHandler(msg.data, data.login);
}

export async function validateCodeAcces(accountType, accesGroupe) {
  const answer = await server.validateCodeAcces({
    type: accountType,
    accesGroupe: accesGroupe,
  });
  if (answer) {
    session.setRole(accountType);
  }
  return answer;
}

export async function joinGroupAsync(accessCode) {
  return server.joinGroupAsync(accessCode);
}
export async function leaveGroupAsync(key) {
  return server.leaveGroupAsync(key);
}

export function storeCredentialsAfterSuccesfulAuth(login, password) {
  //https://developer.mozilla.org/en-US/docs/Web/API/CredentialsContainer/store
  // Check if the browser supports password credentials (and the Credential Management API)
  if (session.isAnonymous()) return;
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

async function loginServerAnswerHandler(data) {
  if (data.validationRequired) {
    ui.showValidationRequiredMessage(data.login, data.validationMessage);
    await nav.initScreen("landing");
    return false;
  } else if (data.token !== undefined) {
    session.setToken(data.login, data.token);
    await postLoginHandler();
    return true;
  } else {
    await frontErrorHandler("Réponse erronée du serveur", true);
    return false;
  }
}

export async function showLogin() {
  setAnonymousSession(false);
  await nav.initScreen("connect");
}

export async function showAnonymous() {
  setAnonymousSession(true);
  loginHandler("anonymous", "anonymous");
}

export async function showInscription() {
  setAnonymousSession(false);
  await nav.initScreen("inscription1");
}

export async function showLandingScreen() {
  await nav.initScreen("landing");
}
/* includes the token in all AJAX requests */
export async function postLoginHandler() {
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

  await dataload.loadZippedData();
  const msg = await server.updateAdminInfosAsync();
  setAdminInfos(msg.infos);
  ui.initPostData(session.getLogin(), msg.infos);
  const profileCompleteness = msg.infos.profileCompleteness;
  session.setProfileCompletenessLevel(profileCompleteness);
  const msgp = await server.getProfileAsync();
  data.loadProfile(msgp.profile);
  await nav.startNavigation();

  //ui.showValidationRequiredMessage(session.getLogin(), "bla bla");
}

export async function getProfile() {
  const msg = await server.getProfileAsync();
  data.loadProfile(msg.profile);
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
      await frontErrorHandler({ msg: "Réponse erronée du serveur" }, true);
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
      await frontErrorHandler({ msg: "Réponse erronée du serveur" }, true);
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

export async function getSelection() {
  return server.getSelection();
}

export function updateProfile(name, value, action) {
  console.log("Updating profile " + name + " " + value + " " + action);
  server.updateProfile({ name: name, value: value, action: action }, () => {});
}

export function removeFromBin(key) {
  data.removeFromBin(key);
  updateSuggestions([{ fl: key, status: data.SUGG_WAITING }]);
}

export function updateSuggestions(suggestions, handler = null) {
  server.updateProfile({ suggestions: suggestions }, handler);
}

export function toTeacher() {
  server.switchRole("pp", postLoginHandler);
}

export function toLyceen() {
  server.switchRole("lyceen", postLoginHandler);
}

async function disconnect() {
  //$(".front-feedback").html("Vous êtes déconnecté(e).").show();
  $(".front-error").html("");
  $(".server-error").html("");
  session.clear();
  if (session.isLoggedIn()) server.disconnect();
  data.init();
  //ui.showConnectionScreen();
  await showLandingScreen();
}

export async function setTeacherOpinion(key, opinion) {
  await server.setTeacherFeedback(
    //studentLogin, key, type, content
    session.getSelectedStudent(),
    key,
    "opinion",
    opinion
  );
}

export async function setTeacherComment(key, comment) {
  await server.setTeacherFeedback(
    //studentLogin, key, type, content
    session.getSelectedStudent(),
    key,
    "comment",
    comment
  );
}

export async function updateAdminInfos() {
  const msg = await server.updateAdminInfosAsync();
  if (msg == undefined || msg.infos === undefined) {
    await frontErrorHandler({ msg: "Réponse erronée du serveur" }, true);
  }
  const infos = msg.infos;
  setAdminInfos(infos);
  return infos;
}

function setAdminInfos(infos) {
  session.setRole(infos.role);
  if (infos.groups && infos.groups.length > 0 && !session.isAdminOrTeacher()) {
    const myGroup = infos.groups[0];
    console.log("Autoselecting group " + myGroup.name);
    session.setSelectedGroup(myGroup.id);
    session.setSingleGroupFlag(true);
    session.setGroupInfo(myGroup);
  } else {
    session.setSingleGroupFlag(false);
  }
  if (infos.groups && infos.groups.length > 0 && session.isAdminOrTeacher()) {
    const myGroup = infos.groups[0];
    session.setGroupInfo(myGroup);
  }

  session.setNoGroupOpenFlag(
    infos.openGroups === undefined || infos.openGroups.length == 0
  );
  session.setAdminInfos(infos);

  setCurrentGroupIfNeeded(infos);
}
function setCurrentGroupIfNeeded(infos) {
  //ensures a current group is selected
  const curGroup = session.getSelectedGroup();
  if (
    (curGroup === undefined || curGroup === null) &&
    infos.groups &&
    infos.groups.length > 0
  ) {
    session.setSelectedGroup(infos.groups[0].id);
  }
}

function showSelectGroupTab(msg) {
  setAdminInfos(msg);
  if (!session.isAdmin()) {
    ui.showGroupsTab();
  }
}

export async function getStudentSelection(login) {
  const curGroup = session.getSelectedGroup();
  return server.getStudentSelection(curGroup, login);
}

export async function getStudentProfile(login) {
  const msg = await server.getStudentProfileAsync(login);
  return msg.profile;
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

export async function getGroupDetails(groupId) {
  const msg = await server.getSelectedGroupDetailsAsync(groupId);
  return msg.details;
}

export function addTransitionToHistory(tabName, data) {
  window.history.pushState({ tabName: tabName, data: data }, tabName, null);
}

export async function onBackButtonEvent(e) {
  const state = e.state;
  if (state === undefined || state === null) return false;
  console.log("PopState " + state?.tabName);
  if (state?.tabName) {
    if (state.data) {
      session.setSelectedGroup(state.data.curGroup);
      session.setSelectedStudent(state.data.curStudent);
      session.setCurrentSearch(state.curSearch);
    }
    await nav.setScreen(state.tabName, false);
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

function setSelectedGroup(group) {
  addTransitionToHistory("groups", {
    curGroup: group,
    curStudent: null,
  });
  session.setSelectedGroup(group);
}

function setSelectedStudent(student) {
  addTransitionToHistory("groups", {
    curGroup: session.getSelectedGroup(),
    curStudent: student,
  });
  session.setSelectedStudent(student);
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
  server.sendResetPasswordEmail(email);
}

async function serverErrorHandler(error) {
  let msg = "";
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
  await disconnect();
  if (error.status === 0) {
    ui.displayServerError(
      "le serveur n'est pas joignable, veuillez SVP vérifier votre connexion internet."
    );
  } else {
    ui.displayServerError(msg);
  }
}

export function setAnonymousSession(ano) {
  session.setAnonymous(ano);
}

export async function frontErrorHandler(error, severe) {
  error.login = session.getLogin();
  const msg = JSON.stringify(error);
  console.error(msg);
  await disconnect();
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

async function disconnectAndShowFeedback(feedback) {
  await disconnect();
  ui.displayClientError(feedback);
  //$(".front-feedback").html(feedback).show();
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
