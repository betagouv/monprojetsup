import $ from "jquery";
import * as data from "./../../data/data";
import Autocomplete from "./bootstrap-5-autocomplete-tuned";
import { sourceToItems } from "./sourceToItems";

export function setUpAutoComplete(
  id,
  addHandler,
  trashHandler,
  feedbackHandler,
  threshold
) {
  // Autocomplete section : https://github.com/Honatas/bootstrap-4-autocomplete
  const field = document.getElementById(id + "_autocomplete");
  if (field === null) {
    console.log("No container " + id + "_autocomplete");
    return;
  }
  const itemsSource = sourceToItems(id);
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
      updateAutoCompleteListItem(id, itemsSource);
      registerTrashItemHandlers(id, trashHandler, itemsSource);
    },
    feedbackHandler: feedbackHandler,
    highlightClass: "text-danger",
    threshold: threshold !== undefined ? threshold : 2,
    maximumItems: 0,
  });

  updateAutoCompleteListItem(id, itemsSource);
  registerTrashItemHandlers(id, trashHandler, itemsSource);
}

function updateAutoCompleteListItem(id, itemsSource) {
  const liste = data.getListFromProfile(id);
  if (liste === undefined) return;

  let l = [];

  l.push('<div class="container">');
  l.push('<div class="profile-cell-container">');
  for (let item of liste) {
    let key = null;
    let label = null;

    label = item;

    if (label === null || label === undefined || label === "") continue;
    l.push('<div class="profile-cell">');

    l.push(`<span class="profile-cell-label">`);
    l.push(label);
    l.push("</span>");
    l.push(
      `
      <a class="trashItem${id}" 
        label="${label}"
        key="${key}"
        title="Supprimer"
        >
        <span class="fr-icon-close-line" aria-hidden="true"></span>
        </a>`
    );
    l.push("</div> ");
  }

  l.push("</div>");
  l.push("</div> ");
  $("#" + id + "_list").html(l.join(""));
}

function registerTrashItemHandlers(id, trashHandler, itemsSource) {
  if (trashHandler) {
    $(`.trashItem${id}`)
      .off("click")
      .on("click", function () {
        const value = $(this).attr("label");
        const key = $(this).attr("key");
        trashHandler(key, id, value);
        updateAutoCompleteListItem(id, itemsSource);
        registerTrashItemHandlers(id, trashHandler, itemsSource);
      });
  }
}
