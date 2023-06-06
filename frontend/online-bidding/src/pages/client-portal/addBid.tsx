import { apiGetBiddingBwicId, apiSubmitBidding, apiUpdateBidding } from '../../api/bidding.api';
import { BiddingItem, BidSubmitParams } from '../../models/bidding/bidding';
import { message, Button, Form, Input, Modal, Spin } from 'antd';
import { FC, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { setLoadingStatus, setBiddingItem } from '../../stores/bidding.store';
import { BWICItem } from '../../models/bwic/bwic';
import { WorkStatus } from '../../models';

export interface BidPageProps {
  bwic?: BWICItem,
  label: string
}

const layout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 16,
  },
};

const AddBidPage: FC<BidPageProps> = (props) => {
  const dispatch = useDispatch();

  const [isModalVisible, setIsModalVisible] = useState(false);
  const { username } = useSelector(state => state.user);
  const { loadingStatus } = useSelector(state => state.bidding);
  const [form] = Form.useForm();
  const { bwic, label } = props;
  let initValues: Partial<BidSubmitParams> = {};

  if (bwic) {
    initValues = {
      bwicId: bwic.id,
      clientId: username,
      size: bwic.size
    }
  }

  const showModal = () => {
    setIsModalVisible(true);
    fetchBiddingBwicId();
  };

  const onCancel = () => {
    setIsModalVisible(false);
  };

  const onOk = () => {
    form.submit();
  }

  const fetchBiddingBwicId = async () => {
    dispatch(
      setLoadingStatus(WorkStatus.IN_PROGRESS)
    );

    const { data, success } = await apiGetBiddingBwicId({
      bwicId: bwic?.id,
      clientId: username
    });

    if (success) {
      if (data) {
        form.setFieldsValue({
          id: data.id,
          bwicId: data.bwic?.id,
          clientId: username,
          price: data.price,
          size: data.size
        });
      }
    }

    dispatch(
      setLoadingStatus(success ? WorkStatus.SUCCESS : WorkStatus.ERROR)
    );
  }

  const onFinished = async (form: BidSubmitParams) => {
    const { data, success } = await (form.id ? apiUpdateBidding(form) : apiSubmitBidding(form));

    if (success) {
      if (data) {
        dispatch(
          setBiddingItem(data)
        );
        setIsModalVisible(false);

        message.success('Bidding success.');
        return;
      }
    }

    message.error('Bidding fail.');
  };

  return (
    <div>
      <Button type="primary" onClick={showModal}>
        {label}
      </Button>
      <Modal title="Bidding" visible={isModalVisible} onCancel={onCancel} onOk={onOk}>
        <Spin size="large" spinning={loadingStatus !== WorkStatus.SUCCESS} >
          <Form<BidSubmitParams> {...layout} form={form} initialValues={initValues} onFinish={onFinished} name="biddingForm">
            {/* <h2>Bidding</h2> */}
            <Form.Item name="id" hidden={true}>
            </Form.Item>
            <Form.Item name="bwicId" hidden={true}>
            </Form.Item>
            <Form.Item label="Bond Cusip">
              <Input disabled={true} value={bwic?.cusip} />
            </Form.Item>
            <Form.Item name="clientId" label="Client Id">
              <Input placeholder="Client Id" disabled={true} />
            </Form.Item>
            <Form.Item name="size" label="Size">
              <Input placeholder="Size" disabled={true} />
            </Form.Item>
            <Form.Item name="price" rules={[{ required: true, message: 'Please enter price.' }]} label="Price">
              <Input placeholder="Price" />
            </Form.Item>
          </Form>
        </Spin>
      </Modal>
    </div>
  );
};

export default AddBidPage;
