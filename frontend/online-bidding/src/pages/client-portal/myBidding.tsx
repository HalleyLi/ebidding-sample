import MyTable, { dateSort, generateFilters, numberSort, strSort } from '../../components/core/table';
import './index.scss'
import React, { FC, useCallback, useContext, useEffect, useRef, useState } from 'react';
import { BiddingItem, BidSubmitParams } from '../../models/bidding/bidding';
import { apiGetAllBidding, apiUpdateBidding } from '../../api/bidding.api';
import { useDispatch, useSelector } from 'react-redux';
import { setBiddingItem, setBiddingItems, setLoadingStatus } from '../../stores/bidding.store';
import { Form, Input, message, Spin } from 'antd';
import { WorkStatus } from '../../models';
import DeleteBidPage from './deleteBid';
import { convertDate } from '../../utils/dateUtils';
import { formatBidding } from '../../utils/biddingUtils';
import SearchForm from '../search';
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

const MyBiddingPage: FC = () => {
  const dispatch = useDispatch();
  const { loadingStatus, biddingList } = useSelector(state => state.bidding);
  const biddingTableData = formatBidding(biddingList);

  const components = {
    body: {
      row: EditableRow,
      cell: EditableCell
    },
    pagination: false
  };

  const columns = [
    {
      title: "Bond Cusip",
      dataIndex: 'bondCusip',
      key: 'bondCusip',
      width: 150,
      filters: generateFilters('bondCusip', biddingTableData),
      filterSearch: true,
      onFilter: (value: any, record: any) => record.bondCusip.startsWith(value),
      sorter: (obj1: any, obj2: any) => strSort("bondCusip", obj1, obj2),
    },
    {
      title: 'Client Name',
      dataIndex: 'clientId',
      key: 'clientId',
      width: 100,
      filters: generateFilters('clientId', biddingTableData),
      filterSearch: true,
      onFilter: (value: any, record: any) => record.clientId.startsWith(value),
      sorter: (obj1: any, obj2: any) => strSort("clientId", obj1, obj2),
    },
    {
      title: 'Client Rank',
      dataIndex: 'rank',
      key: 'rank',
      width: 150,
      filters: generateFilters('rank', biddingTableData),
      filterSearch: true,
      onFilter: (value: any, record: any) => record.rank.toString().startsWith(value),
      sorter: (obj1: any, obj2: any) => numberSort("rank", obj1, obj2),
    },
    {
      title: 'Price',
      dataIndex: 'price',
      key: 'price',
      width: 150,
      filters: generateFilters('price', biddingTableData),
      filterSearch: true,
      onFilter: (value: any, record: any) => record.price.toString().startsWith(value),
      sorter: (obj1: any, obj2: any) => numberSort("price", obj1, obj2),
      render: (value: any, record: any) => {
        return `${value}`.replace(new RegExp(/\B(?=(\d{3})+(?!\d))/g), ',');
      },
      onCell: (record: any) => ({
        record,
        editable: false,
        dataIndex: 'price',
        title: 'Price',
        handleSave: onFinished,
      })
    },
    {
      title: 'Size',
      dataIndex: 'size',
      key: 'size',
      width: 150,
      filters: generateFilters('size', biddingTableData),
      filterSearch: true,
      onFilter: (value: any, record: any) => record.size.toString().startsWith(value),
      sorter: (obj1: any, obj2: any) => numberSort("size", obj1, obj2),
      render: (value: any, record: any) => {
        return `${value}`.replace(new RegExp(/\B(?=(\d{3})+(?!\d))/g), ',');
      },
    },
    {
      title: 'Bid Timestamp',
      dataIndex: 'effectiveTime',
      key: 'effectiveTime',
      width: 200,
      filters: generateFilters('effectiveTime', biddingTableData),
      filterSearch: true,
      onFilter: (value: any, record: any) => record.effectiveTime.startsWith(value),
      sorter: (obj1: any, obj2: any) => dateSort("effectiveTime", obj1, obj2),
    },
    {
      title: 'Feedback',
      dataIndex: 'feedback',
      key: 'feedback',
      width: 150,
    },
    // {
    //   title: 'Edit',
    //   key: 'editAction',
    //   render: (text: any, record: any) => (
    //     <UpdateBidPage bid={record}></UpdateBidPage>
    //   ),
    // },
    // {
    //   title: 'Cancel Bidding',
    //   key: 'cancelAction',
    //   render: (text: any, record: any) => (
    //     <DeleteBidPage data={record}></DeleteBidPage>
    //   ),
    // },
  ];

  const onFinished = async (row: any) => {
    const form: Partial<BidSubmitParams> = {
      id: row.id,
      price: row.price
    };
    const { data, success } = await apiUpdateBidding(form);

    if (success) {
      if (data) {
        dispatch(
          setBiddingItem(data)
        );

        message.success('Bidding success.');
        return;
      }
    }

    message.error('Bidding fail.');
  };

  const fetchAllBiddings = useCallback(async (search?: any) => {
    dispatch(
      setLoadingStatus(WorkStatus.IN_PROGRESS)
    );

    const { data, success } = await apiGetAllBidding(search);
    if (success) {
      dispatch(
        setBiddingItems({ biddingList: data.rows })
      );
    }
  }, [])

  useEffect(() => {
    fetchAllBiddings();
  }, [fetchAllBiddings]);

  console.log("biddingList", biddingList)

  return (
    <div className="bidding-homepage">
      <Spin size="large" spinning={loadingStatus !== WorkStatus.SUCCESS} >
        <SearchForm onFinish={fetchAllBiddings}></SearchForm>
        <MyTable
          components={components} dataSource={biddingTableData} rowKey={record => record.id} height="100%" columns={columns}>
        </MyTable>
      </Spin>
    </div>
  );
};

export default MyBiddingPage;
