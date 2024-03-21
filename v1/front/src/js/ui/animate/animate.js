/* some animation tools: fadein , fadeout, animate icons */
import $ from "jquery";

export {
  fadeIn,
  fadeOut,
  fadeOutQuick,
  fadeInQuick,
  scrollTop,
  fadeOutThenScrollTop,
  animateHeart,
  animateSuggestion,
};

function fadeOut(after) {
  $(".myFadable").fadeOut("slow", function () {
    if (after) after();
  });
}

function fadeOutQuick(after) {
  $(".myFadable").fadeOut(500, function () {
    if (after) after();
  });
}

function fadeOutThenScrollTop() {
  $(".myFadable").fadeOut("slow", function () {
    scrollTop();
  });
}

function fadeIn(after) {
  $(".myFadable").fadeIn("slow", function () {
    if (after) after();
  });
}

function fadeInQuick(after) {
  $(".myFadable").fadeIn(500, function () {
    if (after) after();
  });
}

function scrollTop() {
  $("html, body").animate({ scrollTop: 0 }, 100);
}

export function scrollToDiv(divname) {
  var offset = $(`#${divname}`).offset();
  $("html, body").animate(
    {
      scrollTop: offset.top - 60,
    },
    100
  );
}

const timing = {
  duration: 1000,
  iterations: 1,
};

const scaling = [
  { transform: "scale(1)" },
  { transform: "scale(0.5)" },
  { transform: "scale(2.5)" },
  { transform: "scale(1)" },
];

const shaking = [
  { transform: "rotate(0)" },
  { transform: "rotate(60deg)" },
  { transform: "rotate(0)" },
  { transform: "rotate(-60deg)" },
  { transform: "rotate(0)" },
];

function animateHeart() {
  document.querySelector("#nav-favoris-svg").animate(scaling, timing);
}

function animateSuggestion() {
  //document.querySelector("#nav-suggestions-icon").animate(scaling, timing);
}
