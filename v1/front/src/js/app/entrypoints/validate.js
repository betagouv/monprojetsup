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
import { validateAccount } from "../../services/services";
import { frontErrorHandler } from "./../app";

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

  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const email = urlParams.get("email");
  const token = urlParams.get("token");

  validateAccount(email, token, (msg) => {
    $("#result").html(`<div class="alert alert-info">${msg.message}</div>`);
  });
});
