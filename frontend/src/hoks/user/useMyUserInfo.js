import { useSelector } from 'react-redux';

const useMyUserInfo = () => {
  return useSelector((state) => state?.userReducer);
};
export default useMyUserInfo;