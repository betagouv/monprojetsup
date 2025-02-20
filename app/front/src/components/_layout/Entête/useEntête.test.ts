import useEntête from "./useEntête";
import { i18n } from "@/configuration/i18n/i18n";
import useUtilisateur from "@/features/utilisateur/ui/useUtilisateur";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { renderHook } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";

vi.mock("@/features/utilisateur/ui/useUtilisateur", () => ({
  default: vi.fn(),
}));

const utilisateurNonConnecté = {
  id: undefined,
  seDéconnecter: vi.fn(),
  prénom: undefined,
  nom: undefined,
  email: undefined,
  estExpert: false,
  estAuthentifié: false,
};

const utilisateurConnecté = {
  ...utilisateurNonConnecté,
  id: "123",
  prénom: "Jean",
  nom: "Dupont",
  estAuthentifié: true,
};

describe("useEntête", () => {
  const queryClient = new QueryClient();
  const wrapper = ({ children }: { children: React.ReactNode }) =>
    QueryClientProvider({ client: queryClient, children });

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("Lorsque l'utilisateur n'est pas connecté", () => {
    beforeAll(() => {
      vi.mocked(useUtilisateur).mockReturnValue(utilisateurNonConnecté);
    });

    it("affiche un seul lien d'accès rapide pour se connecter", () => {
      // WHEN
      const { result } = renderHook(() => useEntête(), {
        wrapper,
      });

      // THEN
      expect(result.current.accèsRapides).toEqual([
        {
          iconId: "fr-icon-arrow-go-back-fill",
          linkProps: {
            href: process.env.VITE_AVENIRS_URL,
            className: "after:!content-none",
          },
          text: i18n.ENTÊTE.PLATEFORME_AVENIRS,
        },
        {
          iconId: "fr-icon-user-fill",
          linkProps: { to: "/connexion" },
          text: i18n.ENTÊTE.SE_CONNECTER,
        },
      ]);
    });
  });

  describe("Lorsque l'utilisateur est connecté sur d'autres pages", () => {
    beforeAll(() => {
      vi.mocked(useUtilisateur).mockReturnValue(utilisateurConnecté);
    });

    it("affiche tous les liens d'accès rapide", () => {
      // WHEN
      const { result } = renderHook(() => useEntête(), {
        wrapper,
      });

      // THEN
      const expectedAccèsRapides = [
        {
          text: i18n.PAGE_PROFIL.SE_DÉCONNECTER,
        },
        {
          text: i18n.ENTÊTE.PLATEFORME_AVENIRS,
        },
        {
          text: "Jean Dupont",
        },
      ];

      expect(result.current.accèsRapides).toBeDefined();
      if (result.current.accèsRapides) {
        for (const [index, accès] of result.current.accèsRapides.entries()) {
          expect(accès).toHaveProperty("text", expectedAccèsRapides[index].text);
        }
      }
    });

    it("affiche le menu de navigation", () => {
      // WHEN
      const { result } = renderHook(() => useEntête(), {
        wrapper,
      });

      // THEN
      expect(result.current.navigation).toEqual([
        { text: i18n.NAVIGATION.TABLEAU_DE_BORD, linkProps: { to: "/" } },
        { text: i18n.NAVIGATION.FORMATIONS, linkProps: { to: "/formations" } },
        { text: i18n.NAVIGATION.FAVORIS, className: "hidden", linkProps: { to: "/favoris" } },
        { text: i18n.NAVIGATION.PROFIL, className: "hidden", linkProps: { to: "/profil" } },
        { text: i18n.NAVIGATION.PROFIL, className: "", linkProps: { to: "/eleve/inscription/projet" } },
      ]);
    });
  });
});
