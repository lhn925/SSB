package sky.Sss.domain.feed.model;

import sky.Sss.domain.user.model.ContentsType;

public enum FeedType {
    TRACK, PLAYLIST, TRACK_REPOST,PLAYLIST_REPOST;


    public static FeedType getRepostFeedType (ContentsType contentsType) {
        FeedType feedType = null;
        switch (contentsType) {
            case TRACK ->feedType = FeedType.TRACK_REPOST;
            case PLAYLIST ->feedType = FeedType.PLAYLIST_REPOST;
        }
        return feedType;
    }

}
