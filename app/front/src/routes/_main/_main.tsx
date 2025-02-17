import { queryÉlèveKeys } from '@/features/élève/ui/élèveQueries'
import { createFileRoute } from '@tanstack/react-router'
import { type QueryClient } from '@tanstack/react-query'
import { référentielDonnéesQueryOptions } from '@/features/référentielDonnées/ui/référentielDonnéesQueries'
import { z } from 'zod'
import MainLayout from '@/components/_layout/MainLayout/MainLayout'

const tableauDeBordSearchSchema = z.object({
  associationPS: z.enum(['ok', 'erreur']).optional(),
})

const chargerDonnées = async (queryClient: QueryClient) => {
  await queryClient.ensureQueryData(référentielDonnéesQueryOptions)
}

export const Route = createFileRoute('/_main/_main')({
  validateSearch: (searchParamètres) =>
    tableauDeBordSearchSchema.parse(searchParamètres),
  component: MainLayout,
  loader: async ({ context: { queryClient }, cause }) => {
    await chargerDonnées(queryClient)
    if (cause !== 'stay') {
      queryClient.removeQueries({ queryKey: [queryÉlèveKeys.PROGRESSION] })
    }
  },
})
