using System;
using Unity.Notifications.Android;
using UnityEngine;
using UnityEngine.UI;

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

        notifyButton.onClick.AddListener(() =>
        {
            var notification = new AndroidNotification()
            {
                Title = "通知タイトル",
                Text = "通知本文",
                FireTime = DateTimeOffset.Now.DateTime + TimeSpan.FromSeconds(10.0),
            };
            AndroidNotificationCenter.SendNotification(notification, AndroidChannelId);
        });
#endif
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}
