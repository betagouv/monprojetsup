import { type Formation } from "@/features/formation/domain/formation.interface";

export type CommentaireProps = {
  formationId: Formation["id"];
};

export type UseCommentaireArgs = {
  formationId: CommentaireProps["formationId"];
};

interface FormElements extends HTMLFormControlsCollection {
  commentaire: HTMLTextAreaElement;
}
export interface CommentaireFormElement extends HTMLFormElement {
  readonly elements: FormElements;
}
