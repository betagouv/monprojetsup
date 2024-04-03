import { type CaseÀCocherProps } from "./CaseÀCocher.interface";
import { useId } from "react";

const CaseÀCocher = ({ légende, description, options, status, registerHookForm }: CaseÀCocherProps) => {
  const id = useId();

  const classEnFonctionDuStatus = () => {
    if (status?.type === "erreur") return { fieldset: "fr-fieldset--error", message: "fr-message--error" };
    if (status?.type === "succès") return { fieldset: "fr-fieldset--valid", message: "fr-message--valid" };

    return { fieldset: "", message: "" };
  };

  return (
    <fieldset
      aria-labelledby={`${id}-legend ${id}-messages`}
      className={`fr-fieldset ${classEnFonctionDuStatus().fieldset}`}
      id={id}
      role="group"
    >
      <legend
        className="fr-fieldset__legend--regular fr-fieldset__legend"
        id={`${id}-legend`}
      >
        {légende}
        {description && <span className="fr-hint-text">{description}</span>}
      </legend>
      {options.map((option) => (
        <div
          className="fr-fieldset__element fr-fieldset__element--inline"
          key={option.label}
        >
          <div className="fr-checkbox-group">
            <input
              aria-describedby={`${option.valeur}-messages`}
              disabled={status?.type === "désactivé"}
              id={option.valeur}
              name={option.valeur}
              type="checkbox"
              {...registerHookForm}
            />
            <label
              className="fr-label"
              htmlFor={option.valeur}
            >
              {option.label}
              {option.description && <span className="fr-hint-text">{option.description}</span>}
            </label>
            <div
              aria-live="assertive"
              className="fr-messages-group"
              id={`${option.valeur}-messages`}
            />
          </div>
        </div>
      ))}
      <div
        aria-live="assertive"
        className="fr-messages-group"
        id={`${id}-messages`}
      >
        {status && ["erreur", "succès"].includes(status.type) && (
          <p
            className={`fr-message ${classEnFonctionDuStatus().message}`}
            id="radio-rich-status-message"
          >
            {status.message}
          </p>
        )}
      </div>
    </fieldset>
  );
};

export default CaseÀCocher;
