import { type IndicateurÉtapesProps } from "./IndicateurÉtapes.interface";
import { useEffect, useState } from "react";

const IndicateurÉtapes = ({ étapes, indexÉtapeActuelle }: IndicateurÉtapesProps) => {
  const [indexÉtapeCourante, setIndexÉtapeCourante] = useState(indexÉtapeActuelle);
  const [indexÉtapeSuivante, setIndexÉtapeSuivante] = useState(indexÉtapeActuelle + 1);

  const nombreÉtapes = étapes.length;

  useEffect(() => {
    setIndexÉtapeCourante(indexÉtapeActuelle);
    setIndexÉtapeSuivante(indexÉtapeActuelle + 1);
  }, [indexÉtapeActuelle]);

  return (
    <div className="fr-stepper fr-mb-0">
      <p className="fr-stepper__title">
        {étapes[indexÉtapeCourante]}{" "}
        <span className="fr-stepper__state">
          Étape {indexÉtapeActuelle + 1} sur {nombreÉtapes}
        </span>{" "}
      </p>{" "}
      <div
        className="fr-stepper__steps"
        data-fr-current-step={indexÉtapeActuelle + 1}
        data-fr-steps={nombreÉtapes}
      />
      {indexÉtapeSuivante < nombreÉtapes && (
        <p className="fr-stepper__details">
          <span className="fr-text--bold">Étape suivante :</span> {étapes[indexÉtapeSuivante]}
        </p>
      )}
    </div>
  );
};

export default IndicateurÉtapes;
