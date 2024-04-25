export type DomaineProfessionnel = {
  id: string;
  nom: string;
};

export type CatégorieDomainesProfessionnels = {
  nom: string;
  emoji: string;
  domainesProfessionnels: DomaineProfessionnel[];
};
