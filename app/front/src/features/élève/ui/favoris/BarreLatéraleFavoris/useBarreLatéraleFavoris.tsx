import { UseBarreLatéraleFavorisArgs } from "./BarreLatéraleFavoris.interface";
import {
  actionsListeEtAperçuStore,
  élémentAffichéListeEtAperçuStore,
} from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";

export default function useBarreLatéraleFavoris({ métiers, formations }: UseBarreLatéraleFavorisArgs) {
  const élémentAffiché = élémentAffichéListeEtAperçuStore();
  const { changerÉlémentAffiché } = actionsListeEtAperçuStore();

  const auChangementDeCatégorie = (catégorieSélectionnée: "formations" | "métiers") => {
    if (catégorieSélectionnée === "formations") {
      changerÉlémentAffiché({ id: formations?.[0]?.id ?? null, type: "formation" });
    } else {
      changerÉlémentAffiché({ id: métiers?.[0]?.id ?? null, type: "métier" });
    }
  };

  return {
    élémentAffiché,
    auChangementDeCatégorie,
  };
}
