/* prettier-ignore-start */

/* eslint-disable */

// @ts-nocheck

// noinspection JSUnusedGlobalSymbols

// This file is auto-generated by TanStack Router

import { createFileRoute } from '@tanstack/react-router'

// Import Routes

import { Route as rootRoute } from './routes/__root'
import { Route as InscriptionImport } from './routes/_inscription'
import { Route as ArticlesIndexImport } from './routes/articles/index'
import { Route as InscriptionInscriptionScolariteIndexImport } from './routes/_inscription/inscription/scolarite/index'
import { Route as InscriptionInscriptionProjetIndexImport } from './routes/_inscription/inscription/projet/index'

// Create Virtual Routes

const IndexLazyImport = createFileRoute('/')()

// Create/Update Routes

const InscriptionRoute = InscriptionImport.update({
  id: '/_inscription',
  getParentRoute: () => rootRoute,
} as any)

const IndexLazyRoute = IndexLazyImport.update({
  path: '/',
  getParentRoute: () => rootRoute,
} as any).lazy(() => import('./routes/index.lazy').then((d) => d.Route))

const ArticlesIndexRoute = ArticlesIndexImport.update({
  path: '/articles/',
  getParentRoute: () => rootRoute,
} as any).lazy(() =>
  import('./routes/articles/index.lazy').then((d) => d.Route),
)

const InscriptionInscriptionScolariteIndexRoute =
  InscriptionInscriptionScolariteIndexImport.update({
    path: '/inscription/scolarite/',
    getParentRoute: () => InscriptionRoute,
  } as any).lazy(() =>
    import('./routes/_inscription/inscription/scolarite/index.lazy').then(
      (d) => d.Route,
    ),
  )

const InscriptionInscriptionProjetIndexRoute =
  InscriptionInscriptionProjetIndexImport.update({
    path: '/inscription/projet/',
    getParentRoute: () => InscriptionRoute,
  } as any).lazy(() =>
    import('./routes/_inscription/inscription/projet/index.lazy').then(
      (d) => d.Route,
    ),
  )

// Populate the FileRoutesByPath interface

declare module '@tanstack/react-router' {
  interface FileRoutesByPath {
    '/': {
      preLoaderRoute: typeof IndexLazyImport
      parentRoute: typeof rootRoute
    }
    '/_inscription': {
      preLoaderRoute: typeof InscriptionImport
      parentRoute: typeof rootRoute
    }
    '/articles/': {
      preLoaderRoute: typeof ArticlesIndexImport
      parentRoute: typeof rootRoute
    }
    '/_inscription/inscription/projet/': {
      preLoaderRoute: typeof InscriptionInscriptionProjetIndexImport
      parentRoute: typeof InscriptionImport
    }
    '/_inscription/inscription/scolarite/': {
      preLoaderRoute: typeof InscriptionInscriptionScolariteIndexImport
      parentRoute: typeof InscriptionImport
    }
  }
}

// Create and export the route tree

export const routeTree = rootRoute.addChildren([
  IndexLazyRoute,
  InscriptionRoute.addChildren([
    InscriptionInscriptionProjetIndexRoute,
    InscriptionInscriptionScolariteIndexRoute,
  ]),
  ArticlesIndexRoute,
])

/* prettier-ignore-end */
