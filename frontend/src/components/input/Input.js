export function Input({name,error,message,iconClass,onKeyUp,placeholder,type,onBlur}) {
  return (
      <>
        <div className={"input-group form-join "+iconClass
            + (error ? ' error' : ' on')}>
          <input type={type} name={name} id={name}
                 placeholder={placeholder}
                 onBlur={onBlur}
                 className={"form-control " + (error
                     ? 'border-danger' : '')} onKeyUp={onKeyUp}/>
        </div>
        <div className="form-text text-danger">
          <small id="id-NotThyme-msg">{error
              ? message : ''}</small>
        </div>
      </>
  )
}