import Head from "@/components/_layout/Head/Head";
import ListeEtAperçuBarreLatérale from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuBarreLatérale/ListeEtAperçuBarreLatérale";
import ListeEtAperçuContenu from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuContenu/ListeEtAperçuContenu";
import ListeEtAperçuLayout from "@/components/_layout/ListeEtAperçuLayout/ListeEtAperçuLayout";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import { i18n } from "@/configuration/i18n/i18n";
import { récupérerFormationsQueryOptions } from "@/features/formation/ui/formationQueries";
import { récupérerMétiersQueryOptions } from "@/features/métier/ui/métierQueries";
import BarreLatéraleFavoris from "@/features/élève/ui/favoris/BarreLatéraleFavoris/BarreLatéraleFavoris";
import ContenuFavoris from "@/features/élève/ui/favoris/ContenuFavoris/ContenuFavoris";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useQuery } from "@tanstack/react-query";

const FavorisPage = () => {
  const { data: élève } = useQuery(élèveQueryOptions);

  const { data: formations } = useQuery(
    récupérerFormationsQueryOptions(élève?.formationsFavorites?.map((formationFavorite) => formationFavorite.id) ?? []),
  );

  const { data: métiers } = useQuery(récupérerMétiersQueryOptions(élève?.métiersFavoris ?? []));

  if (!formations || !métiers) {
    return <AnimationChargement />;
  }

  return (
    <>
      <Head titre={i18n.PAGE_FAVORIS.TITRE_PAGE} />
      <ListeEtAperçuLayout variante="favoris">
        <ListeEtAperçuBarreLatérale>
          <BarreLatéraleFavoris
            formations={formations}
            métiers={métiers}
          />
        </ListeEtAperçuBarreLatérale>
        <ListeEtAperçuContenu>
          <ContenuFavoris />
        </ListeEtAperçuContenu>
      </ListeEtAperçuLayout>
    </>
  );
};

export default FavorisPage;
