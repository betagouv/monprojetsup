import { ModaleCatégorieProps } from "@/components/FiltresGroupésParCatégorie/ModaleCatégorie/ModaleCatégorie.interface.tsx";
import { i18n } from "@/configuration/i18n/i18n.ts";
import { ModalProps } from "@codegouvfr/react-dsfr/Modal";

const ModaleCatégorie = ({ contenuModaleDétail, modale }: ModaleCatégorieProps) => {
  const boutonFermer: ModalProps["buttons"] = {
    children: i18n.COMMUN.FERMER,
    priority: "tertiary",
    size: "large",
    doClosesModal: true,
    type: "button",
  };

  return (
    <modale.Component
      buttons={boutonFermer}
      iconId="fr-icon-information-line"
      title={i18n.COMMUN.DÉTAILS_CATÉGORIES}
    >
      <ul className="m-0 grid list-none grid-flow-row gap-6 p-0">
        {contenuModaleDétail.map(
          (filtre) =>
            filtre.description !== null && (
              <li key={filtre.id}>
                <p className="fr-text--md mb-0 text-[--text-default-grey]">
                  <strong>{filtre.nom}</strong> : {filtre.description}
                </p>
              </li>
            ),
        )}
      </ul>
    </modale.Component>
  );
};

export default ModaleCatégorie;
