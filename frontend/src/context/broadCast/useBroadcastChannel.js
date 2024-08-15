import {createContext, useContext, useEffect, useState} from "react";

const BroadcastChannelContext = createContext(null);

export const useBroadcastChannel = () => {
  return useContext(BroadcastChannelContext);
}


export const BroadcastChannel = ({channelName,children}) => {

  const [channel, setChannel] = useState(null);

  useEffect(() => {

    const broadcastChannel = new BroadcastChannel(channelName);
    setChannel(broadcastChannel);

    return () => {
      broadcastChannel.close();
    }
  },[channelName])

  return (
      <BroadcastChannelContext.Provider value={channel}>
        {children}
      </BroadcastChannelContext.Provider>
  );
}