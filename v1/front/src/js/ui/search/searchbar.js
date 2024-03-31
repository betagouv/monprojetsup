import $ from "jquery";
import * as events from "../../app/events";
import * as data from "../../data/data";
import * as autocomplete_pills from "../autocomplete/autocomplete_pills";

import { Modal } from "bootstrap";

export function clearInput($div) {
  const $input = $("input", $div);
  $input.val("");
  const $pillsContainer = $(`.container-fluid`, $div);
  $pillsContainer.empty();
}

export function setupSearchBar($div, source) {
  let itemsSource = null;
  if (source == "metiers") itemsSource = data.getSearchTags(["metier"]);
  else if (source == "formations")
    itemsSource = data.getSearchTags(["filiere"]);
  else itemsSource = [];

  let tags = {};
  let knownKeys = new Set();
  for (const [key, _] of Object.entries(itemsSource)) {
    const label = data.getExtendedLabel(key);
    const labelClean = removeDiacritics(label).toLowerCase();
    if (tags[labelClean] === undefined) tags[labelClean] = [];
    tags[labelClean].push(key);
    knownKeys.add(key);
  }
  //a list of pairs

  //on ajoute les tags
  if (useTagsInSearch) {
    const tagTosources = data.getOptions("tagsSources");
    for (const [tag, sources] of Object.entries(tagTosources)) {
      for (const key of sources) {
        if (knownKeys.has(key)) {
          const labelClean = removeDiacritics(tag).toLowerCase();
          if (tags[labelClean] === undefined) tags[labelClean] = [];
          tags[labelClean].push(key);
        }
      }
    }
  }
  //on purpose we add an element and its generalization
  tags = Object.fromEntries(
    Object.entries(tags).map((pair) => [
      pair[0],
      [...new Set(pair[1].concat(data.convertKeysToGroups(pair[1]).groups))],
    ])
  );

  const $input = $("input", $div);
  const $pillsContainer = $(`.container-fluid`, $div);

  autocomplete_pills.setup(
    tags,
    2,
    (x) => events.selectChoice(x, true),
    (x) => {
      events.rejectChoice(x, true);
      //setupSearchBar($div, source); //need to resetup myself, but could be lighter though
    },
    events.handlers.showDetails,
    data.isSelected,
    data.isRejected,
    data.getExtendedLabel,
    $input,
    $pillsContainer,
    (str) => events.handlers.newSearchCallback({ str: str, source: source })
  );
}

function removeDiacritics(str) {
  return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
}

const useTagsInSearch = true;

export function getSearchBar() {
  const $div = $(`<div><div
              class="d-flex flex-row pill rounded-pill border border-4 p-3 center w-100 search_bar"
            >
              <h2><i class="m-2 bi bi-search"></i></h2>
              <input autofocus class="form-control" type="text" placeholder="" />
              <span role="button" class="btn erase-search"><h2><i class="m-2 bi bi-x-lg "></i></h2></span>
            </div>
            
            <div class="container-fluid"></div></div>`);
  const $pillsContainer = $(`.container-fluid`, $div);
  const $input = $("input", $div);
  $(".erase-search", $div).on("click", () => {
    $pillsContainer.empty();
    $input.val("");
  });
  $input.on("focus", () => {
    window.scrollTo($input[0]);
    //$("body")[0].scrollIntoView({ behavior: "smooth" });
    //    .scrollTo($input);
  });
  return $div;
}

export function openSearchModal(pid, source) {
  const id = "searchSugg" + source;

  const $div = $(`<div 
    class="modal fade min-vh-100"  
    tabindex="-1" 
    role="dialog" 
    aria-labelledby="modalLabel"
    aria-hidden="true">
    <div class="modal-dialog modal-xl  modal-dialog-centered text-start fst-normal fs-6 fw-normal" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close btn-primary btn" data-bs-dismiss="modal" aria-label="Fermer">
          <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
        </div>
      </div>
    </div>
        `);

  const $searchBar = getSearchBar();
  $(".modal-body", $div).append($searchBar);

  setupSearchBar($searchBar, source);

  const myModal = Modal.getOrCreateInstance($div);
  myModal.show(document.getElementById(pid));
}
