import $ from "jquery";

import * as data from "./../../data/data";
import * as events from "../../app/events";
import { getChoiceList } from "./exploration";

export function reloadTab() {
  const ok = data.getSuggestionsApproved();
  const isWaiting = data.getNbSuggestionsWaiting() > 0;

  const isFavoris = ok && ok.length > 0;

  $("#visualiser-favoris-div").toggle(isFavoris);
  $("#favoris-nonempty-list").toggle(isFavoris);

  $("#favoris-empty-list-suggestions").toggle(!isFavoris && isWaiting);
  $("#favoris-empty-list-no-suggestion").toggle(!isFavoris && !isWaiting);

  $("#favoris-nb").html(isFavoris ? ok.length : "");

  //ajout de la barre d'ajout manuel aux favoris,
  //seulement si il y a au moins un favori
  $("#favoris-searchbar").html("");

  const okform = ok.filter((s) => data.isFiliere(s.fl));
  const okmetier = ok.filter((s) => data.isMetier(s.fl));

  //le lien de recherche vers la carte de psup
  updateVisualiserFavorisSurCartePsupButton(okform);
  if (okform && okform.length > 0) {
    const $choiceList = getChoiceList("Mes formations", okform, false);
    //$(".interest_list", $choiceList).prepend($(mapHtml));
    $("#favoris-formations-liste-div").empty().append($choiceList);
  }
  $("#favoris-metiers-liste-div")
    .empty()
    .append(getChoiceList("Mes métiers", okmetier, false));

  $(".remove-from-bin")
    .off("click")
    .on("click", function (event) {
      const id = $(this).attr("filiere");
      events.removeFromBinHandler(id, false);
    });

  $(".remove-from-bin-and-accept")
    .off("click")
    .on("click", function (event) {
      const id = $(this).attr("filiere");
      events.selectChoice(id, false);
    });

  $(".forget-suggestion")
    .off("click")
    .on("click", function (event) {
      const id = $(this).attr("filiere");
      events.rejectChoice(id);
    });
}

function updateVisualiserFavorisSurCartePsupButton(okform) {
  const display = okform && okform.length > 0;
  $("#visualiser-favoris-button").toggle(display);
  if (display) {
    $("#visualiser-favoris-button")
      .off("click")
      .on("click", () => {
        const keys = data
          .getSuggestionsApproved()
          .filter((s) => data.isFiliere(s.fl) && data.getLabel(s.fl))
          .map((sugg) => sugg.fl);
        /* this is dirty trick to avoid popup blocking while preserving lazy 
      server roi computation */
        let forsOfInterest = null;
        events.handlers.getFormationsOfInterest(
          keys,
          (fors) => (forsOfInterest = fors)
        );
        let i = 0;
        const intervalms = 100;
        let check = function () {
          setTimeout(function () {
            if (i++ >= 5000 / intervalms) return;
            if (forsOfInterest === null) {
              check();
            } else {
              const uri = data.getParcoursupSearchAdress(
                keys,
                "Mes formations favorites",
                forsOfInterest
              );
              events.handlers.logUrlOpen(uri);
              window.open(uri, "_blank");
            }
          }, intervalms);
        };
        check();
        /* end of dirty trick */
      });
  }
}
