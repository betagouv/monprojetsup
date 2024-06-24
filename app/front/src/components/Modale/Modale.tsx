import { type ModaleProps } from "./Modale.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { createPortal } from "react-dom";

const Modale = ({ id, titre, boutons, children }: ModaleProps) => {
  return (
    <>
      {createPortal(
        <dialog
          aria-labelledby={`modale-titre-${id}`}
          className="fr-modal"
          id={id}
          role="dialog"
        >
          <div className="fr-container fr-container--fluid fr-container-md">
            <div className="fr-grid-row fr-grid-row--center">
              <div className="fr-col-12 fr-col-md-8 fr-col-lg-6">
                <div className="fr-modal__body">
                  <div className="fr-modal__header">
                    <button
                      aria-controls={id}
                      className="fr-btn--close fr-btn"
                      title={i18n.ACCESSIBILITÉ.FERMER_MODALE}
                      type="button"
                    >
                      {i18n.COMMUN.FERMER}
                    </button>
                  </div>
                  <div className="fr-modal__content">
                    <h1
                      className="fr-modal__title"
                      id={`modale-titre-${id}`}
                    >
                      {titre}
                    </h1>
                    {children}
                  </div>
                  {boutons && <div className="fr-modal__footer justify-start sm:justify-end">{boutons}</div>}
                </div>
              </div>
            </div>
          </div>
        </dialog>,
        document.body,
      )}
    </>
  );
};

export default Modale;
