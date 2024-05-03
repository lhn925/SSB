import { useSelector } from 'react-redux';

const useUserInfo = () => {
  return useSelector((state) => state?.userReducer);
};
export default useUserInfo;