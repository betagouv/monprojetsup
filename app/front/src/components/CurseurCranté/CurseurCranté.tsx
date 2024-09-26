import { type CurseurCrantéProps } from "./CurseurCranté.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { useId } from "react";

const CurseurCranté = ({
  label,
  description,
  valeurMax,
  valeurMin,
  valeurParDéfaut,
  pas = 1,
  status,
  registerHookForm,
  auClicSurNeVeutPasRépondre,
  neVeutPasRépondre = false,
}: CurseurCrantéProps) => {
  const id = useId();

  const classEnFonctionDuStatus = () => {
    if (neVeutPasRépondre) return { rangeGroup: "fr-range-group--disabled", message: "" };

    if (status?.type === "erreur") return { rangeGroup: "fr-range-group--error", message: "fr-message--error" };
    if (status?.type === "succès") return { rangeGroup: "fr-range-group--valid", message: "fr-message--valid" };

    return { rangeGroup: "", message: "" };
  };

  return (
    <div>
      <div
        className={`fr-range-group ${classEnFonctionDuStatus().rangeGroup}`}
        id={`range-${id}-group`}
      >
        <label
          className="fr-label"
          htmlFor={id}
        >
          {label}
          {description && <span className="fr-hint-text">{description}</span>}
        </label>
        <div className="fr-range fr-range--step">
          <span className="fr-range__output">{neVeutPasRépondre ? valeurMin : valeurParDéfaut}</span>
          <input
            aria-describedby={`range-${id}-messages`}
            defaultValue={neVeutPasRépondre ? valeurMin : valeurParDéfaut}
            disabled={neVeutPasRépondre}
            id={id}
            max={valeurMax}
            min={valeurMin}
            name={id}
            step={pas}
            type="range"
            {...registerHookForm}
          />
          <span
            aria-hidden="true"
            className="fr-range__min"
          >
            {valeurMin}
          </span>
          <span
            aria-hidden="true"
            className="fr-range__max"
          >
            {valeurMax}
          </span>
        </div>

        <div
          aria-live="polite"
          className="fr-messages-group"
          id={`range-${id}-messages`}
        >
          {status && ["erreur", "succès"].includes(status.type) && (
            <p className={`fr-message ${classEnFonctionDuStatus().message}`}>{status.message}</p>
          )}
        </div>
      </div>
      <div className="fr-checkbox-group fr-checkbox-group--sm mt-4">
        <input
          checked={neVeutPasRépondre}
          id={`${id}-nsp`}
          name={`${id}-nsp`}
          onChange={() => {
            auClicSurNeVeutPasRépondre(!neVeutPasRépondre);
          }}
          type="checkbox"
        />
        <label
          className="fr-label"
          htmlFor={`${id}-nsp`}
        >
          {i18n.COMMUN.NE_VEUT_PAS_RÉPONDRE}
        </label>
      </div>
    </div>
  );
};

export default CurseurCranté;
