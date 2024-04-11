export {
  isLoggedIn,
  clear,
  getLogin,
  getToken,
  setToken,
  getRole,
  setRole,
  setSelectedGroup,
  getSelectedGroup,
  getSelectedStudent,
  setSelectedStudent,
  //getMode,
  isAdminOrTeacher,
  isAdmin,
  isAnonymous,
  setSingleGroupFlag,
  getSingleGroupFlag,
};

export const anonymousLogin = "__anonymous__";

export const Roles = {
  student: "student",
  teacher: "teacher",
  admin: "admin",
};

function setRole(roleStr) {
  let role = Roles.student;
  switch (roleStr) {
    case "ADMIN":
      role = Roles.admin;
      break;
    case "TEACHER":
      role = Roles.teacher;
      break;
  }
  sessionStorage["role"] = role;
}

function getRole() {
  return sessionStorage["role"];
}

function isAnonymous() {
  const login = getLogin();
  return login !== undefined && login.includes(anonymousLogin);
}

function isAdminOrTeacher() {
  const role = getRole();
  return role === Roles.admin || role === Roles.teacher;
}
function isAdmin() {
  return getRole() === Roles.admin;
}

function isLoggedIn() {
  return (
    sessionStorage["login"] !== undefined &&
    sessionStorage["token"] !== undefined
  );
}

function setToken(login, token) {
  sessionStorage["login"] = login;
  sessionStorage["token"] = token;
}

export function setProfileCompletenessLevel(level) {
  sessionStorage["profileCompleteness"] = level;
}

export function getProfileCompletenessLevel() {
  return sessionStorage["profileCompleteness"] || 0;
}

function getLogin() {
  return sessionStorage.login;
}

function getToken() {
  return sessionStorage.token;
}

export function getScreen() {
  return sessionStorage.screen;
}

export function saveScreen(screen) {
  sessionStorage.screen = screen;
}

function setSelectedGroup(group) {
  if (group == null) {
    delete sessionStorage.group;
  } else {
    sessionStorage.group = group;
  }
}

function getSelectedGroup() {
  return sessionStorage.group;
}

export function isAStudentalreadyInAGroup() {
  return (
    !isAnonymous() &&
    !isAdminOrTeacher() &&
    getSelectedGroup() !== undefined &&
    getSelectedGroup() !== null
  );
}

export function atLeastOneGroupOpen() {}

function setSelectedStudent(student) {
  if (student == null) {
    delete sessionStorage.student;
  } else {
    sessionStorage.student = student;
  }
}

function getSelectedStudent() {
  return sessionStorage.student;
}

function clear() {
  sessionStorage.clear();
}

function setSingleGroupFlag(isSingle) {
  sessionStorage.singleGroup = isSingle;
}

function getSingleGroupFlag() {
  return sessionStorage.singleGroup === "true";
}

export function setGroupInfo(infos) {
  sessionStorage.groupInfos = JSON.stringify(infos);
}

export function getGroupInfo(infos) {
  if (sessionStorage.groupInfos) return JSON.parse(sessionStorage.groupInfos);
  return null;
}

export function setNoGroupOpenFlag(isSingle) {
  sessionStorage.noGroupOpen = isSingle;
}

export function getNoGroupOpenFlag() {
  return sessionStorage.noGroupOpen === "true";
}

export function setAdminInfos(infos) {
  sessionStorage.adminInfos = JSON.stringify(infos);
}

export function getCachedAdminInfos() {
  return JSON.parse(sessionStorage.adminInfos);
}

/*
function isEvalENS() {
  const infos = getCachedAdminInfos();
  if (infos?.config) {
    return infos.config.evalENS;
  } else {
    return false;
  }
}*/

export function hideFormations() {
  return false;
  /*
  try {
    const infos = getCachedAdminInfos();
    return (
      infos?.config &&
      infos.config.evalENS &&
      (infos.config.expeENSGroup === undefined ||
        infos.config.expeENSGroup !== "T")
    );
  } catch (e) {
    return undefined;
  }*/
}

export function isExpeENS() {
  return false;
  /*
  const infos = getCachedAdminInfos();
  return infos?.config?.evalENS;*/
}

export function isExpeENSTest() {
  return false;
  /*
  const infos = getCachedAdminInfos();
  return infos?.config?.evalENS && infos.config.expeENSGroup === "T";
  */
}

export function showMinimalStats() {
  const infos = getCachedAdminInfos();
  return (
    infos?.config &&
    infos.config.evalENS &&
    infos.config.statsVisibilityRandomisationENS !== undefined &&
    infos.config.statsVisibilityRandomisationENS === false
  );
}

export function isEvalIndivisible() {
  return false;
  /*
  const infos = getCachedAdminInfos();
  return infos?.config.evalIndivisible;*/
}
