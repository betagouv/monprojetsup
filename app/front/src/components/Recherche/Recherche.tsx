import { RechercheProps } from "./Recherche.interface";
import useRecherche from "./useRecherche";
import ChampDeRecherche from "@/components/ChampDeRecherche/ChampDeRecherche";
import { i18n } from "@/configuration/i18n/i18n.ts";

const Recherche = <T extends object>({
  rechercheCallback,
  label,
  description,
  nombreDeCaractèresMinimumRecherche,
}: RechercheProps<T>) => {
  const { debouncedSetRecherche, statusChampDeRecherche, nombreDeRésultats } = useRecherche({
    nombreDeCaractèresMinimumRecherche,
    rechercheCallback,
  });

  return (
    <div>
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
