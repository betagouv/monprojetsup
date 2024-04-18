import { postToSpringService } from "./api/call_service";

export function getProfile(onSuccess = null) {
  postToSpringService("profile/get", {}, onSuccess, false, "get");
}

export function updateProfile(update, onSuccess = null) {
  postToSpringService(
    "profile/update",
    {
      update: update,
    },
    onSuccess
  );
}

export function getMessages(user, group, topic, onSuccess = null) {
  postToSpringService(
    "profile/messages",
    {
      user: user,
      group: group,
      topic: topic,
    },
    onSuccess
  );
}

export function addMessage(
  curGroup,
  curStudent,
  topic,
  comment,
  onSuccess = null
) {
  postToSpringService(
    "profile/message",
    {
      group: curGroup,
      student: curStudent,
      topic: topic,
      comment: comment,
    },
    onSuccess
  );
}

export async function getSelection() {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "profile/favoris",
      {},
      (data) => {
        resolve(data);
      },
      false,
      (error) => reject(error)
    );
  });
}
