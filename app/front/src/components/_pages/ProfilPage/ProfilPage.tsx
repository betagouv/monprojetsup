import ProfilÉlève from "@/features/élève/ui/profil/ProfilÉlève/ProfilÉlèvePage";
import useUtilisateur from "@/features/utilisateur/ui/hooks/useUtilisateur/useUtilisateur";

const ProfilPage = () => {
  const utilisateur = useUtilisateur();

  if (utilisateur.type === "enseignant") return "Profil enseignant";
  if (utilisateur.type === "élève") return <ProfilÉlève />;

  return null;
};

export default ProfilPage;
