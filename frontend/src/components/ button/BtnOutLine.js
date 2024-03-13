
export function ButtonOutLine({id,text,event}) {
  return (
      <button onClick={event} type="button" id={id} className="btn btn-outline-info mt-1">{text}</button>
  )

}