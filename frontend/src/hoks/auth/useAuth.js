import { useSelector } from 'react-redux';

const useAuth = () => {
  return useSelector((state) => state?.authReducer);
};
export default useAuth;