import { AlertProps } from "@codegouvfr/react-dsfr/Alert";

export type ToastStoreState = {
  estOuvert: boolean;
  titre?: string;
  description?: string;
  type?: AlertProps["severity"];
};

export type ToastStoreActions = {
  actions: {
    déclencherToast: (
      titre: NonNullable<ToastStoreState["titre"]>,
      description: NonNullable<ToastStoreState["description"]>,
      type: NonNullable<ToastStoreState["type"]>,
    ) => void;
    fermerToast: () => void;
  };
};

export type ToastStore = ToastStoreState & ToastStoreActions;
