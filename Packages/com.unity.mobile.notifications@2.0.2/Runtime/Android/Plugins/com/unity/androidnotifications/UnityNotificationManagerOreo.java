package com.unity.androidnotifications;

import android.app.Activity;
import android.app.NotificationChannel;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.unity3d.player.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UnityNotificationManagerOreo extends UnityNotificationManager {
    final String TAG = "sample";

    public UnityNotificationManagerOreo(Context context, Activity activity) {
        super(context, activity);
    }

    @Override
    public void registerNotificationChannel(
            String id,
            String name,
            int importance,
            String description,
            boolean enableLights,
            boolean enableVibration,
            boolean canBypassDnd,
            boolean canShowBadge,
            long[] vibrationPattern,
            int lockscreenVisibility) {
        assert Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setDescription(description);
        channel.enableLights(enableLights);
        channel.enableVibration(enableVibration);
        channel.setBypassDnd(canBypassDnd);
        channel.setShowBadge(canShowBadge);
        channel.setVibrationPattern(vibrationPattern);
        channel.setLockscreenVisibility(lockscreenVisibility);

        int soundId = R.raw.sample_notify;
        setSound(channel, soundId);
        exportSound(soundId);

        getNotificationManager().createNotificationChannel(channel);
    }

    private void setSound(NotificationChannel channel, int soundId) {
        // 通知音差し替え
        StringBuilder sb = new StringBuilder()
            .append(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .append(File.pathSeparator)
            .append(File.separator)
            .append(File.separator)
            .append(mContext.getPackageName())
            .append(File.separator)
            .append(mContext.getResources().getResourceTypeName(soundId))
            .append(File.separator)
            .append(mContext.getResources().getResourceEntryName(soundId));
        Uri uri = Uri.parse(sb.toString());
        AudioAttributes attributes = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build();
        channel.setSound(uri, attributes);
    }

    private void exportSound(int soundId) {
        // 通知音コピー
        try {
            File dir = new File(mContext.getExternalMediaDirs()[0], Environment.DIRECTORY_NOTIFICATIONS);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            InputStream inputStream = mContext.getResources().openRawResource(soundId);
            String name = mContext.getResources().getResourceEntryName(soundId) + ".wav";
            Log.i(TAG, "export " + name + " to " + dir.toString() + ".");

            File path = new File(dir.toString() + File.separator + name);
            FileOutputStream outputStream = new FileOutputStream(path, false);
            int DEFAULT_BUFFER_SIZE = 1024 * 4;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int size = 0;
            while ((size = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, size);
            }
            inputStream.close();
            outputStream.close();

            MediaScannerConnection.scanFile(
                mContext,
                new String[] { path.toString() },
                new String[] { "audio/wav" },
                (String scannedPath, Uri scannedUri) -> {
                });
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    protected static NotificationChannelWrapper getOreoNotificationChannel(Context context, String id) {
        assert Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

        List<NotificationChannelWrapper> channelList = new ArrayList<NotificationChannelWrapper>();

        for (NotificationChannel ch : getNotificationManager(context).getNotificationChannels()) {
            if (ch.getId() == id)
                return notificationChannelToWrapper(ch);
        }
        return null;
    }

    protected static NotificationChannelWrapper notificationChannelToWrapper(NotificationChannel channel) {
        NotificationChannelWrapper wrapper = new NotificationChannelWrapper();

        wrapper.id = channel.getId();
        wrapper.name = channel.getName().toString();
        wrapper.importance = channel.getImportance();
        wrapper.description = channel.getDescription();
        wrapper.enableLights = channel.shouldShowLights();
        wrapper.enableVibration = channel.shouldVibrate();
        wrapper.canBypassDnd = channel.canBypassDnd();
        wrapper.canShowBadge = channel.canShowBadge();
        wrapper.vibrationPattern = channel.getVibrationPattern();
        wrapper.lockscreenVisibility = channel.getLockscreenVisibility();

        return wrapper;
    }

    @Override
    public void deleteNotificationChannel(String id) {
        assert Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

        getNotificationManager().deleteNotificationChannel(id);
    }

    @Override
    public NotificationChannelWrapper[] getNotificationChannels() {
        assert Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

        List<NotificationChannelWrapper> channelList = new ArrayList<NotificationChannelWrapper>();

        for (NotificationChannel ch : getNotificationManager().getNotificationChannels()) {
            channelList.add(notificationChannelToWrapper(ch));
        }

        return channelList.toArray(new NotificationChannelWrapper[channelList.size()]);
    }
}
