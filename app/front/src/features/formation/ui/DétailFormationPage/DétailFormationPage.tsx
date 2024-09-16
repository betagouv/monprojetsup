import ListeEtAperçuBarreLatérale from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuBarreLatérale/ListeEtAperçuBarreLatérale";
import ListeEtAperçuContenu from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuContenu/ListeEtAperçuContenu";
import ListeEtAperçuLayout from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuLayout";
import {
  actionsListeEtAperçuStore,
  élémentAffichéListeEtAperçuStore,
} from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import BarreLatéraleFicheFormation from "@/features/formation/ui/BarreLatéraleFicheFormation/BarreLatéraleFicheFormation";
import FicheFormation from "@/features/formation/ui/FicheFormation/FicheFormation";
import {
  rechercherFormationsQueryOptions,
  suggérerFormationsQueryOptions,
} from "@/features/formation/ui/formationQueries";
import { useQuery } from "@tanstack/react-query";
import { getRouteApi } from "@tanstack/react-router";
import { useEffect } from "react";

const DétailFormationPage = () => {
  const route = getRouteApi("/_auth/formations/");
  const { recherche, formation } = route.useSearch();

  const élémentAffiché = élémentAffichéListeEtAperçuStore();
  const { changerÉlémentAffiché } = actionsListeEtAperçuStore();

  const { data: suggestions } = useQuery({
    ...suggérerFormationsQueryOptions,
    enabled: recherche === undefined,
  });

  const { data: résultatsDeRecherche } = useQuery({
    ...rechercherFormationsQueryOptions(recherche),
    enabled: recherche !== undefined,
  });

  useEffect(() => {
    if (formation && élémentAffiché === undefined) {
      changerÉlémentAffiché({
        type: "formation",
        id: formation,
      });
    }
  }, [changerÉlémentAffiché, formation, élémentAffiché]);

  if (!résultatsDeRecherche && !suggestions) {
    return <AnimationChargement />;
  }

  return (
    <ListeEtAperçuLayout variante="formations">
      <ListeEtAperçuBarreLatérale>
        <BarreLatéraleFicheFormation
          recherche={recherche}
          résultatsDeRecherche={résultatsDeRecherche}
          suggestions={suggestions}
        />
      </ListeEtAperçuBarreLatérale>
      <ListeEtAperçuContenu>
        <FicheFormation id={élémentAffiché?.id ?? résultatsDeRecherche?.[0]?.id ?? suggestions?.[0]?.id ?? ""} />
      </ListeEtAperçuContenu>
    </ListeEtAperçuLayout>
  );
};

export default DétailFormationPage;
