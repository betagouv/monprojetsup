interface FormElements extends HTMLFormControlsCollection {
  commentaire: HTMLTextAreaElement;
}
export interface CommentaireFormElement extends HTMLFormElement {
  readonly elements: FormElements;
}
