import { type ListeFormationsProps } from "./ListeFormations.interface";
import CarteFormation from "@/features/formation/ui/CarteFormation/CarteFormation";

const ListeFormations = ({ formations }: ListeFormationsProps) => {
  return (
    <ul className="m-0 grid list-none gap-6">
      {formations.map((formation) => (
        <li key={formation.id}>
          <CarteFormation
            affinité={formation.affinité}
            communes={formation.communes}
            key={formation.id}
            métiersAccessibles={formation.métiersAccessibles}
            nom={formation.nom}
            sélectionnée={false}
          />
        </li>
      ))}
    </ul>
  );
};

export default ListeFormations;
