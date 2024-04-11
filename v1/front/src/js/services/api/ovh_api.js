/* default configuration for local debug with server running on port 8000 */

import $ from "jquery";

const API = "/api/1.2/";

const IPserver = "https://" + document.location.hostname; //https calls

export function callAPI(serviceName, data, successHandler, errorHandler, type) {
  const url = IPserver + API + serviceName;
  console.log(`Sending to ${url}`);
  $.ajax({
    type: type,
    url: url,
    dataType: "json",
    data: data,
    contentType: "application/json; charset=utf-8",
    success: successHandler,
    error: errorHandler,
  });
}
