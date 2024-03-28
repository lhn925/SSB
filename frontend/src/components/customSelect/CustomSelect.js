
import React from 'react';

export function CustomSelect({
  selectBox,toggling
}) {

  return <>
    <div className="custom-select-div">
      <div className="custom-select-container mb-3 ">
        <div className={`custom-select ${selectBox.isOpen ? 'open' : ''}`}
             onClick={toggling}>
          {selectBox.selectedOption.value}
        </div>
        {selectBox.isOpen && (
            <ul className="custom-select-options">
              {selectBox.options.map( (option,index) => (
                  !option.sub ? <li
                      className={` ${selectBox.selectedOption.value === option.value ? 'custom-selected' : 'custom-unselected'}`}
                      onClick={selectBox.onOptionClicked(option)}
                                    data-id={option.value}
                                    key={option.value + index}>
                        {option.value}
                      </li> :(
                      // 서브 타입 추가
                      <React.Fragment key={index}>
                      <h3 className="track_type_title normal_font">{option.value}</h3>
                        <SubTypesSelect
                            selectBox={selectBox}
                            onOptionClicked={selectBox.onOptionClicked}
                            options={option.subTypes} />
                      </React.Fragment>)
              ))}
            </ul>
        )}
      </div>
    </div>
  </>
}

function SubTypesSelect({options,selectBox,onOptionClicked}) {

  return <>
    {options.map((option,index) => (
        <li
            className={` ${selectBox.selectedOption.value === option.value ? 'custom-selected' : 'custom-unselected'}`}
            onClick={onOptionClicked(option)}
            data-id={option.value}
            key={option.value + index}>
          {option.value}

        </li>
    ))}
  </>


}