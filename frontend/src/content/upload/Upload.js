import ModalContent from "modal/content/ModalContent";
import {Link} from "react-router-dom";
import {
  URL_UPLOAD, URL_UPLOAD_MY_TRACKS
} from "content/UrlEndpoints";
import Nav from "components/nav/Nav";

export function Upload({}) {

  const root = "upload";
  const tabs = [
    {id: "upload", title: "Upload", url: URL_UPLOAD},
    // {id: "my_tracks", title: "My Tracks", url: URL_UPLOAD_MY_TRACKS}
  ];
  return (
      <div className="row justify-content-center">
        <div className="col-12 col-md-10">
          <div className="tabs">
            <Nav currentRoot={root} tabs={tabs}/>
          </div>
        </div>
      </div>
  )
}