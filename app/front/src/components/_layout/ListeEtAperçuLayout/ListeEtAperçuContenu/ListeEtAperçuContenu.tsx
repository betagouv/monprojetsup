import { type ListeEtAperçuContenuProps } from "./ListeEtAperçuContenu.interface";
import {
  actionsListeEtAperçuStore,
  afficherBarreLatéraleEnMobileListeEtAperçuStore,
} from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import Bouton from "@/components/Bouton/Bouton";
import { i18n } from "@/configuration/i18n/i18n";

const ListeEtAperçuContenu = ({ children }: ListeEtAperçuContenuProps) => {
  const { changerAfficherBarreLatéraleEnMobile } = actionsListeEtAperçuStore();
  const afficherBarreLatéraleEnMobile = afficherBarreLatéraleEnMobileListeEtAperçuStore();

  const afficherContenuPrincipal = () => {
    return afficherBarreLatéraleEnMobile ? "hidden lg:block" : "";
  };

  return (
    <div className={`bg-white ${afficherContenuPrincipal()}`}>
      <div
        aria-live="assertive"
        className="pb-12 pt-6 lg:pl-14"
      >
        <div className="ml-[-1rem] pb-6 lg:hidden">
          <Bouton
            auClic={() => changerAfficherBarreLatéraleEnMobile(!afficherBarreLatéraleEnMobile)}
            icône={{ classe: "fr-icon-arrow-left-line", position: "gauche" }}
            type="button"
            variante="quaternaire"
          >
            {i18n.COMMUN.BOUTON_AFFICHER_BARRE_LATÉRALE}
          </Bouton>
        </div>
        {children}
      </div>
    </div>
  );
};

export default ListeEtAperçuContenu;
