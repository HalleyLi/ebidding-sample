import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { WorkStatus } from '../models';
import { BWICPopularState } from '@/models/bwic/bwic-popular';

const initialState: BWICPopularState = {
  loadingStatus: WorkStatus.IDE,
  bwicPopularList: [],
  totalCount: 0
};

const bwicPopularSlice = createSlice({
  name: 'bwicpopular',
  initialState,
  reducers: {
    setLoadingStatus(state, action: PayloadAction<WorkStatus>) {
      const status = action.payload;
      Object.assign(state, {
        loadingStatus: status
      })
    },
    setBwicPopularItems(state, action: PayloadAction<Partial<BWICPopularState>>) {
      const { bwicPopularList } = action.payload;

      if (bwicPopularList) {
        Object.assign(state, {
          loadingStatus: WorkStatus.SUCCESS,
          bwicPopularList: bwicPopularList,
          totalCount: bwicPopularList.length
        })
      };

    }
  },
});

export const { setLoadingStatus, setBwicPopularItems } = bwicPopularSlice.actions;

export default bwicPopularSlice.reducer;