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

  loadProfile({
    niveau: "term",
    bac: "Générale",
    duree: "indiff",
    apprentissage: "C",
    geo_pref: ["Rennes"],
    spe_classes: ["Mathématiques", "Sciences de la vie et de la Terre"],
    scores: {
      T_IDEO2_4812: 1,
      T_ROME_609891024: 1,
      T_IDEO2_4817: 1,
      T_IDEO2_4815: 1,
      T_IDEO2_4807: 1,
      T_ROME_1088162470: 1,
      T_ROME_860291826: 1,
      T_ROME_1316643679: 1,
      T_ROME_731379930: 1,
      T_IDEO2_4824: 1,
      T_ITM_1180: 1,
      T_ITM_PERSO4: 1,
      T_ITM_1044: 1,
      T_ITM_1491: 1,
    },
    moygen: "14",
    choices: {
      MET_171: {
        fl: "MET_171",
        status: 1,
      },
      MET_163: {
        fl: "MET_163",
        status: 1,
      },
      MET_332: {
        fl: "MET_332",
        status: 1,
      },
      MET_343: {
        fl: "MET_343",
        status: 2,
      },
      MET_288: {
        fl: "MET_288",
        status: 2,
      },
      MET_771: {
        fl: "MET_771",
        status: 2,
      },
      MET_7860: {
        fl: "MET_7860",
        status: 2,
      },
      fl2047: {
        fl: "fl2047",
        status: 1,
      },
      MET_419: {
        fl: "MET_419",
        status: 1,
      },
      MET_869: {
        fl: "MET_869",
        status: 2,
      },
      MET_828: {
        fl: "MET_828",
        status: 2,
      },
      MET_7858: {
        fl: "MET_7858",
        status: 2,
      },
      MET_827: {
        fl: "MET_827",
        status: 2,
      },
      MET_884: {
        fl: "MET_884",
        status: 2,
      },
      MET_344: {
        fl: "MET_344",
        status: 2,
      },
      MET_311: {
        fl: "MET_311",
        status: 2,
      },
      MET_201: {
        fl: "MET_201",
        status: 2,
      },
      MET_787: {
        fl: "MET_787",
        status: 1,
      },
      MET_699: {
        fl: "MET_699",
        status: 2,
      },
      MET_215: {
        fl: "MET_215",
        status: 2,
      },
      MET_335: {
        fl: "MET_335",
        status: 1,
      },
      MET_812: {
        fl: "MET_812",
        status: 2,
      },
    },
  });

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
