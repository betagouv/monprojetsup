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
    case "teacher":
    case "pp":
      role = Roles.teacher;
      break;
  }
  sessionStorage["role"] = role;
}

export function setAnonymous(ano) {
  if (ano) {
    sessionStorage["ano"] = "1";
  } else {
    sessionStorage["ano"] = "0";
  }
}
function isAnonymous() {
  const ano = sessionStorage["ano"];
  return ano !== undefined && ano === "1";
}

function getRole() {
  return sessionStorage["role"];
}

/*
function isAnonymous() {
  const login = getLogin();
  return login !== undefined && login.includes(anonymousLogin);
}*/

function isAdminOrTeacher() {
  const role = getRole();
  return role === Roles.admin || role === Roles.teacher;
}
export function isFakeLyceen() {
  const infos = getCachedAdminInfos();
  return isStudent() && (infos.type == "pp" || infos.type == "admin");
}
export function isStudent() {
  const role = getRole();
  return role === Roles.student;
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
export function getCurrentSearch() {
  const str = sessionStorage.currentSearch;
  if (str == undefined || str == null || str == "undefined" || str == null)
    return "";
  return str;
}
export function setCurrentSearch(str) {
  if (str == undefined || str == null || str == "undefined" || str == null)
    return;
  sessionStorage.currentSearch = str;
}

export function isAStudentalreadyInAGroup() {
  return (
    !isAnonymous() &&
    !isAdminOrTeacher() &&
    getSelectedGroup() !== undefined &&
    getSelectedGroup() !== null
  );
}
export function isAStudentThatCouldJoinAGroup() {
  return (
    !isAnonymous() &&
    !isAdminOrTeacher() &&
    (getSelectedGroup() === "" ||
      getSelectedGroup() === undefined ||
      getSelectedGroup() === null)
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
export function setSelectedStudentName(studentName) {
  if (studentName == null) {
    delete sessionStorage.studentName;
  } else {
    sessionStorage.studentName = studentName;
  }
}

function getSelectedStudent() {
  return sessionStorage.student;
}
export function getSelectedStudentName() {
  return sessionStorage.studentName;
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
export function getGroupName() {
  if (sessionStorage.groupInfos)
    return JSON.parse(sessionStorage.groupInfos)?.name;
  return "";
}

export function setNoGroupOpenFlag(isSingle) {
  sessionStorage.noGroupOpen = isSingle;
}

export function getNoGroupOpenFlag() {
  return sessionStorage.noGroupOpen === "true";
}

export function setAdminInfos(infos) {
  sessionStorage.adminInfos = JSON.stringify(infos);
  let expertMode = null;
  for (const group of infos.groups) {
    if (group.expeENSGroup === "profils_ref") expertMode = true;
  }
  setExpertMode(expertMode);
}

export function setCurrentGroupIfNeeded(infos) {
  //ensures a current group is selected
  const curGroup = getSelectedGroup();
  if (
    (curGroup === undefined || curGroup === null) &&
    infos.groups &&
    infos.groups.length > 0
  ) {
    let id = infos.groups[0].id;
    if (isExpert()) {
      for (const group of infos.groups) {
        if (group.expeENSGroup === "profils_ref") id = group.id;
      }
    }
    setSelectedGroup(id);
  }
}

function setExpertMode(mode) {
  if (mode === null) delete sessionStorage.expertMode;
  else sessionStorage.expertMode = mode;
}

export function isExpert() {
  return sessionStorage.expertMode === "true";
}

export function getCachedAdminInfos() {
  if (sessionStorage.adminInfos) return JSON.parse(sessionStorage.adminInfos);
  return {};
}

export function hideFormations() {
  return false;
}

export function isExpeENS() {
  return false;
}

export function isExpeENSTest() {
  return false;
  /*
  const infos = getCachedAdminInfos();
  return infos?.config?.evalENS && infos.config.expeENSGroup === "T";
  */
}

export function getConfig() {
  const infos = getCachedAdminInfos();
  return infos?.config;
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
