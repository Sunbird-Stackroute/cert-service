package org.sunbird;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ESResponseMapper {
    public List<Map<String, Object>> content;
    public int count;
    public String scrollId;
    public boolean timedOut;
    public int scrollCount;

    @JsonCreator
    public ESResponseMapper(
            @JsonProperty("hits") Map<String, Object> hits,
            @JsonProperty("_scroll_id") String scrollId,
            @JsonProperty("timed_out") boolean timedOut) {
        this.content = (List<Map<String, Object>>) hits.get("hits");
        this.count = (int) hits.get("total");
        this.scrollId = scrollId;
        this.timedOut = timedOut;
        this.scrollCount = content.size();
    }

    public List<Map<String, Object>> getContent() {
        return content;
    }

    public int getCount() {
        return count;
    }

    public String getScrollId() {
        return scrollId;
    }

    public boolean isTimedOut() {
        return timedOut;
    }

    public int getScrollCount() {
        return scrollCount;
    }

}