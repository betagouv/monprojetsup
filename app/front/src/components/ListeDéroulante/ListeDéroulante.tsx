import { type ListeDéroulanteProps } from "./ListeDéroulante.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { useId } from "react";

const ListeDéroulante = ({
  label,
  description,
  options,
  status,
  obligatoire = false,
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
    <div className={`fr-select-group ${classEnFonctionDuStatus().selectGroup} !mb-0`}>
      <label
        className="fr-label"
        htmlFor={`select-${id}`}
      >
        {label} {obligatoire && <span className="text-[--artwork-minor-red-marianne]">*</span>}
        {description && <span className="fr-hint-text">{description}</span>}
      </label>
      <select
        aria-describedby={`select-message-${id}`}
        className={`fr-select ${classEnFonctionDuStatus().select}`}
        defaultValue={valeurOptionSélectionnéeParDéfaut ?? ""}
        disabled={status?.type === "désactivé"}
        id={`select-${id}`}
        required={obligatoire ?? false}
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
      <div id={`select-message-${id}`}>
        {status && ["erreur", "succès"].includes(status.type) && (
          <p className={classEnFonctionDuStatus().message}>{status.message}</p>
        )}
      </div>
    </div>
  );
};

export default ListeDéroulante;
