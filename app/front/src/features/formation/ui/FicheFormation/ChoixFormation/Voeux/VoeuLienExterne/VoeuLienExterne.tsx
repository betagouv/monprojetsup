import BoutonSquelette from "@/components/BoutonSquelette/BoutonSquelette.tsx";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import { VoeuLienExterneProps } from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/VoeuLienExterne/VoeuLienExterneProps";
import { Toggle } from "@radix-ui/react-toggle";

const VoeuLienExterne = ({ voeu, mettreÀJourUnVoeu, estFavoris }: VoeuLienExterneProps) => {
  return (
    <>
      <div>
        <LienExterne
          ariaLabel={voeu.nom}
          href={voeu.urlParcoursup}
          taille="petit"
          variante="simple"
        >
          {voeu.nom}
        </LienExterne>
      </div>
      <Toggle
        aria-label="Voeu favoris"
        onPressedChange={() => mettreÀJourUnVoeu(voeu.id)}
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

export default VoeuLienExterne;
