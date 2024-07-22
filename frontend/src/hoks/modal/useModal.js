import {useDispatch, useSelector} from "react-redux";
import {modalActions} from "store/modalType/modalType";

const useModal = () => {
  const dispatch = useDispatch();
  const modal = useSelector((state) => state?.modalType);
  const closeModal = () => {
    dispatch(modalActions.closeModal());
  };
  const openModal = () => {
    dispatch(modalActions.openModal());
  };
  const changeModalType = (type) => {
    dispatch(modalActions.changeType({type}));
  };

  return {openModal, closeModal, changeModalType, modal};
};

export default useModal;