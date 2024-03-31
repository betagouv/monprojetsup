import $ from "jquery";
import * as data from "../../data/data";
import * as events from "../../app/events";
import * as search from "../search/searchbar";
import * as tierlist from "../cards/cards";
import * as session from "../../app/session";

const activeClass = "btn-question";
const inactiveClass = "btn-secondary";

export function setupSearchType(type) {
  if (type === undefined) type = getActiveSuggestionType();
  //events.handlers.logAction("setupSearchType " + type);
  reloadSuggestions(type);
  search.setupSearchBar($("#sugg_search_bar"), type);
}

export function reloadTab() {
  setupSearchType();
  search.clearInput($("#sugg_search_bar"));
}

export function reloadSuggestions(type) {
  if (type === undefined) type = getActiveSuggestionType();

  let suggestions = data.getSuggestionsWaiting();

  if (type == "formations")
    suggestions = suggestions.filter((s) => data.isFiliere(s.fl));
  else suggestions = suggestions.filter((s) => data.isMetier(s.fl));

  const hasSugg = suggestions.length > 0;

  const $liste = $(`#sugg_waiting_list`);
  $liste.toggle(hasSugg);
  $(`#sugg_none`).toggle(!hasSugg);

  if (hasSugg) {
    const maxNSugg = 5;
    const groups = suggestions.slice(0, maxNSugg).map((sugg) => sugg.fl);
    events.handlers.logAction("reloadSuggestions " + type + " " + groups);

    const handlers = {
      like: (id) => {
        $liste.fadeOut(500, () => {
          events.selectChoice(id, true);
          $liste.fadeIn(1000);
        });
      },
      dislike: (id) => {
        $liste.fadeOut(500, () => {
          events.rejectChoice(id, true);
          $liste.fadeIn(1000);
        });
      },
      moreinfo: events.handlers.showDetails,
    };

    const $interestsList = getInterestsList(groups, handlers, false);
    $(`#sugg_waiting_list`).empty().append($interestsList);
    //$("#test p").delay(1000).animate({ opacity: 1 }, 700);​
  } else {
    events.handlers.logAction("reloadSuggestions " + type + " empty");
    $(`#sugg_waiting_list`).empty();
  }
}

export function hideFormations() {
  const hide = session.hideFormations();
  $(".formationsExploration").toggle(!hide);
  $(".metiersExploration").toggle(hide);
}

let current_source = "formations";

export function initOnce() {
  const hide = session.hideFormations();
  if (hide) {
    current_source = "metiers";
    $("#progressbar-step-metiers").addClass("is-current");
    $("#progressbar-step-formations").removeClass("is-current");
  } else {
    current_source = "formations";
    $("#progressbar-step-formations").addClass("is-current");
    $("#progressbar-step-metiers").removeClass("is-current");
  }
  reloadSuggestions(current_source);
  $("#sugg_search_bar").empty().append(search.getSearchBar());

  $(".explorations_nav")
    .off("click")
    .on("click", (event) => {
      current_source = $(event.target).attr("source");
      events.handlers.logAction("explorations_nav " + current_source);
      $(".is-current").removeClass("is-current");
      $(".is-complete").removeClass("is-complete");
      $(event.target).closest(".explorations_nav").addClass("is-current");
      let $prev = $(".is-current").prev();
      while ($prev.length > 0) {
        $prev.addClass("is-complete");
        $prev = $prev.prev();
      }
      reloadTab();
    });
}

function getActiveSuggestionType() {
  const hide = session.hideFormations();
  if (hide === undefined) return current_source;
  if (session.hideFormations()) return "metiers";
  else return current_source;
}

function getInterestsList(groups, handlers, sober) {
  const $items = $(`<div class="interest_list container-fluid"></div>`);
  for (const id of groups) {
    if (id === undefined) continue;
    let name = data.getLabel(id);
    if (name === undefined || name === null) continue;

    const $item = tierlist.getSuggCard(
      id,
      name,
      handlers,
      data.isRejected(id),
      sober
    );

    $items.append($item);
  }
  return $items;
}

//the monster
export function getChoiceList(titre, list, excluded, extraHtml) {
  const $div = $(`<div></div>`);
  $div.append($(`<h3>${titre}</h3>`));
  //handlers depends of excluded or not
  const handlers = {
    moreinfo: events.handlers.showDetails,
  };
  if (excluded) {
    handlers.like = (id, $item) => {
      if (excluded) {
        $item.hide(500, () => {
          events.selectChoice(id, true);
          $item.remove();
        });
      } else {
        events.selectChoice(id, true);
      }
    };
  } else {
    handlers.dislike = (id, $item) => {
      $item.hide(500, () => {
        events.rejectChoice(id, true);
        $item.remove();
      });
    };
  }
  $div.append(
    getInterestsList(
      list.map((s) => s.fl),
      handlers,
      true
    )
  );
  return $div;
}

export function collapsibleContentHtml(shown, hidden, uuid) {
  let id = "collapseSpecs" + uuid;
  return `
  <div
class=" w-80 mx-2 p-3 rounded alert alert-dark ">
  <div  
  type="button" 

  data-bs-toggle="collapse" 
  data-bs-target="#${id}" 
  aria-expanded="false" 
  aria-controls="${id}">
    ${shown}
  <div class="collapse text-left" id="${id}">
  ${hidden}
  </div>
  </div>
  </div>
   `;
}
