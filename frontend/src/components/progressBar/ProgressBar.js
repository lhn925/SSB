
import "css/progress.css"

export const ProgressBar = ({percentage,height,width}) =>{
  return (
      <div className="progress" style={{height:`${height}px`,width:`${width}%`}}>
        <div
            className="progress-bar"
            role="progressbar"
            style={{ width: `${percentage ? percentage : 0}%` , display:`${percentage ? `block` : `none`}` }}
            aria-valuenow={percentage}
            aria-valuemin="0"
            aria-valuemax="25">
        </div>
      </div>
  );
}