import DéclarationAccessibilitéPage from "@/features/pages-pied-de-page/ui/DéclarationAccessibilitéPage/DéclarationAccessibilitéPage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_auth/declaration-accessiblite/")({
  component: DéclarationAccessibilitéPage,
});
