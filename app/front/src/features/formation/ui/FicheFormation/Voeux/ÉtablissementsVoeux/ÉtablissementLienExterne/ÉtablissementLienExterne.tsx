import BoutonSquelette from "@/components/BoutonSquelette/BoutonSquelette.tsx";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import { ÉtablissementLienExterneProps } from "@/features/formation/ui/FicheFormation/Voeux/ÉtablissementsVoeux/ÉtablissementLienExterne/ÉtablissementLienExterne.interface";
import { Toggle } from "@radix-ui/react-toggle";

const ÉtablissementLienExterne = ({ établissement, mettreÀJourUnVoeu, estFavoris }: ÉtablissementLienExterneProps) => {
  return (
    <>
      <div>
        <LienExterne
          ariaLabel={établissement.nom}
          href={établissement.urlParcoursup}
          taille="petit"
          variante="simple"
        >
          {établissement.nom}
        </LienExterne>
      </div>
      <Toggle
        aria-label="Voeu favoris"
        onPressedChange={() => mettreÀJourUnVoeu(établissement.id)}
        pressed={estFavoris}
      >
        <BoutonSquelette
          aria-hidden="true"
          icône={{
            classe: estFavoris ? "fr-icon-heart-fill" : "fr-icon-heart-line",
          }}
          label="Voeu favoris"
          taille="petit"
          variante="tertiaire"
        />
      </Toggle>
    </>
  );
};

export default ÉtablissementLienExterne;
