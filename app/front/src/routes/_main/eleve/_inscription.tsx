import LayoutInscriptionÉlève from '@/features/élève/ui/ParcoursInscriptionÉlève/LayoutInscriptionÉlève/LayoutInscriptionÉlève'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_main/eleve/_inscription')({
  component: LayoutInscriptionÉlève,
})
