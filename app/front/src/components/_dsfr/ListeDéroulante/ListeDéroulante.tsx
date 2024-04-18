import { type ListeDéroulanteProps } from "./ListeDéroulante.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { useId } from "react";

const ListeDéroulante = ({
  label,
  description,
  options,
  status,
  registerHookForm,
  valeurOptionSélectionnéeParDéfaut,
}: ListeDéroulanteProps) => {
  const id = useId();

  const classEnFonctionDuStatus = () => {
    if (status?.type === "erreur")
      return { selectGroup: "fr-select-group--error", select: "fr-select--error", message: "fr-error-text" };
    if (status?.type === "succès")
      return { selectGroup: "fr-select-group--valid", select: "fr-select--valid", message: "fr-valid-text" };
    if (status?.type === "désactivé") return { selectGroup: "fr-select-group--disabled", select: "", message: "" };

    return { selectGroup: "", select: "", message: "" };
  };

  return (
    <div className={`fr-select-group ${classEnFonctionDuStatus().selectGroup}`}>
      <label
        className="fr-label"
        htmlFor={`select-${id}`}
      >
        {label}
        {description && <span className="fr-hint-text">{description}</span>}
      </label>
      <select
        aria-describedby={`select-message-${id}`}
        className={`fr-select ${classEnFonctionDuStatus().select}`}
        defaultValue={valeurOptionSélectionnéeParDéfaut ?? ""}
        disabled={status?.type === "désactivé"}
        id={`select-${id}`}
        {...registerHookForm}
      >
        <option
          disabled
          hidden
          value=""
        >
          {i18n.COMMUN.SÉLECTIONNER_OPTION}
        </option>
        {options.map((option) => (
          <option
            key={option.valeur}
            value={option.valeur}
          >
            {option.label}
          </option>
        ))}
      </select>
      {status && ["erreur", "succès"].includes(status.type) && (
        <p
          className={classEnFonctionDuStatus().message}
          id={`select-message-${id}`}
        >
          {status.message}
        </p>
      )}
    </div>
  );
};

export default ListeDéroulante;
