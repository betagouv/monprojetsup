import { type router } from "@/configuration/lib/tanstack-router";

export default interface FilArianeProps {
  libelléPageCourante: string;
  chemin: Array<{ nom: string; lien: keyof (typeof router)["routesByPath"] }>;
}
