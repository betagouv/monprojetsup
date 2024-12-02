import { ListeDeFavorisProps } from "./ListeDeFavoris.interface";
import useListeDeFavoris from "./useListeDeFavoris";
import Bouton from "@/components/Bouton/Bouton.tsx";
import Favori from "@/components/SélecteurFavoris/Favori/Favori";
import { i18n } from "@/configuration/i18n/i18n";

const ListeDeFavoris = ({
  favoris,
  nombreFavorisAffichésParDéfaut = Number.POSITIVE_INFINITY,
}: ListeDeFavorisProps) => {
  const { id, nombreFavorisAffichés, favorisAffichés, afficherPlusDeFavoris } = useListeDeFavoris({
    favoris,
    nombreFavorisAffichésParDéfaut,
  });

  return (
    <div
      id={`liste-favoris-${id}`}
      tabIndex={-1}
    >
      <ul className="m-0 grid grid-flow-row justify-start gap-2 p-0">
        {favorisAffichés.map((favori) => (
          <li
            className="grid grid-flow-col justify-between gap-2 p-0"
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
        <Bouton
          auClic={afficherPlusDeFavoris}
          label={i18n.COMMUN.FAVORIS.VOIR_PLUS}
          taille="petit"
          type="button"
          variante="quinaire"
        />
      )}
    </div>
  );
};

export default ListeDeFavoris;
