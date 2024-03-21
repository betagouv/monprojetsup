import ArticlesPage from "@/features/article/ui/ArticlesPage/ArticlesPage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/articles/")({ component: ArticlesPage });
