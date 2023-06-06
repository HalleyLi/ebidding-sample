import { createSlice, PayloadAction, Dispatch } from '@reduxjs/toolkit';
import { apiLogin, apiLogout } from '../api/user.api';
import { LoginParams, Role } from '../models/user/login';
import { Locale, UserState } from '../models/user/user';
import { createAsyncAction } from './utils';
import { getGlobalState } from '../utils/getGloabal';
import { clientMenuList, salesMenuList } from '../models/layout/menu.interface';

const initialState: UserState = {
  ...getGlobalState(),
  noticeCount: 0,
  locale: (localStorage.getItem('locale')! || 'en_US') as Locale,
  newUser: JSON.parse(localStorage.getItem('newUser')!) ?? true,
  logged: localStorage.getItem('t') ? true : false,
  menuList: localStorage.getItem('role') === Role.SALES ? salesMenuList : clientMenuList,
  username: localStorage.getItem('username') || '',
  role: Role.SALES as Role,
};

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setUserItem(state, action: PayloadAction<Partial<UserState>>) {
      const { username, role } = action.payload;

      if (username !== state.username) {
        localStorage.setItem('username', action.payload.username || '');
      }

      if (role) {
        localStorage.setItem('role', role);
        const menuList = role === Role.SALES ? salesMenuList : clientMenuList
        Object.assign(state, { menuList: menuList });
      }

      Object.assign(state, action.payload);
    },
  },
});

export const { setUserItem } = userSlice.actions;

export default userSlice.reducer;

export const loginAsync = createAsyncAction<LoginParams, boolean>(payload => {
  return async dispatch => {
    const { data, success } = await apiLogin(payload);

    if (success) {
      localStorage.setItem('t', data.token);
      localStorage.setItem('username', data.name);
      localStorage.setItem('role', data.role);
      dispatch(
        setUserItem({
          logged: true,
          username: data.name,
          menuList: data.role === Role.SALES ? salesMenuList : clientMenuList
        }),
      );

      return true;
    }

    return false;
  };
});

export const logoutAsync = () => {
  return async (dispatch: Dispatch) => {
    const { success } = await apiLogout({ token: localStorage.getItem('t')! });

    if (success) {
      localStorage.clear();
      dispatch(
        setUserItem({
          logged: false,
        }),
      );

      return true;
    }

    return false;
  };
};