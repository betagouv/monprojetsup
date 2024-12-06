import FicheFormation from "./FicheFormation/FicheFormation";
import ListeEtAperçuBarreLatérale from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuBarreLatérale/ListeEtAperçuBarreLatérale";
import ListeEtAperçuContenu from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuContenu/ListeEtAperçuContenu";
import ListeEtAperçuLayout from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuLayout";
import {
  actionsListeEtAperçuStore,
  élémentAffichéListeEtAperçuStore,
  rechercheListeEtAperçuStore,
} from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import BarreLatéraleFormation from "@/features/formation/ui/FormationPage/BarreLatéraleFormation/BarreLatéraleFormation";
import {
  rechercherFichesFormationsQueryOptions,
  suggérerFormationsQueryOptions,
} from "@/features/formation/ui/formationQueries";
import useFormation from "@/features/formation/ui/useFormation";
import useMétier from "@/features/métier/ui/useMétier";
import { useQuery } from "@tanstack/react-query";
import { useLocation } from "@tanstack/react-router";
import { useEffect } from "react";

const FormationPage = () => {
  const recherche = rechercheListeEtAperçuStore();
  const { changerÉlémentAffiché } = actionsListeEtAperçuStore();
  const élémentAffiché = élémentAffichéListeEtAperçuStore();
  const { hash } = useLocation();
  const { estUnIdDeFormation } = useFormation();
  const { estUnIdDeMétier } = useMétier();

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
    } else if (hash !== "" && (estUnIdDeFormation(hash) || estUnIdDeMétier(hash))) {
      changerÉlémentAffiché({
        id: hash,
        type: "formation",
      });
    } else {
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

  if (hash !== "" && hash !== élémentAffiché.id && (estUnIdDeFormation(hash) || estUnIdDeMétier(hash))) {
    changerÉlémentAffiché({
      id: hash,
      type: "formation",
    });
  }

  return (
    <ListeEtAperçuLayout variante="formations">
      <ListeEtAperçuBarreLatérale nombreRésultats={résultatsDeRecherche?.length ?? suggestions?.length ?? 0}>
        <BarreLatéraleFormation
          chargementEnCours={chargementRechercheEnCours || chargementSuggestionsEnCours}
          résultatsDeRecherche={résultatsDeRecherche}
          suggestions={suggestions}
        />
      </ListeEtAperçuBarreLatérale>
      <ListeEtAperçuContenu>{élémentAffiché.id && <FicheFormation id={élémentAffiché.id} />}</ListeEtAperçuContenu>
    </ListeEtAperçuLayout>
  );
};

export default FormationPage;
