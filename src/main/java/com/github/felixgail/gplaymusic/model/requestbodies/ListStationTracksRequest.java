package com.github.felixgail.gplaymusic.model.requestbodies;

import com.github.felixgail.gplaymusic.model.enums.StationSeedType;
import com.github.felixgail.gplaymusic.model.shema.Station;
import com.github.felixgail.gplaymusic.model.shema.Track;
import com.github.felixgail.gplaymusic.model.shema.snippets.StationSeed;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ListStationTracksRequest extends PagingRequest implements Serializable {
    @Expose
    private int contentFilter = 1;
    @Expose
    private List<StationRequest> stations;

    /**
     * Create a request to return a list of Tracks for a Station.
     *
     * @param station              A Station the {@link Track}s will be received for.
     * @param numEntries           number of {@link Track}s that will be requested. Max. 1000.
     * @param recentlyPlayedTracks A List of Tracks that have recently been played. Tracks from this list will not be
     *                             in the result.
     */
    public ListStationTracksRequest(Station station, int numEntries, List<Track> recentlyPlayedTracks) {
        this(station, numEntries, recentlyPlayedTracks, null, -1);
    }

    /**
     * Create a request to return a list of Tracks for a Station.
     *
     * @param station              A Station the {@link Track}s will be received for.
     * @param numEntries           number of {@link Track}s that will be requested. Max. 1000.
     * @param recentlyPlayedTracks A List of Tracks that have recently been played. Tracks from this list will not be
     *                             in the result.
     */
    public ListStationTracksRequest(Station station, int numEntries, List<Track> recentlyPlayedTracks, String nextPageToken, int maxResults) {
        super(nextPageToken, maxResults);
        stations = Collections.singletonList(new StationRequest(station, numEntries, recentlyPlayedTracks));
    }

    public int getContentFilter() {
        return contentFilter;
    }

    public List<StationRequest> getStations() {
        return stations;
    }

    private class StationRequest implements Serializable {
        @Expose
        private int numEntries;
        @Expose
        private String radioId;
        @Expose
        private List<RecentlyPlayedTrack> recentlyPlayed;

        StationRequest(Station station, int numEntries, List<Track> recentlyPlayedTracks) {
            if(station.getId() != null) {
                this.radioId = station.getId();
            }else {
                if(station.getSeed() != null) {
                    StationSeed seed = station.getSeed();
                    if(seed.getSeedType().equals(StationSeedType.CURATED_STATION)) {
                        this.radioId = seed.getSeed();
                    }
                }
                throw new IllegalArgumentException("Unable to extract radio ID");
            }
            if (numEntries > -1 && numEntries < 79) {
                this.numEntries = numEntries;
            } else {
                this.numEntries = 25;
            }
            if (recentlyPlayedTracks != null) {
                this.recentlyPlayed = new LinkedList<>();
                for (Track track : recentlyPlayedTracks) {
                    this.recentlyPlayed.add(new RecentlyPlayedTrack(track));
                }
            }
        }
    }

    private class RecentlyPlayedTrack implements Serializable {
        @Expose
        private String id;
        @Expose
        private int type;

        RecentlyPlayedTrack(Track track) {
            this.id = track.getID();
            this.type = track.getID().startsWith("T") ? 1 : 0;
        }
    }
}
