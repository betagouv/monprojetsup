import ListeEtAperçuBarreLatérale from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuBarreLatérale/ListeEtAperçuBarreLatérale";
import ListeEtAperçuContenu from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuContenu/ListeEtAperçuContenu";
import ListeEtAperçuLayout from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuLayout";
import {
  actionsListeEtAperçuStore,
  rechercheListeEtAperçuStore,
  élémentAffichéListeEtAperçuStore,
} from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import BarreLatéraleFicheFormation from "@/features/formation/ui/BarreLatéraleFicheFormation/BarreLatéraleFicheFormation";
import FicheFormation from "@/features/formation/ui/FicheFormation/FicheFormation";
import {
  rechercherFichesFormationsQueryOptions,
  suggérerFormationsQueryOptions,
} from "@/features/formation/ui/formationQueries";
import { useQuery } from "@tanstack/react-query";
import { useLocation } from "@tanstack/react-router";
import { useEffect } from "react";

const DétailFormationPage = () => {
  const recherche = rechercheListeEtAperçuStore();
  const { changerÉlémentAffiché } = actionsListeEtAperçuStore();
  const élémentAffiché = élémentAffichéListeEtAperçuStore();
  const { hash } = useLocation();

  const { data: suggestions, isFetching: chargementSuggestionsEnCours } = useQuery({
    ...suggérerFormationsQueryOptions,
    enabled: recherche === undefined,
  });

  const { data: résultatsDeRecherche, isFetching: chargementRechercheEnCours } = useQuery({
    ...rechercherFichesFormationsQueryOptions(recherche),
    enabled: recherche !== undefined,
  });

  useEffect(() => {
    if (recherche) {
      changerÉlémentAffiché({
        id: résultatsDeRecherche?.[0]?.id ?? null,
        type: "formation",
      });
    } else {
      if (hash !== "") {
        changerÉlémentAffiché({
          id: hash,
          type: "formation",
        });
      }

      changerÉlémentAffiché({
        id: suggestions?.[0]?.id ?? null,
        type: "formation",
      });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [changerÉlémentAffiché, recherche, résultatsDeRecherche, suggestions]);

  if (!résultatsDeRecherche && !suggestions) {
    return <AnimationChargement />;
  }

  if (hash !== "" && hash !== élémentAffiché.id) {
    changerÉlémentAffiché({
      id: hash,
      type: "formation",
    });
  }

  return (
    <ListeEtAperçuLayout variante="formations">
      <ListeEtAperçuBarreLatérale nombreRésultats={résultatsDeRecherche?.length ?? suggestions?.length ?? 0}>
        <BarreLatéraleFicheFormation
          chargementEnCours={chargementRechercheEnCours || chargementSuggestionsEnCours}
          résultatsDeRecherche={résultatsDeRecherche}
          suggestions={suggestions}
        />
      </ListeEtAperçuBarreLatérale>
      <ListeEtAperçuContenu>{élémentAffiché.id && <FicheFormation id={élémentAffiché.id} />}</ListeEtAperçuContenu>
    </ListeEtAperçuLayout>
  );
};

export default DétailFormationPage;
