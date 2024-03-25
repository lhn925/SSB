

export function CustomSelect({toggling,isOpen,selectedOption,onOptionClicked,options}) {

  return <>
    <div className="custom-select-div">
      <div className="custom-select-container mb-3 ">
        <div className={`custom-select ${isOpen ? 'open' : ''}`}
             onClick={toggling}>
          {selectedOption}
        </div>
        {isOpen && (
            <ul className="custom-select-options">
              {options.map(option => (
                  <li onClick={onOptionClicked(option)}
                      key={option}>
                    {option}
                  </li>
              ))}
            </ul>
        )}
      </div>
    </div>
  </>

}