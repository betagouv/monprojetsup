import $ from "jquery";
import * as data from "./../../data/data";
import * as events from "../../app/events";
import { params } from "../../config/params";
import { Modal } from "bootstrap";
import * as cards from "../cards/cards";
import * as explanation from "./../../data/explanation";
import * as session from "../../app/session";

const anneeStats = params.anneeStats;

function capitalizeFirstLetter(str) {
  if (str.charAt) return str.charAt(0).toUpperCase() + str.slice(1);
}

export function show(grpid, stats, explanations, exemples) {
  const sugg = { fl: grpid, expl: explanations };
  const hideFormations = session.hideFormations();
  if (!hideFormations || !data.isFiliere(grpid)) {
    const dontpush = false;
    if (data.isFiliere(grpid)) {
      events.handlers.getFormationsOfInterest([grpid], (forsOfInterest) =>
        doOpenDetailsModal(
          sugg,
          grpid,
          stats,
          exemples,
          dontpush,
          forsOfInterest,
          hideFormations
        )
      );
    } else {
      doOpenDetailsModal(
        sugg,
        grpid,
        stats,
        exemples,
        dontpush,
        [],
        hideFormations
      );
    }
  }
}

let detailsStack = [];

function doOpenDetailsModal(
  sugg,
  grpid,
  stats,
  exemples,
  dontpush,
  forsOfInterest = [],
  hideFormations
) {
  if (!dontpush) {
    detailsStack.push({
      sugg: sugg,
      grpid: grpid,
      stats: stats,
      exemples: exemples,
      forsOfInterest: forsOfInterest,
    });
  }

  events.handlers.logAction("doOpenDetailsModal " + grpid);

  const $div = $("#details_modal");

  const myModal = Modal.getOrCreateInstance($div);

  const label = capitalizeFirstLetter(data.getExtendedLabel(grpid));
  $(".details_modal_header", $div)
    .empty()
    .append($(`<h3 class="my-2">${label}</h3>`));

  $("#close_button", $div)
    .off("click")
    .on("click", () => {
      detailsStack = [];
      myModal.hide();
    });

  $("#back_button", $div)
    .off("click")
    .on("click", () => {
      $(".modal-body", $div).fadeOut(500, () => {
        if (detailsStack.length > 0) {
          detailsStack.pop();
        }
        if (detailsStack.length > 0) {
          const last = detailsStack[detailsStack.length - 1];
          doOpenDetailsModal(
            last.sugg,
            last.grpid,
            last.stats,
            last.exemples,
            true,
            last.forsOfInterest //we save a second call to server and speed things up
          );
        } else {
          myModal.hide();
        }
        $(".modal-body", $div).fadeIn(500);
      });
    });
  const showBackButton = detailsStack.length > 1;
  $("#back_button").css("visibility", showBackButton ? "visible" : "hidden");
  const moreInfosHandlers = (key) => {
    //do something
    $(".modal-body", $div).fadeOut(500, () => {
      events.handlers.showDetails(key);
      $(".modal-body", $div).fadeIn(500);
    });
  };

  const $details = getChoiceDetails(
    sugg,
    stats,
    exemples,
    moreInfosHandlers,
    forsOfInterest,
    hideFormations
  );

  $(".details_modal_content", $div)
    .empty()
    .append($details)
    .append(
      $(
        `<hr class="yellow-hr w-100" />
      <div id="" class="d-flex py-3 justify-content-around choice-div"></div>`
      )
    );

  /*
  const $pid = $("#modal_container_");
  $pid.empty().append($div);*/

  const $choice = cards.getChoiceCard(
    grpid,
    null,
    {
      like: () => {
        events.selectChoice(grpid, true);
        myModal.hide();
      },
      //dunno: () => myModal.hide(500),
      dislike: () => {
        events.rejectChoice(grpid, true);
        myModal.hide(500);
      },
    },
    true,
    data.isSelected(grpid)
  );
  $(".choice-div", $div).empty().append($choice);

  myModal.show();
}

export function getChoiceDetails(
  sugg,
  stats,
  exemples,
  moreInfosHandler,
  forsOfInterest = [],
  hideFormations = false
) {
  const expeENS = false;
  const showMinimalStats = false;

  let $div = $(`<div class="px-md-5 "></div>`);

  let id = sugg === undefined ? undefined : sugg.fl;
  if (id === undefined) return "";
  let name = data.getExtendedLabel(id);
  if (name === undefined || name == null) return "";

  /* descriptif */
  const summary = data.getSummary(id);
  const descriptif = data.getDescriptif(id);
  if (summary) {
    const $subdiv = $(`<div class="descriptif-formation py-2 px-4 m-2">`);
    $subdiv.append($(`<p>${summary}</p>`));
    if (descriptif) {
      const $descriptif = $(`<div>${descriptif}</div>`);
      $subdiv.append(
        $(
          collapsibleContentHtml(
            "<b>La description complète...</b>",
            $descriptif,
            "descriptifComplet" + id
          )
        )
      );
    }
    $div.append($subdiv);
  } else if (descriptif) {
    $div.append(
      $(
        `<div class="descriptif-formation py-2 px-4 m-2"><p>${descriptif}<p></div>`
      )
    );
  }

  /* urls */
  $div.append($(`<hr class="w-100 yellow-hr"/>`));
  for (const $subdiv of getUrlsDiv(id, forsOfInterest)) $div.append($subdiv);

  /* métiers */
  if (!hideFormations || data.isFiliere(id) || data.isSecteur(id)) {
    //todo: use exemples
    if (exemples === undefined || exemples == []) {
      exemples = data.isSecteur(id)
        ? data.getLiensSecteursMetiers(id)
        : data.getLiensMetiersFormations(id);
      if (exemples) {
        exemples.sort((a, b) => 0.5 - Math.random());
      }
    }

    const autres = data.getAsKeyLabelArray(exemples);

    if (autres) {
      const category =
        data.isFiliere(id) || data.isSecteur(id)
          ? "Exemples de Métiers"
          : "Exemples de Formations";
      const $subdiv = $(
        `<div class="pourquoi m-2 p-2"><b><i class="bi bi-box me-2"></i>${category}</b></div>`
      );
      $div.append($(`<hr class="w-100 yellow-hr"/>`));
      $subdiv.append(getListChoicesDiv(autres, moreInfosHandler));
      $div.append($subdiv);
    }
  }

  /* explications */
  const expl = sugg.expl;
  if (expl && expl.length > 0) {
    const category = data.isFiliere(id)
      ? "cette formation"
      : "ce domaine professionnel";
    const $subdiv = $(
      `<div class="pourquoi m-2 p-2"><p><b><i class="bi bi-question-circle me-2"></i>Pourquoi ${category} pourrait t'intéresser</b></p></div>`
    );
    const $explDiv = explanation.getHTMLExplanations(expl, false);
    if ($explDiv != null) {
      $div.append($(`<hr class="w-100 yellow-hr"/>`));
      $subdiv.append($explDiv);
      $div.append($subdiv);
    }
  }

  /* stats */
  let bac = data.getProfileValue("bac");
  if (bac === undefined) bac = data.tousBacs;
  if (stats && Object.keys(stats.stats).length > 0) {
    const niveau = data.getProfileValue("niveau");
    const showNotes = niveau == "prem" || niveau == "term";
    const $stats = getStatsSectionDiv2(id, stats, bac, showNotes, expeENS);
    if ($stats) {
      let $subdiv = $(
        `<div class="pourquoi m-2 p-2">
        <p>
        <b>
        <i class="bi bi-arrow-right me-2">
        </i><b>Les attendus de la formation</b>
        </p>
        </div>`
      );
      $div.append($(`<hr class="w-100 yellow-hr"/>`));
      if (!showMinimalStats) {
        $subdiv.append($stats);
      } else {
        $subdiv = $(
          `<div class="pourquoi m-2 p-2">
        </div>`
        );
        const $hidediv = $(
          collapsibleContentHtml(
            "<b>Les attendus de la formation</b>",
            $stats,
            "stats" + id
          )
        );
        $subdiv.append($hidediv);
      }
      $div.append($subdiv);
    }
  }

  /* groupe */
  const groups = data.convertGroupsToKeys([id]);
  if (groups.length >= 2 || groups[0] != id) {
    const $ll = $(`<div class="d-flex flex-column align-items-start"></div>`);
    for (const key of groups) {
      const label = data.getExtendedLabel(key);
      const $div = $(
        `<div> <hr class="yellow-hr w-100" />
        </div>`
      );
      $div.append($(`<div>${label}</div>`));
      for (const $subdiv of getUrlsDiv(key)) $div.append($subdiv);
      $ll.append($div);
    }
    $div.append($(`<hr class="w-100 yellow-hr"/>`));
    $div.append(
      $(
        collapsibleContentHtml(
          "<b>Cette formation a plusieurs variantes...</b>",
          $ll,
          "variants" + id
        )
      )
    );
  }

  /* debug */
  if (expl) {
    const explDebug = expl.filter((expl) => expl.debug);
    if (explDebug.length > 0) {
      const fullName = data.getExtendedLabel(id) + "(" + id + ")";
      $div.append($(`<hr class="w-100 yellow-hr"/>`));
      $div.append(
        $(
          collapsibleContentHtml(
            "Debug",
            $(explanation.getHTMLExplanationsDebug(expl, fullName)),
            "debug" + id
          )
        )
      );
    }
  }
  return $div;
}

function notGood(stats) {
  const result =
    stats == null ||
    stats === undefined ||
    ((stats.statsScol === undefined ||
      stats.statsScol[data.moyGenIndex()] === undefined) &&
      (stats.nbAdmis === undefined || stats.nbAdmis < 10));
  return result;
}

function getStatsSectionDiv2(fl, statsAll, bac, showNotes) {
  const $div = $(`<div></div>`);

  const o = data.getStats(statsAll, bac);
  const stats = o.stats;
  const statsBac = o.statsBac;
  const statsTousBacs = o.statsTousBacs;

  const admisTousBacs =
    statsTousBacs == null ? undefined : statsTousBacs.nbAdmis;
  if (admisTousBacs) {
    $div.append(
      `<p>Nombre de lycéens admis en 2023: <span class="stats rounded-pill p-2">${admisTousBacs}</span></p>`
    );
  }
  const showMinimalStats = false;
  const expeENS = false;
  const $ul = $(`<ul></ul>`);
  if ((showNotes && statsTousBacs) || statsBac) {
    $ul.append(
      getStatsScolDiv(bac, statsTousBacs, statsBac, !showMinimalStats)
    );
  }
  $ul.append(getAttendus(fl));
  $ul.append($(getEDS(fl)));
  if (statsTousBacs) {
    $ul.append(getStatsBacDiv(statsTousBacs, stats));
    //$ul.append($(getStatsEDS(statsTousBacs, fl)));
    //$ul.append($(getStatsEDSSimple(statsTousBacs, fl)));
  }
  $div.append($ul);

  //PARTIE ELEVES

  return $div;
}

export function getStatsScolLine(stat) {
  const note5 = stat.rangEch5 / data.statNotesDivider();
  const per5 = (100 * (note5 - 9)) / 11;
  const note25 = stat.rangEch25 / data.statNotesDivider();
  const per25 = (100 * (note25 - 9)) / 11;
  const note75 = stat.rangEch75 / data.statNotesDivider();
  const per75 = (100 * (note75 - 9)) / 11;
  const note95 = stat.rangEch95 / data.statNotesDivider();
  const per95 = (100 * (note95 - 9)) / 11;

  return $(`
    <ol class="stats-bar mt-3">
    <span class="stats-bar-step out" style=";width:${per5}%"> 
      <span class="stats-bar-step-label rounded-pill py-1 px-2 stats">${note5}</span>
    </span>

    <span class="stats-bar-step" style="width:${per25 - per5}%"> 
    <span style="position: relative; top:5px" class="mt-2"><b>20%</b></span> 
    <span class="stats-bar-step-label rounded-pill py-1 px-2 stats">${note25}</span>
    </span>

    <span class="stats-bar-step is-mediane" style="width:${per75 - per25}%">  
    <span style="position: relative; top:5px" class="mt-2"><b>50%</b></span> 
    <span class="stats-bar-step-label rounded-pill py-1 px-2 stats">${note75}</span>
    </span>

        <span class="stats-bar-step " style="width:${per95 - per75}%">  
    <span style="position: relative; top:5px" class="mt-2"><b>20%</b></span> 
    <span class="stats-bar-step-label rounded-pill py-1 px-2 stats">${note95}</span>
    </span>

    <span class="stats-bar-step out"  style="width:${100 - per95}%">
    </span>

    </ol>
    </li>`);
}

function getStatsScolDiv(bac, statsTousBacs, statsBac) {
  const highlight = false;
  let stats = statsBac;
  let serie = data.ppBac(bac);
  let qualifSerieBac = "";
  if (notGood(stats)) {
    stats = statsTousBacs;
    if (notGood(stats)) {
      return $("");
    } else if (bac != "") {
      serie = "Toutes";
      qualifSerieBac = " (toutes séries de bacs confondues) ";
    }
  } else {
    qualifSerieBac =
      serie != ""
        ? " (série '" + serie + "')"
        : " (toutes séries de bacs confondues)";
  }
  const stat = stats.statsScol[data.moyGenIndex()];

  const $div = $(
    `<li class="${highlight ? "alert alert-danger" : ""}">
    <b>Moyenne générale des admis ${qualifSerieBac}</b></li>`
  );
  $div.append(getStatsScolLine(stat));

  return $div;
}

function format(x, nbDigits) {
  return new Intl.NumberFormat("fr-FR", {
    maximumFractionDigits: nbDigits,
    style: "percent",
    roundingMode: "floor",
  })
    .format(x)
    .replace(",", ".");
}

function getStatsBacDiv(statsTousBacs, statsAll) {
  const $div = $(`<li><b>Type de bac</b></li>`);

  const admisTousBacs =
    statsTousBacs == null ? undefined : statsTousBacs.nbAdmis;

  const nbAdmis = [];
  let total = -1;
  for (const o of Object.entries(statsAll)) {
    const nb = o[1].nbAdmis;
    let bac = o[0];
    if (bac == data.tousBacs) {
      total = nb;
    } else if (nb > 0) {
      nbAdmis.push(o);
    }
  }

  if (total > 0 && nbAdmis.length > 0) {
    nbAdmis.sort((x, y) => {
      return y[1].nbAdmis - x[1].nbAdmis;
    });
    const $ol = $(`<ol class="stats-bar mt-3"></ol>`);
    const $ol2 = $(`<ol ></ol>`);

    /*<ol class="progressbar mt-3">
    <span class="stats-bar-step out" style=";width:${per5}%"> 
      <span class="stats-bar-step-label rounded-pill py-1 px-2 stats">${note5}</span>
    </span>*/

    let lastpct = 0;
    for (const d of nbAdmis) {
      let bac = data.ppBac(d[0]);
      const nb = d[1].nbAdmis;
      const pct = (100 * nb) / total;
      if (pct < 1) continue;

      if (pct >= 30) {
        const $bar = $(`
      <span class="stats-bar-step" 
      style=";width:${Math.round(pct)}%"> 
    </span>`);
        const $bacpill = $(`<span style="position: relative;
      top:20px" class="rounded-pill py-1 px-2 stats">
      ${bac} 
      ${format(pct / 100, 1)}
    </span>`);
        $bar.append($bacpill);
        $ol.append($bar);
      } else {
        const $bar = $(`
      <span class="stats-bar-step out" 
      style=";width:${Math.round(pct)}%"> 
    </span>`);
        const $bacpill = $(`<span style="float: right"
         class="rounded-pill py-1 px-2 out stats">
      ${bac} 
      ${format(pct / 100, 1)}
      </span>`);

        $ol.append($bar);
        $ol2.append($bacpill);
      }
    }
    $div.append($ol);
    $div.append($ol2);
    $div.append(
      $(
        `<p style="color: gray"><em>Les séries en deça de 1% sont masquées.</em></p>`
      )
    );
  }

  return $div;
}

function getAttendus(fl) {
  const d = data.getEDSData(fl);
  const $result = $("<div></div>");
  if (d) {
    const label = d.label;
    const eds = d.eds;
    if (eds.attendus) {
      $result.append(
        $(
          `<li><b>Attendus pour accéder et réussir en '${label}'</b><div class="attendus"></div></li>`
        )
      );
      $(".attendus", $result).append(
        collapsibleContentHtml(
          "Les attendus en détail",
          $(`<div>${eds.attendus}</div>`),
          "attendus" + fl
        )
      );
    }
  }
  return $result;
  //  if (edsData) return `<li><b>Les attendus</b><p>${specsText}</p>`;
}

function getEDS(fl) {
  const l = [];
  const d = data.getEDSData(fl);
  if (d) {
    const label = d.label;
    const eds = d.eds;
    if (eds.recoEDS) {
      l.push(
        `<li><b>Conseils sur le parcours au lycée</b><p>${eds.recoEDS}</p>`
      );
    }
  }
  return l.join("");
  //  if (edsData) return `<li><b>Les attendus</b><p>${specsText}</p>`;
}

function getStatsEDS(statsTousBacs, fl) {
  const l = [];

  const admisTousBacs =
    statsTousBacs == null ? undefined : statsTousBacs.nbAdmis;

  if (
    statsTousBacs !== undefined &&
    statsTousBacs !== null &&
    statsTousBacs.statsSpecs !== undefined
  ) {
    const specsCodes = Object.entries(statsTousBacs.statsSpecs);
    if (specsCodes.length > 0) {
      specsCodes.sort((x, y) => y[1] - x[1]);

      //button label: top three
      const specs = data.getData().specialites;
      let total = 0;
      for (const specCod in specsCodes) {
        const label = specs[specsCodes[specCod][0]];
        if (label !== undefined) {
          total += specsCodes[specCod][1];
        }
      }
      if (total > 0 && admisTousBacs > 0) {
        l.push(`<li>`);
        const moreThanTwoThird = [];
        const moreThanHalf = [];
        const moreThanOneThird = [];

        for (const specCod in specsCodes) {
          const stat = specsCodes[specCod][1];
          if (stat === undefined) continue;
          const label = specs[specsCodes[specCod][0]];
          if (!label) continue;
          const pct = (100 * stat) / admisTousBacs;
          if (pct >= 66) moreThanTwoThird.push(label);
          else if (pct > 50) moreThanHalf.push(label);
          else if (pct > 33) moreThanOneThird.push(label);
        }

        l.push(`<b>Enseignements de spécialités</b>`);
        l.push(`<ul class="">`);
        if (moreThanTwoThird.length > 0) {
          const s = moreThanTwoThird.length > 1 ? "s" : "";
          l.push(
            `<li><b>très fréquents${s}</b> (choisi${s} par plus de deux admis sur trois): ${listToSep(
              moreThanTwoThird
            )}.</li>`
          );
        }
        if (moreThanHalf.length > 0) {
          const s = moreThanHalf.length > 1 ? "s" : "";
          l.push(
            `<li><b>majoritaire${s}</b> (choisi${s} par plus d'un admis sur deux): ${listToSep(
              moreThanHalf
            )}.</li>`
          );
        }
        if (moreThanOneThird.length > 0) {
          const s = moreThanOneThird.length > 1 ? "s" : "";
          l.push(
            `<li><b>fréquent${s}</b> (choisi${s} par plus d'un admis sur trois): ${listToSep(
              moreThanOneThird
            )}.</li>`
          );
        }
        l.push(`</ul>`);
        l.push(`</li>`);
      }
    }
    return l.join("");
  }
}

function listToSep(l) {
  const result = [];
  for (const i in l) {
    result.push(
      `<br><span class="pill text-black stats rounded-pill p-1 m-1">`
    );
    result.push(l[i]);
    result.push(`</span>`);
  }
  return result.join("");
}

function getUrlsDiv(subkey, forsOfInterest = []) {
  const urls = [...data.getUrls(subkey, true)];
  const label = data.getExtendedLabel(subkey);
  let found = urls.length > 0;
  if (data.isFiliere(subkey)) {
    let uri = data.getParcoursupSearchAdress([subkey], label, forsOfInterest);
    uri = uri.replace("'", " "); //this caracter is ok in uris but not in html
    urls.push(uri);
    found = true;
  }

  const l = [];
  if (found) {
    l.push(
      $(
        `<div class="d-flex justify-content-center mt-2 "><p>En savoir plus:</p></div>`
      )
    );
    const $div = $(`<div class="d-flex justify-content-center "></div>`);
    for (const url of urls) {
      $div.append(getUrlDiv(url, false));
    }
    l.push($div);
  }
  return l;
}

function getUrlDiv(url, minimal) {
  const l = [];
  let icon = "img/globe-europe-africa.svg";
  let text = "Internet";
  let tooltip = "+ d'infos";
  if (url.includes("onisep") || url.includes("terminales")) {
    icon = "img/onisep-logo.png";
    text = "Onisep";
  }
  if (url.includes("pole")) {
    icon = "img/logo-imt.png";
    text = "IMT";
  }
  if (url.includes("carte")) {
    icon = "img/logo_parcoursup_small.svg";
    text = "Parcoursup";
    tooltip = "+ d'infos sur le site Parcoursup";
  }
  if (minimal) {
    text = "";
  }
  const uri = url.replace("'", " ");

  const $button = $(`<button href=""
        type="button"
        role="button"
        tooltip="${tooltip}"
        style="max-height: 100px;max-width: 100px; "
        class="mx-4 w-50 btn-url text-center"
    ></button>`);
  $button.on("click", () => {
    events.handlers.logUrlOpen(uri);
    window.open(uri, "_blank");
    return false;
  });
  $button.append(
    `<div class="d-flex flex-column justify-content-center">
      <span class="text-white pt-1 pb-3">${text}</span>
      <span class="text-white"><img src="${icon}" class="img-fluid text-white pb-3" style="max-height: 60px;max-width: 60px; " /></span>
    </div>`
  );
  return $button;
}

function getListChoicesDiv(liste, moreInfoHandler) {
  const $div = $(`<div class="container-fluid"></div>`);

  const entries = Object.entries(liste);
  if (entries.length > 5) {
    entries.splice(5);
    liste = Object.fromEntries(entries);
  }

  const $list = cards.getCardsDiv(
    liste,
    data.isRejected,
    (x) => events.selectChoice(x, true),
    (x) => events.rejectChoice(x, true),
    moreInfoHandler
  );
  $div.append($list);

  return $div;
}

function collapsibleContentHtml(shown, $hidden, uuid) {
  let id = "collapseSpecs" + uuid;
  const $result = $(`
  <div
class=" w-80 mx-2 p-1 rounded btn question ">
  <div  
  type="button" 
  data-bs-toggle="collapse" 
  data-bs-target="#${id}" 
  aria-expanded="false" 
  aria-controls="${id}">
    ${shown}
  <div class="collapse text-start" id="${id}">
  </div>
  </div>
  </div>
   `);
  $(".collapse", $result).append($hidden);
  return $result;
}
