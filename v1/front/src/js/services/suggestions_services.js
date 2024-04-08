import { error } from "jquery";
import { postToSpringService, getFromSpringService } from "./api/call_service";

export function getExplanations(profile, key, onSuccess = null) {
  postToSpringService(
    "explanations",
    {
      profile: profile,
      key: key,
    },
    onSuccess,
    true
  );
}

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

export async function getExplanationsAsync(key, profile) {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "explanations",
      {
        key: key,
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
