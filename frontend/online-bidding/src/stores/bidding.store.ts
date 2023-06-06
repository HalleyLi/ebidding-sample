import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { BiddingItem, BiddingState } from '../models/bidding/bidding';
import { WorkStatus } from '../models';

const initialState: BiddingState = {
  loadingStatus: WorkStatus.IDE,
  biddingList: [],
  totalCount: 0
};

const biddingSlice = createSlice({
  name: 'bidding',
  initialState,
  reducers: {
    setLoadingStatus(state, action: PayloadAction<WorkStatus>) {
      const status = action.payload;
      Object.assign(state, {
        loadingStatus: status
      })
    },
    setBiddingItems(state, action: PayloadAction<Partial<BiddingState>>) {
      const { biddingList } = action.payload;

      if (biddingList) {
        Object.assign(state, {
          loadingStatus: WorkStatus.SUCCESS,
          biddingList: biddingList,
          totalCount: biddingList.length
        })
      };

    },
    setBiddingItem(state, action: PayloadAction<BiddingItem>) {
      const biddingItem = action.payload;

      if (biddingItem) {
        const newBiddingItems = state.biddingList.filter(item => item.id !== biddingItem.id);

        const biddingItems = [biddingItem, ...newBiddingItems];
        Object.assign(state, {
          biddingList: biddingItems,
          totalCount: biddingItems.length
        })
      }
    },
    removeBiddingItem(state, action: PayloadAction<string>) {
      const deletedBiddingId = action.payload;

      if (deletedBiddingId) {
        let newBiddingItems = state.biddingList.filter(item => item.id !== deletedBiddingId);

        Object.assign(state, {
          biddingList: newBiddingItems,
          totalCount: newBiddingItems.length
        })
      }
    },
  },
});

export const { setLoadingStatus, setBiddingItems, setBiddingItem, removeBiddingItem } = biddingSlice.actions;

export default biddingSlice.reducer;