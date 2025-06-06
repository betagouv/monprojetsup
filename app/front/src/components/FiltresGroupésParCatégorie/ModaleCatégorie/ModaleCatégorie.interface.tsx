import { FiltresGroupésParCatégorieProps } from "@/components/FiltresGroupésParCatégorie/FiltresGroupésParCatégorie.interface.tsx";
import { createModal } from "@codegouvfr/react-dsfr/Modal";

export type ModaleCatégorieProps = {
  contenuModaleDétail: FiltresGroupésParCatégorieProps["catégories"][number]["filtres"];
  modale: ReturnType<typeof createModal>;
};
