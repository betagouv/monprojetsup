import { ListeDeFavorisProps } from "./ListeDeFavoris.interface";
import useListeDeFavoris from "./useListeDeFavoris";
import Bouton from "@/components/Bouton/Bouton.tsx";
import Favori from "@/components/SélecteurFavoris/Favori/Favori";
import { i18n } from "@/configuration/i18n/i18n";

const ListeDeFavoris = ({
  favoris,
  nombreFavorisAffichésParDéfaut = Number.POSITIVE_INFINITY,
  listeDeSuggestions,
}: ListeDeFavorisProps) => {
  const { id, nombreFavorisAffichés, favorisAffichés, afficherPlusDeFavoris } = useListeDeFavoris({
    favoris,
    nombreFavorisAffichésParDéfaut,
  });

  if (favorisAffichés.length === 0) return null;

  return (
    <div
      className="mt-4"
      id={`liste-favoris-${id}`}
      tabIndex={-1}
    >
      <ul
        aria-label={
          listeDeSuggestions
            ? i18n.ACCESSIBILITÉ.LISTE_SUGGESTIONS_FAVORIS
            : i18n.ACCESSIBILITÉ.LISTE_FAVORIS_SÉLECTIONNÉS
        }
        className="m-0 grid grid-flow-row justify-stretch gap-2 p-0"
      >
        {favorisAffichés.map((favori) => (
          <li
            className="grid grid-flow-col items-center justify-between gap-2 p-0"
            key={favori.id}
          >
            <Favori
              ariaLabel={favori.ariaLabel}
              callbackMettreÀJour={favori.callbackMettreÀJour}
              désactivé={favori.désactivé}
              estFavori={favori.estFavori}
              icôneEstFavori={favori.icôneEstFavori}
              icôneEstPasFavori={favori.icôneEstPasFavori}
              id={favori.id}
              nom={favori.nom}
              title={favori.title}
              url={favori.url}
            />
          </li>
        ))}
      </ul>
      {favoris.length > nombreFavorisAffichés && (
        <div className="mt-2 *:p-0">
          <Bouton
            auClic={afficherPlusDeFavoris}
            taille="petit"
            type="button"
            variante="quinaire"
          >
            {i18n.COMMUN.FAVORIS.VOIR_PLUS}
          </Bouton>
        </div>
      )}
    </div>
  );
};

export default ListeDeFavoris;
