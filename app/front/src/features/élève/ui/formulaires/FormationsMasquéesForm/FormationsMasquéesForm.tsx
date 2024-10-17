import useFormationsMasquéesForm from "./useFormationsMasquéesForm.ts";
import FormationMasquée from "@/features/formation/ui/FormationMasquée/FormationMasquée";

const FormationsMasquéesForm = () => {
  const formationsMasquées = useFormationsMasquéesForm();

  if (formationsMasquées === undefined) return null;

  return (
    <ul className="m-0 grid list-none gap-8 p-0">
      {formationsMasquées.map((formation) => (
        <li key={formation.id}>
          <hr className="p-4" />
          <FormationMasquée formation={formation} />
        </li>
      ))}
    </ul>
  );
};

export default FormationsMasquéesForm;
