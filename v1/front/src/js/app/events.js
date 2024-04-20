/*! Copyright 2022 © Ministère de l'Enseignement Supérieur, de la Recherche et de
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

import * as data from "../data/data";
import * as animate from "../ui/animate/animate";
import * as toast from "../ui/animate/toasts";
import * as app from "./app";
import * as ui from "../ui/ui";
import * as tunnel from "../ui/tunnel";
import * as server from "../services/services";
import DOMPurify from "dompurify";

export { handlers };

let handlers = {
  commentChanged: app.commentChanged,
  setGroupAdmin: app.setGroupAdmin,
  createGroup: app.createGroup,
  deleteGroup: app.deleteGroup,
  deleteUser: app.deleteUser,
  resetStudentPasswordByTeacher: resetStudentPasswordByTeacher,
  resetUserPassword: resetUserPasswordByAdmin,
  acceptAccountCreation: (user) =>
    server.moderateAccountCreation(user, true, app.showAdminTab),
  rejectAccountCreation: (user) =>
    server.moderateAccountCreation(user, false, app.showAdminTab),
  setModeration: (moderate, type) =>
    server.setModeration(moderate, type, app.showAdminTab),
  addGroupMember: app.addGroupMember,
  changeGroupStatus: app.changeGroupStatus,
  joinGroup: app.joinGroup,
  becomeGroupAdmin: app.becomeGroupAdmin,
  setSelectedGroup: app.setSelectedGroup,
  setSelectedStudent: app.setSelectedStudent,
  showDetails: app.showDetails,
  startPreferencesTunnel: tunnel.startPreferencesTunnel,
  startProfileTunnel: tunnel.startProfileTunnel,
  finishTunnel: app.finishTunnel,
  getFormationsOfInterest: app.getFormationsOfInterest,
  logUrlOpen: app.logUrlOpen,
  logAction: app.logAction,
  addTransitionToHistory: app.addTransitionToHistory,
  newSearchCallback: app.newSearchCallback,
};

// const questionIdsTriggeringUIReloadWhenChanged = ["bac", "niveau"]; // #BB
const questionIdsTriggeringUIReloadWhenChanged = [];

export function removeElementFromProfileList(id, elt) {
  const changed = data.removeFromListOrMap(id, elt);
  if (changed) {
    app.updateProfile(id, elt, "rem");
  }
}

export function addElementToProfileListHandler(id, elt) {
  const changed = data.addElementInBackOfProfileList(id, elt);
  if (changed) {
    app.updateProfile(id, elt, "add");
  }
}

export function toggleProfileScoreHandler(key) {
  const selected = data.toggleScore(key);
  app.updateProfile("scores", key, selected ? "add" : "rem");
  return selected;
}

/**/

function changeSuggestionStatus(id, newStatus, silent) {
  //toast_accept(id);
  const [sugg, changed] = data.getOrCreateSugg(id, newStatus);
  if (changed) {
    ui.updateHearts(id, newStatus == data.SUGG_APPROVED);
    app.updateSuggestionsAndReloadUI(!silent, [
      { fl: sugg.fl, status: sugg.status },
    ]);
    //ui.updateFavoris();
  }
  return changed;
}

export function rejectChoice(id, silent) {
  const changed = changeSuggestionStatus(id, data.SUGG_REJECTED, silent);
  if (changed) {
    if (++tutoModalRefusedOpenedOnce <= 2) {
      const label = data.getLabel(id);
      tunnel.openTutoModal("", `<b>Tu ne verras plus '${label}'.</b>`, "OK");
    }
  }
}

let tutoModalOpenedOnce = 0;
let tutoModalRefusedOpenedOnce = 0;

export function selectChoice(id, silent) {
  const changed = changeSuggestionStatus(id, data.SUGG_APPROVED, silent);
  if (changed) {
    if (++tutoModalOpenedOnce <= 2) {
      const label = data.getLabel(id);
      tunnel.openTutoModal(
        "",
        `<b>Tu as ajouté '${label}' à ta sélection.</b><br/>
    <br/><i>Retrouve les métiers et formations que tu as sélectionnés dans le menu "Sélections".</i>`,
        "OK"
      );
    }
    //toast.toast("", "Ajouté à ta sélection.");
    animate.animateHeart();
  }
  //todo someday update explanations for this id
  //possible: add one API request
  //see corresponding issue
}

export function removeFromBinHandler(id, silent) {
  data.removeFromBin(id);
  app.updateSuggestionsAndReloadUI(!silent, [{ fl: id, status: null }]);
  toast.toast("", "Cet élément n'est plus exclu des suggestions.");
}

export function emptyBinHandler(silent) {
  const suggs = data.getSuggestionsRejected();
  const suggsRemoved = suggs.map((s) => {
    return { fl: s.fl, status: null };
  });
  data.emptyBin();
  app.updateSuggestionsAndReloadUI(!silent, suggsRemoved);
  toast.toast(
    "",
    "Corbeille vidée: plus aucun élèment n'est exclu des suggestions."
  );
}

export function profileValueChangedHandler(id, dirty) {
  const sanitized = DOMPurify.sanitize(dirty);
  data.setProfileValue(id, sanitized);
  app.updateProfileAndReloadUI(false, id, sanitized, "add");
  if (id == "bac" || id == "niveau") {
    tunnel.updateUI();
  }
}
export function profileValueChangedHandler2(id, dirty) {
  const sanitized = DOMPurify.sanitize(dirty);
  data.setProfileValue(id, sanitized);
  app.updateProfile(id, sanitized, "add");
}

function resetStudentPasswordByTeacher(user) {
  server.resetStudentPasswordByTeacher(user, (msg) => {
    window.alert("Nouveau mot de passe: " + msg.password);
  });
}
function resetUserPasswordByAdmin(user) {
  server.resetUserPasswordByAdmin(user, (msg) => {
    window.alert("Nouveau mot de passe: " + msg.password);
  });
}
