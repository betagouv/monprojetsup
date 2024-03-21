import ArticlesError from "@/features/article/ui/ArticlesError/ArticlesError";
import { articlesQueryOptions } from "@/features/article/ui/options";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/articles/")({
  loader: ({ context: { queryClient } }) => queryClient.ensureQueryData(articlesQueryOptions),
  errorComponent: ArticlesError,
});
