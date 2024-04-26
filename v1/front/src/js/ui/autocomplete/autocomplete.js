import $ from "jquery";
import * as data from "./../../data/data";
import Autocomplete from "./bootstrap-5-autocomplete-tuned";
import { sourceToItems } from "./sourceToItems";

export function setUpAutoComplete(
  id,
  addHandler,
  trashHandler,
  feedbackHandler,
  threshold,
  updateListHandler = null
) {
  // Autocomplete section : https://github.com/Honatas/bootstrap-4-autocomplete
  const field = document.getElementById(id + "_autocomplete");
  if (field === null) {
    //console.log("No container " + id + "_autocomplete");
    return;
  }
  const itemsSource = sourceToItems(id);
  if (itemsSource == null || itemsSource.length == 0) {
    $(`#${id}_autocomplete`).hide();
    return false;
  } else {
    $(`#${id}_autocomplete`).show();
  }
  const ac = new Autocomplete(field, {
    source: itemsSource,
    label: "l",
    value: "v",
    key: "k",
    useGroups: false,
    onSelectItems: function (title, listeItems) {
      $("#" + id + "_autocomplete").val(""); //permet d'effacer la boite de saisie
      //if (data.getProfileValue(id) === undefined) data.setProfileValue(id, []);
      if (listeItems === undefined) return;
      if (addHandler) {
        for (const item of listeItems) {
          addHandler(item.key, item.label, item.value);
        }
      }
      updateAutoCompleteListItem(id, trashHandler);
    },
    feedbackHandler: feedbackHandler,
    highlightClass: "text-danger",
    threshold: threshold !== undefined ? threshold : 2,
    maximumItems: 0,
    updateListHandler: updateListHandler,
    maskList: true,
  });

  updateAutoCompleteListItem(id, trashHandler);
  return true;
}

export function updateAutoCompleteListItem(id, trashHandler) {
  const liste = data.getLabelsListFromProfile(id);
  if (liste === undefined) return;

  const $container = $(
    `<div class="container">
    <div class="profile-cell-container">
    </div>
    </div>`
  );

  let l = [];

  for (let item of liste) {
    let label = item;
    if (label === null || label === undefined || label === "") continue;
    const $cell = $(`
      <div class="profile-cell">
        <span class="profile-cell-label"><span>${label}</span>
          <span class="trashItem fr-icon-close-line" 
            label="${label}"
            title="Supprimer"
            >
          </span>
        </span>
      </div>`);
    $(".trashItem", $cell).on("click", function (event) {
      trashHandler(label);
      updateAutoCompleteListItem(id, trashHandler);
      event.preventDefault();
    });
    $(".profile-cell-container", $container).append($cell);
  }
  $("#" + id + "_list")
    .empty()
    .append($container);
}
