//import * as bootstrap from "./../../dist/bootstrap.bundle.min.js";
import { Dropdown } from "bootstrap";

const DEFAULTS = {
  threshold: 2,
  maximumItems: 5,
  highlightTyped: true,
  highlightClass: "text-primary",
  label: "label",
  value: "value",
  showValue: false,
  showValueBeforeLabel: false,
  onUpdateListHandler: null,
  feedbackHandler: null,
  maskList: false,
};

export default class Autocomplete {
  constructor(field, options) {
    this.field = field;
    this.options = Object.assign({}, DEFAULTS, options);
    this.dropdown = null;

    field.parentNode.classList.add("dropdown");
    field.setAttribute("data-bs-toggle", "dropdown");
    field.classList.add("dropdown-toggle");

    const dropdown = ce(
      `<div class="dropdown-menu dropdown-menu-autocomplete"></div>`
    );
    if (this.options.dropdownClass)
      dropdown.classList.add(this.options.dropdownClass);

    insertAfter(dropdown, field);

    this.dropdown = new Dropdown(field, this.options.dropdownOptions);

    field.addEventListener("click", (e) => {
      if (this.createItems() === 0) {
        e.stopPropagation();
        this.dropdown.hide();
      }
    });

    field.addEventListener("input", () => {
      if (this.options.onInput) this.options.onInput(this.field.value);
      this.renderIfNeeded();
    });

    field.addEventListener("keydown", (e) => {
      if (e.keyCode === 27) {
        this.dropdown.hide();
        return;
      }
      if (e.keyCode === 40) {
        this.dropdown._menu.children[0]?.focus();
        return;
      }
    });
  }

  setData(data) {
    this.options.data = data;
    this.renderIfNeeded();
  }

  renderIfNeeded() {
    if (this.createItems() > 0 && !this.options.maskList) this.dropdown.show();
    else this.field.click();
  }

  createItem(lookup, item) {
    let label;
    if (this.options.highlightTyped) {
      const idx = removeDiacritics(item.label)
        .toLowerCase()
        .indexOf(removeDiacritics(lookup).toLowerCase());
      const className = Array.isArray(this.options.highlightClass)
        ? this.options.highlightClass.join(" ")
        : typeof this.options.highlightClass == "string"
        ? this.options.highlightClass
        : "";
      label =
        item.label.substring(0, idx) +
        `<span class="${className}">${item.label.substring(
          idx,
          idx + lookup.length
        )}</span>` +
        item.label.substring(idx + lookup.length, item.label.length);
    } else {
      label = item.label;
    }

    if (this.options.showValue) {
      if (this.options.showValueBeforeLabel) {
        label = `${item.value} ${label}`;
      } else {
        label += ` ${item.value}`;
      }
    }

    return ce(
      `<button type="button" class="dropdown-item dropdown-item-autocomplete" data-label="${item.label}" data-value="${item.value}" data-key="${item.key}">${label}</button>`
    );
  }

  createItem2(lookups, item) {
    let label;
    const opts = this.options;
    const lookup = lookups.join("");
    if (opts.highlightTyped && lookup.length > 0) {
      let idx = 0;
      const maxIterations = 100; //never knows, this is js...
      const chunks = [];
      const buffer = item.label
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .replace("'", " ")
        .toLowerCase();
      const lookuplc = lookup
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .replace("'", " ")
        .toLowerCase();
      const className = Array.isArray(this.options.highlightClass)
        ? this.options.highlightClass.join(" ")
        : typeof this.options.highlightClass == "string"
        ? this.options.highlightClass
        : "";
      for (let i = 0; i < maxIterations; i++) {
        const oldidx = idx;
        idx = buffer.indexOf(lookuplc, idx);
        if (idx < 0) {
          chunks.push(item.label.substring(oldidx));
          break;
        } else if (idx > oldidx) {
          chunks.push(item.label.substring(oldidx, idx));
          continue;
        } else {
          chunks.push('<span class="' + className + '">');
          chunks.push(item.label.substring(idx, idx + lookup.length));
          chunks.push("</span>");
          idx += lookup.length;
        }
      }
      label = chunks.join("");
    } else {
      label = item.label;
    }
    return ce(
      '<button  style="word-wrap: break-word; white-space: normal;" type="button" class="dropdown-item"' +
        `data-label="${item.label}" data-value="${item.value}" data-key="${item.key}"` +
        '">' +
        label +
        "</button>"
    );
  }
  createItems() {
    let stringSearch = this.field.value
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .replace("'", "")
      .toLowerCase();

    const items = this.field.nextSibling;
    items.textContent = "";

    if (
      stringSearch.length == 0 ||
      stringSearch.length < this.options.threshold
    ) {
      this.dropdown.hide();
      if (this.options.feedbackHandler) {
        this.options.feedbackHandler(
          0,
          stringSearch.length,
          this.options.threshold
        );
      }
      if (this.options.updateListHandler) {
        this.options.updateListHandler([]);
      }
      return 0;
    }
    //

    const lookups = stringSearch.split(" ");

    /*************************** */
    let count = 0;
    const opts = this.options;
    let indexes = {};
    let appearsAsPrefix = {};
    let itemsToSort = [];
    for (const lookup of lookups) {
      if (lookup == "") continue;
      let matchedAsPrefix = [];
      let matchedAsNotPrefix = [];
      //if (itemsToSort.length > 0) keys = itemsToSort.map((x) => x.key);
      itemsToSort = [];
      for (const x of opts.source) {
        const item = {
          key: opts.key ? x[opts.key] : x.k,
          label: opts.label ? x[opts.label] : x.l,
          value: opts.value ? x[opts.value] : x.v,
        };
        const index = item.value.indexOf(lookup);
        if (index >= 0) {
          indexes[item.key] = index;
          if (index == 0) {
            appearsAsPrefix[item.key] = true;
          } else {
            const index2 = item.value.indexOf(" " + lookup);
            appearsAsPrefix[item.key] = index2 >= 0;
          }
          if (appearsAsPrefix[item.key]) {
            matchedAsPrefix.push(item);
          } else {
            matchedAsNotPrefix.push(item);
          }
          if (opts.maximumItems > 0 && ++count >= opts.maximumItems) {
            break;
          }
        }
      }
      if (matchedAsPrefix.length > 20) {
        itemsToSort = matchedAsPrefix;
      } else {
        itemsToSort = matchedAsPrefix.concat(matchedAsNotPrefix);
      }
    }
    //on se restreint aux matches préfixes si possible

    if (itemsToSort === undefined || itemsToSort.length == 0) {
      if (this.options.feedbackHandler) {
        this.options.feedbackHandler(
          0,
          stringSearch.length,
          this.options.threshold
        );
      }
      if (this.options.updateListHandler) {
        this.options.updateListHandler([]);
      }
      if (!this.options.maskList) {
        const noresult = ce(`<div class="dropdown-item">Aucun résultat</div>`);
        noresult.addEventListener("click", (e) => {
          this.field.value = "";
          this.dropdown.hide();
        });
        items.append(noresult);
        return 1;
      } else {
        return 0;
      }
    }

    itemsToSort.sort(function (first, second) {
      if (first.key == second.key) return 0;
      if (first.key == "") return -1;
      if (second.key == "") return 1;
      const indexFirst = indexes[first.key];
      const indexsecond = indexes[second.key];
      const appearsAsPrefixFirst = appearsAsPrefix[first.key];
      const appearsAsPrefixsecond = appearsAsPrefix[second.key];
      if (appearsAsPrefixFirst != appearsAsPrefixsecond) {
        return appearsAsPrefixFirst ? -1 : 1;
      } else if (true || indexFirst == indexsecond) {
        return first.label.localeCompare(second.label);
      } else {
        return indexFirst - indexsecond;
      }
    });

    const itemAll = {
      key: "",
      label: undefined,
      value: "",
    };

    let itemsToShow = itemsToSort;
    if (opts.useGroups && itemsToSort.length >= 3) {
      /*const indexLookup = itemsToSort.map((item) => item.value).indexOf(lookup);
      //todo match against item value
      if (indexLookup >= 0) {
        itemsToSort.splice(indexLookup, 1);
      }*/
      let label = "";
      let first = true;
      for (const item of itemsToSort) {
        if (!first) label += ", ";
        first = false;
        //if (item.label != lookup) {
        label += item.label;
      }
      itemAll.label = label;
      //itemsToSort.unshift(itemAll);
      itemsToShow = [itemAll];
    }

    if (!this.options.maskList) {
      for (const item of itemsToShow) {
        items.append(this.createItem2(lookups, item));
      }

      this.field.nextSibling
        .querySelectorAll(".dropdown-item")
        .forEach((item) => {
          item.addEventListener("click", (e) => {
            let label = e.currentTarget.getAttribute("data-label");
            let value = e.currentTarget.getAttribute("data-value");
            let key = e.currentTarget.getAttribute("data-key");

            this.field.value = ""; //RAZ field

            if (this.options.onSelectItems) {
              //const key = $(this).data("value");
              const liste =
                key == ""
                  ? itemsToSort
                  : [{ key: key, label: label, value: value }];
              this.options.onSelectItems(stringSearch, liste);
            }

            //this.dropdown.show();
            this.dropdown.hide();
            this.field.click();
          });
        });
    }

    if (this.options.updateListHandler) {
      this.options.updateListHandler(itemsToShow, lookups);
    }
    if (this.options.feedbackHandler) {
      this.options.feedbackHandler(
        itemsToShow.length,
        stringSearch.length,
        this.options.threshold
      );
    }

    return items.childNodes.length;
  }
}

/**
 * @param html
 * @returns {Node}
 */
function ce(html) {
  let div = document.createElement("div");
  div.innerHTML = html;
  return div.firstChild;
}

/**
 * @param elem
 * @param refElem
 * @returns {*}
 */
function insertAfter(elem, refElem) {
  return refElem.parentNode.insertBefore(elem, refElem.nextSibling);
}

/**
 * @param {String} str
 * @returns {String}
 */
function removeDiacritics(str) {
  return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
}
