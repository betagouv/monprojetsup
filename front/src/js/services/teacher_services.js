import { postToSpringService } from "./api/call_service";

export function getAdminInfos(onSuccess = null) {
  postToSpringService("teacher/groups/list", {}, onSuccess);
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

export function resetStudentPassword(user, onSuccess = null) {
  postToSpringService(
    "teacher/student/resetPassword",
    {
      user: user,
    },
    onSuccess
  );
}
