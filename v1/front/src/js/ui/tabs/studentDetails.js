import $ from "jquery";
import { handlers } from "../../app/events";
import { params } from "../../config/params";
import * as data from "../../data/data";
import { getHTMLExplanations } from "../../data/explanation";
import { getStatsScolLine } from "../../ui/details/detailsModal";
import * as session from "../../app/session";

/******************************************************* */
/*************** LOADING student profile ***************/
/******************************************************* */

const anneeStats = params.anneeStats;

/**
 * init the screen for a teacher with a student selected
 * @param {*} group
 */
export function loadStudentProfile(profile, statsGroupes) {
  $("#seeGroupButton").off();
  $("#seeGroupButton").on("click", () => {
    handlers.setSelectedStudent(null);
  });

  const nom = profile.nom ? profile.nom.toUpperCase() : "";
  const prenom = profile.prenom ? profile.prenom : "";
  const nomAffiche = `${prenom} ${nom}  (${profile.login})`;

  const $div = $(`
  <div>
  <br/>
  <h1 class="text-center">${nomAffiche}</h1>
  <br/>
  <div class="rounded p-4 my-3  question">
  <button type="button" class="btn btn-success " 
                  data-bs-toggle="modal"
                data-bs-target="#passwordResetModal"
        >Réinitialiser le mot de passe</button>
  </div>
  ${getProfileHtml(profile)}
  <div class="rounded p-4 my-3  question " >
  <h2>Notes et messages</h2>
  <p>Ces messages seront visibles par l'élève concerné.</p>
  <div class="student_chat"></div>
  </div>`);

  const approved = data.getSuggestionsApproved(profile);
  const rejected = data.getSuggestionsRejected(profile);

  //str.push('<div class="rounded p-4 my-3  question">');
  const hideFormations = session.hideFormations();

  if (!hideFormations) {
    $div.append(
      getFavorisDiv(
        "Formations sélectionnées",
        profile,
        statsGroupes,
        approved.filter((s) => data.isFiliere(s.fl)),
        true
      )
    );
  }
  $div.append(
    getFavorisDiv(
      "Métiers sélectionnés",
      profile,
      null,
      approved.filter((s) => data.isMetier(s.fl)),
      true
    )
  );
  $div.append(
    getFavorisDiv(
      "Elements exclus",
      profile,
      statsGroupes,
      rejected.filter((s) => !hideFormations || !data.isFiliere(s.fl)),
      false
    )
  );

  //str.push(plain2html(JSON.stringify(copy, null, 2)));
  //str.push(plain2html(JSON.stringify(motsCles, null, 2)));
  $("#studentDetailsViewDiv").empty().append($div);

  $(".remove_interest_container").toggle(false);

  registerFormationDetailsHandler(statsGroupes);

  $("#doResetPassword")
    .off("click")
    .on("click", () => {
      handlers.resetStudentPasswordByTeacher(profile.login);
    });
}

function getProfileHtml(profile) {
  const str = [];
  str.push('<div class="rounded p-4  question">');
  str.push(`<h2>Profil</h2>`);

  for (const q of params.questions) {
    if (q.id === "motscles" || q.id === undefined || q.type === "tierlist")
      continue;
    //if (q.txt === undefined) continue;
    if (q.type.startsWith("searchbar")) continue;
    str.push(`<div class="row">`);
    str.push('<div class="col">');
    str.push(`${q.txt}`);
    str.push("</div>");
    str.push('<div class="col">');
    let value = profile[q.id];
    if (q.type === "radio") {
      value = q.options[value];
    }
    str.push(value);
    str.push("</div>");
    str.push("</div>");
  }
  str.push("</div>");

  return str.join("");
}

function getFavorisHtml2(filieres, stats, profile, selected) {
  const $div = $(`<div class="accordion " id="accordionFavorisStudent">`);
  const color = selected ? "  " : " bg-excluded";

  const dates = {};
  for (const f of filieres) {
    dates[f.fl] = f.date === undefined ? new Date(0) : new Date(f.date);
  }

  for (const f of filieres) {
    const statsFl = stats ? stats[f.fl] : undefined;

    let name = data.getExtendedLabel(f.fl);
    if (!name) continue;

    let dateStr =
      isNaN(dates[f.fl]) || dates[f.fl].getFullYear() < 2020
        ? ""
        : dates[f.fl].toLocaleDateString("fr");
    const $subdiv = $(`<div class="accordion-item ${color} my-4 p-2"></div>`);
    $div.append($subdiv);
    $subdiv.append(
      $(`<h2 
        class="accordion-header ${color}" 
        id="heading${f.fl}">
      <button 
      class="accordion-button ${color} collapsed"
       type="button" 
       ${selected ? "" : " excluded "}
       data-bs-toggle="collapse" 
       data-bs-target="#collapse${f.fl}" 
       aria-controls="collapse${f.fl}"
       aria-expanded="false"
       >
        <em class="me-3">${dateStr}</em>
        <h4>${name}</h4><br/> 
      </button>
    </h2>
    <div 
    id="collapse${f.fl}" 
    class="accordion-collapse collapse hide ${color}" 
    aria-labelledby="heading${f.fl}" 
    igonoredata-bs-parent="#accordionFavorisStudent">
      <div class="accordion-body getFavorisDetailsHtml">
      </div>
    </div>`)
    );
    $(".getFavorisDetailsHtml", $subdiv).append(
      getFavorisDetailsHtml(f, profile, stats ? stats[f.fl] : null, name, color)
    );
  }
  return $div;
}

function getFavorisDiv(titre, profile, stats, liste, selected) {
  const $div = $(
    `<div class="my-3 p-3 ${selected ? "" : "bg-excluded"} rounded"></div>`
  );
  $div.append(
    $(`<h2 class="my-2 ${selected ? "" : "bg-excluded"} ">${titre}</h2>`)
  );

  if (liste === undefined || liste.length === 0) {
    $div.append($(`<p>Aucun élément dans la sélection.</p>`));
  } else {
    $div.append(getFavorisHtml2(liste, stats, profile, selected));
  }

  return $div;
}

function getSeePsupCarteHtml(fl, bac, name) {
  const uri = data.getParcoursupSearchAdress([fl], name);
  const $div = $(`<div class="text-center"></div>`);
  const $button = $(`<button  
      bac="${data.getTypeBacGeneric(bac).index}"
      role="button"
      class="rounded question m-4 p-4"
      >
      <span>
      <h2>
      <i class="bi bi-geo-alt-fill"></i>
      </h2>
<b>Voir sur la carte</b></span>
                </button>`).on("click", () => {
    handlers.logUrlOpen(uri);
    window.open(uri, "_blank");
  });
  $div.append($button);
  return $div;
}
function getFavorisDetailsHtml(sugg, profile, statsAll, name, color) {
  const fl = sugg.fl;

  let bac = profile.bac;
  if (bac === undefined || bac === null) bac = data.tousBacs;

  const $div = $(`<div class="d-grid w-80 gap-2 ${color}" ></div>`);
  $div.append($(getExplanationsHtml(fl, sugg)));

  if (data.isFiliere(fl)) {
    $div.append(getStatsSectionDiv(fl, statsAll, bac, name));

    if (statsAll) {
      const statsFor = statsAll.statsFormations;
      if (statsFor && Object.entries(statsFor).length > 0) {
        $div.append(
          $(
            `<div class="question p-3 m-2">
        <b>Détails sur un établissement en particulier</b><br/>` +
              getStatsFormationsHtml(bac, Object.entries(statsFor), fl) +
              `</div>`
          )
        );
      }
    }

    const $seePsupDiv = $(`<div class=" w-80  p-3 rounded"></div>`);
    $seePsupDiv.append(getSeePsupCarteHtml(fl, bac, name));

    $div.append($seePsupDiv);
  }

  return $div;
}

export function getStatsSectionDiv(fl, statsAll, bac, name) {
  if (statsAll === undefined) return "";

  const o = data.getStats(statsAll, bac);
  const stats = o.stats;
  const statsBac = o.statsBac;
  const statsTousBacs = o.statsTousBacs;

  if (notGood(statsBac) && notGood(statsTousBacs)) {
    return $(`<p>Pas de données statistiquement significatives 
      sur les profils scolaires des lycéens admis dans cette formation en ${anneeStats}.</p>`);
  }

  const $div = $(`<div></div>`);

  if (!name.includes("apprentissage")) {
    //PARTIE PROF
    $div.append(
      getStatsScolHtml(
        fl,
        bac,
        statsTousBacs,
        statsBac,
        data.getData().matieres,
        true
      )
    );
  }

  const statsBacHtml = getStatsBacHtml(statsTousBacs, stats);
  if (statsBacHtml.title.length > 0) {
    //PARTIE PROF
    if (statsBacHtml.isCollapsible) {
      $div.append(
        $(
          collapsibleContentHtml(
            statsBacHtml.title,
            statsBacHtml.body,
            "xstatess" + fl
          )
        )
      );
    } else {
      $div.append($(`<p>${statstatsBacHtmlsHtml.title}</p>`));
    }
  }
  //PARTIE PROF
  /*
  const statsSpecsHtml = getStatsSpecsHtml(statsTousBacs);
  if (statsSpecsHtml.title.length > 0) {
    if (statsSpecsHtml.isCollapsible) {
      $div.append(
        $(
          collapsibleContentHtml(
            statsSpecsHtml.title,
            statsSpecsHtml.body,
            "xstates" + fl
          )
        )
      );
    } else {
      $div.append($(`<p>${statsSpecsHtml.title}</p>`));
    }
  }*/

  return $div;
}

function getStatsFormationsHtml(bac, stats, fl) {
  const l = [];
  l.push(
    `<select class="form-select form-select-xl getStatsFormations" bac="${bac}" fl="${fl}">`
  );
  l.push("<option selected value=''>");
  l.push("Choisissez un établissement...");
  l.push("</option>");
  stats.sort(function (a, b) {
    return a[1].nom.localeCompare(b[1].nom);
  });
  for (const stat of stats) {
    l.push(`<option value="${stat[0]}">`);
    l.push(stat[1].nom);
    l.push("</option>");
  }
  l.push("</select>");
  l.push(`<div id="details${fl}"></div>`);
  return l.join("");
}

function registerFormationDetailsHandler(statsGroupes) {
  $(".getStatsFormations")
    .off("change")
    .on("change", (event) => {
      const $target = $(event.currentTarget);
      const bac = $target.attr("bac");
      const fl = $target.attr("fl");
      const fr = $target.val();
      let html = "";
      if (
        fl &&
        statsGroupes[fl] &&
        statsGroupes[fl].statsFormations &&
        statsGroupes[fl].statsFormations[fr] &&
        statsGroupes[fl].statsFormations[fr].stat &&
        statsGroupes[fl].statsFormations[fr].stat.stats
      ) {
        const infos = statsGroupes[fl].statsFormations[fr];
        const nom = infos.nom;
        const nomFil = data.getExtendedLabel(fl);
        if (nomFil === undefined || nomFil === null) {
          html = "Pas de données.";
        } else {
          const stats = infos.stat
            ? infos.stat.stats
              ? infos.stat.stats
              : null
            : null;
          let statsTousBacs = null;
          if (stats && stats[data.tousBacs])
            statsTousBacs = stats[data.tousBacs];
          let statsBac = null;
          if (stats && stats[bac] && bac != data.tousBacs)
            statsBac = stats[bac];
          if (!nomFil.includes("apprentissage")) {
            html = getStatsScolHtml(
              fl,
              bac,
              statsTousBacs,
              statsBac,
              data.getData().matieres,
              false
            );
            if (html == "") {
              html = `<div class="alert alert-info m-2 p-2">Pas de données statistiquement significatives 
      sur les profils scolaires des lycéens admis dans cette formation en 2023.</div>`;
            }
          }
        }
      }
      $(`#details${fl}`).html(html);
    });
}

function getExplanationsHtml(fl, sugg) {
  const explications = sugg["expl"];
  const l = [];
  if (explications && explications.length > 0) {
    const explHtml = getHTMLExplanations(explications, false);
    if (explHtml) {
      l.push(
        collapsibleContentHtml(
          "Pourquoi cette suggestion?",
          explHtml.html(),
          "x" + fl
        )
      );
    }
  }
  return l.join("");
}

function getStatsBacHtml(statsTousBacs, statsAll) {
  const l = [];
  const admisTousBacs =
    statsTousBacs == null ? undefined : statsTousBacs.nbAdmis;

  l.push(`<div class="getStatsBacsHtml">`);
  let isCollapsible = false;
  let title = ``;

  const nbAdmis = statsAll ? Object.entries(statsAll) : [];
  const nbAdmisNotNull = [];
  for (const o of nbAdmis) {
    if (
      o[1] !== undefined &&
      o[1] !== null &&
      o[1].nbAdmis !== undefined &&
      o[1].nbAdmis !== null &&
      o[1].nbAdmis > 0
    ) {
      nbAdmisNotNull.push(o);
    }
  }
  if (nbAdmisNotNull.length > 0) {
    nbAdmisNotNull.sort((x, y) => {
      return y[1].nbAdmis - x[1].nbAdmis;
    });
    isCollapsible = true;
    title = `Séries de bac`;
    l.push(`<p>Statistiques sur les ${admisTousBacs} candidats lycéens admis en ${anneeStats}.
    Les catégories en deça de 1% sont masquées.</p>
    <table class="table ">
             <thead>
                <tr>
                <th scope="col">Série de bac</th>
                <th scope="col">nombre de lycéens admis en ${anneeStats}</th>
              </tr>
            </thead>
                <tbody>`);
    let total = -1;
    for (const d of nbAdmisNotNull) {
      let bac = d[0];
      const nb = d[1].nbAdmis;
      if (bac == data.tousBacs) {
        bac = "Toutes les séries";
        total = nb;
      }
      if (total > 0) {
        const pct = (100 * nb) / total;
        if (pct < 1) continue;
      }
      l.push(
        `<th scope='row'>${data.ppBac(bac)}</th>
            <td>${nb}</td></tr>`
      );
    }
    l.push(` </tbody></table>`);
  }
  l.push(`</div>`);
  const body = l.join("");

  return { title: title, isCollapsible: isCollapsible, body: body };
}

function listToSep(l) {
  const result = [];
  for (const i in l) {
    result.push(`'`);
    result.push(l[i]);
    result.push(`'`);
    if (i < l.length - 2) {
      result.push(`, `);
    } else if (i < l.length - 1) {
      result.push(` et `);
    }
  }
  return `<span class="text-black">` + result.join("") + `</span>`;
}

function getStatsSpecsHtml(statsTousBacs) {
  const l = [];
  const admisTousBacs =
    statsTousBacs == null ? undefined : statsTousBacs.nbAdmis;

  l.push(`<div function="getStatsSpecsHtml">`);
  let isCollapsible = false;
  let title = ``;

  //Stats specialités

  const showSpecsStats = true;
  if (
    showSpecsStats &&
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
        title = `EDS`;
        isCollapsible = true;
        l.push(`<p>Statistiques sur les  candidats lycéens admis en ${anneeStats},
        toutes séries confondues.
         Les pourcentages < 1% sont masqués.</p><table class="table ">
             <thead>
                <tr>
                <th scope="col">Spécialité</th>
                <th scope="col">#</th>
                <th scope="col">%</th>
              </tr>
            </thead>
                <tbody>`);
        for (const specCod in specsCodes) {
          const stat = specsCodes[specCod][1];
          if (stat === undefined) continue;
          const pct = (100 * stat) / admisTousBacs;
          if (pct < 1) continue;
          const label = specs[specsCodes[specCod][0]];
          if (!label) continue;
          l.push(
            `<th scope='row'>${label}</th>
            <td>${stat}</td>
            <td>${pct.toFixed(1)}</td></tr>`
          );
        }
        l.push(` </tbody></table>`);
      }
    } else {
      l.push("Pas de données statistiquement significatives.");
    }
  }

  l.push(`</div>`);
  return { title: title, isCollapsible: isCollapsible, body: l.join("") };
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

function getStatsScolHtml(
  fl,
  bac,
  statsTousBacs,
  statsBac,
  matieres,
  collapsible
) {
  //Stats specialités
  const $div = $("<div></div>");
  let shown = `Moyennes`;
  let stats = statsBac;
  let serie = bac;
  let qualifSerieBac = "";
  if (notGood(stats)) {
    stats = statsTousBacs;
    if (notGood(stats)) {
      return `<p>Pas de données statistiquement significatives 
      sur les profils scolaires des lycéens admis dans cette formation en ${anneeStats}.</p>`;
    } else if (bac != "") {
      $div.append(
        `<p class="alert alert-info">Pas de données statistiquement significatives sur les profils scolaires des
      lycéens 
      <b >de la série '${bac}'</b> admis dans cette formation en ${anneeStats}, les données suivantes
      concernant <b>toutes les séries de bac</b>.</p>`
      );
      serie = "Toutes";
      qualifSerieBac = " (toutes séries de bacs confondues) ";
    }
  } else {
    qualifSerieBac =
      serie != ""
        ? " de série '" + serie + "'"
        : " (toutes séries de bacs confondues) ";
  }
  const st = stats.statsScol;
  const mats = {};
  for (const mat in matieres) {
    if (st[mat] !== undefined) {
      const nb = st[mat].nb;
      mats[mat] = {
        label: matieres[mat],
        nb: nb,
        stat: st[mat],
      };
    }
  }
  const matsEntries = Object.entries(mats);

  shown = `Moyennes`;
  if (matsEntries.length == 0) return "";

  matsEntries.sort((x, y) => {
    const d = y[1].nb - x[1].nb;
    if (d !== 0) return d;
    return parseInt(x[0]) - parseInt(y[0]);
  });

  $div.append(
    $(`
        <p>Profils scolaires des lycéens ${qualifSerieBac} admis en ${anneeStats}.</p>`)
  );
  /*
  $div.append(
    $(`     
        <div class="alert alert-info">
        L'intervalle de référence est obtenu en considérant les notes
        des lycéens admis
        en ${anneeStats} série '${serie}'
        et en extrayant l'intervalle situé entre le 1er et le 3ème quartile.
        Si 100 élèves sont admis, l'intervalle 
        est obtenu en triant leurs notes par ordre croissant et en 
        sélectionnant la 25-ième et la 75-ième note.
        </div>`)
  );*/
  const $table = $(`<table class="table "></table>`);
  $div.append($table);
  $table.append(
    $(`         
      <thead>
      <tr>
      <th width="20%" scope="col">Matiere</th>
      <th  width="20%" scope="col">Nb lycéens admis 
      avec une note dans cette matière</b></th>
      <th scope="col" >Intervalle de référence des moyennes des admis</th>
      </tr>
      </thead>
      `)
  );
  //<th scope="col"># moyennes > M75</th>

  //getStatsScolLine(stat)
  for (const entry of matsEntries) {
    const stMat = entry[1];
    const middle50 = stMat.stat;
    const frequencesCumulees = stMat.stat.frequencesCumulees;
    const $row = $(`<tr scope="row"></tr>`);
    $table.append($row);
    $row.append($(`<th>${stMat.label}</th>`));
    let nb25 = frequencesCumulees[middle50.rangEch25 - 1];
    if (nb25 === undefined) nb25 = 0;
    let nb75 = frequencesCumulees[middle50.rangEch75];
    const nbMid = nb75 - nb25;
    $row.append($(`<td> ${stMat.nb} </td>`));
    const $td = $(`<td></td>`);
    $row.append($td);
    $td.append(getStatsScolLine(middle50));
    /*
    $row.append(
      $(`
        <td> [&nbsp;${middle50.rangEch25 / data.statNotesDivider()}&nbsp;
        -
        ${middle50.rangEch75 / data.statNotesDivider()}&nbsp;]</td>`)
    );
    $row.append($(`<td>${nbMid}</td>`));
    $row.append($(`<td>${nb25}</td>`));
    */
    //<td>${stMat.nb - nbMid - nb25} admis</td>`);
    /*
      ll.push("&nbsp;&nbsp;pour 1/4, moyenne <= à " + middle50.rangEch25 / 2);
      ll.push("&nbsp;&nbsp;pour 1/2, moyenne >= à " + middle50.rangEch50 / 2);
      ll.push("&nbsp;&nbsp;pour 3/4, moyenne <= à " + middle50.rangEch75 / 2);
      */
  }

  let l = [];
  if (collapsible && matsEntries.length > 1) {
    l.push(collapsibleContentHtml(shown, $div.html(), fl));
  } else {
    l.push($div.html());
  }
  if (l.length == 2) l = [];
  return l.join("");
}

export function collapsibleContentHtml(shown, hidden, fl, alwaysVisible) {
  let id = "collapseSpecs" + fl;
  let after = "";
  if (hidden) {
    shown = "<b>" + shown + "</b>";
    if (alwaysVisible) {
      after = `<div class="d-flex flex-column justify-content-start align-items-start"><br/>${hidden}</div>`;
    } else {
      after = `
      <div class="collapse " id="${id}"><br/>
        <div class="d-flex flex-column  justify-content-start align-items-start">
        ${hidden} 
        </div>
      </div>`;
    }
  }
  const classes = alwaysVisible
    ? ""
    : `type="button" role="button" 
    data-bs-toggle="collapse" 
    data-bs-target="#${id}" 
    aria-expanded="false" 
    aria-controls="${id}" `;

  return `
  <div ${classes} class="${
    hidden && !alwaysVisible ? "btn question" : "btn question btn-no-event"
  } p-3 m-2">
 
    ${shown}
    ${after}
  </div>
   `;
}
