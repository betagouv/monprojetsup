import { type ListeFormationsProps } from "./ListeFormations.interface";
import {
  actionsListeEtAperçuStore,
  élémentAffichéListeEtAperçuStore,
} from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import { i18n } from "@/configuration/i18n/i18n";
import BoutonRetourAuxSuggestions from "@/features/formation/ui/BoutonRetourAuxSuggestions/BoutonRetourAuxSuggestions";
import CarteFormation from "@/features/formation/ui/CarteFormation/CarteFormation";

const ListeFormations = ({ formations, affichéSurLaPage }: ListeFormationsProps) => {
  const élémentAffiché = élémentAffichéListeEtAperçuStore();
  const { changerÉlémentAffiché, changerAfficherBarreLatéraleEnMobile } = actionsListeEtAperçuStore();

  const estSélectionnée = (positionDansLaListe: number, formationId: string) => {
    if (positionDansLaListe === 0 && élémentAffiché === undefined) {
      changerÉlémentAffiché({ type: "formation", id: formationId });
      return true;
    }

    return Boolean(élémentAffiché?.type === "formation" && formationId === élémentAffiché?.id);
  };

  return (
    <div className="grid h-full justify-center gap-6 px-2 pb-6 lg:justify-normal lg:overflow-y-auto lg:px-6">
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
          <ul className="m-0 grid list-none justify-center gap-6 p-0 lg:justify-normal">
            {formations.map((formation, index) => (
              <li key={formation.id}>
                <CarteFormation
                  affinité={formation.affinité}
                  auClic={() => {
                    changerÉlémentAffiché({ type: "formation", id: formation.id });
                    changerAfficherBarreLatéraleEnMobile(false);
                    scrollTo({ top: 0 });
                  }}
                  communes={formation.communes}
                  id={formation.id}
                  key={formation.id}
                  métiersAccessibles={formation.métiersAccessibles}
                  sélectionnée={estSélectionnée(index, formation.id)}
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
