import CookiesPage from '@/features/pagesObligatoires/ui/CookiesPage/CookiesPage'
import { createLazyFileRoute } from '@tanstack/react-router'

export const Route = createLazyFileRoute('/_public/cookies/')({
  component: CookiesPage,
})
