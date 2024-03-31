import * as session from "../../app/session";
import $ from "jquery";
import { handlers } from "../../app/events";

function getMessage(isMyNote, authorName, dateStr, content) {
  const color = isMyNote
    ? "text-primary border-primary"
    : "text-danger border-danger";
  //const align = isMyNote ? "left" : "right";

  const $div = $(
    `<div class="border border-2 row rounded rounded-2 m-2 p-1"></div>`
  );
  $div.addClass(color);
  if (!isMyNote) {
    $div.append(`<p class="">Reçu le ${dateStr} de ${authorName}</p>`);
  } else {
    $div.append(`<p class="">Ma note du ${dateStr}</p>`);
  }
  $div.append(
    `<p class="text-black">&laquo;&nbsp;<br>${content}&nbsp;&raquo;</p>`
  );
  return $div;
}
export function getSuggHTMLNotes(
  nomAfficheEditeur,
  nomAfficheAuteur,
  editorLogin,
  notes,
  topic,
  placeholder
) {
  if (nomAfficheAuteur == null) nomAfficheAuteur = "resp groupe";
  const $div = $('<div class="m-2 col"></div>');

  const $comments =
    $(`<div class="text-primary row border-primary border border-2 rounded rounded-2 m-2 p-1 ">
    <textarea rows="5" class="form-control " 
  placeholder="${placeholder}"
  ></textarea></div>
  `);
  const $button = $(
    `<div class="row justify-content-end px-4">
    <button role="button" class="btn btn-success col-2">Envoyer</button>
    </div>`
  );

  $button.on("click", () => {
    const value = $("textarea", $comments).val();
    if (value && value != "") {
      handlers.commentChanged(topic, value);
    }
  });

  $div.append($comments).append($button);

  //TODO: put that teacherNames logic elsewhere
  let teacherNames = {};
  let infos = session.getGroupInfo();
  if (
    infos &&
    infos.admins &&
    infos.adminsNames &&
    infos.admins.length == infos.adminsNames.length
  ) {
    for (const i in infos.admins) {
      teacherNames[infos.admins[i]] = infos.adminsNames[i];
    }
  }

  let lastAuthor = null;
  let lastDate = null;
  let lastContent = "";
  let lastIsMyNote = null;
  let first = true;
  for (const note of notes.slice().reverse()) {
    const content = note.content ? note.content : "";
    if (content == "") {
      continue;
    }
    const author = note.author;
    const isMyNote = author === editorLogin;
    //We keep the last one from the current editor editable
    let authorName = "";
    if (isMyNote) {
      authorName = nomAfficheEditeur;
    } else if (teacherNames[author]) {
      authorName = teacherNames[author];
    } else {
      authorName = nomAfficheAuteur;
    }
    const date = note.date === undefined ? new Date(0) : new Date(note.date);
    const dateStr =
      isNaN(date) || date.getFullYear() < 2020
        ? ""
        : date.toLocaleDateString("fr");
    if (lastAuthor != null && (lastAuthor != author || lastDate != dateStr)) {
      if (first && lastIsMyNote) {
        //$("textarea", $comments).val(lastContent.replaceAll("<br>", "\n"));
      }
      $div.append(getMessage(lastIsMyNote, lastAuthor, lastDate, lastContent));

      lastContent = "";
      first = false;
    }
    lastContent = lastContent + content + "<br>";
    lastIsMyNote = isMyNote;
    lastAuthor = author;
    lastDate = dateStr;
  }
  if (lastContent != "") {
    $div.append(getMessage(lastIsMyNote, lastAuthor, lastDate, lastContent));
  }

  /*
  l.push(
    getSuggHTMLGenericNotes(
      note,
      id,
      studentMode
        ? "Remarques, actions à prévoir,..."
        : "Pas de notes pour l'instant.",
      studentMode
    )
  );
  l.push(studentMode ? `<p>Notes animateur</p>` : `<p>Vos commentaires</p>`);
  l.push(
    getSuggHTMLGenericNotes(
      comment,
      id,
      studentMode
        ? "Pas de notes pour l'instant."
        : "Conseils, remarques, actions à entreprendre,...",
      !studentMode
    )
  );
  */
  return $div;
}
