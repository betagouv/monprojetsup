import * as tierlist from "../cards/cards";
import * as data from "../../data/data";
import DOMPurify from "dompurify";

const maxDisplayedPills = 50;

function removeDiacritics(str) {
  if (!str) {
    return "";
  }
  return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
}

let timeout = null;

function debounce(func, wait) {
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}

export function setup(
  tags, // label --> [key1,key2,...]
  threshold,
  selectHandler,
  deselectHandler,
  onMoreInfoHandler,
  isSelected,
  isRejected,
  labellingFunction,
  $field,
  $pillsContainer,
  newSearchCallback
) {
  const cleanTags = Object.fromEntries(
    Object.entries(tags).map((t) => [
      removeDiacritics(t[0])
        .toLowerCase()
        .replace("<span>", "")
        .replace("</span>", "")
        .replace("<br>", ""),
      t[1],
    ])
  );

  const allKeys = new Set();
  for (const keys of Object.values(cleanTags)) {
    for (const key of keys) {
      allKeys.add(key);
    }
  }

  const cleanLabels = Object.fromEntries(
    Object.entries(data.getAllGroupLabels(allKeys)).map((t) => [
      removeDiacritics(t[1])
        .toLowerCase()
        .replace("<span>", "")
        .replace("</span>", "")
        .replace("<br>", ""),
      t[0],
    ])
  );

  $field.off("input").on("input", () => {
    const dirty = $field.val();
    const stringSearch = DOMPurify.sanitize(dirty);
    if (stringSearch.length < threshold) {
      $pillsContainer.empty();
      $pillsContainer.show();
      return;
    }

    debounce(newSearchCallback, 2000)(stringSearch);

    const lookups = stringSearch
      .split(" ")
      .filter((s) => s != "")
      .map((s) => removeDiacritics(s).toLowerCase());

    const matchingKeysArr = []; //maps keys to the number of times they match
    const matchingKeysPrefixArr = []; //maps keys to the number of times they match as prefix
    const matchingLabelsArr = []; //maps keys to the number of times they match as part of label
    const matchingLookups = {}; //map keys to the number of lookups they match
    //changement d'algo: on cherche des keys qui intersectent chaque

    for (const lookup of lookups) {
      const s = new Set();
      const spref = new Set();
      const slabels = new Set();
      for (const [label, keys] of Object.entries(cleanTags)) {
        const index = label.indexOf(lookup);
        const indexPrefix = index == 0 || label.indexOf(" " + lookup) >= 0;
        if (index >= 0) {
          for (const k of keys) {
            s.add(k);
            if (matchingLookups[k] === undefined)
              matchingLookups[k] = new Set();
            matchingLookups[k].add(lookup);
          }
        }
        if (indexPrefix) {
          for (const k of keys) spref.add(k);
        }
      }
      for (const [label, key] of Object.entries(cleanLabels)) {
        const indexPrefix =
          label.startsWith(lookup) ||
          label.indexOf("(" + lookup) >= 0 ||
          label.indexOf(" " + lookup) >= 0;
        if (indexPrefix) {
          slabels.add(key);
        }
      }
      matchingKeysArr.push(s);
      matchingKeysPrefixArr.push(spref);
      matchingLabelsArr.push(slabels);
    }

    const matchingKeys = {};
    for (const s of matchingKeysArr) {
      for (const e of s) {
        const x = matchingKeys[e];
        matchingKeys[e] = x ? x + 1 : 1;
      }
    }
    const matchingKeysPrefix = {};
    for (const s of matchingKeysPrefixArr) {
      for (const e of s) {
        const x = matchingKeysPrefix[e];
        matchingKeysPrefix[e] = x ? x + 1 : 1;
      }
    }
    const matchingLabels = {};
    for (const s of matchingLabelsArr) {
      for (const e of s) {
        const x = matchingLabels[e];
        matchingLabels[e] = x ? x + 1 : 1;
      }
    }

    /** todo: sort keys in the order of maximum prefix match, then maximum matchec */

    let maxMatchingLookups = 0;
    for (const s of Object.values(matchingLookups)) {
      maxMatchingLookups = Math.max(maxMatchingLookups, s.size);
    }

    const allKeys = Object.keys(matchingKeys).filter(
      (k) =>
        matchingLookups[k] !== undefined &&
        matchingLookups[k].size >= maxMatchingLookups
    );
    allKeys.sort((a, b) => {
      if ((matchingLabels[a] || 0) > (matchingLabels[b] || 0)) return -1;
      if ((matchingLabels[a] || 0) < (matchingLabels[b] || 0)) return 1;
      if ((matchingKeysPrefix[a] || 0) > (matchingKeysPrefix[b] || 0))
        return -1;
      if ((matchingKeysPrefix[a] || 0) < (matchingKeysPrefix[b] || 0)) return 1;
      if ((matchingKeys[a] || 0) > (matchingKeys[b] || 0)) return -1;
      if ((matchingKeys[a] || 0) < (matchingKeys[b] || 0)) return 1;
      /*
      if (matchingKeysPrefix[a] > 0 && matchingKeysPrefix[b] == undefined)
        return -1;
      if (matchingKeysPrefix[a] == undefined && matchingKeysPrefix[b] > 0)
        return 1;*/
      return b.localeCompare(a);
    });

    const displayedKeys = allKeys.slice(0, maxDisplayedPills);
    const groupedKeys = data.convertKeysToGroups(displayedKeys).groups;
    const res = Object.fromEntries(
      groupedKeys.map((key) => [key, labellingFunction(key)])
    );

    const $list = tierlist.getCardsDiv(
      res,
      isRejected,
      selectHandler,
      deselectHandler,
      onMoreInfoHandler
    );

    //$pillsContainer.empty().append($("<p>TEST</p>"));
    $pillsContainer.toggle($list.length >= 0);
    $pillsContainer.empty().append($list);
  });
}
