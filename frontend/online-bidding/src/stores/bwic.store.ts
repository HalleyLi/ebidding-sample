import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { BWICState, BWICItem, BWICBidItem } from '../models/bwic/bwic';
import { WorkStatus } from '../models';

const initialState: BWICState = {
  loadingStatus: WorkStatus.IDE,
  bwicList: [],
  totalCount: 0
};

const bwicSlice = createSlice({
  name: 'bwic',
  initialState,
  reducers: {
    setLoadingStatus(state, action: PayloadAction<WorkStatus>) {
      const status = action.payload;
      Object.assign(state, {
        loadingStatus: status
      })
    },
    setBwicItems(state, action: PayloadAction<Partial<BWICState>>) {
      const { bwicList } = action.payload;

      if (bwicList) {
        Object.assign(state, {
          loadingStatus: WorkStatus.SUCCESS,
          bwicList: bwicList,
          totalCount: bwicList.length
        })
      };

    },
    setBwicBidItems(state, action: PayloadAction<BWICBidItem[]>) {
      const bwicBidList = action.payload;

      if (bwicBidList) {
        // format BWIC List
        let bwicList: BWICItem[] = [];
        bwicBidList.forEach(bwicBid => {
          if (bwicBid) {
            const bwicItem: BWICItem = { ...bwicBid.bwicDto, ...{ bidList: bwicBid.bids } };
            bwicList.push(bwicItem);
          }
        });

        Object.assign(state, {
          loadingStatus: WorkStatus.SUCCESS,
          bwicList: bwicList,
          totalCount: bwicList.length
        })
      };

    },
    setBwicItem(state, action: PayloadAction<BWICItem>) {
      const bwicItem = action.payload;

      if (bwicItem) {
        const newBwicItems = state.bwicList.filter(item => item.id !== bwicItem.id);

        const bwicItems = [bwicItem, ...newBwicItems];
        Object.assign(state, {
          bwicList: bwicItems,
          totalCount: bwicItems.length
        })
      }
    },
    removeBwicItem(state, action: PayloadAction<string>) {
      const deletedBwicId = action.payload;

      if (deletedBwicId) {
        let newBwicItems = state.bwicList.filter(item => item.id !== deletedBwicId);

        Object.assign(state, {
          bwicList: newBwicItems,
          totalCount: newBwicItems.length
        })
      }
    },
  },
});

export const { setLoadingStatus, setBwicItems, setBwicBidItems, setBwicItem, removeBwicItem } = bwicSlice.actions;

export default bwicSlice.reducer;