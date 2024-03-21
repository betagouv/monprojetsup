import * as data from "./../../data/data";
import * as tierlist from "../cards/cards";
import $ from "jquery";
import * as search from "./../search/searchbar";

export function getQuestionDiv(q, toggleProfileScoreHandler) {
  switch (q.type) {
    case "radio":
      return $(getRadioQuestionDiv(q));
    case "checkbox":
      return $(getCheckboxQuestionDiv(q));
    case "textarea":
      return $(getTextAreaQuestionDiv(q));
    case "searchbar_metiers":
    case "searchbar_formations":
      return getSearchBardDiv(q);
    case "autocomplete":
      return $(getAutoCompleteQuestionDiv(q.id, q.placeholder, true));
    case "range":
      return $(getRangeQuestionDiv(q));
    case "tierlist":
      return getTierListQuestionDiv(q, toggleProfileScoreHandler);
    default:
      return $("<p>Type de question '" + q.type + "' inconnu</p>");
  }
}

function getCheckboxQuestionDiv(q) {
  if (q.options === undefined || q.options === null || q.options.length == 0)
    return "";
  let str = [];
  str.push(
    `<div class="btn-group-vertical" name="${q.id}" role="group" aria-label="basic">`
  );
  let i = 0;
  for (const value in q.options) {
    const label = q.options[value];
    const selected = data.getProfileValue(q.id);
    //const selected =  == value;
    str.push(
      `<input type="checkbox" value="${value}" name="${q.id}" class="btn-check" id="for${q.id}${i}" autocomplete="off">`
    );
    str.push(
      `<label class="btn btn-outline-secondary" for="for${q.id}${i}">${label}</label>`
    );
    i++;
  }
  str.push(`</div>`);
  if (q.feedback) {
    str.push('<div id="' + q.id + '_feedback"></div>');
  }
  return str.join("");
}

function getRadioQuestionDiv(q) {
  let str = [];
  str.push(`<select name="${q.id}" class="form-select profile-radio-input" >`);
  if (
    data.getProfileValue(q.id) === undefined ||
    data.getProfileValue(q.id) === null
  ) {
    str.push(`<option name="${q.id}" id="fordefault${q.id}"  value=""`);
    str.push(" checked ");
    str.push(`>`);
    str.push(`<label >${q.placeholder}</label>`);
    str.push(`</option>`);
  }
  let i = 0;
  for (const value in q.options) {
    const label = q.options[value];
    str.push(`<option name="${q.id}" id="for${q.id}${i}" value = "${value}" `);
    if (data.getProfileValue(q.id) == value) str.push(" selected ");
    str.push(`>`);
    str.push(`<label for="for${q.id}${i}">` + label + "</label>");
    str.push(`</option>`);
    i++;
  }
  str.push(`</select>`);
  if (q.feedback) {
    str.push('<div id="' + q.id + '_feedback"></div>');
  }
  return str.join("");
}

const useModalWhenSearching = false;

function getSearchBardDiv(q) {
  if (useModalWhenSearching) {
    const $res = $(`<div class="d-flex flex-row ">
                <button
                  type="button"
                  class="btn-secondary btn p-3"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="16"
                    height="16"
                    fill="currentColor"
                    class="bi bi-search"
                    viewBox="0 0 16 16"
                  >
                    <path
                      d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"
                    /></svg
                  >&nbsp;&nbsp;&nbsp;Rechercher...
                </button>
              </div>
              <div id="modal_container_${q.id}"></div>`);

    $(`button`, $res)
      .off("click")
      .on("click", function () {
        search.openSearchModal(`modal_container_${q.type}`, q.source);
      });
    return $res;
  } else {
    const $searchBar = search.getSearchBar();

    search.setupSearchBar($searchBar, q.source);

    return $searchBar;
  }
}

function getTierListQuestionDiv(q, toggleProfileScoreHandler) {
  const items = data.getOptions(q.id);
  const categories = data.getOptions(q.id + "Categories");
  return tierlist.getToggleTierListDiv(
    items,
    categories,
    data.isSelected,
    data.isRejected,
    toggleProfileScoreHandler, //selectHandler
    toggleProfileScoreHandler //deselectHandler
  );
}

function getTextAreaQuestionDiv(q) {
  let content = data.getProfileValue(q.id);
  if (content === undefined || content === null) content = "";
  const placeholder = q.placeholder ? `placeholder="${q.placeholder}..."` : "";
  return `<input class="form-control textarea-profile-input" 
    id="${q.id}" 
    value="${content}"
    ${placeholder}
    ></input>`;
}

export function getAutoCompleteQuestionDiv(id, placeholder, showSelecteditems) {
  //autocomplete will be set up later by 'setUpAutoComplete'
  const result = [];
  result.push(`<input class="form-control" type="text"  id="${id}_autocomplete" 
    placeholder="${placeholder}..."/><br>`);
  if (showSelecteditems) {
    result.push(`<div class="container-fluid" id="${id}_list"></div>`);
  }
  return result.join("");
}

function getRangeQuestionDiv(q) {
  const str = [];
  let res = data.getProfileValue(q.id);
  if (!res) res = "";

  str.push('<div class="p-4 mb-3 rounded question">');
  str.push(`<input 
  class="form-check-input rangeQuestionDivCheckBox"
   type="checkbox" 
   value="" 
   qid="${q.id}"
   id="${q.id}_checkbox"
   >
  <label class="form-check-label" >
    Je ne sais pas / je ne veux pas répondre
  </label>`);

  str.push(
    `<input id="${q.id}_range" qid="${q.id}" type="range" 
    class="form-range profile-range-input" min="0" max="19.5" step = "0.5"
    value="${res}"/>`
  );

  str.push(
    `
    <div id="${q.id}_msg" class="alert alert-info">
    Tu t'es auto-évalué à 
    <output id="${q.id}_output">${res}</output>/20.`
  );
  if (q.feedback) {
    str.push(`<div  id="${q.id}_feedback"></div>`);
  }
  str.push("</div>");

  str.push("</div>");
  return str.join(" ");
}
