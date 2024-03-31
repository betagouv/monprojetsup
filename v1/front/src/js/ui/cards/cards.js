import $ from "jquery";
import * as data from "../../data/data";
import * as session from "../../app/session";

function capitalizeFirstLetter(str) {
  if (str.charAt) return str.charAt(0).toUpperCase() + str.slice(1);
}

const activeClass = "interested";
const inactiveClass = "notinterested";

export function getChoiceCard(id, name, handlers, big, isSelected) {
  /* debut pouces */
  const $div = [];
  // if ($div === undefined) $div = $(`<div class="d-flex flex-row"></div>`);
  const hx = big ? `h1 class="display-1"` : "h2";
  const like_icon = isSelected
    ? "bi-heart-fill red-heart"
    : //: big
      //? "green bi-emoji-smile"
      "bi-heart";
  const dunno_icon = "bi bi-emoji-neutral";
  const no_icon =
    //big ? "bi-emoji-frown" :
    "bi-x-circle-fill";

  if (handlers.moreinfo) {
    const $buttonInfo = $(
      `<button 
        data-toggle="tooltip" title="Plus d'infos"
        type="button"
        class= "btn card-icon card-icon-question mx-2 my-1" 
        >
          <${hx} class="my-auto"><i class="bi bi-info-circle-fill "></i></${hx}>
        </button>`
    ).on("click", () => handlers.moreinfo(id));
    $div.push($buttonInfo);
  }

  if (name && name.length > 0) {
    const $name = $(
      `<button role="button" class="btn  card-text py-3">${capitalizeFirstLetter(
        name
      )}</button>`
    );
    if (handlers.moreinfo) $name.on("click", () => handlers.moreinfo(id));
    $div.push($name);
  }

  if (handlers.like) {
    //const icon = isSelected ? "bi-heart-fill red-heart" : "bi-emoji-smile";
    const $button = $(
      `<button 
          data-toggle="tooltip" title="J'aime"
          type="button"
          class="btn card-icon ms-2" 
        >
          <${hx} class="my-auto "><i class="heart_${id}_icon  
          ${big ? "card-big-icon" : ""} bi ${like_icon} "></i></${hx}>
          </button>`
    ).on("click", () => {
      handlers.like(id);
    });
    $div.push($button);
  }

  if (handlers.dunno) {
    const $button = $(
      `<button 
          data-toggle="tooltip" title="Je ne sais pas"
          type="button"
          class="btn card-icon" 
        >
          <${hx} class="my-auto ">
          <i class="${big ? "card-big-icon" : ""} bi ${dunno_icon} "></i>
          </${hx}>
          </button>`
    ).on("click", () => {
      handlers.dunno(id);
    });
    $div.push($button);
  }

  if (handlers.dislike) {
    const $button = $(
      `<button 
            class="btn card-icon ms-0 px-0 me-4" 
            type="button"
            role="button"
            title="Ne plus voir ça">
                      <${hx} class="my-auto">
                      <i class="bi ${
                        big ? "card-big-icon" : ""
                      } ${no_icon}" "></i></${hx}>
      </button>`
    ).on("click", () => {
      handlers.dislike(id);
    });
    $div.push($button);
  }

  return $div;
}
export function getChoiceCard2(id, name, handlers, big, sober) {
  /* debut pouces */
  const $div = [];
  // if ($div === undefined) $div = $(`<div class="d-flex flex-row"></div>`);

  if (sober) {
    const $buttonInfo = $(
      `<button 
        data-toggle="tooltip" title="Plus d'infos"
        type="button"
        class= "btn align-middle card-text" 
        >
          ${capitalizeFirstLetter(name)}
        </button>`
    );
    if (handlers.moreinfo) $buttonInfo.on("click", () => handlers.moreinfo(id));
    $div.push($buttonInfo);
    return $div;
  } else {
    return getChoiceCard(id, name, handlers, big, data.isSelected(id));
  }
}

const excludedClass = "card-excluded";
export function getSuggCard(id, name, handlers, isExcluded, sober) {
  const className = isExcluded ? excludedClass : "";
  const hideInfos = data.isFiliere(id) && session.hideFormations();
  if (hideInfos && sober) {
    sober = false;
    delete handlers.moreinfo;
  }
  const $item = $(
    `
        <div key="${id}" 
        class="card-${id}-div  d-inline-block ${className} me-4 my-2 card
         ">
         <div class=" d-flex flex-row choice-row">
         </div>
        </div>
   `
  );

  const handlers2 = {};
  if (handlers.like)
    handlers2.like = () => {
      handlers.like(id, $item);
    };
  if (handlers.dislike)
    handlers2.dislike = () => {
      handlers.dislike(id, $item);
    };
  if (!hideInfos && handlers.moreinfo) {
    handlers2.moreinfo = handlers.moreinfo;
  }

  const $choiceMenu = getChoiceCard2(id, name, handlers2, false, sober);
  $(".choice-row", $item).append($choiceMenu);

  return $item;
}

export function getCardsDiv(
  items,
  isRejected,
  onSelect,
  onDeselect,
  OnMoreInfo
) {
  let i = 0;
  const $div = $(`<div class="d-inline-block"></div>`);

  const handlers = {
    like: onSelect,
    dislike: onDeselect,
    moreinfo: OnMoreInfo,
  };

  for (const [key, label] of Object.entries(items)) {
    if (label === undefined) continue;
    if (isRejected(key)) continue;
    const $button = getSuggCard(key, label, handlers, false);

    $div.append($button);
    i++;
  }
  return $div;
}

export function getToggleTierListDiv(
  items,
  categories,
  isSelected,
  isRejected,
  onSelect,
  onDeselect
) {
  let i = 0;
  const $div = $(`<div class="d-inline-block"></div>`);

  for (const [key, label] of Object.entries(items)) {
    if (label === undefined) continue;
    if (isRejected(key)) continue;
    const $button = getToggleTierListPill(
      key,
      label,
      isSelected,
      onSelect,
      onDeselect
    );

    $div.append($button);
    i++;
  }
  return $div;
}
function getToggleTierListPill(key, label, isSelected, onSelect, onDeselect) {
  const btnStyle = isSelected(key) ? activeClass : inactiveClass;
  const $button = $(`<div  
            key="${key}"
            type="button"
            data-toggle="button"
            aria-pressed="false"
            autocomplete="off"
            class="btn-pill btn ${btnStyle} tier-list-item
            rounded-pill m-2 px-4 py-3
            "></div>`);
  /* content */
  let highlighted = {};
  const labelCapitalized = capitalizeFirstLetter(label);
  let labelHtml = [];
  for (let idx = 0; idx < labelCapitalized.length; ) {
    if (idx in highlighted) {
      const lookup = highlighted[idx];
      labelHtml.push(
        `<b class="text-">${labelCapitalized.substring(
          idx,
          idx + lookup.length
        )}</b>`
      );
      idx += lookup.length;
    } else {
      labelHtml.push(labelCapitalized[idx]);
      idx++;
    }
  }
  $button.html(labelHtml.join(""));
  attachHandler($button, key, isSelected, onSelect, onDeselect);
  return $button;
}

function attachHandler($button, key, isSelected, onSelect, onDeselect) {
  /* handler */
  $button.off("click").on("click", function (event) {
    const wasItemSelectedBeforeAction = isSelected(key); //we got dynamic computation of whtether the item is selected
    const target = event.target.closest(".btn-pill");
    if (!wasItemSelectedBeforeAction) {
      $(target).removeClass(inactiveClass);
      $(target).addClass(activeClass);
      if (onSelect) {
        onSelect(key);
      }
    } else {
      $(target).removeClass(activeClass);
      $(target).addClass(inactiveClass);
      if (onDeselect) {
        onDeselect(key);
      }
    }
  });
}

/**
 * @param {String} str
 * @returns {String}
 */
function removeDiacritics(str) {
  return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
}
