package com.brentvatne.exoplayer;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Vector;

import javax.annotation.Nullable;

public class ReactExoplayerViewManager extends ViewGroupManager<ReactExoplayerView> {

    private static final String REACT_CLASS = "RCTVideo";

    private static final String PROP_SRC = "src";
    private static final String PROP_SRC_URI = "uri";
    private static final String PROP_SRC_TYPE = "type";
    private static final String PROP_RESIZE_MODE = "resizeMode";
    private static final String PROP_REPEAT = "repeat";
    private static final String PROP_PAUSED = "paused";
    private static final String PROP_MUTED = "muted";
    private static final String PROP_VOLUME = "volume";
    private static final String PROP_PROGRESS_UPDATE_INTERVAL = "progressUpdateInterval";
    private static final String PROP_SEEK = "seek";
    private static final String PROP_RATE = "rate";
    private static final String PROP_PLAY_IN_BACKGROUND = "playInBackground";
    private static final String PROP_DISABLE_FOCUS = "disableFocus";

    public static final String PROP_SELECTED_VIDEO_TRACK = "selectedVideoTrack";
    public static final String PROP_SELECTED_AUDIO_TRACK = "selectedAudioTrack";
    public static final String PROP_SELECTED_TEXT_TRACK = "selectedTextTrack";


    private static final String PROP_USE_PEER = "usePeer";
    private static final String PROP_SIGMA_UID = "sigmaUid";
    private static final String PROP_SIGMA_DRM_URL = "sigmaDrmUrl";
    private static final String PROP_SIGMA_CLIENT_ID = "sigmaClientId";
    private static final String PROP_SIGMA_AUTHEN_TOKEN = "sigmaAuthenToken";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected ReactExoplayerView createViewInstance(ThemedReactContext themedReactContext) {
        return new ReactExoplayerView(themedReactContext);
    }

    @Override
    public void onDropViewInstance(ReactExoplayerView view) {
        view.cleanUpResources();
    }

    @Override
    public @Nullable Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
        for (String event : VideoEventEmitter.Events) {
            builder.put(event, MapBuilder.of("registrationName", event));
        }
        return builder.build();
    }

    @Override
    public @Nullable Map<String, Object> getExportedViewConstants() {
        return MapBuilder.<String, Object>of(
                "ScaleNone", Integer.toString(ResizeMode.RESIZE_MODE_FIT),
                "ScaleAspectFit", Integer.toString(ResizeMode.RESIZE_MODE_FIT),
                "ScaleToFill", Integer.toString(ResizeMode.RESIZE_MODE_FILL),
                "ScaleAspectFill", Integer.toString(ResizeMode.RESIZE_MODE_CENTER_CROP)
        );
    }

    @ReactProp(name = PROP_SRC)
    public void setSrc(final ReactExoplayerView videoView, @Nullable ReadableMap src) {
        Context context = videoView.getContext().getApplicationContext();
        String uriString = src.hasKey(PROP_SRC_URI) ? src.getString(PROP_SRC_URI) : null;
        String extension = src.hasKey(PROP_SRC_TYPE) ? src.getString(PROP_SRC_TYPE) : null;

        if (TextUtils.isEmpty(uriString)) {
            return;
        }

        if (startsWithValidScheme(uriString)) {
            Uri srcUri = Uri.parse(uriString);

            if (srcUri != null) {
                videoView.setSrc(srcUri, extension);
            }
        } else {
            int identifier = context.getResources().getIdentifier(
                uriString,
                "drawable",
                context.getPackageName()
            );
            if (identifier == 0) {
                identifier = context.getResources().getIdentifier(
                    uriString,
                    "raw",
                    context.getPackageName()
                );
            }
            if (identifier > 0) {
                Uri srcUri = RawResourceDataSource.buildRawResourceUri(identifier);
                if (srcUri != null) {
                    videoView.setRawSrc(srcUri, extension);
                }
            }
        }
    }

    @ReactProp(name = PROP_RESIZE_MODE)
    public void setResizeMode(final ReactExoplayerView videoView, final String resizeModeOrdinalString) {
        videoView.setResizeModeModifier(convertToIntDef(resizeModeOrdinalString));
    }

    @ReactProp(name = PROP_REPEAT, defaultBoolean = false)
    public void setRepeat(final ReactExoplayerView videoView, final boolean repeat) {
        videoView.setRepeatModifier(repeat);
    }

    @ReactProp(name = PROP_PAUSED, defaultBoolean = false)
    public void setPaused(final ReactExoplayerView videoView, final boolean paused) {
        videoView.setPausedModifier(paused);
    }

    @ReactProp(name = PROP_MUTED, defaultBoolean = false)
    public void setMuted(final ReactExoplayerView videoView, final boolean muted) {
        videoView.setMutedModifier(muted);
    }

    @ReactProp(name = PROP_VOLUME, defaultFloat = 1.0f)
    public void setVolume(final ReactExoplayerView videoView, final float volume) {
        videoView.setVolumeModifier(volume);
    }

    @ReactProp(name = PROP_PROGRESS_UPDATE_INTERVAL, defaultFloat = 250.0f)
    public void setProgressUpdateInterval(final ReactExoplayerView videoView, final float progressUpdateInterval) {
        videoView.setProgressUpdateInterval(progressUpdateInterval);
    }

    @ReactProp(name = PROP_SEEK)
    public void setSeek(final ReactExoplayerView videoView, final float seek) {
        videoView.seekTo(Math.round(seek * 1000f));
    }

    @ReactProp(name = PROP_SELECTED_VIDEO_TRACK)
    public void setSelectedVideoTrack(final ReactExoplayerView videoView, final float index) {
//        if (index < videoView.getNumberTrack(DemoPlayer.TYPE_VIDEO))
            videoView.setSelectedTrack(C.TRACK_TYPE_VIDEO,Math.round(index));
    }

    @ReactProp(name = PROP_SELECTED_AUDIO_TRACK)
    public void setSelectedAudioTrack(final ReactExoplayerView videoView, final float index) {
//        if (index < videoView.getNumberTrack(DemoPlayer.TYPE_AUDIO))
            videoView.setSelectedTrack(C.TRACK_TYPE_AUDIO,Math.round(index));
    }

    @ReactProp(name = PROP_SELECTED_TEXT_TRACK)
    public void setSelectedTextTrack(final ReactExoplayerView videoView, final float index) {
//        if (index < videoView.getNumberTrack(DemoPlayer.TYPE_TEXT))
            videoView.setSelectedTrack(C.TRACK_TYPE_TEXT,Math.round(index));
    }

    @ReactProp(name = PROP_RATE)
    public void setRate(final ReactExoplayerView videoView, final float rate) {
        videoView.setRateModifier(rate);
    }

    @ReactProp(name = PROP_PLAY_IN_BACKGROUND, defaultBoolean = false)
    public void setPlayInBackground(final ReactExoplayerView videoView, final boolean playInBackground) {
        videoView.setPlayInBackground(playInBackground);
    }

    @ReactProp(name = PROP_DISABLE_FOCUS, defaultBoolean = false)
    public void setDisableFocus(final ReactExoplayerView videoView, final boolean disableFocus) {
        videoView.setDisableFocus(disableFocus);
    }

    private boolean startsWithValidScheme(String uriString) {
        return uriString.startsWith("http://")
                || uriString.startsWith("https://")
                || uriString.startsWith("content://")
                || uriString.startsWith("file://")
                || uriString.startsWith("asset://");
    }

    private @ResizeMode.Mode int convertToIntDef(String resizeModeOrdinalString) {
        if (!TextUtils.isEmpty(resizeModeOrdinalString)) {
            int resizeModeOrdinal = Integer.parseInt(resizeModeOrdinalString);
            return ResizeMode.toResizeMode(resizeModeOrdinal);
        }
        return ResizeMode.RESIZE_MODE_FIT;
    }

    @ReactProp(name = PROP_USE_PEER)
    public void setUsePeer(final ReactExoplayerView videoView,final boolean usePeer){
        videoView.setUsePeer(usePeer);
    }

    @ReactProp(name = PROP_SIGMA_UID)
    public void setSigmaUid(final ReactExoplayerView videoView,final String uid){
        videoView.setSigmaUid(uid);
    }

    @ReactProp(name = PROP_SIGMA_CLIENT_ID)
    public void setSigmaClientId(final ReactExoplayerView videoView,final String clientId){
        videoView.setSigmaClientId(clientId);
    }

    @ReactProp(name = PROP_SIGMA_AUTHEN_TOKEN)
    public void setSigmaAuthenToken(final ReactExoplayerView videoView,final String token){
        videoView.setSigmaAuthenToken(token);
    }

    @ReactProp(name = PROP_SIGMA_DRM_URL)
    public void setSigmaDrmUrl(final ReactExoplayerView videoView,final String drmUrlJson){
        Vector<String> list = new Vector<String>();
        try {
            JSONArray jsonArray = new JSONArray(drmUrlJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                String url = jsonArray.getString(i);
                list.add(url);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        videoView.setSigmaDrmUrl(list);
    }
}
