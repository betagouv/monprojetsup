import $ from "jquery";
import * as nav from "../../app/navigation";

import * as app from "../../app/app";

function errorHtml(id, errorMsg) {
  if (errorMsg === undefined || errorMsg === null || errorMsg.length == 0) {
    $(`#id`).empty();
  } else {
    $(`#id`).html(
      `<p class="fr-message fr-message--error" id="radio-error-message-error">
    ${errorMsg}
          </p>`
    );
  }
}
export function init() {}
