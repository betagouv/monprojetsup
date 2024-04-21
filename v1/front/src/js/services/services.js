/**
 * Manages sessions and services
 *
 * */

import { postToSpringService } from "./api/call_service";

import * as profile_services from "./profile_services";
import * as account_services from "./account_services";
import * as suggestions_services from "./suggestions_services";
import * as teacher_services from "./teacher_services";
import * as admin_services from "./admin_services";

export function sendError(error) {
  postToSpringService("log/error", { error: error }, null, true);
}

export function trace(action) {
  postToSpringService("log/trace", {
    action: action,
  });
}

/************** PROFILE **************** */

export const getProfile = profile_services.getProfile;
export const getProfileAsync = profile_services.getProfileAsync;
export const updateProfile = profile_services.updateProfile;
export const getMessages = profile_services.getMessages;
export const addMessage = profile_services.addMessage;
export const getSelection = profile_services.getSelection;

/********************* SUGGESTIONS ******************* */

export const getExplanations = suggestions_services.getExplanations;
export const getSuggestions = suggestions_services.getSuggestions;
export const getFormationsAffinities =
  suggestions_services.getFormationsAffinities;
export const getStats = suggestions_services.getStats;
export const getFormationsOfInterest =
  suggestions_services.getFormationsOfInterest;
export const getDetails = suggestions_services.getDetails;
export const search = suggestions_services.search;

/**************** ACCOUNT SERVICES ******************** */

export const passwordLogin = account_services.passwordLogin;
export const oidcLogin = account_services.oidcLogin;
export const disconnect = account_services.disconnect;
export const createAccount = account_services.createAccount;
export const validateAccount = account_services.validateAccount;
export const sendResetPasswordEmail = account_services.sendResetPasswordEmail;
export const setNewPassword = account_services.setNewPassword;
export const validateCodeAcces = account_services.validateCodeAcces;
export const joinGroup = account_services.joinGroup;

/**************** TEACHER SERVICES ************* */
export const updateAdminInfos = teacher_services.getAdminInfos;
export const getStudentProfile = teacher_services.getStudentProfile;
export const getSelectedGroupDetails = teacher_services.getSelectedGroupDetails;
export const switchRole = teacher_services.switchRole;
export const addGroupMember = teacher_services.addGroupMember;
export const resetStudentPasswordByTeacher =
  teacher_services.resetStudentPassword;

/************** ADMIN ********************* */

export const setGroupAdmin = admin_services.setGroupAdmin;
export const createGroup = admin_services.createGroup;
export const deleteUser = admin_services.deleteUser;
export const moderateAccountCreation = admin_services.moderateAccountCreation;
export const setModeration = admin_services.setModeration;
export const changeGroupStatus = admin_services.changeGroupStatus;
export const resetUserPasswordByAdmin = admin_services.resetUserPassword;
