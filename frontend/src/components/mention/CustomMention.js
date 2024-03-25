import {Mention, MentionsInput} from "react-mentions";

export function CustomMention (tags,tagsList,onChangeEvent) {

  return <MentionsInput
      id="additionalTags"
      name="additionalTags"
      placeholder="Add tags to describe the genre and mood of your track"
      className="hashTag_mention mb-3" value={tags}
      onChange={onChangeEvent()}>
    <Mention trigger="#"
             className="mentionTextarea mention-tag"
             data={tagsList}
             displayTransform={(id, display) => `#${display}`}
             renderSuggestion={(suggestion, search, highlightedDisplay, index,
                 focused) => (
                 <div className={`user ${focused ? 'focused' : ''}`}>
                   {highlightedDisplay}
                 </div>)
             }/>
  </MentionsInput>;
}