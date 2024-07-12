import TableauDeBordÉlève from "@/features/élève/ui/tableauDeBord/TableauDeBordÉlève/TableauDeBordÉlève";
import useUtilisateur from "@/features/utilisateur/ui/hooks/useUtilisateur/useUtilisateur";

const TableauDeBordPage = () => {
  const utilisateur = useUtilisateur();

  if (utilisateur.type === "enseignant") return "Tableau de bord enseignant";
  if (utilisateur.type === "élève") return <TableauDeBordÉlève />;

  return null;
};

export default TableauDeBordPage;
