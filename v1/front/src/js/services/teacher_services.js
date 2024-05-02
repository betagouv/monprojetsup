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

export function switchRole(role, onSuccess = null) {
  postToSpringService(
    "teacher/role",
    {
      role: role,
    },
    onSuccess
  );
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
export function getStudentProfileAsync(curGroup, curStudent) {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "teacher/student/profile",
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
