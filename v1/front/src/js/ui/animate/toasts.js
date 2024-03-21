import $ from "jquery";
import { Toast } from "bootstrap";
export { toast, toast_remove_all };

function toast(title, message) {
  //toast_remove_all();
  const uuid = Math.floor(Math.random() * 100000000);
  $(".toaster").empty();
  $(".toaster").prepend(
    '<div id="' +
      uuid +
      '" class="toast text-white bg-black  align-items-center" data-delay="800" role="alert" aria-live="assertive" aria-atomic="true">' +
      '<div class="d-flex">' +
      '<div class="toast-body text-white">' +
      message +
      "</div>" +
      '<button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>' +
      "</div>" +
      "</div>"
  );
  for (const myToastEl of $("#" + uuid)) {
    var myToast = Toast.getOrCreateInstance(myToastEl);
    if (myToast !== null) {
      myToast.show();
    }
  }
}

function toast_remove_all() {
  for (const myToastEl of $(".toast")) {
    var myToast = Toast.getOrCreateInstance(myToastEl);
    if (myToast) {
      myToast.dispose();
    }
  }
  $(".toaster").html("");
}
