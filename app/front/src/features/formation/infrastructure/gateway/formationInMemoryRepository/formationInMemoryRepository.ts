import { FormationBuilder } from "@/features/formation/domain/formation.builder";
import { type Formation } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";

export class formationInMemoryRepository implements FormationRepository {
  public async récupérerToutes(): Promise<Formation[] | undefined> {
    const formation1 = new FormationBuilder()
      .avecId("1")
      .avecNom("Nom de la formation A assez long pour tester")
      .avecDescriptifs({
        général: "Description générale A",
        spécialités: "Description spécialités A",
        attendu: "Prérequis A",
      })
      .avecUrls(["https://formationa.com"])
      .avecMétiersAccessibles([
        {
          id: "123",
          nom: "Métier A1",
        },
        {
          id: "1234",
          nom: "Métier A2",
        },
        {
          id: "1235",
          nom: "Métier A3",
        },
        {
          id: "1236",
          nom: "Métier A4",
        },
        {
          id: "1237",
          nom: "Métier A5",
        },
      ])
      .avecAffinité(0.8)
      .construire();

    const formation2 = new FormationBuilder()
      .avecId("2")
      .avecNom("Formation B")
      .avecDescriptifs({
        général: "Description générale B",
        spécialités: "Description spécialités B",
        attendu: "Prérequis B",
      })
      .avecUrls(["https://formationb.com"])
      .avecMétiersAccessibles([
        {
          id: "123",
          nom: "Métier B1",
        },
        {
          id: "1234",
          nom: "Métier B2",
        },
      ])
      .avecAffinité(0.6)
      .construire();

    const formation3 = new FormationBuilder().avecId("3").avecNom("Formation C").avecUrls([]).construire();

    return [formation1, formation2, formation3];
  }
}
