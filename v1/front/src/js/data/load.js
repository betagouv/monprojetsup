import $ from "jquery";
import { getData, postLoad, loadProfile } from "./data";
import * as jszip from "jszip";
import * as jsziputils from "./jszip-utils.min";

export { loadZippedData };
/*************************
Loading data at startup, from a zip file
*************************/

/* (asynchronous) loading is over when those four guys are true */
let dataLoaded = false;
let data2Loaded = false;

/* this guy reads the zip file and extract a binary buffer called datas,
then pass it to loadDatas below */
async function loadZippedData() {
  console.log(`loadZippedData récup des données`);

  loading_log("Récupération des données...", "data");
  try {
    jsziputils.getBinaryContent("data/data.zip", function (err, data) {
      if (err) {
        console.log(err);
        return;
      }
      loadDatas(data);
    });
  } catch (e) {
    console.log("Failed to load zip file " + e);
  }

  while (!dataLoaded || !data2Loaded) {
    console.log(`loadZippedData waiting data load...`);

    await new Promise((r) => setTimeout(r, 500));
  }

  console.log(`loadZippedData postload`);
  postLoad();

  console.log("Chargement des données terminé");
}

/* this is where the zip content is decoded back into the json files
generated on server side, 
and the json objects are loaded in global js variables */
async function loadDatas(datas) {
  const data = getData();

  loaded_log("data");
  jszip.loadAsync(datas).then(function (zip) {
    /************
     * mots clés et stats scolaires
     * *********/
    let zipObj = zip.file("frontendData.json");
    if (zipObj) {
      zipObj.async("string").then(function (str) {
        loading_log("Chargement des statistiques...", "tags");
        const jdata = JSON.parse(str);
        data.labels = jdata.labels;
        data.matieres = jdata.matieres;
        data.statsAdmis = jdata.statsAdmis;
        loaded_log("tags");
        dataLoaded = true;
      });
    } else {
      console.log("No file " + "frontendData.json" + " in zip file");
    }
    /************
     * données onisep + autres
     * *********/
    let zipObj2 = zip.file("frontendData2.json");
    if (zipObj2) {
      zipObj2.async("string").then(function (str) {
        loading_log("Chargement des données...", "tags");
        const jdata = JSON.parse(str);

        data.specialites = jdata.specialites.specialites;
        data.specialitesParBac = jdata.specialites.specialitesParBac;
        data.cities = jdata.cities.cities;
        data.tagsSources = jdata.sources.sources;
        data.interets = jdata.interets.interets;
        data.interestsGroups = jdata.interets.groupes;
        data.domainesPro = jdata.thematiques.groupes;
        data.thematiques = jdata.thematiques.thematiques;
        data.metiers = jdata.metiers.metiers;
        data.descriptifs = jdata.descriptifs.keyToDescriptifs;
        data.urls = jdata.urls;
        data.labelsTypes = jdata.labelsTypes;
        data.constants = jdata.constants;
        data.keyToGroup = jdata.groups;
        data.metiersToFormations = jdata.liensMetiersFormations;
        data.secteursToMetiers = jdata.liensSecteursMetiers;
        data.eds = jdata.eds;
        data.grillesAnalyseCandidatures = jdata.grillesAnalyseCandidatures;
        data.grillesAnalyseCandidaturesLabels =
          jdata.grillesAnalyseCandidaturesLabels;
        data.profileFields = new Set(jdata.profileFields);

        loaded_log("tags");
        data2Loaded = true;
      });
    } else {
      console.log("No file " + "frontendData.json" + " in zip file");
    }
  });
}

/* some user feedback when loading */
function loading_log(msg, id) {
  $("#loadingContainer").append("<p id=" + id + ">" + msg + "</p>");
}

function loaded_log(id) {
  const node = $("#" + id + ":first-child");
  node.append("terminé.");
}
