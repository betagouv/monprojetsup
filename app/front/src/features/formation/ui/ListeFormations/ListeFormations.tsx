import { type ListeFormationsProps } from "./ListeFormations.interface";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import { i18n } from "@/configuration/i18n/i18n";
import BoutonRetourAuxSuggestions from "@/features/formation/ui/BoutonRetourAuxSuggestions/BoutonRetourAuxSuggestions";
import CarteFormation from "@/features/formation/ui/CarteFormation/CarteFormation";

const ListeFormations = ({ formations, formationIdAffichée }: ListeFormationsProps) => {
  return (
    <div className="grid h-full justify-center gap-6 px-2 pb-6 lg:overflow-y-auto lg:px-6">
      {formations.length === 0 ? (
        <>
          <BoutonRetourAuxSuggestions />
          <p className="mb-0">{i18n.PAGE_FORMATION.AUCUN_RÉSULTAT}</p>
        </>
      ) : (
        <>
          <p className="mb-0 text-center">
            {i18n.PAGE_FORMATION.SUGGESTIONS_TRIÉES_AFFINITÉ}{" "}
            <LienInterne
              ariaLabel={i18n.PAGE_FORMATION.SUGGESTIONS_TRIÉES_AFFINITÉ_SUITE}
              href="/profil"
              variante="simple"
            >
              {i18n.PAGE_FORMATION.SUGGESTIONS_TRIÉES_AFFINITÉ_SUITE}
            </LienInterne>
          </p>
          <ul className="m-0 grid list-none justify-center gap-6 p-0">
            {formations.map((formation) => (
              <li key={formation.id}>
                <CarteFormation
                  affinité={formation.affinité}
                  communes={formation.communes}
                  id={formation.id}
                  key={formation.id}
                  métiersAccessibles={formation.métiersAccessibles}
                  nom={formation.nom}
                  sélectionnée={formation.id === formationIdAffichée}
                />
              </li>
            ))}
          </ul>
        </>
      )}
    </div>
  );
};

export default ListeFormations;
