/* eslint-disable @typescript-eslint/require-await */
import { type Commune } from "@/features/commune/domain/commune.interface";
import { type CommuneRepository } from "@/features/commune/infrastructure/communeRepository.interface";

export class communeInMemoryRepository implements CommuneRepository {
  private _COMMUNES: Commune[] = [
    {
      codeInsee: "67482",
      codePostal: "",
      nom: "Strasbourg",
      latitude: 48.579_831,
      longitude: 7.761_454,
    },
    {
      codeInsee: "59582",
      codePostal: "",
      nom: "Strazeele",
      latitude: 50.721_379,
      longitude: 2.627_294,
    },
    {
      codeInsee: "21231",
      codePostal: "",
      nom: "Dijon",
      latitude: 47.331_848,
      longitude: 5.033_601,
    },
    {
      codeInsee: "21278",
      codePostal: "",
      nom: "Fontaine-lès-Dijon",
      latitude: 47.344_925,
      longitude: 5.025_268,
    },
    {
      codeInsee: "21485",
      codePostal: "",
      nom: "Plombières-lès-Dijon",
      latitude: 47.339_834,
      longitude: 4.973_987,
    },
    {
      codeInsee: "21481",
      codePostal: "",
      nom: "Perrigny-lès-Dijon",
      latitude: 47.263_807,
      longitude: 5.007_331,
    },
    {
      codeInsee: "21605",
      codePostal: "",
      nom: "Sennecey-lès-Dijon",
      latitude: 47.292_861,
      longitude: 5.105_431,
    },
    {
      codeInsee: "21027",
      codePostal: "",
      nom: "Asnières-lès-Dijon",
      latitude: 47.381_884,
      longitude: 5.043_378,
    },
    {
      codeInsee: "21315",
      codePostal: "",
      nom: "Hauteville-lès-Dijon",
      latitude: 47.366_121,
      longitude: 4.995_572,
    },
    {
      codeInsee: "68330",
      codePostal: "",
      nom: "Strueth",
      latitude: 47.582_753,
      longitude: 7.125_181,
    },
    {
      codeInsee: "46312",
      codePostal: "",
      nom: "Strenquels",
      latitude: 44.977_331,
      longitude: 1.635_43,
    },
    {
      codeInsee: "67483",
      codePostal: "",
      nom: "Struth",
      latitude: 48.891_225,
      longitude: 7.258_887,
    },
  ];

  public async rechercher(recherche: string): Promise<Commune[] | Error> {
    return this._COMMUNES.filter((commune) => commune.nom.toLowerCase().includes(recherche.toLowerCase()));
  }
}
