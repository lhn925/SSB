import { useSelector } from 'react-redux';

const useUpload = () => {
  return useSelector((state) => state?.uploadInfo);
};
export default useUpload;