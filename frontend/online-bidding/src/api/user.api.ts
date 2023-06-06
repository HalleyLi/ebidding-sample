import { request } from './request';
import { LoginParams, LogoutParams, LogoutResult, Account } from '../models/user/login';

/** Login */
export const apiLogin = (data: LoginParams) => request<Account>('post', '/api/v1/account/login', data);

/** Logout */
export const apiLogout = (data: LogoutParams) => request<LogoutResult>('post', '/api/v1/account/logout', data);
