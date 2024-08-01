import ProfilÉlève from "@/features/élève/ui/profil/ProfilÉlève/ProfilÉlève";
import ProfilEnseignant from "@/features/enseignant/ui/profil/ProfilEnseignant/ProfilEnseignant";
import useUtilisateur from "@/features/utilisateur/ui/hooks/useUtilisateur/useUtilisateur";

const ProfilPage = () => {
  const utilisateur = useUtilisateur();

  if (utilisateur.type === "enseignant") return <ProfilEnseignant />;
  if (utilisateur.type === "élève") return <ProfilÉlève />;

  return null;
};

export default ProfilPage;
