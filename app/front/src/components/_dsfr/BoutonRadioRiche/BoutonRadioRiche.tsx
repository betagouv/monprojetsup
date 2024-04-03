import { type BoutonRadioRicheProps } from "./BoutonRadioRiche.interface";
import { useId } from "react";

const BoutonRadioRiche = ({ légende, description, options, status, registerHookForm }: BoutonRadioRicheProps) => {
  const id = useId();

  const estEnColonne = options.length <= 2;

  const classEnFonctionDuStatus = () => {
    if (status?.type === "erreur") return { fieldset: "fr-fieldset--error", message: "fr-message--error" };
    if (status?.type === "succès") return { fieldset: "fr-fieldset--valid", message: "fr-message--valid" };

    return { fieldset: "", message: "" };
  };

  return (
    <fieldset
      aria-labelledby="radio-rich-legend radio-rich-messages"
      className={`fr-fieldset ${classEnFonctionDuStatus().fieldset}`}
      id={id}
      role="group"
    >
      <legend
        className="fr-fieldset__legend--regular fr-fieldset__legend"
        id="radio-rich-legend"
      >
        {légende}
        {description && <span className="fr-hint-text">{description}</span>}
      </legend>
      <div className="fr-grid-row">
        {options.map((option) => (
          <div
            className={`fr-fieldset__element ${estEnColonne ? "fr-fieldset__element--inline fr-col-6" : "fr-col-12"}`}
            key={option.label}
          >
            <div className={`fr-radio-group fr-radio-rich ${estEnColonne ? "h-full" : ""}`}>
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
              <div
                aria-hidden="true"
                className="fr-radio-rich__img fr-h3 fr-mb-0"
              >
                {option.pictogramme}
              </div>
            </div>
          </div>
        ))}
      </div>
      <div
        aria-live="assertive"
        className="fr-messages-group"
        id="radio-rich-status-messages"
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

export default BoutonRadioRiche;
