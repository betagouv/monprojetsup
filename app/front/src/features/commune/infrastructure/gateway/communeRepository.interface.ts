import { type Commune } from "@/features/commune/domain/commune.interface";

export type CommuneRepository = {
  rechercher: (recherche: string) => Promise<Commune[] | Error>;
};
