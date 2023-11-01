import Login from "modal/content/login/Login";
import Join from "modal/content/join/Join";

function Content({type,closeModal,setModalVisible}) {
  console.log("type "+ type)
  if (type == "join") {
    return (
        <>
          <Join closeModal={closeModal} setModalVisible={setModalVisible} />
        </>
    );
  } else {
    return (
        <>
          <Login closeModal={closeModal}/>
        </>
    );
  }

}

export default Content;