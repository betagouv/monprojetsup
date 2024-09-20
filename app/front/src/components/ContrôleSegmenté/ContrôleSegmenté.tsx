import { type ContrôleSegmentéProps } from "./ContrôleSegmenté.interface";
import { useId } from "react";

const ContrôleSegmenté = ({ légende, éléments, auClic, valeurSélectionnéeParDéfaut }: ContrôleSegmentéProps) => {
  const id = useId();

  return (
    <fieldset className="fr-segmented fr-segmented--no-legend">
      <legend className="fr-segmented__legend">{légende}</legend>
      <div className="fr-segmented__elements">
        {éléments.map((élément) => (
          <div
            className="fr-segmented__element"
            key={élément.valeur}
          >
            <input
              defaultChecked={valeurSélectionnéeParDéfaut === élément.valeur}
              id={`${élément.valeur}-${id}`}
              name={id}
              onClick={() => auClic(élément.valeur)}
              type="radio"
              value={élément.valeur}
            />
            <label
              className="fr-label justify-center"
              htmlFor={`${élément.valeur}-${id}`}
            >
              {élément.label}
            </label>
          </div>
        ))}
      </div>
    </fieldset>
  );
};

export default ContrôleSegmenté;
