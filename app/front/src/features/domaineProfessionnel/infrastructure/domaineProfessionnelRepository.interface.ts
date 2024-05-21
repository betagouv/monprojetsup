import { type CatégorieDomainesProfessionnels } from "@/features/domaineProfessionnel/domain/domaineProfessionnel.interface";

export type DomaineProfessionnelRepository = {
  récupérerTousGroupésParCatégorie: () => Promise<CatégorieDomainesProfessionnels[] | undefined>;
};
