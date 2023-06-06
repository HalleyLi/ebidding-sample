import { Button, message, Modal } from 'antd';
import { FC, useState } from 'react';
import { useDispatch } from 'react-redux';
import { BwicCancelParams } from '@/models/bwic/bwic';
import { removeBwicItem } from '../../stores/bwic.store';
import { apiCancelBwic } from '../../api/bwic.api';
const DeleteBwicPage: FC<{ bwicData?: any }> = (props) => {
    const dispatch = useDispatch();
    const { bwicData } = props;
    const [isModalVisible, setIsModalVisible] = useState(false);

    const showModal = () => {
        setIsModalVisible(true);
    };

    const onCancel = () => {
        setIsModalVisible(false);
    };

    const deleteBwic = async () => {
        setIsModalVisible(false);
        const form: BwicCancelParams = { id: bwicData.id };
        const { data, success } = await apiCancelBwic(form);

        if (success) {
            if (data) {
                dispatch(
                    removeBwicItem(data?.id)
                );
                setIsModalVisible(false);
                console.log('Delete Bwic success');
                message.success('Delete Bwic success');
            }
        }
    };

    return (
        <div className='delete-bwic'>
            <a onClick={showModal}>Delete</a>
            <Modal title="Confirm" visible={isModalVisible} onOk={deleteBwic} onCancel={onCancel}>
                <p>Are you confirm to delete this BWIC?</p>
                <p>{bwicData?.name}</p>
            </Modal>
        </div>
    );
};

export default DeleteBwicPage;

