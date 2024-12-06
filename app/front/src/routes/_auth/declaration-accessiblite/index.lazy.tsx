import DéclarationAccessibilitéPage from "@/features/pagesObligatoires/ui/DéclarationAccessibilitéPage/DéclarationAccessibilitéPage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_auth/declaration-accessiblite/")({
  component: DéclarationAccessibilitéPage,
});
