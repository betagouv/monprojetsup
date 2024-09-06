import { type ListeFormationsProps } from "./ListeFormations.interface";
import CarteFormation from "@/features/formation/ui/CarteFormation/CarteFormation";

const ListeFormations = ({ formations, formationIdAffichée }: ListeFormationsProps) => {
  return (
    <ul className="m-0 grid h-full list-none justify-center gap-6 px-2 pb-6 lg:overflow-y-auto xl:px-7">
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
  );
};

export default ListeFormations;
