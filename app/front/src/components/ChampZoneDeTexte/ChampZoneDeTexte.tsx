import { type ChampZoneDeTexteProps } from "./ChampZoneDeTexte.interface";
import ChampDeSaisieSquelette from "@/components/ChampDeSaisieSquelette/ChampDeSaisieSquelette";
import useChampDeSaisieSquelette from "@/components/ChampDeSaisieSquelette/useChampDeSaisieSquelette";
import { useId } from "react";
import TextareaAutosize from "react-textarea-autosize";

const ChampZoneDeTexte = ({
  entête,
  status,
  placeholder,
  auChangement,
  obligatoire,
  id,
  valeurParDéfaut,
}: ChampZoneDeTexteProps) => {
  const idGénéré = useId();

  const { propsInput } = useChampDeSaisieSquelette({
    id: id ?? idGénéré,
    status,
    obligatoire,
    placeholder,
    auChangement,
  });

  return (
    <ChampDeSaisieSquelette
      auChangement={auChangement}
      entête={entête}
      id={id ?? idGénéré}
      obligatoire={obligatoire}
      status={status}
    >
      <TextareaAutosize
        defaultValue={valeurParDéfaut}
        maxRows={6}
        minRows={1}
        {...propsInput}
      />
    </ChampDeSaisieSquelette>
  );
};

export default ChampZoneDeTexte;
