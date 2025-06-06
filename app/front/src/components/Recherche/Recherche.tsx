import { RechercheProps } from "./Recherche.interface";
import useRecherche from "./useRecherche";
import ChampDeRecherche from "@/components/ChampDeRecherche/ChampDeRecherche";
import { i18n } from "@/configuration/i18n/i18n.ts";

const Recherche = ({
  rechercheCallback,
  label,
  description,
  nombreDeCaractèresMinimumRecherche,
  nombreDeCaractèresMaximumRecherche,
  nombreDeRésultats,
}: RechercheProps) => {
  const { debouncedSetRecherche, statusChampDeRecherche } = useRecherche({
    nombreDeCaractèresMinimumRecherche,
    nombreDeCaractèresMaximumRecherche,
    rechercheCallback,
    nombreDeRésultats,
  });

  return (
    <div className="[&>.fr-input-group]:mb-0">
      <ChampDeRecherche
        auChangement={(événement) => debouncedSetRecherche(événement.target.value ?? undefined)}
        entête={{ description, label }}
        obligatoire={false}
        status={statusChampDeRecherche}
      />
      <div
        aria-live="polite"
        className="sr-only"
      >
        {nombreDeRésultats && nombreDeRésultats > 0 && (
          <p>
            {nombreDeRésultats} {i18n.ACCESSIBILITÉ.NOUVEAUX_RÉSULATS}
          </p>
        )}
      </div>
    </div>
  );
};

export default Recherche;
