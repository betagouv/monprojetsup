import { type IndicateurÉtapesProps } from "./IndicateurÉtapes.interface";
import { useEffect, useState } from "react";

const IndicateurÉtapes = ({ étapes, étapeActuelle }: IndicateurÉtapesProps) => {
  const [indexÉtapeCourante, setIndexÉtapeCourante] = useState(étapeActuelle - 1);
  const [indexÉtapeSuivante, setIndexÉtapeSuivante] = useState(étapeActuelle);

  const nombreÉtapes = étapes.length;

  useEffect(() => {
    setIndexÉtapeCourante(étapeActuelle - 1);
    setIndexÉtapeSuivante(étapeActuelle);
  }, [étapeActuelle]);

  return (
    <div className="fr-stepper fr-mb-0">
      <h2 className="fr-stepper__title">
        {étapes[indexÉtapeCourante]}{" "}
        <span className="fr-stepper__state">
          Étape {étapeActuelle} sur {nombreÉtapes}
        </span>{" "}
      </h2>{" "}
      <div
        className="fr-stepper__steps"
        data-fr-current-step={étapeActuelle}
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
