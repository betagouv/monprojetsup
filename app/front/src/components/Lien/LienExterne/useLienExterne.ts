import { dépendances } from "@/configuration/dépendances/dépendances";

export default function UseLienExterne(url: string) {
  return dépendances.suivreLienExterne.run(url);
}
