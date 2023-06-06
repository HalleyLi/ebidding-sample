import { apiSubmitBwic } from '../../api/bwic.api';
import { BWICItem, BwicSubmitParams } from '@/models/bwic/bwic';
import { setBwicItem } from '../../stores/bwic.store';
import { Button, DatePicker, Form, Input, message, Modal } from 'antd';
import { FC, useState } from 'react';
import { useDispatch } from 'react-redux';
const UpdateBwicPage: FC<{ bwic?: BWICItem }> = (props) => {
  const dispatch = useDispatch();

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [form] = Form.useForm();
  const { bwic } = props;
  let initValues: Partial<BwicSubmitParams> = {
    id: bwic?.id,
    clientId: bwic?.clientId,
    // dueDate: bwic?.dueDate,
    size: bwic?.size
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

  const onFinished = async (form: BwicSubmitParams) => {
    const { data, success } = await apiSubmitBwic(form);

    if (success) {
      if (data) {
        dispatch(
          setBwicItem(data)
        );
        setIsModalVisible(false);

        message.success('Update BWIC success.');
        return;
      }
    }

    message.error('Update BWIC fail.');
  };

  return (
    <div>
      <a onClick={showModal}>Edit</a>
      <Modal title="Bidding" visible={isModalVisible} onCancel={onCancel} onOk={onOk}>
        <Form<BwicSubmitParams> form={form} initialValues={initValues} onFinish={onFinished} name="bwicForm">
          <h2>Update BWIC</h2>
          <Form.Item name="id" hidden={true}>
          </Form.Item>
          <Form.Item name="clientId" rules={[{ required: true, message: 'Please enter client ID.' }]} label="Client ID">
            <Input placeholder="Client ID" />
          </Form.Item>
          <Form.Item name="dueDate" label="Due Date" rules={[{ required: true, message: 'Please select time!' }]}>
            <DatePicker showTime format="YYYY-MM-DD" />
          </Form.Item>
          <Form.Item name="size" rules={[{ required: true, message: 'Please enter Size.' }]} label="Size">
            <Input placeholder="Size" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default UpdateBwicPage;
