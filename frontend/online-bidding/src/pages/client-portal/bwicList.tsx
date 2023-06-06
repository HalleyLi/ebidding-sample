import MyTable, { generateFilters, strSort } from '../../components/core/table';
import './index.scss'
import { FC, useCallback, useContext, useEffect, useRef, useState } from 'react';
import AddBidPage from './addBid';
import { apiGetAllBwicsBids } from '../../api/bwic.api';
import { useDispatch, useSelector } from 'react-redux';
import { setBwicBidItems, setLoadingStatus } from '../../stores/bwic.store';
import { WorkStatus } from '../../models';
import { Button, Form, Input, InputNumber, message, Spin, Tag } from 'antd';
import SearchForm from '../search';
import DeleteBidPage from './deleteBid';
import React from 'react';
import { BidSubmitParams } from '../../models/bidding/bidding';
import { apiSubmitBidding, apiUpdateBidding } from '../../api/bidding.api';
import { setBiddingItem } from '../../stores/bidding.store';
import { BWICBidingItem, BWICBidInsertItem } from '../../models/bwic/bwic';
import { convertDate } from '../../utils/dateUtils';

const EditableContext = React.createContext(null);

const EditableRow = ({ index, ...props }: any) => {
  const [form] = Form.useForm() as any;
  return (
    <Form form={form} component={false}>
      <EditableContext.Provider value={form}>
        <tr {...props} />
      </EditableContext.Provider>
    </Form>
  );
};

const EditableCell = ({ title, editable, children, dataIndex, record, handleSave, ...restProps }: any) => {
  const [editing, setEditing] = useState(false);
  const inputRef = useRef(null) as any;
  const form = useContext(EditableContext) as any;
  useEffect(() => {
    if (editing) {
      inputRef.current.focus();
    }
  }, [editing]);

  const toggleEdit = () => {
    setEditing(!editing);
    // record = record && record.bidList && record.bidList.length !== 0 ? record.bidList[0] : new BWICBidingItem();
    // record = record && record.bidList && record.bidList.length !== 0? record.bidList[0] : new BWICBidingItem();
    form.setFieldsValue({
      [dataIndex]: record[dataIndex],
    });
  };

  const save = async () => {
    try {
      const values = await form.validateFields();
      toggleEdit();
      handleSave({ ...record, ...values });
    } catch (errInfo) {
      console.log('Save failed:', errInfo);
    }
  };

  let childNode = children;

  if (editable) {
    childNode = editing ? (
      <Form.Item
        style={{
          margin: 0,
        }}
        name={dataIndex}
        rules={[
          {
            required: true,
            message: `${title} is required.`,
          },
        ]}
      >
        <Input ref={inputRef} onPressEnter={save} onBlur={save} />
      </Form.Item>
    ) : (
      <div
        className="editable-cell-value-wrap"
        style={{
          paddingRight: 24,
        }}
        onClick={toggleEdit}
      >
        {children}
      </div>
    );
  }

  return <td {...restProps}>{childNode}</td>;
};

// const { Column } = MyTable;

const BwicListPage: FC = () => {
  const dispatch = useDispatch();
  const { loadingStatus, bwicList } = useSelector(state => state.bwic);

  const fetchAllBwics = useCallback(async (search?: any) => {
    dispatch(
      setLoadingStatus(WorkStatus.IN_PROGRESS)
    );

    const { data, success } = await apiGetAllBwicsBids(search);
    if (success) {
      dispatch(
        setBwicBidItems(data?.rows)
      );
    }
  }, [dispatch]);

  useEffect(() => {
    fetchAllBwics();
  }, [fetchAllBwics]);

  const components = {
    body: {
      row: EditableRow,
      cell: EditableCell
    },
    pagination: false
  };

  const getPrice = (record: any) => {
    return record && record.bidList && record.bidList.length !== 0 ? record.bidList[0].price : 0;
  }

  const getFeedback = (record: any) => {
    return record && record.bidList && record.bidList.length !== 0 ? record.bidList[0].feedback : null;
  }

  const bidcolumns : any = [
    {
      title: "Bond Cusip",
      dataIndex: 'cusip',
      key: 'cusip',
      width: 150,
      fixed: "left",
      filters: generateFilters('cusip', bwicList),
      filterSearch: true,
      onFilter: (value: any, record: any) => record.cusip.startsWith(value),
      sorter: (obj1: any, obj2: any) => strSort("bwicList", obj1, obj2),
    },
    {
      title: 'Issuer',
      dataIndex: 'issuer',
      key: 'issuer',
      width: 150,
      filters: generateFilters('issuer', bwicList),
      filterSearch: true,
      onFilter: (value: any, record: any) => record.issuer.startsWith(value),
      sorter: (obj1: any, obj2: any) => strSort("issuer", obj1, obj2),
    },
    {
      title: 'Due Date',
      dataIndex: 'dueDate',
      key: 'dueDate',
      width: 200,
      filters: generateFilters('dueDate', bwicList),
      filterSearch: true,
      render: (timestamp: any) => convertDate(timestamp),
      onFilter: (value: any, record: any) => record.dueDate.toString().startsWith(value),
      sorter: (obj1: any, obj2: any) => strSort("dueDate", obj1, obj2),
    },
    {
      title: 'Size',
      dataIndex: 'size',
      key: 'size',
      width: 120,
      filters: generateFilters('size', bwicList),
      filterSearch: true,
      onFilter: (value: any, record: any) => record.size.toString().startsWith(value),
      sorter: (obj1: any, obj2: any) => strSort("size", obj1, obj2),
      // formatter: (value: any, record: any) => { return `$ ${value}`.replace(new RegExp(/\B(?=(\d{3})+(?!\d))/g), ',') },
      // parser: (value: any, record: any) => { return value.replace(new RegExp(/\$\s?|(,*)/g), '') },
      render: (value: any, record: any) => {
        return `${value}`.replace(new RegExp(/\B(?=(\d{3})+(?!\d))/g), ',');
      }
    },
    {
      title: 'Coupon',
      dataIndex: 'coupon',
      key: 'coupon',
      width: 120,
      filters: generateFilters('coupon', bwicList),
      filterSearch: true,
      onFilter: (value: any, record: any) => record.coupon.toString().startsWith(value),
      sorter: (obj1: any, obj2: any) => strSort("coupon", obj1, obj2),
    },
    {
      title: 'Rating',
      dataIndex: 'rating',
      key: 'rating',
      width: 120,
      filters: generateFilters('rating', bwicList),
      filterSearch: true,
      onFilter: (value: any, record: any) => record.rating.toString().startsWith(value),
      sorter: (obj1: any, obj2: any) => strSort("rating", obj1, obj2),
    },
    {
      title: 'Price',
      dataIndex: 'price',
      key: 'price',
      width: 100,
      filters: generateFilters('price', bwicList),
      filterSearch: true,
      onFilter: (value: any, record: any) => record.price.toString().startsWith(value),
      sorter: (obj1: any, obj2: any) => strSort("price", obj1, obj2),
      render: (value: any, record: any) => {
        if (record.isOverDue) {
          // return <div className='grey-price'>{getPrice(record)}</div>
          // return <Tag color="red">￥{getPrice(record)}</Tag>
          return <InputNumber color='red'
            min={0}
            value={getPrice(record)}
            formatter={value => `${value}`.replace(new RegExp(/\B(?=(\d{3})+(?!\d))/g), ',')}
            parser={(value: any) => value.replace(new RegExp(/\$\s?|(,*)/g), '')}
            step="0.00000000000001"
            stringMode
            disabled={true}
          />
        } else {
          // return <div className='green-price'>{getPrice(record)}</div>
          return <InputNumber
            min={0}
            value={getPrice(record)}
            formatter={value => `${value}`.replace(new RegExp(/\B(?=(\d{3})+(?!\d))/g), ',')}
            parser={(value: any) => value.replace(new RegExp(/\$\s?|(,*)/g), '')}
            step="0.00000000000001"
            stringMode
          />
          // <Input prefix="￥" value={getPrice(record)}/>
          // return <Tag color="green">￥{getPrice(record)} </Tag>
        }
      },
      onCell: (record: any) => ({
        record: record && record.bidList && record.bidList.length !== 0 ? record.bidList[0] : new BWICBidingItem(record.id, record.clientId, record.size, record.isOverDue),
        // record: record && record.bidList && record.bidList.length !== 0 ? record : new BWICBidInsertItem(record.id, record.size, record.isOverDue),
        editable: !record.isOverDue,
        dataIndex: 'price',
        title: 'Price',
        handleSave: onFinished,
      })
    },
    {
      title: 'Start Price',
      dataIndex: 'startingPrice',
      key: 'startingPrice',
      width: 120,
      filters: generateFilters('startingPrice', bwicList),
      filterSearch: true,
      onFilter: (value: any, record: any) => record.startingPrice.toString().startsWith(value),
      sorter: (obj1: any, obj2: any) => strSort("startingPrice", obj1, obj2),
    },
    {
      title: 'Status',
      dataIndex: 'isOverDue',
      key: 'isOverDue',
      width: 100,
      filters: generateFilters('isOverDue', bwicList),
      filterSearch: true,
      onFilter: (value: any, record: any) => record.isOverDue.startsWith(value),
      sorter: (obj1: any, obj2: any) => strSort('isOverDue', obj1, obj2),
      render: (value: any, record: any) => {
        return record.isOverDue ? 'overdue' : 'active';
      }
    },
    {
      title: 'Feedback',
      dataIndex: 'feedback',
      key: 'feedback',
      width: 150,
      filters: generateFilters('feedback', bwicList),
      filterSearch: true,
      render: (value: any, record: any) => getFeedback(record),
      onFilter: (value: any, record: any) => record.bidList[0]["feedback"].toString().startsWith(value),
    },
    // {
    //   title: 'Action',
    //   key: 'action',
    //   render: (text: any, record: any) => {
    //     if (record.isOverDue) {
    //       return '';
    //     } else {
    //       return <AddBidPage bwic={record} label={"Bidding"}></AddBidPage>
    //     }
    //   }
    // },
    {
      title: 'Cancel Bidding',
      key: 'cancelAction',
      width: 100,
      render: (text: any, record: any) => {
        if (record && record.bidList && record.bidList.length !== 0 && !record.isOverDue) {
          return <DeleteBidPage data={record.bidList[0]} isBwicBid={true}></DeleteBidPage>
        } else {
          return '';
        }
      },
    },
  ]

  const onFinished = async (row: BWICBidingItem) => {
    const form: Partial<BidSubmitParams> = {
      id: row.id,
      price: row.price,
      bwicId: row.bwicId,
      size: row.size,
      isOverDue: row.isOverDue
    };
    if (form.isOverDue) {
      message.error('The Bwic is overdue. You couldn\'t bidding.');
      return;
    }
    const { data, success } = await (form.id ? apiUpdateBidding(form) : apiSubmitBidding(form));
    if (success) {
      if (data) {
        dispatch(
          setBiddingItem(data)
        );
        message.success('Bidding success.');
        const bwicResponse = await apiGetAllBwicsBids(null);
        if (bwicResponse.success) {
          dispatch(
            setBwicBidItems(bwicResponse.data?.rows)
          )
          // message.success('Bwic list updated success.');
          return;
        } else {
          // message.error('Bwic list updated fail.');
        }
        return;
      }
    } else {
      message.error('Bidding fail.');
    }
  };

  return (
    <div className="bwic-homepage">
      {/* <Button type="primary" className='bwic-add-button' onClick={fetchAllBwics} ghost>Refresh</Button> */}
      <Spin size="large" spinning={loadingStatus !== WorkStatus.SUCCESS} >
        <SearchForm onFinish={fetchAllBwics}></SearchForm>
        {/* <MyTable<BWICItem> dataSource={bwicList} rowKey={record => record.id} height="100%">
          <Column title="Bond Cusip" dataIndex="cusip" key="cusip"
            filters={generateFilters('cusip', bwicList)}
            filterSearch={true}
            onFilter={(value, record: any) => record.cusip.startsWith(value)}
            sorter={(obj1: any, obj2: any) => strSort("cusip", obj1, obj2)} />
          <Column title="Issuer" dataIndex="issuer" key="issuer"
            filters={generateFilters('issuer', bwicList)}
            filterSearch={true}
            onFilter={(value, record: any) => record.issuer.startsWith(value)}
            sorter={(obj1: any, obj2: any) => strSort("issuer", obj1, obj2)} />
          <Column title="Sales Id" dataIndex="clientId" key="clientId"
            filterSearch={true}
            filters={generateFilters('clientId', bwicList)}
            onFilter={(value, record: any) => record.issuer.startsWith(value)}
            sorter={(obj1: any, obj2: any) => strSort("clientId", obj1, obj2)} />
          <Column title="Due Date" width={200} dataIndex="dueDate" key="dueDate" render={(timestamp: any) => convertDate(timestamp)}
            filterSearch={true}
            filters={generateFilters('dueDate', bwicList)}
            onFilter={(value, record: any) => record.dueDate.startsWith(value)}
            sorter={(obj1: any, obj2: any) => strSort("dueDate", obj1, obj2)} />
          <Column title="Size" dataIndex="size" key="size"
            filterSearch={true}
            filters={generateFilters('size', bwicList)}
            onFilter={(value, record: any) => record.size?.toString().startsWith(value)}
            sorter={(obj1: any, obj2: any) => strSort("size", obj1, obj2)} />
          <Column
            title="Action"
            key="action"
            render={(text, record: any) => (
              <AddBidPage bwic={record} label={"Bidding"}></AddBidPage>
            )}
          />

          <Column
            title="Action"
            key="action"
            render={(text, record: any) => (
              <DeleteBidPage data={record}></DeleteBidPage>
            )}
          />
        </MyTable> */}
        <MyTable components={components} dataSource={bwicList} rowKey={record => record.id}
          height="100%" columns={bidcolumns} scroll={{ x: 1500 }}></MyTable>
      </Spin>
    </div>
  );
};

export default BwicListPage;
