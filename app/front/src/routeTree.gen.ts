/* prettier-ignore-start */

/* eslint-disable */

// @ts-nocheck

// noinspection JSUnusedGlobalSymbols

// This file is auto-generated by TanStack Router

import { createFileRoute } from '@tanstack/react-router'

// Import Routes

import { Route as rootRoute } from './routes/__root'
import { Route as AuthImport } from './routes/_auth'
import { Route as ParcoursupCallbackIndexImport } from './routes/parcoursup-callback/index'
import { Route as AuthFormationsIndexImport } from './routes/_auth/formations/index'
import { Route as AuthFavorisIndexImport } from './routes/_auth/favoris/index'
import { Route as AuthEleveInscriptionImport } from './routes/_auth/eleve/_inscription'

// Create Virtual Routes

const AuthEleveImport = createFileRoute('/_auth/eleve')()
const AuthIndexLazyImport = createFileRoute('/_auth/')()
const AuthProfilIndexLazyImport = createFileRoute('/_auth/profil/')()
const AuthEleveInscriptionInscriptionScolariteIndexLazyImport = createFileRoute(
  '/_auth/eleve/_inscription/inscription/scolarite/',
)()
const AuthEleveInscriptionInscriptionProjetIndexLazyImport = createFileRoute(
  '/_auth/eleve/_inscription/inscription/projet/',
)()
const AuthEleveInscriptionInscriptionMetiersIndexLazyImport = createFileRoute(
  '/_auth/eleve/_inscription/inscription/metiers/',
)()
const AuthEleveInscriptionInscriptionInteretsIndexLazyImport = createFileRoute(
  '/_auth/eleve/_inscription/inscription/interets/',
)()
const AuthEleveInscriptionInscriptionFormationsIndexLazyImport =
  createFileRoute('/_auth/eleve/_inscription/inscription/formations/')()
const AuthEleveInscriptionInscriptionEtudeIndexLazyImport = createFileRoute(
  '/_auth/eleve/_inscription/inscription/etude/',
)()
const AuthEleveInscriptionInscriptionDomainesIndexLazyImport = createFileRoute(
  '/_auth/eleve/_inscription/inscription/domaines/',
)()
const AuthEleveInscriptionInscriptionConfirmationIndexLazyImport =
  createFileRoute('/_auth/eleve/_inscription/inscription/confirmation/')()

// Create/Update Routes

const AuthRoute = AuthImport.update({
  id: '/_auth',
  getParentRoute: () => rootRoute,
} as any)

const AuthEleveRoute = AuthEleveImport.update({
  path: '/eleve',
  getParentRoute: () => AuthRoute,
} as any)

const AuthIndexLazyRoute = AuthIndexLazyImport.update({
  path: '/',
  getParentRoute: () => AuthRoute,
} as any).lazy(() => import('./routes/_auth/index.lazy').then((d) => d.Route))

const ParcoursupCallbackIndexRoute = ParcoursupCallbackIndexImport.update({
  path: '/parcoursup-callback/',
  getParentRoute: () => rootRoute,
} as any)

const AuthProfilIndexLazyRoute = AuthProfilIndexLazyImport.update({
  path: '/profil/',
  getParentRoute: () => AuthRoute,
} as any).lazy(() =>
  import('./routes/_auth/profil/index.lazy').then((d) => d.Route),
)

const AuthFormationsIndexRoute = AuthFormationsIndexImport.update({
  path: '/formations/',
  getParentRoute: () => AuthRoute,
} as any)

const AuthFavorisIndexRoute = AuthFavorisIndexImport.update({
  path: '/favoris/',
  getParentRoute: () => AuthRoute,
} as any)

const AuthEleveInscriptionRoute = AuthEleveInscriptionImport.update({
  id: '/_inscription',
  getParentRoute: () => AuthEleveRoute,
} as any)

const AuthEleveInscriptionInscriptionScolariteIndexLazyRoute =
  AuthEleveInscriptionInscriptionScolariteIndexLazyImport.update({
    path: '/inscription/scolarite/',
    getParentRoute: () => AuthEleveInscriptionRoute,
  } as any).lazy(() =>
    import(
      './routes/_auth/eleve/_inscription/inscription/scolarite/index.lazy'
    ).then((d) => d.Route),
  )

const AuthEleveInscriptionInscriptionProjetIndexLazyRoute =
  AuthEleveInscriptionInscriptionProjetIndexLazyImport.update({
    path: '/inscription/projet/',
    getParentRoute: () => AuthEleveInscriptionRoute,
  } as any).lazy(() =>
    import(
      './routes/_auth/eleve/_inscription/inscription/projet/index.lazy'
    ).then((d) => d.Route),
  )

const AuthEleveInscriptionInscriptionMetiersIndexLazyRoute =
  AuthEleveInscriptionInscriptionMetiersIndexLazyImport.update({
    path: '/inscription/metiers/',
    getParentRoute: () => AuthEleveInscriptionRoute,
  } as any).lazy(() =>
    import(
      './routes/_auth/eleve/_inscription/inscription/metiers/index.lazy'
    ).then((d) => d.Route),
  )

const AuthEleveInscriptionInscriptionInteretsIndexLazyRoute =
  AuthEleveInscriptionInscriptionInteretsIndexLazyImport.update({
    path: '/inscription/interets/',
    getParentRoute: () => AuthEleveInscriptionRoute,
  } as any).lazy(() =>
    import(
      './routes/_auth/eleve/_inscription/inscription/interets/index.lazy'
    ).then((d) => d.Route),
  )

const AuthEleveInscriptionInscriptionFormationsIndexLazyRoute =
  AuthEleveInscriptionInscriptionFormationsIndexLazyImport.update({
    path: '/inscription/formations/',
    getParentRoute: () => AuthEleveInscriptionRoute,
  } as any).lazy(() =>
    import(
      './routes/_auth/eleve/_inscription/inscription/formations/index.lazy'
    ).then((d) => d.Route),
  )

const AuthEleveInscriptionInscriptionEtudeIndexLazyRoute =
  AuthEleveInscriptionInscriptionEtudeIndexLazyImport.update({
    path: '/inscription/etude/',
    getParentRoute: () => AuthEleveInscriptionRoute,
  } as any).lazy(() =>
    import(
      './routes/_auth/eleve/_inscription/inscription/etude/index.lazy'
    ).then((d) => d.Route),
  )

const AuthEleveInscriptionInscriptionDomainesIndexLazyRoute =
  AuthEleveInscriptionInscriptionDomainesIndexLazyImport.update({
    path: '/inscription/domaines/',
    getParentRoute: () => AuthEleveInscriptionRoute,
  } as any).lazy(() =>
    import(
      './routes/_auth/eleve/_inscription/inscription/domaines/index.lazy'
    ).then((d) => d.Route),
  )

const AuthEleveInscriptionInscriptionConfirmationIndexLazyRoute =
  AuthEleveInscriptionInscriptionConfirmationIndexLazyImport.update({
    path: '/inscription/confirmation/',
    getParentRoute: () => AuthEleveInscriptionRoute,
  } as any).lazy(() =>
    import(
      './routes/_auth/eleve/_inscription/inscription/confirmation/index.lazy'
    ).then((d) => d.Route),
  )

// Populate the FileRoutesByPath interface

declare module '@tanstack/react-router' {
  interface FileRoutesByPath {
    '/_auth': {
      id: '/_auth'
      path: ''
      fullPath: ''
      preLoaderRoute: typeof AuthImport
      parentRoute: typeof rootRoute
    }
    '/parcoursup-callback/': {
      id: '/parcoursup-callback/'
      path: '/parcoursup-callback'
      fullPath: '/parcoursup-callback'
      preLoaderRoute: typeof ParcoursupCallbackIndexImport
      parentRoute: typeof rootRoute
    }
    '/_auth/': {
      id: '/_auth/'
      path: '/'
      fullPath: '/'
      preLoaderRoute: typeof AuthIndexLazyImport
      parentRoute: typeof AuthImport
    }
    '/_auth/eleve': {
      id: '/_auth/eleve'
      path: '/eleve'
      fullPath: '/eleve'
      preLoaderRoute: typeof AuthEleveImport
      parentRoute: typeof AuthImport
    }
    '/_auth/eleve/_inscription': {
      id: '/_auth/eleve/_inscription'
      path: '/eleve'
      fullPath: '/eleve'
      preLoaderRoute: typeof AuthEleveInscriptionImport
      parentRoute: typeof AuthEleveRoute
    }
    '/_auth/favoris/': {
      id: '/_auth/favoris/'
      path: '/favoris'
      fullPath: '/favoris'
      preLoaderRoute: typeof AuthFavorisIndexImport
      parentRoute: typeof AuthImport
    }
    '/_auth/formations/': {
      id: '/_auth/formations/'
      path: '/formations'
      fullPath: '/formations'
      preLoaderRoute: typeof AuthFormationsIndexImport
      parentRoute: typeof AuthImport
    }
    '/_auth/profil/': {
      id: '/_auth/profil/'
      path: '/profil'
      fullPath: '/profil'
      preLoaderRoute: typeof AuthProfilIndexLazyImport
      parentRoute: typeof AuthImport
    }
    '/_auth/eleve/_inscription/inscription/confirmation/': {
      id: '/_auth/eleve/_inscription/inscription/confirmation/'
      path: '/inscription/confirmation'
      fullPath: '/eleve/inscription/confirmation'
      preLoaderRoute: typeof AuthEleveInscriptionInscriptionConfirmationIndexLazyImport
      parentRoute: typeof AuthEleveInscriptionImport
    }
    '/_auth/eleve/_inscription/inscription/domaines/': {
      id: '/_auth/eleve/_inscription/inscription/domaines/'
      path: '/inscription/domaines'
      fullPath: '/eleve/inscription/domaines'
      preLoaderRoute: typeof AuthEleveInscriptionInscriptionDomainesIndexLazyImport
      parentRoute: typeof AuthEleveInscriptionImport
    }
    '/_auth/eleve/_inscription/inscription/etude/': {
      id: '/_auth/eleve/_inscription/inscription/etude/'
      path: '/inscription/etude'
      fullPath: '/eleve/inscription/etude'
      preLoaderRoute: typeof AuthEleveInscriptionInscriptionEtudeIndexLazyImport
      parentRoute: typeof AuthEleveInscriptionImport
    }
    '/_auth/eleve/_inscription/inscription/formations/': {
      id: '/_auth/eleve/_inscription/inscription/formations/'
      path: '/inscription/formations'
      fullPath: '/eleve/inscription/formations'
      preLoaderRoute: typeof AuthEleveInscriptionInscriptionFormationsIndexLazyImport
      parentRoute: typeof AuthEleveInscriptionImport
    }
    '/_auth/eleve/_inscription/inscription/interets/': {
      id: '/_auth/eleve/_inscription/inscription/interets/'
      path: '/inscription/interets'
      fullPath: '/eleve/inscription/interets'
      preLoaderRoute: typeof AuthEleveInscriptionInscriptionInteretsIndexLazyImport
      parentRoute: typeof AuthEleveInscriptionImport
    }
    '/_auth/eleve/_inscription/inscription/metiers/': {
      id: '/_auth/eleve/_inscription/inscription/metiers/'
      path: '/inscription/metiers'
      fullPath: '/eleve/inscription/metiers'
      preLoaderRoute: typeof AuthEleveInscriptionInscriptionMetiersIndexLazyImport
      parentRoute: typeof AuthEleveInscriptionImport
    }
    '/_auth/eleve/_inscription/inscription/projet/': {
      id: '/_auth/eleve/_inscription/inscription/projet/'
      path: '/inscription/projet'
      fullPath: '/eleve/inscription/projet'
      preLoaderRoute: typeof AuthEleveInscriptionInscriptionProjetIndexLazyImport
      parentRoute: typeof AuthEleveInscriptionImport
    }
    '/_auth/eleve/_inscription/inscription/scolarite/': {
      id: '/_auth/eleve/_inscription/inscription/scolarite/'
      path: '/inscription/scolarite'
      fullPath: '/eleve/inscription/scolarite'
      preLoaderRoute: typeof AuthEleveInscriptionInscriptionScolariteIndexLazyImport
      parentRoute: typeof AuthEleveInscriptionImport
    }
  }
}

// Create and export the route tree

interface AuthEleveInscriptionRouteChildren {
  AuthEleveInscriptionInscriptionConfirmationIndexLazyRoute: typeof AuthEleveInscriptionInscriptionConfirmationIndexLazyRoute
  AuthEleveInscriptionInscriptionDomainesIndexLazyRoute: typeof AuthEleveInscriptionInscriptionDomainesIndexLazyRoute
  AuthEleveInscriptionInscriptionEtudeIndexLazyRoute: typeof AuthEleveInscriptionInscriptionEtudeIndexLazyRoute
  AuthEleveInscriptionInscriptionFormationsIndexLazyRoute: typeof AuthEleveInscriptionInscriptionFormationsIndexLazyRoute
  AuthEleveInscriptionInscriptionInteretsIndexLazyRoute: typeof AuthEleveInscriptionInscriptionInteretsIndexLazyRoute
  AuthEleveInscriptionInscriptionMetiersIndexLazyRoute: typeof AuthEleveInscriptionInscriptionMetiersIndexLazyRoute
  AuthEleveInscriptionInscriptionProjetIndexLazyRoute: typeof AuthEleveInscriptionInscriptionProjetIndexLazyRoute
  AuthEleveInscriptionInscriptionScolariteIndexLazyRoute: typeof AuthEleveInscriptionInscriptionScolariteIndexLazyRoute
}

const AuthEleveInscriptionRouteChildren: AuthEleveInscriptionRouteChildren = {
  AuthEleveInscriptionInscriptionConfirmationIndexLazyRoute:
    AuthEleveInscriptionInscriptionConfirmationIndexLazyRoute,
  AuthEleveInscriptionInscriptionDomainesIndexLazyRoute:
    AuthEleveInscriptionInscriptionDomainesIndexLazyRoute,
  AuthEleveInscriptionInscriptionEtudeIndexLazyRoute:
    AuthEleveInscriptionInscriptionEtudeIndexLazyRoute,
  AuthEleveInscriptionInscriptionFormationsIndexLazyRoute:
    AuthEleveInscriptionInscriptionFormationsIndexLazyRoute,
  AuthEleveInscriptionInscriptionInteretsIndexLazyRoute:
    AuthEleveInscriptionInscriptionInteretsIndexLazyRoute,
  AuthEleveInscriptionInscriptionMetiersIndexLazyRoute:
    AuthEleveInscriptionInscriptionMetiersIndexLazyRoute,
  AuthEleveInscriptionInscriptionProjetIndexLazyRoute:
    AuthEleveInscriptionInscriptionProjetIndexLazyRoute,
  AuthEleveInscriptionInscriptionScolariteIndexLazyRoute:
    AuthEleveInscriptionInscriptionScolariteIndexLazyRoute,
}

const AuthEleveInscriptionRouteWithChildren =
  AuthEleveInscriptionRoute._addFileChildren(AuthEleveInscriptionRouteChildren)

interface AuthEleveRouteChildren {
  AuthEleveInscriptionRoute: typeof AuthEleveInscriptionRouteWithChildren
}

const AuthEleveRouteChildren: AuthEleveRouteChildren = {
  AuthEleveInscriptionRoute: AuthEleveInscriptionRouteWithChildren,
}

const AuthEleveRouteWithChildren = AuthEleveRoute._addFileChildren(
  AuthEleveRouteChildren,
)

interface AuthRouteChildren {
  AuthIndexLazyRoute: typeof AuthIndexLazyRoute
  AuthEleveRoute: typeof AuthEleveRouteWithChildren
  AuthFavorisIndexRoute: typeof AuthFavorisIndexRoute
  AuthFormationsIndexRoute: typeof AuthFormationsIndexRoute
  AuthProfilIndexLazyRoute: typeof AuthProfilIndexLazyRoute
}

const AuthRouteChildren: AuthRouteChildren = {
  AuthIndexLazyRoute: AuthIndexLazyRoute,
  AuthEleveRoute: AuthEleveRouteWithChildren,
  AuthFavorisIndexRoute: AuthFavorisIndexRoute,
  AuthFormationsIndexRoute: AuthFormationsIndexRoute,
  AuthProfilIndexLazyRoute: AuthProfilIndexLazyRoute,
}

const AuthRouteWithChildren = AuthRoute._addFileChildren(AuthRouteChildren)

export interface FileRoutesByFullPath {
  '': typeof AuthRouteWithChildren
  '/parcoursup-callback': typeof ParcoursupCallbackIndexRoute
  '/': typeof AuthIndexLazyRoute
  '/eleve': typeof AuthEleveInscriptionRouteWithChildren
  '/favoris': typeof AuthFavorisIndexRoute
  '/formations': typeof AuthFormationsIndexRoute
  '/profil': typeof AuthProfilIndexLazyRoute
  '/eleve/inscription/confirmation': typeof AuthEleveInscriptionInscriptionConfirmationIndexLazyRoute
  '/eleve/inscription/domaines': typeof AuthEleveInscriptionInscriptionDomainesIndexLazyRoute
  '/eleve/inscription/etude': typeof AuthEleveInscriptionInscriptionEtudeIndexLazyRoute
  '/eleve/inscription/formations': typeof AuthEleveInscriptionInscriptionFormationsIndexLazyRoute
  '/eleve/inscription/interets': typeof AuthEleveInscriptionInscriptionInteretsIndexLazyRoute
  '/eleve/inscription/metiers': typeof AuthEleveInscriptionInscriptionMetiersIndexLazyRoute
  '/eleve/inscription/projet': typeof AuthEleveInscriptionInscriptionProjetIndexLazyRoute
  '/eleve/inscription/scolarite': typeof AuthEleveInscriptionInscriptionScolariteIndexLazyRoute
}

export interface FileRoutesByTo {
  '/parcoursup-callback': typeof ParcoursupCallbackIndexRoute
  '/': typeof AuthIndexLazyRoute
  '/eleve': typeof AuthEleveInscriptionRouteWithChildren
  '/favoris': typeof AuthFavorisIndexRoute
  '/formations': typeof AuthFormationsIndexRoute
  '/profil': typeof AuthProfilIndexLazyRoute
  '/eleve/inscription/confirmation': typeof AuthEleveInscriptionInscriptionConfirmationIndexLazyRoute
  '/eleve/inscription/domaines': typeof AuthEleveInscriptionInscriptionDomainesIndexLazyRoute
  '/eleve/inscription/etude': typeof AuthEleveInscriptionInscriptionEtudeIndexLazyRoute
  '/eleve/inscription/formations': typeof AuthEleveInscriptionInscriptionFormationsIndexLazyRoute
  '/eleve/inscription/interets': typeof AuthEleveInscriptionInscriptionInteretsIndexLazyRoute
  '/eleve/inscription/metiers': typeof AuthEleveInscriptionInscriptionMetiersIndexLazyRoute
  '/eleve/inscription/projet': typeof AuthEleveInscriptionInscriptionProjetIndexLazyRoute
  '/eleve/inscription/scolarite': typeof AuthEleveInscriptionInscriptionScolariteIndexLazyRoute
}

export interface FileRoutesById {
  __root__: typeof rootRoute
  '/_auth': typeof AuthRouteWithChildren
  '/parcoursup-callback/': typeof ParcoursupCallbackIndexRoute
  '/_auth/': typeof AuthIndexLazyRoute
  '/_auth/eleve': typeof AuthEleveRouteWithChildren
  '/_auth/eleve/_inscription': typeof AuthEleveInscriptionRouteWithChildren
  '/_auth/favoris/': typeof AuthFavorisIndexRoute
  '/_auth/formations/': typeof AuthFormationsIndexRoute
  '/_auth/profil/': typeof AuthProfilIndexLazyRoute
  '/_auth/eleve/_inscription/inscription/confirmation/': typeof AuthEleveInscriptionInscriptionConfirmationIndexLazyRoute
  '/_auth/eleve/_inscription/inscription/domaines/': typeof AuthEleveInscriptionInscriptionDomainesIndexLazyRoute
  '/_auth/eleve/_inscription/inscription/etude/': typeof AuthEleveInscriptionInscriptionEtudeIndexLazyRoute
  '/_auth/eleve/_inscription/inscription/formations/': typeof AuthEleveInscriptionInscriptionFormationsIndexLazyRoute
  '/_auth/eleve/_inscription/inscription/interets/': typeof AuthEleveInscriptionInscriptionInteretsIndexLazyRoute
  '/_auth/eleve/_inscription/inscription/metiers/': typeof AuthEleveInscriptionInscriptionMetiersIndexLazyRoute
  '/_auth/eleve/_inscription/inscription/projet/': typeof AuthEleveInscriptionInscriptionProjetIndexLazyRoute
  '/_auth/eleve/_inscription/inscription/scolarite/': typeof AuthEleveInscriptionInscriptionScolariteIndexLazyRoute
}

export interface FileRouteTypes {
  fileRoutesByFullPath: FileRoutesByFullPath
  fullPaths:
    | ''
    | '/parcoursup-callback'
    | '/'
    | '/eleve'
    | '/favoris'
    | '/formations'
    | '/profil'
    | '/eleve/inscription/confirmation'
    | '/eleve/inscription/domaines'
    | '/eleve/inscription/etude'
    | '/eleve/inscription/formations'
    | '/eleve/inscription/interets'
    | '/eleve/inscription/metiers'
    | '/eleve/inscription/projet'
    | '/eleve/inscription/scolarite'
  fileRoutesByTo: FileRoutesByTo
  to:
    | '/parcoursup-callback'
    | '/'
    | '/eleve'
    | '/favoris'
    | '/formations'
    | '/profil'
    | '/eleve/inscription/confirmation'
    | '/eleve/inscription/domaines'
    | '/eleve/inscription/etude'
    | '/eleve/inscription/formations'
    | '/eleve/inscription/interets'
    | '/eleve/inscription/metiers'
    | '/eleve/inscription/projet'
    | '/eleve/inscription/scolarite'
  id:
    | '__root__'
    | '/_auth'
    | '/parcoursup-callback/'
    | '/_auth/'
    | '/_auth/eleve'
    | '/_auth/eleve/_inscription'
    | '/_auth/favoris/'
    | '/_auth/formations/'
    | '/_auth/profil/'
    | '/_auth/eleve/_inscription/inscription/confirmation/'
    | '/_auth/eleve/_inscription/inscription/domaines/'
    | '/_auth/eleve/_inscription/inscription/etude/'
    | '/_auth/eleve/_inscription/inscription/formations/'
    | '/_auth/eleve/_inscription/inscription/interets/'
    | '/_auth/eleve/_inscription/inscription/metiers/'
    | '/_auth/eleve/_inscription/inscription/projet/'
    | '/_auth/eleve/_inscription/inscription/scolarite/'
  fileRoutesById: FileRoutesById
}

export interface RootRouteChildren {
  AuthRoute: typeof AuthRouteWithChildren
  ParcoursupCallbackIndexRoute: typeof ParcoursupCallbackIndexRoute
}

const rootRouteChildren: RootRouteChildren = {
  AuthRoute: AuthRouteWithChildren,
  ParcoursupCallbackIndexRoute: ParcoursupCallbackIndexRoute,
}

export const routeTree = rootRoute
  ._addFileChildren(rootRouteChildren)
  ._addFileTypes<FileRouteTypes>()

/* prettier-ignore-end */

/* ROUTE_MANIFEST_START
{
  "routes": {
    "__root__": {
      "filePath": "__root.tsx",
      "children": [
        "/_auth",
        "/parcoursup-callback/"
      ]
    },
    "/_auth": {
      "filePath": "_auth.tsx",
      "children": [
        "/_auth/",
        "/_auth/eleve",
        "/_auth/favoris/",
        "/_auth/formations/",
        "/_auth/profil/"
      ]
    },
    "/parcoursup-callback/": {
      "filePath": "parcoursup-callback/index.tsx"
    },
    "/_auth/": {
      "filePath": "_auth/index.lazy.tsx",
      "parent": "/_auth"
    },
    "/_auth/eleve": {
      "filePath": "_auth/eleve",
      "parent": "/_auth",
      "children": [
        "/_auth/eleve/_inscription"
      ]
    },
    "/_auth/eleve/_inscription": {
      "filePath": "_auth/eleve/_inscription.tsx",
      "parent": "/_auth/eleve",
      "children": [
        "/_auth/eleve/_inscription/inscription/confirmation/",
        "/_auth/eleve/_inscription/inscription/domaines/",
        "/_auth/eleve/_inscription/inscription/etude/",
        "/_auth/eleve/_inscription/inscription/formations/",
        "/_auth/eleve/_inscription/inscription/interets/",
        "/_auth/eleve/_inscription/inscription/metiers/",
        "/_auth/eleve/_inscription/inscription/projet/",
        "/_auth/eleve/_inscription/inscription/scolarite/"
      ]
    },
    "/_auth/favoris/": {
      "filePath": "_auth/favoris/index.tsx",
      "parent": "/_auth"
    },
    "/_auth/formations/": {
      "filePath": "_auth/formations/index.tsx",
      "parent": "/_auth"
    },
    "/_auth/profil/": {
      "filePath": "_auth/profil/index.lazy.tsx",
      "parent": "/_auth"
    },
    "/_auth/eleve/_inscription/inscription/confirmation/": {
      "filePath": "_auth/eleve/_inscription/inscription/confirmation/index.lazy.tsx",
      "parent": "/_auth/eleve/_inscription"
    },
    "/_auth/eleve/_inscription/inscription/domaines/": {
      "filePath": "_auth/eleve/_inscription/inscription/domaines/index.lazy.tsx",
      "parent": "/_auth/eleve/_inscription"
    },
    "/_auth/eleve/_inscription/inscription/etude/": {
      "filePath": "_auth/eleve/_inscription/inscription/etude/index.lazy.tsx",
      "parent": "/_auth/eleve/_inscription"
    },
    "/_auth/eleve/_inscription/inscription/formations/": {
      "filePath": "_auth/eleve/_inscription/inscription/formations/index.lazy.tsx",
      "parent": "/_auth/eleve/_inscription"
    },
    "/_auth/eleve/_inscription/inscription/interets/": {
      "filePath": "_auth/eleve/_inscription/inscription/interets/index.lazy.tsx",
      "parent": "/_auth/eleve/_inscription"
    },
    "/_auth/eleve/_inscription/inscription/metiers/": {
      "filePath": "_auth/eleve/_inscription/inscription/metiers/index.lazy.tsx",
      "parent": "/_auth/eleve/_inscription"
    },
    "/_auth/eleve/_inscription/inscription/projet/": {
      "filePath": "_auth/eleve/_inscription/inscription/projet/index.lazy.tsx",
      "parent": "/_auth/eleve/_inscription"
    },
    "/_auth/eleve/_inscription/inscription/scolarite/": {
      "filePath": "_auth/eleve/_inscription/inscription/scolarite/index.lazy.tsx",
      "parent": "/_auth/eleve/_inscription"
    }
  }
}
ROUTE_MANIFEST_END */
