import useFormationsMasquées from "./useFormationsMasquées.ts";
import Titre from "@/components/Titre/Titre.tsx";
import { i18n } from "@/configuration/i18n/i18n.ts";
import FormationMasquée from "@/features/formation/ui/FormationMasquée/FormationMasquée.tsx";

const FormationsMasquées = () => {
  const { formationsMasquées } = useFormationsMasquées();

  if (formationsMasquées === undefined) return null;

  return (
    <>
      <div className="*:mb-10">
        <Titre
          niveauDeTitre="h2"
          styleDeTitre="h4"
        >
          {i18n.PAGE_PROFIL.FORMATIONS_MASQUÉES.TITRE}
        </Titre>
      </div>
      {formationsMasquées?.length > 0 ? (
        <ul className="m-0 grid list-none gap-8 p-0">
          {formationsMasquées.map((formation, index) => (
            <li key={formation.id}>
              <FormationMasquée formation={formation} />
              {formationsMasquées.length !== index + 1 && <hr className="mt-8 pb-[1px]" />}
            </li>
          ))}
        </ul>
      ) : (
        <p className="fr-text mb-1 text-[--text-mention-grey]">{i18n.PAGE_PROFIL.FORMATIONS_MASQUÉES.MESSAGE_AUCUNE}</p>
      )}
    </>
  );
};

export default FormationsMasquées;
