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
import "../../../scss/styles.scss";

import $ from "jquery";
import * as ui from "./../../ui/ui";
import { setNewPassword } from "../../services/services";
import { toast } from "../../ui/animate/toasts";
import { frontErrorHandler, storeCredentialsAfterSuccesfulAuth } from "../app";

/* the new $( document ).ready( handler ), see https://api.jquery.com/ready/ */
$(async function () {
  window.onerror = function (msg, url, line, columnNo, error) {
    const errObj = {
      msg: msg,
      url: url,
      line: line,
      columnNo: columnNo,
      error: error,
    };
    frontErrorHandler(errObj, true);
  };

  ui.injectHtml();
  $("#beforePasswordChange").show();
  $("#afterPasswordChange").hide();

  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const email = urlParams.get("email");
  const token = urlParams.get("token");

  $(".inputPasswordEyeIcon")
    .off("mouseover")
    .on("mouseover", function () {
      $(".inputPasswordEyeIcon").removeClass("bi-eye-slash");
      $(".inputPasswordEyeIcon").addClass("bi-eye");
      for (const ipe of $(".inputPassword")) ipe.type = "text";
    })
    .off("mouseout")
    .on("mouseout", function () {
      $(".inputPasswordEyeIcon").removeClass("bi-eye");
      $(".inputPasswordEyeIcon").addClass("bi-eye-slash");
      for (const ipe of $(".inputPassword")) ipe.type = "password";
    });

  $("#sendNewPasssword")
    .off("click")
    .on("click", () => {
      const newPassword = $("#inputPassword").val();
      $(this).prop("disabled", true);
      setNewPassword(email, newPassword, token, (msg) => {
        storeCredentialsAfterSuccesfulAuth(email, newPassword);
        toast("", "Votre nouveau mot de passe a bien été pris en compte.");
        $("#beforePasswordChange").hide();
        $("#afterPasswordChange").show();
        sleep(2000).then(() => {
          window.location.replace("https://monprojetsup.fr/index.html");
        });
      });
    });
});

// sleep time expects milliseconds
function sleep(time) {
  return new Promise((resolve) => setTimeout(resolve, time));
}
