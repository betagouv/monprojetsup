import { params } from "../config/params";

export {
  //constructor at startup to get things ready
  init,
  //
  //loads a profile coming from server
  loadSuggestions,
  //
  //getters
  getProfileValue,
  getProfileValueAsObject,
  getTags,
  getTypeBacGeneric,
  getNotes,
  //
  //setters
  setProfileValue,
  setProfileSubValue,
  unsetProfileSubValue,
  removeObjectFromProfileListByKeyValue,
  setMsgs,
  //
  //getter for loader, please do not use
  //for other purposes
  getData,
};

/*************************
 Data used by everybody 
 *************************/

const data = {
  profile: {
    login: "",
    scores: {},
    choices: {},
  },
  questions: params.questions,
  questions_account: params.questions_account,
  statsAdmis: {},
  urls: {},
  percentilesMoyGen40: [],
  percentilesMoyBac40: [],
  tagsSources: {},
  filieres: {},
  /*"Collaborateur juriste notarial": [
      "fl11433",
      "fl10433"
    ],*/
  /*************************
 Data used by autocomplete menus
*************************/
  srcAutoComplete: {
    geo_pref: [],
    metiers: {},
    thematiques: {},
    spe_classes: {},
    motscles: [],
  },
};

export const tousBacs = "";
export const tousGroupes = "";
export function moyGenIndex() {
  return data.constants.MOYENNE_GENERALE_CODE;
}
export function moyBacIndex() {
  return data.constants.MOYENNE_BAC_CODE;
}
export function statNotesDivider() {
  return data.constants.PRECISION_PERCENTILES / 20;
}

const scoresFields = ["interets", "thematiques"];

function init() {
  loadProfile({
    login: "",
    sugg_approved: [],
    sugg_rejected: [],
  });
  loadSuggestions([]);
}

function ensureMandatoryProfileFields() {
  if (data.profile === undefined) {
    data.profile = {};
  }
  if (data.suggestions === undefined) {
    data.suggestions = [];
  }
  for (const mandatory_obj_field of [
    "scores",
    "interets",
    "thematiques",
    "choices",
  ]) {
    if (data.profile[mandatory_obj_field] === undefined) {
      data.profile[mandatory_obj_field] = {};
    }
  }
}

/**
 * Returns a object of type { label: , src: } with src in
 * @param {string} key
 * @returns
 */
export function getLabel(key) {
  return data.labels[key];
}
export function getSummary(key) {
  const res = data.descriptifs[key];
  if (res === undefined) return "";
  if (res.summary) return res.summary;
  if (res.presentation) return res.presentation;
  return null;
}

export function getDomainesPro() {
  return data.domainesPro;
}

export function getPrenomNom() {
  return getProfileValue("prenom") + " " + getProfileValue("nom");
}
export function getInterests() {
  return data.interestsGroups;
}

export function getDescriptif(key) {
  const res = data.descriptifs[key];
  if (res === undefined) return "";
  let result = "";

  if (res.presentation) result += `<p>` + res.presentation + "</p>";
  /*
  if (res.url) {
    result += `<a href="${res.url}">En savoir plus...</a>`;
  }*/

  if (res.poursuite) result += "<p>" + res.poursuite + "</p>";

  if (res.metiers) result += "<p>" + res.metiers + "</p>";

  return result.replace("<h3>", "<br/>").replace("</h3>", "<br/>");
}

export function getDescriptifMetiers(key) {
  const res = data.descriptifs[key];
  if (res === undefined) return undefined;
  return res.metiers;
}

export function isFiliere(key) {
  return startsWith(key, data.labelsTypes.filiere);
}
export function isTheme(key) {
  return startsWith(key, data.labelsTypes.theme);
}
export function isSecteur(key) {
  return startsWith(key, data.labelsTypes.secteur);
}
export function isMetier(key) {
  return startsWith(key, data.labelsTypes.metier);
}

export function getExtendedLabel(id) {
  let name = getLabel(id);
  if (name == undefined) {
    return id; //better than undefined before, which was silently dropping some important info
  }
  /*
  const firstIndexOcc = name.indexOf("(");
  if (firstIndexOcc > 0) {
    name = name.substring(0, firstIndexOcc - 1);
  }*/
  return name;
  /*
  if (!name) return;

  if (isFiliere(id)) {
    name = "Formation: " + name.toUpperCase();
  }
  if (isTheme(id)) {
    name = "Domaine: " + name.toUpperCase();
  }
  if (isMetier(id)) {
    name = "Métier: " + name.toUpperCase();
  }
  return name;*/
}

export function getLabelsWithInfix(infix) {
  const res = {};
  for (const [key, label] of Object.entries(data.labels)) {
    const ll = label.toLowerCase();
    if (ll.startsWith(infix) || ll.includes(" " + infix)) {
      res[key] = label;
    }
  }
  return res;
}

function isCoveredByGroup(id) {
  return !!(id in data.keyToGroup && data.keyToGroup[id] != id);
}

export function getAllGroupLabels(keySet) {
  return Object.fromEntries(
    Object.entries(data.labels).filter(
      (t) => keySet.has(t[0]) && !isCoveredByGroup(t[0])
    )
  );
}

export function loadProfile(profile) {
  data.profile = profile;
  ensureMandatoryProfileFields();
  //expand scores
  for (const [key, score] of Object.entries(data.profile.scores)) {
    //if key in index metiers...
    for (const id of scoresFields) {
      if (key in data[id]) {
        data.profile[id][key] = score;
        break;
      }
    }
  }
  loadStats(profile.bac);
}

export function getProfile() {
  for (const id of scoresFields) {
    if (id in data.profile) {
      for (const [key, score] of Object.entries(data.profile[id])) {
        data.profile.scores[key] = score;
      }
    }
  }
  return data?.profile;
}

export function getAnonymousProfile() {
  const profile = getProfile();
  console.log("profile", profile);
  console.log("profileFields", data.profileFields);
  const mapChoices = profile.choices;
  profile.choices = Object.values(mapChoices);
  profile.interests = Object.entries(profile.scores)
    .filter((e) => e[1] > 0)
    .map((e) => e[0]);
  const profileAnonymous = Object.fromEntries(
    Object.entries(profile).filter((x) => data.profileFields.has(x[0]))
  );
  console.log("profileAnonymous", profileAnonymous);
  return profileAnonymous;
}

function loadSuggestions(suggestions) {
  data.suggestions = suggestions;
}

export function getNbSuggestionsWaiting() {
  return data.suggestions === undefined ? 0 : data.suggestions.length;
}

/* the stats depend on the bac of the user */
function loadStats(typeBac) {
  if (!typeBac) typeBac = tousBacs;

  try {
    if (!data) return;
    if (!data.statsAdmis) return;
    if (!data.statsAdmis.parGroupe) return;
    if (!data.statsAdmis.parGroupe[tousGroupes]) return;
    if (!data.statsAdmis.parGroupe[tousGroupes].parBac) return;
    if (!data.statsAdmis.parGroupe[tousGroupes].parBac[typeBac]) return;
    const freqCumulee40 =
      data.statsAdmis.parGroupe[tousGroupes].parBac[typeBac].parMatiere[
        moyGenIndex()
      ].frequencesCumulees;

    const nb = freqCumulee40[freqCumulee40.length - 1];

    data.percentilesMoyGen40 = [];
    for (const x of freqCumulee40) {
      data.percentilesMoyGen40.push(x / nb);
    }

    const freqCumuleeBac40 =
      data.statsAdmis.parGroupe[tousGroupes].parBac[typeBac].parMatiere[
        moyBacIndex()
      ].frequencesCumulees;

    const nbbac = freqCumuleeBac40[freqCumuleeBac40.length - 1];

    data.percentilesMoyBac40 = [];
    for (const x of freqCumuleeBac40) {
      data.percentilesMoyBac40.push(x / nbbac);
    }
  } catch (error) {
    console.log(error);
    data.percentilesMoyGen40 = [];
    data.percentilesMoyBac40 = [];
  }
}

export function getDefaultUrl(key) {
  let label = getLabel(key);
  if (label !== undefined) {
    label = label.replace("en apprentissage", "");
    label = label.replace("-", "").replace("-", "").replace("-", "");
    const idx = label.indexOf("(");
    if (idx > 0) label = label.substr(idx);
    return params.onisepSearchHTTPAdress + label;
  } else {
    return null;
  }
  //return "https://avenirs.onisep.fr/les-metiers-animes/Nouvelles-technologies/administrateur-administratrice-reseaux";
}

export function getUrls(gFlCod, autocomplete) {
  if (gFlCod in data.urls) {
    return data.urls[gFlCod];
  }
  const urls = new Set();
  for (const key of convertGroupsToKeys([gFlCod])) {
    if (key in data.urls) {
      for (const url of data.urls[key]) {
        urls.add(url);
      }
    }
  }
  if (urls.size > 0) {
    return Array.from(urls);
  } else if (autocomplete) {
    //on vérifie qu'on est pas sur un groupe
    const result = [];
    const uri = getDefaultUrl(gFlCod);
    if (uri != null) {
      result.push(encodeURI(uri));
    }
    const uri2 =
      params.internetSearchHTTPAdress + encodeURI(getExtendedLabel(gFlCod));
    result.push(uri2);
    return result;
  } else {
    return [];
  }
}

/*
export function getDefaultUri(gFlCod) {
  const uri = getDefaultUrl(gFlCod);
  if (uri) return encodeURI(uri);
  return null;
}*/

export function getScreenName() {
  let prenom = getProfileValue("prenom");
  let nom = getProfileValue("nom");
  let nomAffiche = prenom + " " + nom;
  if (prenom === undefined && nom === undefined) {
    nomAffiche = getProfileValue("login");
  } else if (prenom === undefined) {
    nomAffiche = nom;
  } else if (nom == undefined) {
    nomAffiche = prenom;
  }
  return nomAffiche;
}

function getProfileValue(id) {
  if (data === undefined) return undefined;
  if (data.profile === undefined) return undefined;
  return data.profile[id];
}

export function getListFromProfile(id) {
  const val = getProfileValue(id);
  return val;
}

function setProfileSubValue(id, title, value) {
  data.profile[id][title] = value;
}

function unsetProfileSubValue(id, title) {
  const dpid = data.profile[id];
  delete dpid[title];
}

function setProfileValue(id, value) {
  data.profile[id] = value;
}

function isObject(l) {
  return l !== null && l !== undefined && typeof l == "object";
}
export function removeFromListOrMap(id, key) {
  const l = data.profile[id];
  if (l !== undefined) {
    if (Array.isArray(l)) {
      const index = l.indexOf(key);
      if (index >= 0) {
        l.splice(index, 1);
        return true;
      }
    }
    if (isObject(l) && l[key] !== undefined) {
      delete l[key];
      if (scoresFields.includes(id) && key in data.profile.scores) {
        delete data.profile.scores[key];
      }
    }
  }
  return false;
}

export function addElementInBackOfProfileList(id, x) {
  return addElementInProfileList(id, x, false, [x]);
}

export function addElementWithScoreToProfile(id, x) {
  const def = {};
  def[x] = 100;
  const result = addElementInProfileList(id, x, false, def);
  data.profile.scores[x] = 100;
  return result;
}

function addElementInProfileList(id, x, addToFront, defaultValue) {
  let old = data.profile[id];
  if (old === undefined) {
    data.profile[id] = defaultValue;
    return true;
  } else if (Array.isArray(old) && !old.includes(x)) {
    if (addToFront) {
      data.profile[id].unshift(x);
    } else {
      data.profile[id].push(x);
    }
    return true;
  } else if (isObject(old) && old[x] === undefined) {
    old[x] = 100;
    return true;
  }
  return false;
}

export function getPercentilesMoyGen40() {
  return data.percentilesMoyGen40;
}

export function getPercentilesMoyBac40() {
  return data.percentilesMoyBac40;
}

function removeObjectFromProfileListByKeyValue(id, key, value) {
  const l = data.profile[id];
  if (l !== undefined) {
    data.profile[id] = l.filter((x) => x[key] != value);
  }
}

function getProfileValueAsObject(id) {
  if (data.profile[id] === undefined || typeof data.profile[id] !== "object") {
    data.profile[id] = {};
  }
  return data.profile[id];
}

function getTags() {
  return data.srcAutoComplete.motscles;
}

function updateSpecialitesOptions() {
  const niveau = getProfileValue("niveau");
  let bac =
    niveau == "term" || niveau == "prem" ? getProfileValue("bac") : tousBacs;
  if (!bac) bac = "";
  const optionsSpecialites = {}; //par defaut pas de specialites
  if (bac == "") {
    for (const [bac2, liste] of Object.entries(data.specialitesParBac)) {
      if (!bac2 || bac2 == "") continue;
      for (const id of liste) {
        if (id in data.specialites) {
          optionsSpecialites[id] =
            "Série " + bac2 + " - " + data.specialites[id];
        }
      }
    }
  } else if (bac in data.specialitesParBac) {
    const optionsSpecialitesIds = data.specialitesParBac[bac];
    for (const id of optionsSpecialitesIds) {
      if (id in data.specialites) {
        optionsSpecialites[id] = data.specialites[id];
      }
    }
  }
  data.srcAutoComplete.spe_classes = optionsSpecialites;
}

/**
 *
 * @param {} list
 * @returns groups element of a list acording to the data.flGroup mapping
 */
export function convertKeysToGroups(list) {
  const seen = new Set();
  const result = [];
  const mapped = {};
  for (const l of list) {
    let grp = l;
    if (!seen.has(l)) {
      if (data.keyToGroup[l]) {
        const overGrp = data.keyToGroup[l];
        mapped[overGrp] = grp; //this is why we use a set: we do not want to overwrite mapped[overGrp]
        grp = overGrp;
      }
      if (!seen.has(grp)) result.push(grp);
    }
    seen.add(l);
    seen.add(grp);
  }
  return { groups: result, grpToKey: mapped };
}

export function convertGroupsToKeys(list) {
  let result = [];
  for (const l of list) {
    const ll = data.groupToKeys[l];
    if (ll) result = result.concat(ll);
    else result.push(l);
  }
  return result;
}

export function getQuestions(category) {
  updateSpecialitesOptions();
  if (category) {
    return data.questions.filter((q) => q.category == category);
  } else {
    return data.questions;
  }
}

export const SUGG_APPROVED = 1;
export const SUGG_REJECTED = 2;

export function getSuggestionsYes() {
  ensureMandatoryProfileFields();
  return Object.values(data.profile.choices)
    .filter((s) => s.status != null && s.status == SUGG_APPROVED)
    .map((s) => s.fl);
}

export function getOrCreateSugg(key, newStatus) {
  let changed = true;
  ensureMandatoryProfileFields();
  let sugg = { fl: key, expl: [{ perso: {} }] }; //important do not include status
  if (newStatus == SUGG_APPROVED) {
    const suggs = data.suggestions.filter((s) => s.fl == key);
    if (suggs.length > 0) {
      sugg = suggs[0];
    } else if (key in data.profile.choices) {
      sugg = data.profile.choices[key];
    }
  }
  changed = sugg.status === undefined || sugg.status !== newStatus;
  sugg.status = newStatus;
  data.profile.choices[key] = sugg;
  return [sugg, changed];
}

export function getSuggestionsRejected(profile) {
  ensureMandatoryProfileFields();
  if (!profile) profile = data.profile;
  return Object.values(profile.choices).filter(
    (s) => s.status != null && s.status == SUGG_REJECTED
  );
}

export function getSuggestionsApproved(profile) {
  ensureMandatoryProfileFields();
  if (!profile) profile = data.profile;
  return Object.values(profile.choices).filter(
    (s) => s.status != null && s.status == SUGG_APPROVED
  );
}
/*
export function getSuggestion(grpid) {
  ensureMandatoryProfileFields();
  let result = data.profile.choices[grpid];
  if (result) return result;
  for (const sugg of data.suggestions) {
    if (
      sugg.fl == grpid ||
      (data.groupToKeys[sugg.fl] && data.groupToKeys[sugg.fl].includes(grpid))
    ) {
      return sugg;
    }
  }
  return null;
}*/

function filterMap(m, f) {
  return Object.fromEntries(Object.entries(m).filter((e) => f(e[1])));
}

export function emptyBin() {
  data.profile.choices = filterMap(
    data.profile.choices,
    (s) => s.status == null || s.status != SUGG_REJECTED
  );
}

export function removeFromBin(id) {
  data.profile.choices = filterMap(data.profile.choices, (s) => s.fl != id);
}

export function getSuggestionsWaiting() {
  ensureMandatoryProfileFields();
  return data.suggestions;
}

function getNotes(topic) {
  if (!data.profile.msgs || !data.profile.msgs[topic]) {
    return [];
  }
  return data.profile.msgs[topic];
}

function setMsgs(msgs, replace) {
  if (!data.profile.msgs) data.profile.msgs = {};
  if (replace) {
    data.profile.msgs = msgs;
  } else {
    for (const topic in msgs) {
      data.profile.msgs[topic] = msgs[topic];
    }
  }
}

export function getSourceAutoComplete(id) {
  return data.srcAutoComplete[id];
}

export function getOptions(id) {
  return data[id];
}

export function getTagsSources() {
  return data.tagsSources;
}

export function getScore(key) {
  if (data.profile.scores === undefined) return undefined;
  const result = data.profile.scores[key];
  return result === undefined ? 0 : result;
}

export function getProfileKeys() {
  const result = [];
  if (data.profile.scores !== undefined) {
    for (const key in data.profile.scores) {
      if (data.profile.scores[key] !== 0 && data.profile.scores[key] !== "0") {
        result.push(key);
      }
    }
  }
  for (const key in data.profile.choices) {
    if (data.profile.choices[key].status == SUGG_APPROVED) result.push(key);
  }
  return result;
}

export function isFavoris(key) {
  return (
    key in data.profile.choices &&
    data.profile.choices[key].status == SUGG_APPROVED
  );
}
export function isInBin(key) {
  return (
    key in data.profile.choices &&
    data.profile.choices[key].status == SUGG_REJECTED
  );
}
export function isZeroScore(key) {
  if (data.profile.scores === undefined) return undefined;
  const result = data.profile.scores[key];
  return result !== undefined && result == 0;
}

export function isSelected(key) {
  return getScore(key) > 0 || isFavoris(key);
}

export function isRejected(key) {
  return isZeroScore(key) || isInBin(key);
}

export function getPositiveScores() {
  if (data.profile.scores === undefined) return [];
  const result = [];
  for (const [key, value] of Object.entries(data.profile.scores)) {
    if (value !== 0 && value !== "0") {
      result.push(key);
    }
  }
  return result;
}

export function toggleScore(key) {
  const wasSelected =
    key in data.profile.scores &&
    data.profile.scores[key] !== 0 &&
    data.profile.scores[key] !== "0";
  if (wasSelected) {
    delete data.profile.scores[key];
  } else {
    data.profile.scores[key] = 1;
  }
  return data.profile.scores[key];
}

//please do not use outside "load"
function getData() {
  return data;
}

function getTypeBacGeneric(bac) {
  if (bac === undefined) {
    const profile = data.profile;
    if (profile === undefined) {
      return { index: 0, label: "Tous" };
    }
    bac = data.profile.bac;
  }
  if (bac === undefined || bac === null || bac === tousBacs) {
    return { index: 0, label: "Tous" };
  }
  switch (bac[0]) {
    case "G":
      return { index: 1, label: "Générale" };
    case "S":
      return { index: 2, label: "Technologique" };
    case "P":
      return { index: 3, label: "Professionnel" };
    default:
      return { index: 0, label: "Tous" };
  }
}

export function postLoad() {
  //in this order
  updateAutoComplete();

  //create ungroup structure
  createGroupToKeysCorrespondance();

  //create metiers to formatiosn
  createMetiersToFormations();

  //select one id per Interest  groups
  createKeysForGroups();
}

function createKeysForGroups() {
  let i = 0;
  for (const group of data.interestsGroups) {
    group.key = "group_interest_" + i++;
    for (const item of group.items) {
      if (item.keys === undefined || item.keys.length == 0)
        throw new Error("Empty group");
      item.key = item.keys[0];
    }
  }
  i = 0;
  for (const group of data.domainesPro) {
    group.key = "group_domaine_pro_" + i++;
  }
}

function createMetiersToFormations() {
  data.liensFormationsMetiers = {};
  for (const [met, fils] of Object.entries(data.metiersToFormations)) {
    data.liensFormationsMetiers[met] = fils;
    for (const key of fils) {
      if (!(key in data.liensFormationsMetiers)) {
        data.liensFormationsMetiers[key] = [];
      }
      data.liensFormationsMetiers[key].push(met);
    }
  }
}

export function getLiensMetiersFormations(key) {
  return data.liensFormationsMetiers[key];
}

export function getLiensSecteursMetiers(key) {
  return data.secteursToMetiers[key];
}

export function getAsKeyLabelArray(l) {
  if (l === undefined) return undefined;
  if (l === null) return null;
  return Object.fromEntries(
    l.map((key) => [key, getLabel(key)]).filter((e) => e[1] !== undefined)
  );
}

function createGroupToKeysCorrespondance() {
  data.groupToKeys = {};
  for (const [key, group] of Object.entries(data.keyToGroup)) {
    let l = data.groupToKeys[group];
    if (l === undefined) {
      l = [];
      data.groupToKeys[group] = l;
    }
    l.push(key);
  }
}

export function ppBac(bac) {
  if (bac) {
    if (bac == "P") return "Pro";
    if (bac == "PA") return "Pro Agricole";
  }
  return bac;
}

export function getEDSData(grp) {
  //const keys = convertGroupsToKeys([grp]);
  if (data.eds[grp]) {
    const label = getLabel(grp);
    return { label: label, eds: data.eds[grp] };
  }
  const keys = convertGroupsToKeys([grp]);
  for (const key of keys) {
    if (key != grp) {
      //pray there is no loop in the data
      const res = getEDSData(key);
      if (res) return res;
    }
  }
  return null;
}

function updateAutoComplete() {
  data.srcAutoComplete.geo_pref = data.cities;
  data.srcAutoComplete.motscles = Object.keys(data.tagsSources);
  data.srcAutoComplete.thematiques = data.thematiques;
  updateSpecialitesOptions();
}

export function startsWith(str, prefixes) {
  if (str == undefined) {
    return false;
  }
  for (const pref of prefixes) {
    if (str.startsWith(pref)) return true;
  }
  return false;
}

/* calcul de matching simple sans inclure les mots-clés */
export function getSearchTags(keys) {
  let okPref = [];
  //data.labelsTypes
  //on crée un filtre du type '(MET|T_ITM|fl|fr)(.*)'
  for (const [key, prefixes] of Object.entries(data.labelsTypes)) {
    if (keys.includes(key)) {
      okPref = okPref.concat(prefixes);
    }
  }
  //would regexp be really better?  let regexp = '(a|b)(.*)';
  const result = {};
  for (const [key, label] of Object.entries(data.labels)) {
    if (startsWith(key, okPref)) {
      result[key] = label;
    }
  }

  for (const [key, grp] of Object.entries(data.keyToGroup)) {
    if (key in result && grp in data.labels) {
      result[grp] = data.labels[grp];
      //we keep it delete result[key];
    }
  }
  return result;
}

function convertFlCodToCarteId(fl) {
  if (fl.startsWith("fl")) {
    const flNum = parseInt(fl.substr(2));
    if (flNum >= 1000000) {
      return "fl" + (flNum - 1000000) + "x las";
    } else {
      return fl + "x";
    }
  }
}

export function getParcoursupSearchAdress(groups, searchBanner, gtas = []) {
  const keys = convertGroupsToKeys(groups);
  let flStrs = keys.map((fl) => convertFlCodToCarteId(fl)).join(" ");
  let uri = params.parcoursupCarteHTTPAdress + "?search=" + encodeURI(flStrs);

  if (gtas && gtas.length > 0) {
    uri += "&center_on_interests=" + gtas.join(",");
  }
  /*
  if (searchBanner !== null) {
    uri += "&search_banner=" + encodeURI(searchBanner);
  }*/
  return uri;
}

export function getStats(statsAll, bac) {
  const stats = statsAll ? (statsAll.stats ? statsAll.stats : null) : null;
  let statsTousBacs = null;
  if (stats && stats[tousBacs]) statsTousBacs = stats[tousBacs];
  let statsBac = null;
  if (stats && stats[bac] && bac != tousBacs) statsBac = stats[bac];
  return {
    stats: stats,
    statsBac: statsBac,
    statsTousBacs: statsTousBacs,
  };
}
