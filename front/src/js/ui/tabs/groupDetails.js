import $ from "jquery";
import { handlers } from "../../app/events";
import { collapsibleContentHtml } from "./studentDetails";
import * as session from "../../app/session";

/******************************************************* */
/*************** LOADING details of a group ***************/
/******************************************************* */

/**
 * init the screen for a teacher with a group but no student selected
 * @param {*} group
 */

export function loadGroupDetails(details) {
  const groupName = details.groupName;
  const groupId = details.groupId;

  const str1 = [];
  const hasSeveralGroups = !session.getSingleGroupFlag();
  if (hasSeveralGroups) {
    str1.push(`<div
                      type="button"
                      id="changeGroupButton"
                      class="btn btn-primary col m-3"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="16"
                        height="16"
                        fill="currentColor"
                        class="bi bi-arrow-left"
                        viewBox="0 0 16 16"
                      >
                        <path
                          fill-rule="evenodd"
                          d="M15 8a.5.5 0 0 0-.5-.5H2.707l3.147-3.146a.5.5 0 1 0-.708-.708l-4 4a.5.5 0 0 0 0 .708l4 4a.5.5 0 0 0 .708-.708L2.707 8.5H14.5A.5.5 0 0 0 15 8z"
                        />
                      </svg>
                    </div>
  `);
  }
  str1.push("<div class='row'>");
  str1.push(
    `<div class="fw-bold fs-6 m-3 col">
      <h2>${groupName}</h2>                  
      </div>`
  );

  /* Ouverture / fermeture de groupe */
  str1.push(getOpenCloseGroupButtonHtml(details.isOpened, groupId));
  str1.push("</div>");
  if (details?.expeENSGroup !== "T") {
    str1.push(
      `<div class="alert alert-info">
        Code d'accès au groupe à transmettre aux élèves: ${details.token}</div>`
    );
  }
  /* Visualisation des admins */
  str1.push("<div class='row'>");
  const str2 = ["<ul>"];
  for (const admin of details.admins) {
    str2.push("<li>" + admin + "</li>");
  }
  str2.push("</ul>");
  str1.push(collapsibleContentHtml("Admins", str2.join(""), "admins"));

  str1.push("</div>");

  const noStudent =
    details.students == undefined || details.students.length == 0;
  if (noStudent) {
    str1.push(`<p>Il n'y a actuellement aucun élève dans le groupe.</p>`);
  }

  $("#groupsAdminNoStudentSelectedText").html(str1.join(""));

  $("#changeGroupButton").off();
  $("#changeGroupButton").on("click", () => {
    handlers.setSelectedGroup(null);
  });
  $(".setSelectedGroupStatusButton").off();
  $(".setSelectedGroupStatusButton").on("click", (event) => {
    /*
    const grpId = $(event.currentTarget).attr("grpId");
    const openGroup = $(event.currentTarget).attr("openGroup");
    handlers.changeGroupStatus(grpId, openGroup);*/
  });

  const showAddMember =
    details.moderationList !== undefined && details.moderationList.length > 0;
  $("#addMemberToGroupDiv").toggle(showAddMember);
  if (showAddMember) {
    $(`#addMemberToGroup`)
      .empty()
      .append("<option disabled selected>Choisissez...</option>");
    for (const user of details.moderationList) {
      $(`#addMemberToGroup`).append(
        `<option value="` + user + `">` + user + `</option>`
      );
    }
  }

  if (noStudent) {
    $("#groupsAdminNoStudentSelectedTable").hide();
  } else {
    $("#groupsAdminNoStudentSelectedTable").show();
    const str = [];

    const bg_color = `class="interest"`;
    /*  */
    for (const user of details.students) {
      const color =
        user.health == 0
          ? "bg-success"
          : user.health >= 2
          ? "bg-danger"
          : "bg-warning";
      str.push(`
    <tr  >
      <th scope="row">
      <btn href="#" studentLogin='${user.login}' role="button" class="btn link-info selectStudentButton">
      ${user.name}
      </btn></th>
      <td ${bg_color}>${user.profileComplete}%</td>
      <td ${bg_color}>${user.likes}</td>
      <td ${bg_color}>${user.bins}</td>
      <td class="${color}">${user.msg}</td>
    </tr>`);
    }
    $("#groupsAdminNoStudentSelectedTableBody").html(str.join(""));

    $(".selectStudentButton").off();
    $(".selectStudentButton").on("click", (event) => {
      const studentLogin = $(event.currentTarget).attr("studentLogin");
      handlers.setSelectedStudent(studentLogin);
    });
  }
  /* Ajout de membre */
}

function getOpenCloseGroupButtonHtml(isOpened, id) {
  const buttonLabel = isOpened ? "Ouvert" : "Fermé";
  const buttonType = isOpened ? "btn-success" : "btn-danger";
  /*type="button" id="changeGroupButton" class="btn btn-primary"*/
  return `<div type="button" grpId="${id}"
                openGroup="${!isOpened}"
                class='btn setSelectedGroupStatusButton ${buttonType} col m-4'>
                ${buttonLabel}
                </div>`;
}
