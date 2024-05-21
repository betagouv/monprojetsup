export type VilleHTTP = {
  type: "Feature";
  geometry: {
    type: "Point";
    coordinates: [number, number];
  };
  properties: {
    label: string;
    score: number;
    id: string;
    type: "municipality";
    name: string;
    postcode: string;
    citycode: string;
    x: number;
    y: number;
    city: string;
    population: number;
    context: string;
    importance: number;
    municipality: string;
  };
};

export type RechercherVillesRéponseHTTP = {
  features: VilleHTTP[];
};
