import { type paths } from "@/types/api-mps";

export type IMpsApiHttpClient = {
  get: <O extends object>(endpoint: keyof paths, paramètresDeRequête?: URLSearchParams) => Promise<O | undefined>;
  post: <O extends object>(endpoint: keyof paths, body: object) => Promise<O | undefined>;
};
