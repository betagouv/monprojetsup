import { type CatégorieDomainesProfessionnels } from "@/features/domaineProfessionnel/domain/domaineProfessionnel.interface";
import { type DomaineProfessionnelRepository } from "@/features/domaineProfessionnel/infrastructure/domaineProfessionnelRepository.interface";

export class RécupérerDomainesProfessionnelsGroupésParCatégorieUseCase {
  public constructor(private readonly _domaineProfessionnelRepository: DomaineProfessionnelRepository) {}

  public async run(): Promise<CatégorieDomainesProfessionnels[] | undefined> {
    return await this._domaineProfessionnelRepository.récupérerTousGroupésParCatégorie();
  }
}
