import { type BoutonRadioRicheProps } from "./BoutonRadioRiche.interface";
import { useId } from "react";

const BoutonRadioRiche = ({
  légende,
  description,
  options,
  status,
  obligatoire = false,
  registerHookForm,
}: BoutonRadioRicheProps) => {
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
      className={`fr-fieldset ${classEnFonctionDuStatus().fieldset} !mb-0`}
      id={id}
      role="group"
    >
      <legend
        className="fr-fieldset__legend--regular fr-fieldset__legend"
        id="radio-rich-legend"
      >
        {légende} {obligatoire && <span className="text-[--artwork-minor-red-marianne]">*</span>}
        {description && <span className="fr-hint-text">{description}</span>}
      </legend>
      <div className="fr-grid-row">
        {options.map((option) => (
          <div
            className={`fr-fieldset__element ${estEnColonne ? "md:fr-col-6 fr-col-12 flex-1 basis-full md:basis-0" : "fr-col-12"}`}
            key={option.label}
          >
            <div className={`fr-radio-group fr-radio-rich ${estEnColonne ? "h-full" : ""}`}>
              <input
                disabled={status?.type === "désactivé"}
                id={option.valeur}
                name={id}
                required={obligatoire ?? false}
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
        id="radio-rich-messages"
      >
        <div id="radio-rich-message">
          {status && ["erreur", "succès"].includes(status.type) && (
            <p className={`fr-message ${classEnFonctionDuStatus().message}`}>{status.message}</p>
          )}
        </div>
      </div>
    </fieldset>
  );
};

export default BoutonRadioRiche;
