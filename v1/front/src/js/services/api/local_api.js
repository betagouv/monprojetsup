/* default configuration for local debug with server running on port 8000 */

import $ from "jquery";

export function callAPI(serviceName, data, successHandler, errorHandler, type) {
  let port =
    serviceName.startsWith("stats") ||
    serviceName.startsWith("suggestions") ||
    serviceName.startsWith("foi") ||
    serviceName.startsWith("affinite") ||
    serviceName.startsWith("explanations")
      ? 8003
      : 8002;
  const IPserver = "http://" + document.location.hostname + ":" + port;
  const API = "/api/1.2/";
  const url = IPserver + API + serviceName;
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
