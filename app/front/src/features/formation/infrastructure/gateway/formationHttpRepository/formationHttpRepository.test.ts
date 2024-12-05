import { formationHttpRepository } from "./formationHttpRepository";
import { RécupérerFichesFormationsRéponseHTTP } from "@/features/formation/infrastructure/gateway/formationHttpRepository/formationHttpRepository.interface";
import { IMpsApiHttpClient } from "@/services/mpsApiHttpClient/mpsApiHttpClient.interface";

type VoeuxParCommuneFavorites =
  RécupérerFichesFormationsRéponseHTTP["formations"][number]["formation"]["communesFavoritesAvecLeursVoeux"];

vitest.mock("@/configuration/dépendances/dépendances", () => ({
  dépendances: {
    logger: {
      consigner: vitest.fn(),
    },
  },
}));

const générerCommuneTestHelper = (id: string) => {
  return {
    codeInsee: `0000${id}`,
    nom: `Commune ${id}`,
    latitude: 123,
    longitude: 123,
  };
};

const générerVoeuTestHelper = (
  id: string,
  distanceEnKm: number,
  commune: VoeuxParCommuneFavorites[number]["commune"],
) => {
  return {
    voeu: {
      id: `idVoeu${id}-${commune.codeInsee}`,
      nom: `Voeu ${id}`,
      commune: {
        nom: commune.nom,
        codeInsee: commune.codeInsee,
      },
    },
    distanceKm: distanceEnKm,
  };
};

describe.each([
  { lien: "https://www.parcoursup.fr/lienverslaCarte", prefixAttendu: "?" },
  { lien: "https://www.parcoursup.fr/lienverslaCarte?search=fl10", prefixAttendu: "&" },
])("_générerLeLienParcourSupAvecCommunesFavorites", ({ lien, prefixAttendu }) => {
  const MpsApiHttpClientMocked = vi.fn() as unknown as IMpsApiHttpClient;

  describe("Si l'utilisateur n'a pas de commune favorite", () => {
    test("doit renvoyer le lien tel quel", () => {
      // GIVEN
      const voeuxParCommuneFavorites: VoeuxParCommuneFavorites = [];

      // WHEN
      const lienGénéré = new formationHttpRepository(MpsApiHttpClientMocked)[
        "_générerLeLienParcourSupAvecCommunesFavorites"
      ](lien, voeuxParCommuneFavorites);

      // THEN
      expect(lienGénéré).toBe(lien);
    });
  });

  describe("Si l'utilisateur a une seule commune favorite", () => {
    const communeA = générerCommuneTestHelper("A");
    const voeuA = générerVoeuTestHelper("A", 1, communeA);
    const voeuB = générerVoeuTestHelper("B", 2, communeA);
    const voeuC = générerVoeuTestHelper("C", 3, communeA);

    describe("Si la commune ne propose aucun voeu à proximité", () => {
      test("doit renvoyer le lien tel quel", () => {
        // GIVEN
        const voeuxParCommuneFavorites: VoeuxParCommuneFavorites = [
          {
            commune: communeA,
            voeuxAvecDistance: [],
          },
        ];

        // WHEN
        const lienGénéré = new formationHttpRepository(MpsApiHttpClientMocked)[
          "_générerLeLienParcourSupAvecCommunesFavorites"
        ](lien, voeuxParCommuneFavorites);

        // THEN
        expect(lienGénéré).toBe(lien);
      });
    });

    describe("Si la commune ne propose qu'un seul voeu à proximité", () => {
      test("doit renvoyer le lien avec comme search params center_on_interests avec l'id du voeu", () => {
        // GIVEN
        const voeuxParCommuneFavorites: VoeuxParCommuneFavorites = [
          {
            commune: communeA,
            voeuxAvecDistance: [voeuA],
          },
        ];

        // WHEN
        const lienGénéré = new formationHttpRepository(MpsApiHttpClientMocked)[
          "_générerLeLienParcourSupAvecCommunesFavorites"
        ](lien, voeuxParCommuneFavorites);

        // THEN
        expect(lienGénéré).toBe(`${lien}${prefixAttendu}center_on_interests=${voeuA.voeu.id}`);
      });
    });

    describe("Si la commune propose plus d'un voeu à proximité", () => {
      test("doit renvoyer le lien avec comme search params center_on_interests avec l'id du voeu le plus proche et le plus éloigné", () => {
        // GIVEN
        const voeuxParCommuneFavorites: VoeuxParCommuneFavorites = [
          {
            commune: communeA,
            voeuxAvecDistance: [voeuA, voeuB, voeuC],
          },
        ];

        // WHEN
        const lienGénéré = new formationHttpRepository(MpsApiHttpClientMocked)[
          "_générerLeLienParcourSupAvecCommunesFavorites"
        ](lien, voeuxParCommuneFavorites);

        // THEN
        expect(lienGénéré).toBe(`${lien}${prefixAttendu}center_on_interests=${voeuA.voeu.id},${voeuC.voeu.id}`);
      });
    });
  });

  describe("Si l'utilisateur a plus d'une commune favorite", () => {
    const communeA = générerCommuneTestHelper("A");
    const voeuADeLaCommuneA = générerVoeuTestHelper("A", 1, communeA);
    const voeuBDeLaCommuneA = générerVoeuTestHelper("B", 2, communeA);
    const voeuCDeLaCommuneA = générerVoeuTestHelper("C", 3, communeA);

    const communeB = générerCommuneTestHelper("B");
    const voeuADeLaCommuneB = générerVoeuTestHelper("A", 1, communeB);
    const voeuBDeLaCommuneB = générerVoeuTestHelper("B", 2, communeB);
    const voeuCDeLaCommuneB = générerVoeuTestHelper("C", 3, communeB);

    const communeC = générerCommuneTestHelper("C");
    const voeuADeLaCommuneC = générerVoeuTestHelper("A", 1, communeC);

    const communeD = générerCommuneTestHelper("D");

    describe("Si aucune des communes ne propose un voeu à proximité", () => {
      test("doit renvoyer le lien tel quel", () => {
        // GIVEN
        const voeuxParCommuneFavorites: VoeuxParCommuneFavorites = [
          {
            commune: communeA,
            voeuxAvecDistance: [],
          },
          {
            commune: communeB,
            voeuxAvecDistance: [],
          },
        ];

        // WHEN
        const lienGénéré = new formationHttpRepository(MpsApiHttpClientMocked)[
          "_générerLeLienParcourSupAvecCommunesFavorites"
        ](lien, voeuxParCommuneFavorites);

        // THEN
        expect(lienGénéré).toBe(lien);
      });
    });

    describe("Si une seule commune propose un seul voeu à proximité", () => {
      test("doit renvoyer le lien avec comme search params center_on_interests avec l'id du voeu", () => {
        // GIVEN
        const voeuxParCommuneFavorites: VoeuxParCommuneFavorites = [
          {
            commune: communeA,
            voeuxAvecDistance: [voeuADeLaCommuneA],
          },
          {
            commune: communeB,
            voeuxAvecDistance: [],
          },
        ];

        // WHEN
        const lienGénéré = new formationHttpRepository(MpsApiHttpClientMocked)[
          "_générerLeLienParcourSupAvecCommunesFavorites"
        ](lien, voeuxParCommuneFavorites);

        // THEN
        expect(lienGénéré).toBe(`${lien}${prefixAttendu}center_on_interests=${voeuADeLaCommuneA.voeu.id}`);
      });
    });

    describe("Si une seule commune propose plusieurs voeux à proximité", () => {
      test("doit renvoyer le lien avec comme search params center_on_interests avec l'id de l'voeu le plus proche et le plus éloigné", () => {
        // GIVEN
        const voeuxParCommuneFavorites: VoeuxParCommuneFavorites = [
          {
            commune: communeA,
            voeuxAvecDistance: [voeuADeLaCommuneA, voeuBDeLaCommuneA, voeuCDeLaCommuneA],
          },
          {
            commune: communeB,
            voeuxAvecDistance: [],
          },
        ];

        // WHEN
        const lienGénéré = new formationHttpRepository(MpsApiHttpClientMocked)[
          "_générerLeLienParcourSupAvecCommunesFavorites"
        ](lien, voeuxParCommuneFavorites);

        // THEN
        expect(lienGénéré).toBe(
          `${lien}${prefixAttendu}center_on_interests=${voeuADeLaCommuneA.voeu.id},${voeuCDeLaCommuneA.voeu.id}`,
        );
      });
    });

    describe("Si plusieurs communes proposent 0 ou plus voeux à proximité", () => {
      test("doit renvoyer le lien avec comme search params center_on_interests avec les ids des voeux les plus proches pour chacune des communes", () => {
        // GIVEN
        const voeuxParCommuneFavorites: VoeuxParCommuneFavorites = [
          {
            commune: communeA,
            voeuxAvecDistance: [voeuADeLaCommuneA, voeuBDeLaCommuneA, voeuCDeLaCommuneA],
          },
          {
            commune: communeB,
            voeuxAvecDistance: [voeuADeLaCommuneB, voeuBDeLaCommuneB, voeuCDeLaCommuneB],
          },
          {
            commune: communeC,
            voeuxAvecDistance: [voeuADeLaCommuneC],
          },
          {
            commune: communeD,
            voeuxAvecDistance: [],
          },
        ];

        // WHEN
        const lienGénéré = new formationHttpRepository(MpsApiHttpClientMocked)[
          "_générerLeLienParcourSupAvecCommunesFavorites"
        ](lien, voeuxParCommuneFavorites);

        // THEN
        expect(lienGénéré).toBe(
          `${lien}${prefixAttendu}center_on_interests=${voeuADeLaCommuneA.voeu.id},${voeuADeLaCommuneB.voeu.id},${voeuADeLaCommuneC.voeu.id}`,
        );
      });
    });

    describe("Si plusieurs communes proposent 0 ou plus voeux à proximité, mais qu'elles ont les mêmes id de voeux les plus proches", () => {
      test("doit renvoyer le lien avec comme search params center_on_interests avec les ids des voeux uniques les plus proches pour chacune des communes", () => {
        // GIVEN
        const voeuxParCommuneFavorites: VoeuxParCommuneFavorites = [
          {
            commune: communeA,
            voeuxAvecDistance: [voeuADeLaCommuneA, voeuBDeLaCommuneA, voeuCDeLaCommuneA],
          },
          {
            commune: communeB,
            voeuxAvecDistance: [voeuADeLaCommuneA, voeuADeLaCommuneB, voeuBDeLaCommuneB, voeuCDeLaCommuneB],
          },
          {
            commune: communeC,
            voeuxAvecDistance: [voeuADeLaCommuneA, voeuADeLaCommuneB, voeuADeLaCommuneC],
          },
          {
            commune: communeD,
            voeuxAvecDistance: [],
          },
        ];

        // WHEN
        const lienGénéré = new formationHttpRepository(MpsApiHttpClientMocked)[
          "_générerLeLienParcourSupAvecCommunesFavorites"
        ](lien, voeuxParCommuneFavorites);

        // THEN
        expect(lienGénéré).toBe(
          `${lien}${prefixAttendu}center_on_interests=${voeuADeLaCommuneA.voeu.id},${voeuADeLaCommuneB.voeu.id},${voeuADeLaCommuneC.voeu.id}`,
        );
      });
    });
  });
});
