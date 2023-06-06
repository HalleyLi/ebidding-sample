import { apiUpdateBidding } from '../../api/bidding.api';
import { BiddingItem, BidSubmitParams } from '../../models/bidding/bidding';
import { message, Button, Form, Input, Modal } from 'antd';
import { FC, useState } from 'react';
import { useDispatch } from 'react-redux';
import { setBiddingItem } from '../../stores/bidding.store';

const UpdateBidPage: FC<{ bid: BiddingItem }> = (props) => {
  const dispatch = useDispatch();

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [form] = Form.useForm();
  const { bid } = props;
  let initValues: Partial<BidSubmitParams> = {
    id: bid?.id,
    price: bid?.price
  }

  const showModal = () => {
    setIsModalVisible(true);
  };

  const onCancel = () => {
    setIsModalVisible(false);
  };

  const onOk = () => {
    form.submit();
  }

  const onFinished = async (form: BidSubmitParams) => {
    const { data, success } = await apiUpdateBidding(form);

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
        Edit Bidding
      </Button>

      <Modal title="Bidding" visible={isModalVisible} onCancel={onCancel} onOk={onOk}>
        <Form<BidSubmitParams> form={form} initialValues={initValues} onFinish={onFinished} name="biddingForm">
          <h2>Update Bidding</h2>
          <Form.Item name="id" hidden={true}>
          </Form.Item>
          <Form.Item name="price" rules={[{ required: true, message: 'Please enter price.' }]} label="Price">
            <Input placeholder="Price" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default UpdateBidPage;
