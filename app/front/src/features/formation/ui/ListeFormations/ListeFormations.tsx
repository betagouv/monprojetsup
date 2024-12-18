import { type ListeFormationsProps } from "./ListeFormations.interface";
import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import CarteFormation from "@/features/formation/ui/CarteFormation/CarteFormation";
import BoutonRetourAuxSuggestions from "@/features/formation/ui/FormationPage/BarreLatéraleFormation/BoutonRetourAuxSuggestions/BoutonRetourAuxSuggestions";

const ListeFormations = ({ formations, affichéSurLaPage }: ListeFormationsProps) => {
  const élémentAffiché = élémentAffichéListeEtAperçuStore();

  return (
    <div
      className="grid h-full justify-center gap-6 px-2 pb-6 lg:justify-normal lg:overflow-y-auto lg:px-6"
      id={constantes.ACCESSIBILITÉ.LISTE_CARTES_ID}
    >
      {formations.length === 0 && affichéSurLaPage === "ficheFormation" ? (
        <>
          <BoutonRetourAuxSuggestions />
          <p className="mb-0 text-center">{i18n.PAGE_FORMATION.AUCUN_RÉSULTAT}</p>
        </>
      ) : (
        <>
          {affichéSurLaPage === "ficheFormation" && (
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
          )}
          <ul
            aria-label={i18n.ACCESSIBILITÉ.LISTE_FORMATIONS}
            className="m-0 grid list-none justify-center gap-6 p-0 lg:justify-normal"
          >
            {formations.map((formation) => (
              <li key={formation.id}>
                <CarteFormation
                  affinité={formation.affinité}
                  communes={formation.communesProposantLaFormation}
                  id={formation.id}
                  key={formation.id}
                  métiersAccessibles={formation.métiersAccessibles}
                  sélectionnée={élémentAffiché.id === formation.id}
                  titre={formation.nom}
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
