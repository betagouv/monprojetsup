import { type VoeuxFicheFormationProps } from "./VoeuxFicheFormation.interface";
import TagFiltre from "@/components/TagFiltre/TagFiltre";
import Titre from "@/components/Titre/Titre";
import { type Élève, type FormationFavorite } from "@/features/élève/domain/élève.interface";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useMutation, useQuery } from "@tanstack/react-query";

const VoeuxFicheFormation = ({ formation }: VoeuxFicheFormationProps) => {
  const { data: élève } = useQuery(élèveQueryOptions);
  const mutationÉlève = useMutation<Élève, unknown, Élève>({ mutationKey: ["mettreÀJourÉlève"] });

  const ambitions: Array<{ niveau: NonNullable<FormationFavorite["niveauAmbition"]>; emoji: string; libellé: string }> =
    [
      {
        niveau: 1,
        emoji: "🛟",
        libellé: "Plan B",
      },
      {
        niveau: 2,
        emoji: "🎯",
        libellé: "Réaliste",
      },
      {
        niveau: 3,
        emoji: "🙏",
        libellé: "Ambitieux",
      },
    ];

  if (!élève) return null;

  const favori = élève.formationsFavorites?.find((formationFavorite) => formation.id === formationFavorite.id);

  const mettreAJourAmbition = (niveauAmbition: FormationFavorite["niveauAmbition"]) => {
    const nouvellesFormationsFavorites =
      élève.formationsFavorites?.map((formationFavorite) => {
        if (formationFavorite.id === formation.id) {
          const nouveauNiveauAmbition = niveauAmbition === formationFavorite.niveauAmbition ? null : niveauAmbition;

          return { ...formationFavorite, niveauAmbition: nouveauNiveauAmbition };
        }

        return formationFavorite;
      }) ?? [];

    mutationÉlève.mutateAsync({
      ...élève,
      formationsFavorites: nouvellesFormationsFavorites,
    });
  };

  return (
    <div className="my-10 border border-solid border-[--border-default-grey] px-10 py-8 shadow-md">
      <div className="*:text-[--text-label-grey]">
        <Titre
          niveauDeTitre="h2"
          styleDeTitre="h4"
        >
          Dis-nous en plus sur ce choix
        </Titre>
      </div>
      <p className="text-[--text-label-grey]">Je dirais que c’est un choix ...</p>
      <ul className="m-0 flex list-none flex-wrap justify-start gap-4 p-0">
        {ambitions.map((ambition) => (
          <li key={ambition.niveau}>
            <TagFiltre
              appuyéParDéfaut={favori?.niveauAmbition === ambition.niveau}
              auClic={() => mettreAJourAmbition(ambition.niveau)}
              emoji={ambition.emoji}
              key={JSON.stringify(élève.formationsFavorites)}
              libellé={ambition.libellé}
            />
          </li>
        ))}
      </ul>
    </div>
  );
};

export default VoeuxFicheFormation;
