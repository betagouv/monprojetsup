import $ from "jquery";
import { handlers } from "../../app/events";

export { reloadTab };

function getUsersOfGroup(infos, selectedGroup) {
  for (const group of infos.groups) {
    if (group.id === selectedGroup) {
      return group.members;
    }
  }
  return [];
}
function getAdminsOfGroup(infos, selectedGroup) {
  for (const group of infos.groups) {
    if (group.id === selectedGroup) {
      return group.admins;
    }
  }
  return [];
}

function reloadTab(infos) {
  //TODO: factor redudant code, someday...
  $("#groupListContainer").empty();

  /****** CHANGEMENT STATUT GROUPE */
  $(".setSelectedGroupStatusButton").off();
  $(".setSelectedGroupStatusButton").on("click", (event) => {
    const grpName = $(event.currentTarget).attr("grpName");
    const openGroup = $(event.currentTarget).attr("openGroup");
    handlers.changeGroupStatus(grpName, openGroup);
  });

  $("confirmEmailSwitch").toggle(infos.confirmEmailNewAccounts !== undefined);
  for (const s of $("#confirmEmailSwitch")) {
    s.checked = infos.checkEmails;
  }
  $("#confirmEmailSwitch")
    .off()
    .on("change", (event) => {
      const moderate = event.currentTarget.checked;
      handlers.setModeration(moderate, "email");
    });

  /* Create group */
  $("#createGroupClasse").html("");
  $("#createGroupButton")
    .off()
    .on("click", () => {
      handlers.createGroup(
        $("#createGroupLycee").val(),
        $("#createGroupClasse").val()
      );
      $("#createGroupClasse").html("");
    });
  $("#createGroupLyceeSelect")
    .empty()
    .append("<option disabled selected>Choisissez...</option>");
  for (const grp of infos.lycees) {
    $("#createGroupLyceeSelect").append(
      `<option value="` + grp.index + `">` + grp.name + `</option>`
    );
  }

  /* Delete group */
  $("#deleteGroupButton")
    .off()
    .on("click", () => {
      handlers.deleteGroup($("#deleteGroupSelect").val());
    });

  /*Reset password  or  Delete user */
  for (const action of [
    "deleteUser",
    "resetUserPassword",
    "acceptAccountCreation",
  ]) {
    $(`#${action}Button`)
      .off()
      .on("click", () => {
        const val = $(`#${action}Select`).val();
        handlers[action](val, false);
        $(`#${action}Select`).val("");
      });
    $(`#${action}Select`)
      .empty()
      .append("<option disabled selected>Choisissez...</option>");
    let l = infos.users;
    if (
      action === "acceptAccountCreation" &&
      infos.acccountCreationModeration !== undefined
    ) {
      for (const user of infos.acccountCreationModeration) {
        $(`#${action}Select`).append(
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
      for (const user of l) {
        $(`#${action}Select`).append(
          `<option value="` + user + `">` + user + `</option>`
        );
      }
    }
  }

  /* Add admin to group */
  $("#addAdminLogin").empty();
  $("#addAdminLogin").append(
    "<option disabled selected>Choisissez...</option>"
  );
  $("#addAdminGroup").append(
    "<option disabled selected>Choisissez...</option>"
  );

  for (const item of infos.users) {
    $("#addAdminLogin").append(
      `<option value="` + item + `">` + item + `</option>`
    );
  }

  $("#setGroupAdminButton").off();
  $("#setGroupAdminButton").on("click", () => {
    handlers.setGroupAdmin(
      $("#addAdminGroup").val(),
      $("#addAdminLogin").val(),
      true
    );
  });

  $("#removeAdminGroup").on("change", () => {
    const selectedGroup = $("#removeAdminGroup").val();
    $("#removeAdminLogin").empty();
    $("#removeAdminLogin").append(`<option  value="">...</option>`);
    for (const item of getAdminsOfGroup(infos, selectedGroup)) {
      $("#removeAdminLogin").append(
        `<option value="` + item + `">` + item + `</option>`
      );
    }
  });

  $("#removeGroupAdminButton").off();
  $("#removeGroupAdminButton").on("click", () => {
    handlers.setGroupAdmin(
      $("#removeAdminGroup").val(),
      $("#removeAdminLogin").val(),
      false
    );
  });

  $(".groupSelect").empty();
  $(".groupSelect").append("<option disabled selected>Choisissez...</option>");
  for (const grp of infos.groups) {
    $(".groupSelect").append(
      `<option value="` + grp.id + `">` + grp.name + `</option>`
    );
  }

  /* Add user to group */
  $("#addUserLogin").empty();
  $("#addUserLogin").append("<option disabled selected>Choisissez...</option>");
  for (const item of infos.users) {
    $("#addUserLogin").append(
      `<option value="` + item + `">` + item + `</option>`
    );
  }

  $("#addGroupMemberButton").off();
  $("#addGroupMemberButton").on("click", () => {
    handlers.addGroupMember(
      $("#addUserGroup").val(),
      $("#addUserLogin").val(),
      true
    );
  });

  $("#removeUserGroup").on("change", () => {
    const selectedGroup = $("#removeUserGroup").val();
    $("#removeUserLogin").empty();
    $("#removeUserLogin").append(`<option  value="">...</option>`);
    for (const item of getUsersOfGroup(infos, selectedGroup)) {
      $("#removeUserLogin").append(
        `<option value="` + item + `">` + item + `</option>`
      );
    }
  });

  $("#removeGroupMemberButton").off();
  $("#removeGroupMemberButton").on("click", () => {
    handlers.addGroupMember(
      $("#removeUserGroup").val(),
      $("#removeUserLogin").val(),
      false
    );
  });
  //}
}
