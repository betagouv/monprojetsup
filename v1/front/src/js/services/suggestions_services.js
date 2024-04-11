import { postToSpringService, getFromSpringService } from "./api/call_service";

export function getSuggestions(profile, onSuccess) {
  postToSpringService(
    "suggestions",
    {
      profile: profile,
    },
    onSuccess,
    true
  );
}

export async function getFormationsAffinities(profile) {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "affinite/formations",
      {
        profile: profile,
      },
      (data) => {
        resolve(data);
      },
      true,
      (error) => reject(error)
    );
  });
}

export async function getExplanations(keys, profile) {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "explanations",
      {
        keys: keys,
        profile: profile,
      },
      (data) => {
        resolve(data);
      },
      true,
      (error) => reject(error)
    );
  });
}

export async function getDetails(keys, profile) {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "details",
      {
        keys: keys,
        profile: profile,
      },
      (data) => {
        resolve(data);
      },
      true,
      (error) => reject(error)
    );
  });
}

export async function search(
  includeFormations,
  includeMetiers,
  pageSize,
  pageNb,
  recherche,
  profile
) {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "recherche",
      {
        includeFormations: includeFormations,
        includeMetiers: includeMetiers,
        pageSize: pageSize,
        pageNb: pageNb,
        recherche: recherche,
        profile: profile,
      },
      (data) => {
        resolve(data);
      },
      true,
      (error) => reject(error)
    );
  });
}

export function getStats(bac, key, onSuccess = null) {
  getFromSpringService(
    "stats",
    {
      bac: bac,
      key: key,
    },
    onSuccess,
    true
  );
}

export function getFormationsOfInterest(geo_pref, keys, onSuccess = null) {
  postToSpringService(
    "foi",
    {
      geo_pref: geo_pref,
      keys: keys,
    },
    onSuccess,
    true
  );
}
