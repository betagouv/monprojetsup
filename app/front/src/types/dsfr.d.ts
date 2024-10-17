/* eslint-disable @typescript-eslint/no-unsafe-function-type */
import { type LinkProps } from "@tanstack/react-router";

declare module "@codegouvfr/react-dsfr/spa" {
  interface RegisterLink {
    Link: (props: LinkProps) => JSX.Element;
  }
}

export declare global {
  interface Window {
    dsfr: Function;
  }
}
