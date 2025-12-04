import { ListeDeFavorisProps } from "./ListeDeFavoris.interface";
import useListeDeFavoris from "./useListeDeFavoris";
import Bouton from "@/components/Bouton/Bouton.tsx";
import Favori from "@/components/SélecteurFavoris/Favori/Favori";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève.ts";

const ListeDeFavoris = ({
  favoris,
  nombreFavorisAffichésParDéfaut = Number.POSITIVE_INFINITY,
  listeDeSuggestions,
  parcoursupSynchronizable,
}: ListeDeFavorisProps) => {
  const { id, nombreFavorisAffichés, favorisAffichés, afficherPlusDeFavoris } = useListeDeFavoris({
    favoris,
    nombreFavorisAffichésParDéfaut,
  });

  if (favorisAffichés.length === 0) return null;

  const eleve = useÉlève();

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
            className="grid grid-flow-col items-start justify-between gap-2 p-0"
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
              idDeLonglet={id}
              nom={favori.nom}
              parcoursupSynchronizable={parcoursupSynchronizable}
              SyncAvecParcoursup={eleve.estVoeuFavoriProvenantDeParcoursupPourÉlève(favori.id)}
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
