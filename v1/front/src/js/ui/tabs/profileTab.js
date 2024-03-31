//true when a profile is being loaded, in which case handlers are deactivated

import $ from "jquery";
import * as data from "./../../data/data";
import * as questionsDiv from "./questionsDiv";
import * as autocomplete from "./../autocomplete/autocomplete";
import * as session from "../../app/session";
import * as events from "../../app/events";

/**
 * refresh the profile tab, loading data from profile
 * would be some better in REACT...
 */
let reloadingTab = false;

export function reloadTab() {
  reloadingTab = true;

  $("#profile-div").html("");
  const questions = data.getQuestions();
  $("#profile-div").append(getProfileDiv(questions));
  updateQuestionsVisibility(questions);
  updateFeedback(questions);
  registerHandlers(questions);

  reloadingTab = false;
}

export function updateFeedback(questions) {
  //some postprocessing for particular questions
  //having feedback
  if (questions) {
    for (const q of questions) {
      //updates feedback below sliders
      if (q.feedback) {
        const value = data.getProfileValue(q.id);
        updateAutoEvaluationFeedback(q.id, value);
      }
    }
  } else {
    const i = 0;
  }
}

export function updateQuestionsVisibility(questions) {
  for (const q of questions) {
    if (q.filtre) {
      const niveau = data.getProfileValue("niveau");
      const ok = eval(q.filtre);
      $(`#${q.id}_div`).toggle(ok);
    }
  }
  updateFeedback(questions);
}

/* computes the main question content */
export function getProfileDiv(questions) {
  const $div = $("<div></div>");
  for (const q of questions) {
    if (session.isAdminOrTeacher() && q.id != "nom" && q.id != "prenom") {
      continue;
    }
    let extraAtt = q.sameLine ? "col" : "";
    let rounded = "rounded";
    let p = "p-4";
    const $question = $(
      `<div id="${q.id}_div" class="profile_question ${extraAtt}
       border mb-5 question ${rounded} ${p}"><div></div></div>`
    );
    $question.append(
      $(`<p class="fs-8"><b id="q_txt_${q.id}">${q.txt}</b></p>`)
    );
    if (q.explain)
      $question.append(
        $(
          `<div id="q_explain_${q.id}" class="fst-normal mb-3">${q.explain}</div>`
        )
      );
    $question.append(
      questionsDiv.getQuestionDiv(q, events.toggleProfileScoreHandler)
    );
    $div.append($question);
  }
  return $div;
}

function autoCompleteFeedbackHandler(
  id,
  resultNb,
  lookupLength,
  lookupLengthThreshold
) {
  const feedbackDom = $(`#${id}_autocomplete_feedback`);
  feedbackDom.removeClass("text-success");
  feedbackDom.removeClass("text-warning");
  feedbackDom.removeClass("text-danger");
  if (resultNb == 0 && lookupLength == 0) {
    feedbackDom.html("&nbsp;");
  } else if (resultNb == 0 && lookupLength < lookupLengthThreshold) {
    feedbackDom.addClass("text-warning");
    feedbackDom.html("Mot trop court");
  } else if (resultNb == 0 && lookupLength >= lookupLengthThreshold) {
    feedbackDom.addClass("text-danger");
    feedbackDom.html("Aucun résultat");
  } else if (resultNb == 1) {
    feedbackDom.html(`1 résultat`);
  } else if (resultNb > 1) {
    feedbackDom.html(`${resultNb} résultats`);
  }
}

export function registerHandlers(questions) {
  //the radio button handler
  $(".profile-radio-input")
    .off("change")
    .on("change", function () {
      if (!reloadingTab) {
        $("#fordefault" + this.name).attr("disabled");
        events.profileValueChangedHandler(this.name, this.value);
        updateFeedback(questions);
        $(`.bacAttribute`).attr("bac", data.getTypeBacGeneric().index);
      }
    });

  //the range handler
  $(".profile-range-input")
    .off("change")
    .on("change", function () {
      if (!reloadingTab) {
        if (this.list) {
          events.profileValueChangedHandler(this.list.id, this.value);
        } else {
          const qid = this.getAttribute("qid");
          $(`#${qid}_msg`).show();
          $("#" + qid + "_checkbox").prop("checked", false);
          $("#" + qid + "_output").val(this.value);
          events.profileValueChangedHandler(qid, this.value);
          updateAutoEvaluationFeedback(qid, this.value);
        }
      }
    });

  $(".rangeQuestionDivCheckBox")
    .off("change")
    .on("change", function () {
      if (this.checked) {
        const qid = this.getAttribute("qid");
        $(`#${qid}_msg`).hide();
        $("#" + qid + "_range").val("");
        $("#" + qid + "_output").val("");
        events.profileValueChangedHandler(qid, "");
        updateAutoEvaluationFeedback(qid, "");
      }
    });
  //the textarea handler
  $(".textarea-profile-input").on("change", function () {
    if (!reloadingTab && this.id !== undefined && this.value !== undefined) {
      events.profileValueChangedHandler(this.id, this.value);
    }
  });

  /* the autocomplete handlers */
  for (const q of questions) {
    //updates feedback below sliders
    if (q.type === "autocomplete") {
      setUpAutoComplete(q.id, q.threshold);
    }
  }
}

export function setUpAutoComplete(id, threshold) {
  autocomplete.setUpAutoComplete(
    id,
    (key, label, value) => {
      if (!reloadingTab) {
        events.addElementToProfileListHandler(id, label);
      }
    },
    (key, label, value) => {
      if (!reloadingTab) {
        events.removeElementFromProfileList(id, value);
      }
    },
    (resultNb, lookupLength, lookupLengthThreshold) => {
      autoCompleteFeedbackHandler(
        id,
        resultNb,
        lookupLength,
        lookupLengthThreshold
      );
    },
    threshold
  );
}

/* format a float value with a number of digits */
function format(x, nbDigits) {
  return new Intl.NumberFormat("fr-FR", {
    maximumFractionDigits: nbDigits,
    style: "percent",
    roundingMode: "floor",
  }).format(x);
}

/* user feedback when changing autoevaluation */
export function updateAutoEvaluationFeedback(id, value) {
  if (!value) {
    $(`#${id}_msg`).hide();
    return;
  }
  $(`#${id}_msg`).show();
  const feedback_node = $("#" + id + "_feedback");

  const bac = data.getProfileValue("bac");
  let typeBacStr = "";
  if (bac !== undefined && bac != "") {
    typeBacStr = " de série de Bac '" + data.ppBac(bac) + "'";
  }

  if (feedback_node) {
    if (value === undefined || value === "") {
      feedback_node.html("");
    }
    // Feedback (https://rschmacker.github.io/files/JMP.pdf)
    // TODO: load grades distribution.
    else if (id == "moygen" || id == "mention") {
      const index40 = Math.max(0, Math.round(2 * value));
      const isMoyGen = id == "moygen";
      const stats = isMoyGen
        ? data.getPercentilesMoyGen40()
        : data.getPercentilesMoyBac40();
      const centile = stats ? stats[index40] : undefined;
      //const percentile = percentilesMoyGen40[index40];
      if (centile !== undefined) {
        const name = isMoyGen ? "moyenne générale" : "moyenne au bac";
        feedback_node.html(
          /*
          `<br/><p>Ton auto-évaluation est également utilisée pour te donner une idée 
          de la répartition des moyennes générales
      parmi les lycéens de terminale admis sur Parcoursup l'année dernière. 
      </p><div class="alert alert-success">*/
          `
            Parmi les lycéens
            ${typeBacStr}
             admis dans une formation sur Parcoursup en 2023,
            ${format(centile, 0)}
            avaient une ${name} inférieure ou égale à
            ${value}
            .`
        );
      } else {
        feedback_node.html("");
      }
    }
  }
}
