export interface Locales<T = any> {
  /** Chinese */
  zh_CN: T;
  /** English */
  en_US: T;
}

export type Language = keyof Locales;

export interface PageData<T> {
  pageNum: number;
  pageSize: number;
  total: number;
  data: T[];
}

/** Response Data Model **/
export type Response<T = any> = {
  code: string;
  message: string;
  success: boolean;
  data: T;
};

export interface ListResponseData<T = any> {
  limit: string,
  offset: number,
  totalElements: 0,
  totalPages: 0,
  rows: T[],
}

export enum WorkStatus {
  IDE = 1,
  IN_PROGRESS,
  SUCCESS,
  ERROR
}

export interface SearchParams {
  startDate?: Date,
  endDate?: Date
}