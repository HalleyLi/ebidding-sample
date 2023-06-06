import { Response } from '../models';
import axios, { AxiosRequestConfig } from 'axios';

const axiosInstance = axios.create({
  timeout: 6000,
});

axiosInstance.interceptors.response.use(
  config => {
    if (config?.data?.message) {
      // $message.success(config.data.message)
    }

    return config?.data;
  },
  error => {
    // if needs to navigate to login page when request exception
    // history.replace('/login');
    let errorMessage = 'System Exception';

    if (error?.message?.includes('Network Error')) {
      errorMessage = 'Network Error';
    } else {
      errorMessage = error?.message;
    }
    console.dir(error);

    return {
      success: false,
      message: errorMessage,
      code: null,
      data: null
    };
  },
);

type Method = 'get' | 'post';

export type MyResponse<T = any> = Promise<Response<T>>;

/**
 *
 * @param method - request methods
 * @param url - request url
 * @param data - request data or params
 */
export const request = <T = any>(
  method: Method,
  url: string,
  data?: any,
  config?: AxiosRequestConfig,
): MyResponse<T> => {
  // const prefix = '/api'
  const prefix = '';
  const token: string = 'Bearer ' + localStorage.getItem("t") || "";

  url = prefix + url;
  if (method === 'post') {
    return axiosInstance.post(url, data, {
      ...{ headers: { 'Authorization': token } },
      ...config
    });
  } else {
    return axiosInstance.get(url, {
      params: data,
      ...{ headers: { 'Authorization': token } },
      ...config,
    });
  }
};
