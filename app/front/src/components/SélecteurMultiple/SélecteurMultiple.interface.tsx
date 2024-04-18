export type SélecteurMultipleProps = {
  label: string;
  texteOptionsSélectionnées: string;
  options: SélecteurMultipleOption[];
  description?: string;
  status?: {
    type: "désactivé" | "erreur" | "succès";
    message?: string;
  };
  valeursOptionsSélectionnéesParDéfaut?: Array<SélecteurMultipleOption["valeur"]>;
  auChangementOptionsSélectionnées?: (valeursOptionsSélectionnées: Array<SélecteurMultipleOption["valeur"]>) => void;
};

export type SélecteurMultipleOption = {
  valeur: string;
  label: string;
};
