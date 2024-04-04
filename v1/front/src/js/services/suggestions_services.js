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
