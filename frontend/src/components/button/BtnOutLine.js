export function BtnOutLine({id, text, event, data_id}) {
  return (
      <button
          data-id={data_id}
          onClick={event} type="button" id={id}
              className="btn btn-outline-info mt-1">{text}</button>
  )

}