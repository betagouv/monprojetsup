import { type Formation } from "@/features/formation/domain/formation.interface";

export type CommentaireVoeuxProps = {
  formationId: Formation["id"];
};

export type UseCommentaireVoeuxArgs = {
  formationId: CommentaireVoeuxProps["formationId"];
};

interface FormElements extends HTMLFormControlsCollection {
  commentairevoeux: HTMLTextAreaElement;
}
export interface CommentaireVoeuxFormElement extends HTMLFormElement {
  readonly elements: FormElements;
}
