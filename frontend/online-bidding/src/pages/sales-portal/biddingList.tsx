import MyTable, { dateSort, numberSort, strSort } from '../../components/core/table';
import { FC, useCallback, useEffect } from 'react';
import { apiGetAllBidding } from '../../api/bidding.api';
import { BiddingItem } from '../../models/bidding/bidding';
import { useDispatch, useSelector } from 'react-redux';
import { setBiddingItems, setLoadingStatus } from '../../stores/bidding.store';
import { WorkStatus } from '../../models';
import { Spin } from 'antd';
import { convertDate } from '../../utils/dateUtils';

const { Column } = MyTable;

const columns = [
  {
    title: "Bond Cusip",
    dataIndex: 'bondId',
    key: 'bondId',
    width: 150,
    sorter: (obj1: any, obj2: any) => strSort("bondId", obj1, obj2),
  },
  {
    title: 'Client Name',
    dataIndex: 'clientId',
    key: 'clientId',
    width: 150,
    sorter: (obj1: any, obj2: any) => strSort("clientId", obj1, obj2),
  },
  {
    title: 'Client Rank',
    dataIndex: 'rank',
    key: 'rank',
    width: 150,
    sorter: (obj1: any, obj2: any) => numberSort("rank", obj1, obj2),
  },
  {
    title: 'Price',
    dataIndex: 'price',
    key: 'price',
    width: 150,
    sorter: (obj1: any, obj2: any) => numberSort("price", obj1, obj2),
    render: (value: any, record: any) => {
      return `${value}`.replace(new RegExp(/\B(?=(\d{3})+(?!\d))/g), ',');
    }
  },
  {
    title: 'Size',
    dataIndex: 'size',
    key: 'size',
    width: 150,
    sorter: (obj1: any, obj2: any) => numberSort("size", obj1, obj2),
    render: (value: any, record: any) => {
      return `${value}`.replace(new RegExp(/\B(?=(\d{3})+(?!\d))/g), ',');
    }
  },
  {
    title: 'Bid Timestamp',
    dataIndex: 'effectiveTime',
    key: 'bidTimestamp',
    width: 200,
    render: (timestamp: any) => convertDate(timestamp),
    sorter: (obj1: any, obj2: any) => dateSort("bidTimestamp", obj1, obj2),
  },
  {
    title: 'Feedback',
    dataIndex: 'feedback',
    key: 'feedback',
    width: 150
  }
];

const BiddingListPage: FC = () => {
  const dispatch = useDispatch();
  const { loadingStatus, biddingList } = useSelector(state => state.bidding);

  const fetchAllBiddings = useCallback(async () => {
    dispatch(
      setLoadingStatus(WorkStatus.IN_PROGRESS)
    );

    const { data, success } = await apiGetAllBidding(null);
    if (success) {
      dispatch(
        setBiddingItems({ biddingList: data.rows })
      );
    }
  }, [])

  useEffect(() => {
    fetchAllBiddings();
  }, [fetchAllBiddings]);

  return (
    <div className="bwic-homepage">
      <Spin size="large" spinning={loadingStatus !== WorkStatus.SUCCESS} >
        <MyTable<BiddingItem> dataSource={biddingList} pagination={false} rowKey={record => record.id} height="100%" columns={columns}>
        </MyTable>
      </Spin>
    </div>
  );
};

export default BiddingListPage;
