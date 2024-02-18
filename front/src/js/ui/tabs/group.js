import $ from "jquery";
import { handlers } from "../../app/events";
import { hideProfileTabs } from "../ui";
import * as animate from "../animate/animate";
import * as session from "../../app/session";
export { reloadTab, loadGroupsInfoStudent };

export const teacherScreens = {
  selGroup: "selectGroupTeacherDiv",
  seeGroup: "selectStudentTeacherDiv",
  seeStudent: "studentDetailsDiv",
};

function selectTeacherScreen(screen) {
  hideProfileTabs();
  for (const [scr, div] of Object.entries(teacherScreens)) {
    $(`#${div}`).toggle(scr == screen);
  }
  animate.scrollTop();
}

function reloadTab() {
  const isAdmin = session.isAdminOrTeacher();
  $("#groupsStudentDiv").toggle(!isAdmin);
  $("#teacherAccountDisconnectDiv").toggle(isAdmin);
  $("#groupsAdminDiv").toggle(isAdmin);

  //plusieurs écrans possibles dans le cas admin
  if (isAdmin) {
    const curGroup = session.getSelectedGroup();
    const curStudent = session.getSelectedStudent();
    if (curGroup !== undefined && curGroup != null && curGroup != "null") {
      if (
        curStudent !== undefined &&
        curStudent != null &&
        curStudent != "null"
      ) {
        selectTeacherScreen("seeStudent");
      } else {
        selectTeacherScreen("seeGroup");
      }
    } else {
      selectTeacherScreen("selGroup");
    }
  }
}

/******************************************************* */
/*************** LOADING the list of groups ***************/
/******************************************************* */

function loadGroupsInfoStudent(groups) {
  if (!groups) {
    return;
  }

  const isAlreadyInAGroup =
    groups &&
    groups.length == 1 &&
    groups[0].members &&
    groups[0].members.length == 1;
  const isWaitingForAGroup =
    groups &&
    groups.length == 1 &&
    groups[0].waiting &&
    groups[0].waiting.length == 1;
  const oneGroupOpen = groups.length > 0;

  $("#groupsStudentnewGroupDiv").toggle(
    !isAlreadyInAGroup && !isWaitingForAGroup && oneGroupOpen
  );
  $("#studentDetailsDiv").hide();
  //$("#groupsStudentnoGroupDiv").toggle(!isAlreadyInAGroup && !oneGroupOpen);

  $("#groupInfoStudenDiv").show();
  if (isAlreadyInAGroup) {
    const grp = groups[0].name;
    $("#groupInfoStudenDiv").html(`Tu fais partie du groupe '${grp}'.`);
  } else if (isWaitingForAGroup) {
    const grp = groups[0].name;
    $("#groupInfoStudenDiv").html(
      `Tu a demandé à rejoindre le groupe '${grp}'.`
    );
  } else {
    $("#groupInfoStudenDiv").html("Tu ne fais partie d'aucun groupe.");
  }
}

/**
 * init the screen for a teacher with no group selected
 *
 * @param {*} groups
 */
export function loadGroupsInfoTeacher(infos) {
  const myGroups = infos.groups;
  const openGroups = infos.openGroups;
  $("#selectGroupTeacherDivWithGroup").toggle(myGroups.length > 0);
  $("#selectGroupTeacherDivNoGroup").toggle(openGroups.length > 0);

  const moderation = infos.acccountCreationModeration;
  if (moderation && moderation.length > 0) {
    const action = "";
    $("#accountCreationByProviseurDiv").show();

    $(`#accountCreationByProviseurButton`)
      .off()
      .on("click", () => {
        const val = $(`#accountCreationByProviseurSelect`).val();
        if (val == null) {
          window.alert("Veuillez choisir un compte");
        } else if (
          confirm(
            "Confirmez-vous le rattachement du compte " +
              $("#accountCreationByProviseurSelect option:selected").text() +
              " à votre lycée?"
          )
        ) {
          handlers.acceptAccountCreation(val, false);
          $(`#accountCreationByProviseurSelect`).val("");
        }
      });
    $(`#rejectAccountCreationByProviseurButton`)
      .off()
      .on("click", () => {
        const val = $(`#accountCreationByProviseurSelect`).val();
        if (val == null) {
          window.alert("Veuillez choisir un compte");
        } else if (
          confirm(
            "Confirmez-vous le refus du rattachement du compte " +
              $("#accountCreationByProviseurSelect option:selected").text() +
              " à votre lycée?"
          )
        ) {
          handlers.rejectAccountCreation(val, false);
          $(`#accountCreationByProviseurSelect`).val("");
        }
      });
    $(`#accountCreationByProviseurSelect`)
      .empty()
      .append("<option disabled selected value=''>Choisissez...</option>");
    for (const user of infos.acccountCreationModeration) {
      $(`#accountCreationByProviseurSelect`).append(
        `<option value="` +
          user +
          `">` +
          user +
          " avec le rôle <b>'" +
          infos.roles[user] +
          `'</b></option>`
      );
    }
  } else {
    $("#accountCreationByProviseurDiv").hide();
  }

  if (myGroups.length > 0) {
    const $div = $('<div class="list-group m-2 p-3"></div>');
    for (const grp of myGroups) {
      const $button =
        $(`<button type="button" class="list-group-item-success list-group-item m-2"
      >
      ${grp.name} 
      </button>`).on("click", (event) => {
          handlers.setSelectedGroup(grp.id);
        });

      $div.append($button);
    }
    $("#groupsAdminSelectGroupDiv").empty().append($div);
  }
  if (openGroups.length > 0) {
    const $div = $('<div class="list-group m-2 p-3"></div>');
    for (const grp of openGroups) {
      const $button = $(`<button type="button" 
      class=" list-group-item-success list-group-item m-2">
      ${grp.name} 
      </button>`).on("click", () => {
        if (
          window.confirm(
            "Etes-vous sûr de vouloir devenir référent du groupe '" +
              grp.name +
              "? Seule l'équipe de direction de votre lycée pourra annuler cette opération."
          )
        ) {
          handlers.becomeGroupAdmin(grp.id);
        }
      });
      $div.append($button);
    }
    $("#joinGroupSelectDiv").empty().append($div);
  }
}
