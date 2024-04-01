import "../../../scss/styles.scss";

import $ from "jquery";
import * as ui from "./../../ui/ui";

/* the new $( document ).ready( handler ), see https://api.jquery.com/ready/ */
$(async function () {
  ui.injectHtml();
});
