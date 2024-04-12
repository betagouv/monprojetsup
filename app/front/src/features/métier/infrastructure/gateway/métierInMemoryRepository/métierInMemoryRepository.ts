import { MétierBuilder } from "@/features/métier/domain/métier.builder";
import { type Métier } from "@/features/métier/domain/métier.interface";
import { type MétierRepository } from "@/features/métier/infrastructure/métierRepository.interface";

export class métierInMemoryRepository implements MétierRepository {
  public async récupérerTous(): Promise<Métier[] | undefined> {
    const métier1 = new MétierBuilder()
      .avecId("1")
      .avecNom("Métier A")
      .avecDescriptif("Description générale A")
      .avecUrls(["https://métiera.com"])
      .avecFormations([
        {
          id: "123",
          nom: "Formation A1",
        },
        {
          id: "1234",
          nom: "Formation A2",
        },
      ])
      .construire();

    const métier2 = new MétierBuilder().avecId("2").avecNom("Métier C").avecUrls([]).construire();

    return [métier1, métier2];
  }
}
