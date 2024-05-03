import { type BoutonRadioProps } from "./BoutonRadio.interface";
import { useId } from "react";

const BoutonRadio = ({ légende, description, options, status, registerHookForm }: BoutonRadioProps) => {
  const id = useId();
  const légendeId = useId();

  const estEnColonne = options.length <= 2;

  const classEnFonctionDuStatus = () => {
    if (status?.type === "erreur") return { fieldset: "fr-fieldset--error", message: "fr-message--error" };
    if (status?.type === "succès") return { fieldset: "fr-fieldset--valid", message: "fr-message--valid" };

    return { fieldset: "", message: "" };
  };

  return (
    <fieldset
      aria-labelledby={`${légendeId} radio-messages`}
      className={`fr-fieldset ${classEnFonctionDuStatus().fieldset} !mb-0`}
      id={id}
      role="group"
    >
      <legend
        className="fr-fieldset__legend--regular fr-fieldset__legend"
        id={légendeId}
      >
        {légende}
        {description && <span className="fr-hint-text">{description}</span>}
      </legend>
      <div className="fr-grid-row">
        {options.map((option) => (
          <div
            className={`fr-fieldset__element ${estEnColonne ? "fr-fieldset__element--inline" : ""}`}
            key={option.label}
          >
            <div className="fr-radio-group">
              <input
                disabled={status?.type === "désactivé"}
                id={option.valeur}
                name={id}
                type="radio"
                value={option.valeur}
                {...registerHookForm}
              />
              <label
                className="fr-label"
                htmlFor={option.valeur}
              >
                {option.label}
                {option.description && <span className="fr-hint-text">{option.description}</span>}
              </label>
            </div>
          </div>
        ))}
      </div>
      <div
        aria-live="assertive"
        className="fr-messages-group"
        id="radio-status-messages"
      >
        {status && ["erreur", "succès"].includes(status.type) && (
          <p
            className={`fr-message ${classEnFonctionDuStatus().message}`}
            id="radio-status-message"
          >
            {status.message}
          </p>
        )}
      </div>
    </fieldset>
  );
};

export default BoutonRadio;
