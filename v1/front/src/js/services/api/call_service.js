import { callAPI } from "./local_api";

import {
  serverErrorHandler,
  disconnectAndShowFeedback,
  showToast,
} from "../../app/app";

import * as session from "../../app/session";

export function getFromSpringService(
  service_name,
  toSend = {},
  onSuccess = null,
  noToken = false
) {
  callService(
    service_name,
    toSend,
    onSuccess,
    noToken,
    "GET",
    session.getLogin(),
    session.getToken()
  );
}

export function postToSpringService(
  service_name,
  toSend = {},
  onSuccess = null,
  noToken = false,
  onError = null
) {
  callService(
    service_name,
    toSend,
    onSuccess,
    noToken,
    "POST",
    session.getLogin(),
    session.getToken(),
    onError
  );
}

function callService(
  service_name,
  toSend,
  onSuccess = null,
  noToken = false,
  type = "POST",
  login = undefined,
  token = undefined,
  onError = null
) {
  console.log("client -- " + service_name + " --> server");
  if (!noToken) {
    toSend.login = login;
    toSend.token = token;
    if (
      toSend.login === null ||
      toSend.token === null ||
      toSend.login === undefined ||
      toSend.token === undefined
    ) {
      /*
      frontErrorHandler(
        {
          cause: `Unauthenticated service call from front to ${service_name} with login '${toSend.login}'token '${toSend.token}' `,
        },
        true
      );*/
      return;
    }
  } /*else {
    if (toSend.token === undefined) toSend.token = "noToken";
  }*/
  let toSendJson;
  if (type == "POST") {
    toSendJson = JSON.stringify(toSend);
  } else {
    // Convert parameters to RFC 3986 compliant string
    toSendJson = null;
    service_name =
      service_name +
      "?" +
      Object.entries(toSend)
        .map((e) => encodeURIComponent(e[0]) + "=" + encodeURIComponent(e[1]))
        .join("&");
  }

  console.log(toSendJson);
  callAPI(
    service_name,
    toSendJson,
    async (answer, textStatus, jqXHR) => {
      const status = answer.header.status;

      //const crsf = jqXHR["X-CSRF-TOKEN"];

      if (status == 0) {
        console.log("client <-- " + service_name + " -- server ok :-)");
        console.log(JSON.stringify(answer));
        const msg = answer.header.userMessage;
        if (msg !== undefined && msg !== null && msg.length > 0) {
          showToast(msg);
        }
        if (onSuccess) onSuccess(answer);
      } else {
        console.log(
          "client <-- " +
            service_name +
            " -- error :-( status " +
            answer.header.status +
            " error " +
            answer.header.error
        );
        if (onError != null) onError(answer.header.error);

        if (answer.header.status == 1) {
          await serverErrorHandler(answer.header.error);
        } else {
          await disconnectAndShowFeedback(answer.header.error);
        }
      }
    },
    async (xhr, status, error) => {
      console.log("client <-xx " + service_name + " xxx server  :-(");
      if (onError != null) onError(xhr);
      await serverErrorHandler(xhr);
    },
    type
  );
}
