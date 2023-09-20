using System;
using UnityEngine;
using UnityEngine.UI;

#if !UNITY_EDITOR && UNITY_ANDROID
using Unity.Notifications.Android;
#endif

#if !UNITY_EDITOR && UNITY_IOS
using Unity.Notifications.iOS;
#endif

public class SampleSceneRoot : MonoBehaviour
{
    [SerializeField] Button notifyButton;

    const string AndroidChannelId = "default";

    // Start is called before the first frame update
    void Start()
    {
#if !UNITY_EDITOR && UNITY_ANDROID
        var channel = new AndroidNotificationChannel(
            id: AndroidChannelId,
            name: "デフォルト",
            description: "すべての通知を受信します。",
            Importance.Default);
        AndroidNotificationCenter.RegisterNotificationChannel(channel);
#endif

        notifyButton.onClick.AddListener(() =>
        {
            ScheduleNotification(
                title: "通知タイトル",
                body: "通知本文",
                fireTime: DateTimeOffset.Now.DateTime + TimeSpan.FromSeconds(10.0));
        });
    }

    void ScheduleNotification(string title, string body, DateTimeOffset fireTime)
    {
#if !UNITY_EDITOR && UNITY_ANDROID
        var notification = new AndroidNotification()
        {
            Title = title,
            Text = body,
            FireTime = fireTime.DateTime,
        };
        AndroidNotificationCenter.SendNotification(notification, AndroidChannelId);
#endif

#if !UNITY_EDITOR && UNITY_IOS
        var trigger = new iOSNotificationTimeIntervalTrigger()
        {
            TimeInterval = fireTime - DateTimeOffset.Now.DateTime,
            Repeats = false,
        };

        var notification = new iOSNotification()
        {
            Title = title,
            Body = body,
            Trigger = trigger,
        };
        iOSNotificationCenter.ScheduleNotification(notification);
#endif
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}
