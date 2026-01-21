import { i18n } from "@/configuration/i18n/i18n.ts";
import { ModalProps } from "@codegouvfr/react-dsfr/Modal";
import { useMemo } from "react";

export default function useModaleParcoursupEvent() {
  const boutons = useMemo((): ModalProps["buttons"] => {
    const boutonDecouvrirLaCarte: ModalProps["buttons"] = {
      children: i18n.PARCOURSUP_MODALE.DECOUVRIR_LA_CARTE,
      size: "large",
      linkProps: { href: "https://dossier.parcoursup.fr/Candidat/carte" },
    };

    return [boutonDecouvrirLaCarte];
  }, []);

  const titre = useMemo(() => {
    return i18n.PARCOURSUP_MODALE.TITRE;
  }, []);

  return { boutons, titre };
}
