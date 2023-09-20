# unity-mobile-notification-sample
Unity + Mobile Notifications パッケージでローカル通知を行う最小構成プロジェクト。  
Unity は 2021.3.29f1、Mobile Notifications は 2.0.2 で確認。  
Android 8 以上と iOS 12 以上に対応。  
動作確認する場合はそのままではビルドできないのでアプリ ID などを必要に応じて設定すること。  

- 通知送信まで
    - https://github.com/kyredyrf/unity-mobile-notification-sample/tree/main
- 通知音差し替え
    - https://github.com/kyredyrf/unity-mobile-notification-sample/tree/feature/sound-replace
    - Assets/Plugins/Android/Sample.androidlib/res/raw 下に sample_notify.wav の名前で音声ファイルを置いてビルドすると通知音がその音に差し替わる
