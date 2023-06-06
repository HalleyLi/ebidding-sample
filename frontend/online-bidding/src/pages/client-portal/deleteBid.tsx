import { apiCancelBidding } from '../../api/bidding.api';
import { BidCancelParams } from '../../models/bidding/bidding';
import { Modal, message } from 'antd';
import { FC, useState } from 'react';
import { useDispatch } from 'react-redux';
import { removeBiddingItem } from '../../stores/bidding.store';
import { apiGetAllBwicsBids } from '../../api/bwic.api';
import { setBwicBidItems } from '../../stores/bwic.store';

const DeleteBidPage: FC<{ data?: any, isBwicBid?: boolean }> = (props) => {
    const dispatch = useDispatch();
    const { data, isBwicBid } = props;
    const [isModalVisible, setIsModalVisible] = useState(false);
    const deleteBidId: string = data?.id;

    const showModal = () => {
        setIsModalVisible(true);
    };

    const onCancel = () => {
        setIsModalVisible(false);
    };

    const deleteBid = async () => {
        const { data, success } = await apiCancelBidding({ id: deleteBidId } as BidCancelParams);

        if (success) {
            // if (bid) {
            dispatch(
                removeBiddingItem(deleteBidId)
            );
            setIsModalVisible(false);

            message.success('Cancel Bidding success.');

            if (isBwicBid) {
                const bwicResponse = await apiGetAllBwicsBids(null);
                if (bwicResponse.success) {
                    dispatch(
                        setBwicBidItems(bwicResponse.data?.rows)
                    )
                    message.success('Bwic list updated success.');
                    return;
                } else {
                    message.error('Bwic list updated fail.');
                }
                return;
            }

            return;
            // }
        }

        message.error('Cancel Bidding fail.');
    };

    return (
        <div className='delete-bid'>
            <a onClick={showModal}>Delete</a>
            <Modal title="Confirm" visible={isModalVisible} onOk={deleteBid} onCancel={onCancel}>
                <p>Are you confirm to delete this Bid?</p>
            </Modal>
        </div>
    );
};

export default DeleteBidPage;
