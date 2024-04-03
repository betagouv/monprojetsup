import $ from "jquery";
import { toast } from "./../animate/toasts";
import { Modal } from "bootstrap";

import * as app from "../../app/app";

function validEmailNeeded() {
  const selected = $("#createAccountSelectType").find(":selected").val();
  return selected !== "lyceen";
}

const validEmailRegex =
  /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;

function isValidEmail(login) {
  return login.match(validEmailRegex);
}

export function init() {
  $(".disconnectButton")
    .off("click")
    .on("click", () => {
      app.disconnect();
    });
  $(".toLyceenButton")
    .off("click")
    .on("click", () => {
      app.toLyceen();
    });
  $(".toTeacherButton")
    .off("click")
    .on("click", () => {
      app.toTeacher();
    });

  /******************* CREATE ACCOUNT *******************  */

  const typeAccountSel = $("#createAccountSelectType");
  //typeAccountSel.off("change").on("change", updateAccounCreationMenu);

  $(".inputPasswordEyeIcon")
    .off("mouseover")
    .on("mouseover", function () {
      $(".inputPasswordEyeIcon").removeClass("bi-eye-slash");
      $(".inputPasswordEyeIcon").addClass("bi-eye");
      for (const ipe of $(".inputPassword")) ipe.type = "text";
    });
  $(".inputPasswordEyeIcon")
    .off("mouseout")
    .on("mouseout", function () {
      $(".inputPasswordEyeIcon").removeClass("bi-eye");
      $(".inputPasswordEyeIcon").addClass("bi-eye-slash");
      for (const ipe of $(".inputPassword")) ipe.type = "password";
    });
}
