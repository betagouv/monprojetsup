/* eslint-disable @typescript-eslint/no-unsafe-assignment */
import useEntête from "./useEntête";
import { constantes } from "@/configuration/constantes";
import { environnement } from "@/configuration/environnement.ts";
import { i18n } from "@/configuration/i18n/i18n";
import useUtilisateur from "@/features/utilisateur/ui/hooks/useUtilisateur/useUtilisateur";
import { useRouterState } from "@tanstack/react-router";
import { renderHook } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";

vi.mock("@tanstack/react-router", () => ({
  useRouterState: vi.fn(),
}));

vi.mock("@/features/utilisateur/ui/hooks/useUtilisateur/useUtilisateur", () => ({
  default: vi.fn(),
}));

const mockUseRouterState = (pathname: string) => ({
  location: {
    pathname,
    href: "",
    search: undefined,
    searchStr: "",
    state: {},
    hash: "",
  },
  status: "idle" as const,
  loadedAt: 0,
  isLoading: false,
  isTransitioning: false,
  matches: [],
  cachedMatches: [],
  resolvedLocation: {
    pathname,
    href: "",
    search: undefined,
    searchStr: "",
    state: {},
    hash: "",
  },
  statusCode: 0,
});

const utilisateurNonConnecté = {
  id: undefined,
  seDéconnecter: vi.fn(),
  prénom: undefined,
  nom: undefined,
  email: undefined,
  estExpert: false,
};

const utilisateurConnecté = {
  ...utilisateurNonConnecté,
  id: "123",
  prénom: "Jean",
  nom: "Dupont",
};

describe("useEntête", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("Lorsque l'utilisateur n'est pas connecté", () => {
    beforeAll(() => {
      vi.mocked(useRouterState).mockReturnValue(mockUseRouterState("/"));
      vi.mocked(useUtilisateur).mockResolvedValue(utilisateurNonConnecté);
    });

    it("affiche un seul lien d'accès rapide pour se connecter", () => {
      // WHEN
      const { result } = renderHook(() => useEntête());

      // THEN
      expect(result.current.accèsRapides).toEqual([
        {
          iconId: "fr-icon-user-fill",
          linkProps: { to: "/" },
          text: i18n.ENTÊTE.SE_CONNECTER,
        },
      ]);
    });

    it("n'affiche pas le menu de navigation", () => {
      // WHEN
      const { result } = renderHook(() => useEntête());

      // THEN
      expect(result.current.navigation).toBeNull();
    });
  });

  describe("Lorsque l'utilisateur est connecté sur une page du parcours d'inscription", () => {
    beforeAll(() => {
      vi.mocked(useRouterState).mockReturnValue(mockUseRouterState(constantes.ÉLÈVE.PATH_PARCOURS_INSCRIPTION));
      vi.mocked(useUtilisateur).mockReturnValue(utilisateurConnecté);
    });

    it("affiche un seul lien d'accès rapide pour se déconnecter", () => {
      // WHEN
      const { result } = renderHook(() => useEntête());

      // THEN
      expect(result.current.accèsRapides).toEqual([
        {
          iconId: "fr-icon-close-line",
          buttonProps: {
            onClick: expect.any(Function),
          },
          text: i18n.PAGE_PROFIL.SE_DÉCONNECTER,
        },
      ]);
    });

    it("n'affiche pas le menu de navigation", () => {
      // WHEN
      const { result } = renderHook(() => useEntête());

      // THEN
      expect(result.current.navigation).toBeNull();
    });
  });

  describe("Lorsque l'utilisateur est connecté sur d'autres pages", () => {
    beforeAll(() => {
      vi.mocked(useRouterState).mockReturnValue(mockUseRouterState("/"));
      vi.mocked(useUtilisateur).mockReturnValue(utilisateurConnecté);
    });

    it("affiche tous les liens d'accès rapide", () => {
      // WHEN
      const { result } = renderHook(() => useEntête());

      // THEN
      expect(result.current.accèsRapides).toEqual([
        {
          iconId: "fr-icon-arrow-go-back-fill",
          linkProps: {
            href: environnement.VITE_AVENIRS_URL,
            className: "after:!content-none",
          },
          text: i18n.ENTÊTE.PLATEFORME_AVENIRS,
        },
        {
          iconId: "fr-icon-user-fill",
          linkProps: { to: "/profil" },
          text: "Jean Dupont",
        },
      ]);
    });

    it("affiche le menu de navigation", () => {
      // WHEN
      const { result } = renderHook(() => useEntête());

      // THEN
      expect(result.current.navigation).toEqual([
        { text: i18n.NAVIGATION.TABLEAU_DE_BORD, linkProps: { to: "/" } },
        { text: i18n.NAVIGATION.FORMATIONS, linkProps: { to: "/formations" } },
        { text: i18n.NAVIGATION.FAVORIS, linkProps: { to: "/favoris" } },
      ]);
    });
  });
});
