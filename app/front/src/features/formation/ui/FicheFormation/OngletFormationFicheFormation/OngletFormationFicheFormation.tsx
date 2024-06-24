import { type OngletFormationFicheFormationProps } from "./OngletFormationFicheFormation.interface";
import ListeLiensExternesSousFormeBouton from "@/components/ListeLiensExternesSousFormeBouton/ListeLiensExternesSousFormeBouton";
import TexteTronqué from "@/components/TexteTronqué/TexteTronqué";

const OngletFormationFicheFormation = ({ texte, liens }: OngletFormationFicheFormationProps) => {
  return (
    <div className="grid gap-6">
      <TexteTronqué
        nombreDeLigneÀAfficher="4"
        texte={texte}
      />
      <ListeLiensExternesSousFormeBouton liens={liens} />
    </div>
  );
};

export default OngletFormationFicheFormation;
