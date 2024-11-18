import LienExterne from "@/components/Lien/LienExterne/LienExterne.tsx";
import { i18n } from "@/configuration/i18n/i18n.ts";
import { CommunesProposantLaFormationProps } from "@/features/formation/ui/CommunesProposantLaFormation/CommunesProposantLaFormation.interface.tsx";

const CommunesProposantLaFormation = ({
  communes,
  explications,
  lienParcoursSup,
}: CommunesProposantLaFormationProps) => {
  if (communes.length === 0) {
    return null;
  }

  const itemPosition = lienParcoursSup ? "items-start" : "items-center";

  return (
    <div className={`grid grid-flow-col ${itemPosition} justify-start gap-2 text-[--text-mention-grey]`}>
      <span
        aria-hidden="true"
        className="fr-icon-map-pin-2-fill fr-icon--sm"
      />
      <div>
        <p className="mb-0 text-sm">
          {i18n.PAGE_FORMATION.COMMUNES_PROPOSANT_FORMATION}
          {explications?.communes && explications?.communes.length > 0 ? (
            <>
              {" "}
              {communes.length} {i18n.PAGE_FORMATION.COMMUNES_PROPOSANT_FORMATION_SUITE}{" "}
              {i18n.PAGE_FORMATION.COMMUNES_PROPOSANT_FORMATION_SUITE_SI_CORRESPONDANCE}{" "}
              <strong>{explications.communes.map((commune) => commune.nom).join(" • ")}</strong>
            </>
          ) : (
            <strong>
              {" "}
              {communes.length} {i18n.CARTE_FORMATION.COMMUNES_PROPOSANT_FORMATION_SUITE}
            </strong>
          )}
        </p>
        {lienParcoursSup && (
          <LienExterne
            ariaLabel={i18n.PAGE_FORMATION.VOIR_SUR_PARCOURSUP}
            href={lienParcoursSup}
            taille="petit"
            variante="simple"
          >
            {i18n.PAGE_FORMATION.VOIR_SUR_PARCOURSUP}
          </LienExterne>
        )}
      </div>
    </div>
  );
};

export default CommunesProposantLaFormation;
