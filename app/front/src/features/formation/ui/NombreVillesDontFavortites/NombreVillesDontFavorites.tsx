import { i18n } from "@/configuration/i18n/i18n.ts";
import { NombreVillesDontFavoritesProps } from "@/features/formation/ui/NombreVillesDontFavortites/NombreVillesDontFavorites.interface.tsx";

const NombreVillesDontFavorites = ({
  communesProposantLaFormation,
  explications,
}: NombreVillesDontFavoritesProps) => {
  return communesProposantLaFormation.length > 0 ? (
    <div className="grid grid-flow-col justify-start gap-2 text-[--text-mention-grey]">
      <span
        aria-hidden="true"
        className="fr-icon-map-pin-2-fill fr-icon--sm"
      />
      <p className="mb-2 text-sm">
        {i18n.PAGE_FORMATION.VILLES_PROPOSANT_FORMATION} {communesProposantLaFormation.length}{" "}
        {i18n.PAGE_FORMATION.VILLES_PROPOSANT_FORMATION_SUITE}
        {explications && communesProposantLaFormation.length > 0 && (
          <>
            {" "}
            {i18n.PAGE_FORMATION.VILLES_PROPOSANT_FORMATION_SUITE_SI_CORRESPONDANCE}{" "}
            <strong>{explications.communes.map((commune) => commune.nom).join(" • ")}</strong>
          </>
        )}
      </p>
    </div>
  ) : null;
};

export default NombreVillesDontFavorites;
