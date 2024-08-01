import { type paths } from "@/types/api-mps";

export type IMpsApiHttpClient = {
  get: <O extends {}>(endpoint: keyof paths, paramètresDeRequête?: string) => Promise<O | undefined>;
  post: <O extends {}>(endpoint: keyof paths, body: {}) => Promise<O | undefined>;
};
