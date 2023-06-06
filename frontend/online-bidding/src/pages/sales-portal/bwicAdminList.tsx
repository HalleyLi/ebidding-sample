import { Button, Drawer, Spin } from 'antd';
import MyTable, { generateFilters, strSort } from '../../components/core/table';
import { Children, FC, useCallback, useEffect, useState } from 'react';
import AddBwicPage from './addBwic';
import DeleteBwicPage from './deleteBwic';
import { apiGetAllBwicsBids } from '../../api/bwic.api';
import { BWICItem } from '../../models/bwic/bwic';
import { useDispatch, useSelector } from 'react-redux';
import { setBwicBidItems, setLoadingStatus } from '../../stores/bwic.store';
import { WorkStatus } from '../../models';
import { convertDate } from '../../utils/dateUtils';
import UpdateBwicPage from './updateBwic';
import SearchForm from '../search';

const { Column } = MyTable;

const BwicAdminPage: FC = () => {
  const dispatch = useDispatch();
  const { loadingStatus, bwicList } = useSelector(state => state.bwic);
  const [drawerVisible, setDrawerVisible] = useState<boolean>(false);

  const showDrawer = () => {
    setDrawerVisible(true);
  }

  const onClose = () => {
    setDrawerVisible(false);
  };

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
  }, [])

  useEffect(() => {
    fetchAllBwics();
  }, [fetchAllBwics]);

  return (
    <div className="bwic-homepage">
      <Button type="primary" className='bwic-add-button' onClick={showDrawer} ghost>Add Bwic</Button>
      <SearchForm onFinish={fetchAllBwics}></SearchForm>
      <Spin size="large" spinning={loadingStatus !== WorkStatus.SUCCESS} >
        <MyTable<BWICItem> pagination={false} dataSource={bwicList} rowKey={record => record.id} scroll={{ x: 1300 }}>
          <Column title="Bond Cusip" dataIndex="cusip" key="cusip"
            width={150}
            filters={generateFilters('cusip', bwicList)}
            filterSearch={true}
            onFilter={(value, record: any) => record.cusip.startsWith(value)}
            sorter={(obj1: any, obj2: any) => strSort("cusip", obj1, obj2)}
            fixed= "left" />
          <Column title="Issuer" dataIndex="issuer" key="issuer"
            filters={generateFilters('issuer', bwicList)}
            filterSearch={true}
            width={150}
            onFilter={(value, record: any) => record.issuer.startsWith(value)}
            sorter={(obj1: any, obj2: any) => strSort("issuer", obj1, obj2)} />
          <Column title="Bond Owner" dataIndex="clientId" key="clientId"
            filterSearch={true}
            width={150}
            filters={generateFilters('clientId', bwicList)}
            onFilter={(value, record: any) => record.clientId.startsWith(value)}
            sorter={(obj1: any, obj2: any) => strSort("clientId", obj1, obj2)} />
          <Column title="Due Date" width={200} dataIndex="dueDate" key="dueDate" render={(timestamp: any) => convertDate(timestamp)}
            filterSearch={true}
            filters={generateFilters('dueDate', bwicList)}
            onFilter={(value, record: any) => record.dueDate.startsWith(value)}
            sorter={(obj1: any, obj2: any) => strSort("dueDate", obj1, obj2)} />
          <Column title="Size" dataIndex="size" key="size" width={120}
            filterSearch={true}
            filters={generateFilters('size', bwicList)}
            onFilter={(value, record: any) => record.size?.toString().startsWith(value)}
            sorter={(obj1: any, obj2: any) => strSort("size", obj1, obj2)}
            render={(text, record: any) => {
              return (record.size) ? `${record.size}`.replace(new RegExp(/\B(?=(\d{3})+(?!\d))/g), ',') : '';
            }}
            />
          <Column title="Top 1 Price" dataIndex="top1Price" key="top1Price"
            width={120} render={(text, record: any) => {
              return (record.bidList?.length > 0 && record.bidList[0]["price"]) ? `${record.bidList[0]["price"]}`.replace(new RegExp(/\B(?=(\d{3})+(?!\d))/g), ',') : '';
            }
            } />
          <Column title="Top 1 Client" dataIndex="top1Client" key="top1Client"
            width={120} render={(text, record: any) =>
              (record.bidList?.length > 0 && record.bidList[0]["clientId"])
            } />
          <Column title="Top 2 Price" dataIndex="top2Price" key="top2Price"
            width={120} render={(text, record: any) => {
              if(record.bidList?.length > 0 && record.bidList[0]["price"]) {
                const value = record.bidList[0]["price"];
                return `${value}`.replace(new RegExp(/\B(?=(\d{3})+(?!\d))/g), ',');
              } else {
                return '';
              }
            }
            } />
          <Column title="Top 2 Client" dataIndex="top2Client" key="top2Client"
            width={120} render={(text, record: any) =>
              (record.bidList?.length > 1 && record.bidList[1]["clientId"])
            } />
          {/* <Column
            title="Edit"
            key="editAction"
            fixed="right"
            render={(text, record: any) => (
              <UpdateBwicPage bwic={record}></UpdateBwicPage>
            )} /> */}
          <Column
            title="Action"
            key="action"
            fixed="right"
            render={(text, record: any) => (
              <DeleteBwicPage bwicData={record}></DeleteBwicPage>
            )} />
        </MyTable>
        <Drawer
          title="Add Bwic"
          width={450}
          onClose={onClose}
          visible={drawerVisible}
          bodyStyle={{ padding: "1rem" }}>
          <AddBwicPage onClose={onClose} />
        </Drawer>
      </Spin>
    </div>
  );
};

export default BwicAdminPage;
