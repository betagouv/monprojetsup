import { postToSpringService } from "./api/call_service";

export function setGroupAdmin(group, name, add, onSuccess = null) {
  postToSpringService(
    "admin/groups/setAdmin",
    {
      groupId: group,
      groupAdminLogin: name,
      addAdmin: add,
    },
    onSuccess
  );
}

export function createGroup(data, onSuccess = null) {
  postToSpringService("admin/groups/create", data, onSuccess);
}

export function deleteUser(user, onSuccess = null) {
  postToSpringService(
    "admin/deleteUser",
    {
      user: user,
    },
    onSuccess
  );
}

export function resetUserPassword(user, onSuccess = null) {
  postToSpringService(
    "admin/user/resetPassword",
    {
      user: user,
    },
    onSuccess
  );
}

export function moderateAccountCreation(user, accept, onSuccess = null) {
  postToSpringService(
    "admin/moderateAccountCreation",
    {
      user: user,
      accept: accept,
    },
    onSuccess
  );
}
export function setModeration(moderate, type, onSuccess = null) {
  postToSpringService(
    "admin/setModeration",
    {
      moderate: moderate,
      type: type,
    },
    onSuccess
  );
}

export function changeGroupStatus(group, status, onSuccess = null) {
  postToSpringService(
    "admin/groups/setStatus",
    {
      name: group,
      openGroup: status,
    },
    onSuccess
  );
}
