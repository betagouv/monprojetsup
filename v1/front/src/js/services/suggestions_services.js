import { postToSpringService } from "./api/call_service";

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

export function getStats(key, bac, onSuccess) {
  postToSpringService(
    "stats",
    {
      key: key,
      bac: bac,
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
