export type Formation = {
  id: string;
  nom: string;
  descriptifs: {
    général: string | null;
    spécialités: string | null;
    attendu: string | null;
  };
  urls: string[];
  métiersAccessibles: Array<{
    id: string;
    nom: string;
  }>;
  affinité?: number;
};
