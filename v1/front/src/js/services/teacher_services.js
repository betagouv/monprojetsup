import { postToSpringService } from "./api/call_service";

export function getAdminInfos(onSuccess = null) {
  postToSpringService("teacher/groups/list", {}, onSuccess);
}

export async function getAdminInfosAsync() {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "teacher/groups/list",
      {},
      (data) => resolve(data),
      false,
      (error) => reject(error)
    );
  });
}

export function getSelectedGroupDetails(group, onSuccess = null) {
  postToSpringService(
    "teacher/groups/details",
    {
      groupId: group,
    },
    onSuccess
  );
}

export async function getSelectedGroupDetailsAsync(group) {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "teacher/groups/details",
      {
        groupId: group,
      },
      (data) => resolve(data),
      false,
      (error) => reject(error)
    );
  });
}

export async function setTeacherFeedback(studentLogin, key, type, content) {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "teacher/student/feedback",
      {
        studentLogin: studentLogin,
        key: key,
        type: type,
        content: content,
      },
      (data) => resolve(data),
      false,
      (error) => reject(error)
    );
  });
}

export function addGroupMember(group, name, add, onSuccess = null) {
  postToSpringService(
    "teacher/groups/add",
    {
      groupId: group,
      memberlogin: name,
      addMember: add,
    },
    onSuccess
  );
}

export async function switchRole(role, onSuccess = null) {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "teacher/role",
      {
        role: role,
      },
      (data) => resolve(data),
      false,
      (error) => reject(error)
    );
  });
}

export async function switchToNewRefProfile() {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "teacher/switchToNewRefProfile",
      {},
      (data) => resolve(data),
      false,
      (error) => reject(error)
    );
  });
}

export function getStudentProfile(curGroup, curStudent, onSuccess = null) {
  postToSpringService(
    "teacher/student/profile",
    {
      groupId: curGroup,
      memberLogin: curStudent,
    },
    onSuccess
  );
}
export function getStudentProfileAsync(curStudent) {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "teacher/student/profile",
      {
        memberLogin: curStudent,
      },
      (data) => resolve(data),
      false,
      (error) => reject(error)
    );
  });
}
export function getStudentSelection(curGroup, curStudent) {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "teacher/student/selection",
      {
        groupId: curGroup,
        memberLogin: curStudent,
      },
      (data) => resolve(data),
      false,
      (error) => reject(error)
    );
  });
}

export function resetStudentPassword(user, onSuccess = null) {
  postToSpringService(
    "teacher/student/resetPassword",
    {
      user: user,
    },
    onSuccess
  );
}
