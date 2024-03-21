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

import * as data from "./../../data/data";
import { getChoiceList } from "./exploration";
import * as events from "../../app/events";

export { reloadTab };

function reloadTab() {
  const nbRejected = data.getSuggestionsRejected().length;
  $("#empty-bin-message").hide();
  if (nbRejected <= 0) {
    $("#bin-nb").html("");
    $("#rejected_list").html("");
  } else {
    $("#bin-nb").html(`<b>(${nbRejected})</b>`);
    $("#rejected_list").empty().append(getSuggRejectedDiv());
  }
}

function getSuggRejectedDiv() {
  const rejected = data.getSuggestionsRejected();
  if (!rejected || rejected.length == 0) return "";
  let $div = $(`
  <div>
  <h3 class="mt-4">Ma corbeille</h3>
  <div
class=" w-80 mx-2 p-3 mb-5rounded btn question ">
  <div  
  type="button" 
  data-bs-toggle="collapse" 
  data-bs-target="#getSuggRejectedDiv" 
  aria-expanded="false" 
  aria-controls="getSuggRejectedDiv">
  <i class="bi bi-trash mx-2"></i>
    Tu as choisi de ne plus voir...<div id="bin-nb"></div>
  <div class="collapse text-left" id="getSuggRejectedDiv">
  </div>
  </div>
  </div>
  </div>
   `);
  const $subdiv = $("#getSuggRejectedDiv", $div);
  $subdiv.append(getChoiceList("", rejected, true));
  const $button =
    $(`  <button filiere="all" id="empty-bin" class="btn btn-secondary">Ne plus exclure ces éléments</button>
`).on("click", () => {
      events.emptyBinHandler(false);
    });
  $subdiv.append($button);
  return $div;
}

function collapsibleContentHtml(shown, hidden, uuid) {
  let id = "collapseSpecs" + uuid;
  return;
}
