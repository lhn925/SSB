export const FileUpload = ({name, ref, isMultiple, onChange, acceptArray}) => {
  return (
      <div>
        <input type="file"
               name={name}
               ref={ref}
               onChange={onChange}
               multiple={isMultiple}
               accept={acceptArray.toString()}
               className="visually-hidden"/>
      </div>
  );
};
