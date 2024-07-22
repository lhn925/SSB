// Context 생성
import {createContext, useCallback, useContext, useState} from "react";

const DropdownContext = createContext();

// Context Provider 컴포넌트
// DropdownProvider 전역 관리
export const DropdownProvider = ({ children }) => {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  const toggleDropdown = useCallback(() => {
    setIsDropdownOpen(prevState => !prevState);
  }, []);

  const closeDropdown = useCallback(() => {
    setIsDropdownOpen(false);
  }, []);
  const openDropdown = useCallback(() => {
    setIsDropdownOpen(true);
  }, []);
  const handleClick = (e) => {
    e.stopPropagation();
    toggleDropdown();
  };
  return (
      <DropdownContext.Provider value={{ isDropdownOpen, toggleDropdown ,closeDropdown,openDropdown,handleClick}}>
        {children}
      </DropdownContext.Provider>
  );
};

// Context를 사용하는 커스텀 훅
export const useDropdown = () => {
  return useContext(DropdownContext);
};