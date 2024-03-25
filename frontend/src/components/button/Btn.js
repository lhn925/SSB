export function Btn({id, text, event, data_id,width}) {
  return (
      <button type="button" data-id={data_id} id={id}
              style={{width:`${width}%`}}
              onClick={event}
              className="btn btn-primary btn-block btn-dark">{text}
      </button>

  )

}