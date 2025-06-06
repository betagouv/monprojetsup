import { MétierSansFormationsAssociées } from "@/features/métier/domain/métier.interface";
import { createModal } from "@codegouvfr/react-dsfr/Modal";

export type ModaleMétierProps = {
  métier: MétierSansFormationsAssociées;
  modale: ReturnType<typeof createModal>;
};

export type UseModaleMétierArgs = {
  métier: ModaleMétierProps["métier"];
};
