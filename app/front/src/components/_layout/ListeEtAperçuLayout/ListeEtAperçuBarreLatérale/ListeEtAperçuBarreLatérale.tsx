import { type ListeEtAperçuBarreLatéraleProps } from "./ListeEtAperçuBarreLatérale.interface";
import {
  actionsListeEtAperçuStore,
  afficherBarreLatéraleEnMobileListeEtAperçuStore,
} from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import Bouton from "@/components/Bouton/Bouton";
import { i18n } from "@/configuration/i18n/i18n";

const ListeEtAperçuBarreLatérale = ({ children, nombreRésultats }: ListeEtAperçuBarreLatéraleProps) => {
  const { changerAfficherBarreLatéraleEnMobile } = actionsListeEtAperçuStore();
  const afficherBarreLatéraleEnMobile = afficherBarreLatéraleEnMobileListeEtAperçuStore();

  const afficherLaBarreLatérale = () => {
    return afficherBarreLatéraleEnMobile ? "" : "hidden lg:grid";
  };

  return (
    <div
      className={`grid content-start gap-8 overflow-hidden pt-6 lg:sticky lg:left-0 lg:top-0 lg:max-h-screen ${afficherLaBarreLatérale()}`}
    >
      {nombreRésultats > 0 && (
        <div className="ml-[-1rem] px-2 lg:hidden lg:px-7">
          <Bouton
            auClic={() => changerAfficherBarreLatéraleEnMobile(!afficherBarreLatéraleEnMobile)}
            icône={{ classe: "fr-icon-arrow-left-line", position: "gauche" }}
            label={i18n.COMMUN.BOUTON_AFFICHER_CONTENU_PRINCIPAL}
            type="button"
            variante="quaternaire"
          />
        </div>
      )}
      {children}
    </div>
  );
};

export default ListeEtAperçuBarreLatérale;
