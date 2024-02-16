import { postToSpringService } from "./api/call_service";

export function getExplanations(profile, key, onSuccess = null) {
  postToSpringService(
    "public/explanations",
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
    "public/suggestions",
    {
      profile: profile,
    },
    onSuccess,
    true
  );
}

export function getFormationsOfInterest(geo_pref, keys, onSuccess = null) {
  postToSpringService(
    "public/foi",
    {
      geo_pref: geo_pref,
      keys: keys,
    },
    onSuccess,
    true
  );
}
