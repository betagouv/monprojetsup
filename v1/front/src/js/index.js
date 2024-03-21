import "../scss/styles.scss";
import $ from "jquery";

import * as session from "./app/session";
import * as data from "./data/data";
import * as ui from "./ui/ui";

import {
  postLoginHandler,
  frontErrorHandler,
  onBackButtonEvent,
  oidcLogin,
} from "./app/app";

$(async function () {
  window.onerror = function (msg, url, line, columnNo, error) {
    const errObj = {
      msg: msg,
      url: url,
      line: line,
      columnNo: columnNo,
      error: error,
    };
    frontErrorHandler(errObj, true);
  };

  window.onload = function () {
    //google_sign_in_init();
  };

  /*
  const toto = (response) => {
    console.log("ID: " + response.credential);
    // Send the ID token to your server for validation.
  };*/

  window.history.pushState(null, null, window.location.pathname);
  window.addEventListener(`popstate`, onBackButtonEvent);

  /* Step 0: init
   */
  data.init();
  ui.initOnce();

  /* Step1: loading data */
  ui.showDataLoadScreen();
  $(".body").css("visibility", "visible");

  console.log(__VERSION__);

  $("#version_appli").html("Version " + __VERSION__);

  /* Step3: trying automatic reconnection based on session cookie */
  if (session.isLoggedIn()) {
    postLoginHandler();

    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
  } else {
    ui.showConnectionScreen();
  }
});

function google_sign_in_init() {
  console.log("Initializing google.accounts.id");
  google.accounts.id.initialize({
    client_id:
      "582371020752-ctredmc15bs37tdakuhqdrcoe62eijbv.apps.googleusercontent.com",
    callback: (response) => {
      //todo pass the jwt to the server and process callback
      console.log("Encoded JWT ID token: " + response.credential);
      oidcLogin(response.credential);
    },
  });
  console.log("Rendering button");
  $("#g_id_placeholder").empty();
  google.accounts.id.renderButton(
    document.getElementById("g_id_placeholder"),
    { theme: "outline", size: "large" } // customization attributes
  );
  //google.accounts.id.prompt(); // also display the One Tap dialog
}

export function onSignIn(googleUser) {
  var profile = googleUser.getBasicProfile();
  console.log("ID: " + profile.getId()); // Do not send to your backend! Use an ID token instead.
  console.log("Name: " + profile.getName());
  console.log("Image URL: " + profile.getImageUrl());
  console.log("Email: " + profile.getEmail()); // This is null if the 'email' scope is not present.
}
