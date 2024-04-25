import * as data from "./data";
import $ from "jquery";
export { getHTMLExplanations };

function getHTMLExplanations(explications, detailed) {
  const $div = $(`<div class="container-fluid px-4"></div>`);

  const str = [];

  const geos = explications.filter((expl) => expl.geo);
  if (geos.length > 0) {
    const villeToDist = {};
    for (const expl of geos) {
      for (const d of expl.geo) {
        let dist = d.distance;
        const city = d.city;
        if (city in villeToDist) {
          dist = Math.min(dist, villeToDist[city]);
        }
        villeToDist[city] = dist;
      }
      for (const { geo } of geos) {
        for (const { city, distance, form } of geo) {
          let dist = distance;
          if (city in villeToDist) {
            dist = Math.min(dist, villeToDist[city]);
          }
          villeToDist[city] = dist;
        }
      }
    }
    let msg = `<p>Une ou plusieurs formations à proximité de `;
    for (const [city, dist] of Object.entries(villeToDist)) {
      msg = msg + ` ${city} (${dist}km)`;
    }
    msg = msg + ".</p>";
    str.push(msg);
  }

  let simis = explications
    .filter((expl) => expl.simi && expl.simi.fl)
    .map((expl) => data.getExtendedLabel(expl.simi.fl))
    .filter((l) => l);
  simis = Array.from(new Set(simis));
  if (simis.length == 1) {
    str.push(`<p>Les candidats sur Parcoursup s'intéressent souvent à la fois à cette formation et à 
       <em>&quot;${simis[0]}&quot;</em>, qui fait partie de ta sélection.</p>`);
  } else if (simis.length > 1) {
    str.push(`<p>Les candidats sur Parcoursup s'intéressent souvent à la fois à cette formation et à 
    d'autres formations de ta sélection: <em>&quot;${simis.join(
      "&quot;,&quot;"
    )}</em>.</p>`);
  }
  /*  } else if (expl.simi && expl.simi.fl) {
   */

  for (const expl of explications) {
    const htmlexpl = getHMLExplanation(expl, detailed);
    if (htmlexpl) {
      str.push(htmlexpl);
    }
  }

  if (str.length == 0) return null;
  $div.append($(str.join("")));
  return $div;
}

export function getHTMLExplanationsDebug(explications, fullName) {
  let debug = [];
  for (const expl of explications) {
    if (expl.debug) {
      debug.push(expl.debug.expl);
    }
  }
  const str = [];
  if (debug && debug.length > 0) {
    str.push(`<div class="alert alert-info"><h3>${fullName}"</h3><br>`);
    str.push("<ul>");
    for (const expl of debug) {
      str.push("<li>" + expl + "</li>");
    }
    str.push("</ul>");
    const $explanations = getHTMLExplanations(explications, true);
    if ($explanations) {
      str.push($explanations.html());
    }
    str.push("</div>");
  }
  return str.join("");
}

function getHMLExplanation(expl, giveDetailsFlag) {
  let str = [];
  if (expl.dur && expl.dur.option == "court") {
    return "<p>Tu as une préférence pour les études courtes.</p>";
  } else if (expl.dur && expl.dur.option == "long") {
    return "<p>Tu as une préférence pour les études longues.</p>";
  } else if (expl.app) {
    return "<p>Cette formation existe en apprentissage.</p>";
  } else if (expl.tag || expl.tags) {
    const labels = [];
    if (giveDetailsFlag && expl.tag && expl.tag.pathes) {
      //al details
      for (const path of expl.tag.pathes) {
        const nodes = path.nodes;
        let strs = [];
        if (nodes) {
          for (const node of nodes) {
            let label = data.getExtendedLabel(node) + " (" + node + ")";
            if (!label) label = node;
            strs.push(label);
          }
        }
        strs.pop();
        const weight = path.weight;
        strs.push(`[score ${Math.round(1000 * weight)}]`);
        labels.push(strs.join(" - "));
      }
    } else {
      //no details
      const s = new Set();
      if (expl?.tags?.ns) {
        for (const node of expl.tags.ns) {
          //let label = data.getExtendedLabel(node);
          let label = data.getExtendedLabel(node);
          s.add(label);
        }
      }
      if (expl?.tag?.pathes) {
        for (const path of expl.tag.pathes) {
          const nodes = path.nodes;
          if (nodes && nodes.length > 0) {
            const node = nodes[0];
            let label = data.getExtendedLabel(node);
            s.add(label);
          }
        }
      }
      for (const l of s) {
        let label = l;
        const firstIndexOcc = label.indexOf("(");
        if (firstIndexOcc > 0) {
          label = label.substring(0, firstIndexOcc - 1);
        }
        labels.push(label);
      }
    }
    const msgs = [];
    //msgs.push(`<p>En lien avec tes choix et ta sélection:</p>`);
    msgs.push(`<div class="d-inline-block">`);
    for (const label of labels) {
      msgs.push(
        `<div  class="btn btn-no-event notinterested m-1 px-2 py-1 tier-list-item
            pill rounded-pill">${label}</div>`
      );
    }
    msgs.push("</div>");
    return msgs.join("");
  } else if (expl.bac) {
    return (
      "<p><b>Moyenne au bac</b>. Tu as auto-évalué ta " +
      " moyenne générale à <b>" +
      +expl.bac.moy +
      "</b>. " +
      "Parmi les lycéennes et lycéens " +
      (expl.bac.bacUtilise == ""
        ? ""
        : "de série '" + expl.bac.bacUtilise + "' ") +
      "admis dans ce type de formation en 2023, la moitié avait une moyenne au bac dans l'intervalle <b>" +
      getHTMLMiddle50(expl.bac) +
      "</b>.</p>"
    );
  } else if (expl.tbac) {
    return (
      "<p><b>Type de bac</b>. " +
      "Parmi les lycéennes et lycéens admis dans cette formation en 2023, " +
      expl.tbac.percentage +
      "% étaient des bacheliers de série '" +
      expl.tbac.bac +
      "'.</p>"
    );
  } else if (expl.perso) {
    return undefined; // "<p>Tu as toi-même ajouté cette formation à ta sélection.</p>";
  } else if (expl.spec) {
    const stats = expl.spec.stats;
    let result = "";
    for (const [spe, pourcentage] of Object.entries(expl.spec.stats)) {
      result +=
        "<p>La spécialité '" +
        spe +
        "' a été choisie par " +
        Math.round(100 * pourcentage) +
        "% des candidats admis dans ce type de formation en 2023.</p>";
    }
    return result;
  } else if (expl.interets) {
    const tags = expl.interets.tags;
    let result =
      "<p>Tu as demandé à consulter les formations correspondant aux mots-clés '" +
      tags +
      "'.</p>";
    return result;
  }
  return null;
}

function getHTMLMiddle50(moy) {
  const intLow = moy.middle50.rangEch25 / data.statNotesDivider();
  const intHigh = moy.middle50.rangEch75 / data.statNotesDivider();
  if (intHigh == intLow) {
    //un cas très rare
    return `[${intLow}, ${intLow + 0.5}[`;
  } else {
    return `[${intLow},${intHigh}]`;
  }
}

/*

   

    public static String similarite(int percentage, String libelle) {
        return }

    */
