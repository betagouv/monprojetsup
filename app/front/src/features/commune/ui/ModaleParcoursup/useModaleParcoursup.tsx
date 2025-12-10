import { i18n } from "@/configuration/i18n/i18n.ts";
import { ModalProps } from "@codegouvfr/react-dsfr/Modal";
import { useMemo } from "react";

export default function useModaleParcoursup() {
  const boutons = useMemo((): ModalProps["buttons"] => {
    const boutonDecouvrirLaCarte: ModalProps["buttons"] = {
      children: i18n.PARCOURSUP_MODALE.DECOUVRIR_LA_CARTE,
      size: "large",
      linkProps: { to: "https://dossier.parcoursup.fr/Candidat/carte" },
    };

    return [boutonDecouvrirLaCarte];
  }, []);

  const titre = useMemo(() => {
    return <h2>{i18n.PARCOURSUP_MODALE.TITRE}</h2>;
  }, []);

  return { boutons, titre };
}
