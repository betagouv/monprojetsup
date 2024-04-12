import { type Formation } from "@/features/formation/domain/formation.interface";

export type FormationRepository = {
  récupérerToutes: () => Promise<Formation[] | undefined>;
};
