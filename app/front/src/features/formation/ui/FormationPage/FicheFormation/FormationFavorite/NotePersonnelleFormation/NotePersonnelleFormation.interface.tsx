interface FormElements extends HTMLFormControlsCollection {
  notePersonnelle: HTMLTextAreaElement;
}
export interface NotePersonnelleFormElement extends HTMLFormElement {
  readonly elements: FormElements;
}
