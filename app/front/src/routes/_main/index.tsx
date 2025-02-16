import TableauDeBordÉlèvePage from '@/features/élève/ui/TableauDeBordÉlèvePage/TableauDeBordÉlèvePage'
import { createFileRoute } from '@tanstack/react-router'


export const Route = createFileRoute('/_main/')({
  component: TableauDeBordÉlèvePage,
})
