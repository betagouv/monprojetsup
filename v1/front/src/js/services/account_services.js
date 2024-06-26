import { postToSpringService } from "./api/call_service";

export function passwordLogin(userid, password, onSuccess = null) {
  //
  postToSpringService(
    "login/password",
    {
      login: userid,
      password: password,
    },
    onSuccess,
    true
  );
}

export function oidcLogin(jwt, provider, onSuccess = null) {
  //
  postToSpringService(
    `login/oidc/${provider}`,
    {
      jwt: jwt,
    },
    onSuccess,
    true
  );
}

export function disconnect(onSuccess = null) {
  postToSpringService("disconnect", {}, onSuccess);
}

export function createAccount(data, onSuccess = null) {
  postToSpringService("public/account/create", { data: data }, onSuccess, true);
}

export async function createAccountAsync(data) {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "public/account/create",
      { data: data },
      (data) => {
        resolve(data);
      },
      true,
      (error) => reject(Error(error))
    );
  });
}

export async function validateCodeAcces(data) {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "public/account/validate",
      { data: data },
      (data) => {
        resolve(data);
      },
      true,
      (error) => {
        reject(Error(error));
      }
    );
  });
}

export function sendResetPasswordEmail(email, onSuccess = null) {
  postToSpringService(
    "public/account/resetPassword",
    {
      email: email,
    },
    onSuccess,
    true
  );
}

export function setNewPassword(email, newPassword, token, onSuccess = null) {
  postToSpringService(
    "account/setNewPassword",
    {
      login: email,
      email: email,
      token: token,
      newPassword: newPassword,
    },
    onSuccess,
    true
  );
}

export function validateAccount(email, token, onSuccess = null) {
  postToSpringService(
    "public/account/confirmEmail",
    {
      email: email,
      token: token,
    },
    onSuccess,
    true
  );
}

export async function joinGroupAsync(accessCode) {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "account/joinGroup",
      {
        accessCode: accessCode,
      },
      (data) => {
        resolve(data);
      },
      false,
      (error) => {
        reject(Error(error));
      }
    );
  });
}
//leaveGroupAsync

export async function leaveGroupAsync(key) {
  return new Promise((resolve, reject) => {
    postToSpringService(
      "account/leaveGroup",
      {
        key: key,
      },
      (data) => {
        resolve(data);
      },
      false,
      (error) => {
        reject(Error(error));
      }
    );
  });
}
