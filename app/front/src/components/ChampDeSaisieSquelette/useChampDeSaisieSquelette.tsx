import { type useChampDeSaisieSqueletteArgs } from "./ChampDeSaisieSquelette.interface";

export default function useChampDeSaisieSquelette({
  id,
  status,
  obligatoire,
  placeholder,
  auChangement,
}: useChampDeSaisieSqueletteArgs) {
  const classesCSSEnFonctionDuStatus = () => {
    if (status?.type === "erreur")
      return { input: "fr-input--error", message: "fr-error-text", inputGroupe: "fr-input-group--error" };
    if (status?.type === "succès")
      return { input: "fr-input--valid", message: "fr-valid-text", inputGroupe: "fr-input-group--valid" };
    if (status?.type === "désactivé") return { input: "", message: "", inputGroupe: "fr-input-group--disabled" };

    return { input: "", message: "", inputGroupe: "" };
  };

  const propsInput = {
    "aria-describedby": `status-${id}`,
    className: `fr-input ${classesCSSEnFonctionDuStatus().input}`,
    disabled: status?.type === "désactivé",
    id,
    name: id,
    onChange: auChangement,
    required: obligatoire ?? false,
    placeholder,
  };

  return {
    classesCSS: classesCSSEnFonctionDuStatus(),
    propsInput,
  };
}
