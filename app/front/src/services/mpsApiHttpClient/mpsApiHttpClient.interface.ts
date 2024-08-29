import { type paths } from "@/types/api-mps";

export type IMpsApiHttpClient = {
  get: <O extends {}>(endpoint: keyof paths, paramètresDeRequête?: URLSearchParams) => Promise<O | undefined>;
  post: <O extends {}>(endpoint: keyof paths, body: {}) => Promise<O | undefined>;
};
