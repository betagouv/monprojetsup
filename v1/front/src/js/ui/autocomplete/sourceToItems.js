import * as data from "./../../data/data";

export function sourceToItems(id) {
  const source = data.getSourceAutoComplete(id);
  let source2 = source;
  if (Array.isArray(source)) {
    //typically an array of strings we want to match against, unconditionally with respect to accents and case
    source2 = {};
    for (const mot of source) {
      source2[mot] = mot;
    }
  }
  let items = [];

  for (const [key, mots] of Object.entries(source2)) {
    const motss = Array.isArray(mots) ? mots : [mots];
    for (const mot of motss) {
      items.push({
        //key
        k: key,
        //label i.e. affichage dans le dropdown
        l: mot,
        //valeur utilisée pour comparaison avec lookup
        v: mot
          .replace(".", "")
          .normalize("NFD")
          .replace(/[\u0300-\u036f]/g, "")
          .replace("'", " ")
          .toLowerCase(),
      });
    }
  }
  return items;
}
