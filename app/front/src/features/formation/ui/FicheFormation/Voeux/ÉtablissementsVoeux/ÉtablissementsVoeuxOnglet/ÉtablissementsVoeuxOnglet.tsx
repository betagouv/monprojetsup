import { type ÉtablissementsVoeuxOngletProps } from "./ÉtablissementsVoeuxOnglet.interface";
import TagFiltre from "@/components/TagFiltre/TagFiltre";
import { i18n } from "@/configuration/i18n/i18n";

const ÉtablissementsVoeuxOnglet = ({ établissements }: ÉtablissementsVoeuxOngletProps) => {
  if (établissements.length === 0) return i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.AUCUN_ÉTABLISSEMENT_À_PROXIMITÉ;

  return (
    <ul className="m-0 flex list-none flex-wrap justify-start gap-4 p-0">
      {établissements.map((établissement) => (
        <li key={établissement.id}>
          <TagFiltre
            appuyéParDéfaut={false}
            auClic={(_estAppuyé) => {}}
            libellé={établissement.nom}
          />
        </li>
      ))}
    </ul>
  );
};

export default ÉtablissementsVoeuxOnglet;
