import TableColumn from '../table-column';
import { Table, TableProps } from 'antd';
import './index.scss'
import { convertDate } from '../../../utils/dateUtils';

interface MyTableProps<T extends object> extends TableProps<T> {
  height?: string;
}

const MyTable = <T extends object = object>(props: MyTableProps<T>) => {
  const { height, pagination, ...rest } = props;

  // const defaultPagination = {
  //   size: 'default',
  //   showQuickJumper: true,
  //   showSizeChanger: true,
  //   pageSizeOptions: ['10', '20', '50', '100', '200'],
  //   defaultPageSize: 50,
  //   showTotal: true
  // };

  // const combinedPagination = typeof pagination === 'object' ? { ...defaultPagination, ...pagination } : {};

  return (
    <div style={{ height }} className="tableClass">
      <Table<T> {...rest} scroll={{ x: 'max-content', y: '100%' }} pagination={false} />
    </div>
  );
};

MyTable.defaultProps = {
  size: 'small',
  height: 'auto',
} as MyTableProps<any>;

MyTable.Column = TableColumn;
MyTable.ColumnGroup = Table.ColumnGroup;

export default MyTable;

// Sort function

export const strSort = (compareField: string, obj1: any, obj2: any) => {
  if (!compareField || !obj1 || !obj2) {
    return 0;
  }

  const value1 = obj1[compareField];
  const value2 = obj2[compareField];
  if (value1 < value2) {
    return -1;
  } else if (value1 > value2) {
    return 1;
  } else {
    return 0;
  }
}

export const numberSort = (compareField: string, obj1: any, obj2: any) => {
  if (!compareField || !obj1 || !obj2) {
    return 0;
  }

  const value1 = obj1[compareField];
  const value2 = obj2[compareField];
  if (value1 < value2) {
    return -1;
  } else if (value1 > value2) {
    return 1;
  } else {
    return 0;
  }
}

export const dateSort = (compareField: string, obj1: any, obj2: any) => {
  if (!compareField || !obj1 || !obj2) {
    return 0;
  }

  const value1 = convertDate(obj1[compareField]);
  const value2 = convertDate(obj2[compareField]);
  if (value1 < value2) {
    return -1;
  } else if (value1 > value2) {
    return 1;
  } else {
    return 0;
  }
}

// Search function

export const generateFilters = (field: string, objList: any[]) => {
  return [... new Set(objList.map(obj => { return obj[field] }))].map(item => { return { text: item, value: item } });
}