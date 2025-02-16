import { type paths } from "@/types/api-mps";

export type IMpsApiHttpClient = {
  get: <O extends object>(endpoint: keyof paths, paramètresDeRequête?: URLSearchParams) => Promise<O | Error>;
  post: <O extends object>(endpoint: keyof paths, body: object) => Promise<O | Error>;
  estAuthentifié: () => boolean;
};
