import dayjs from "dayjs";

export const convertDate = (value: Date) => {
    if (!value) {
        return '';
    }

    return dayjs(value).format("MM/DD/YYYY hh:ss:mm A");
}