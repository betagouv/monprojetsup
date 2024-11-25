import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import { ÉtablissementLienExterneProps } from "@/features/formation/ui/FicheFormation/Voeux/ÉtablissementsVoeux/ÉtablissementsVoeuxOnglet/ÉtablissementLienExterne/ÉtablissementLienExterne.interface";
import { Toggle } from "@radix-ui/react-toggle";

const ÉtablissementLienExterne = ({ établissement, mettreÀJourUnVoeu, estFavoris }: ÉtablissementLienExterneProps) => {
  return (
    <>
      <LienExterne
        ariaLabel={établissement.nom}
        href={établissement.urlParcoursup}
        taille="petit"
        variante="simple"
      >
        {établissement.nom}
      </LienExterne>
      <Toggle
        onPressedChange={() => mettreÀJourUnVoeu(établissement.id)}
        pressed={estFavoris}
      >
        <span
          aria-hidden="true"
          className={
            estFavoris
              ? "fr-icon-heart-fill fr-icon--sm px-1 text-[--artwork-major-blue-france]"
              : "fr-icon-heart-line fr-icon--sm px-1 text-[--artwork-major-blue-france]"
          }
        />
      </Toggle>
    </>
  );
};

export default ÉtablissementLienExterne;
