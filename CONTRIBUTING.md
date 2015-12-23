## Contribute
- Android StudioにKotlinの開発用プラグインをインストールしてください。
  - 参考 : https://kotlinlang.org/docs/tutorials/getting-started.html
- プロジェクトをcloneした上で `/app/src/main/assets/hatena_dummy.json` をコピーして `hatena.json` を同じディレクトリに作成してください。OAuth認証用のキーを設定するファイルです。
  - OAuthの確認までする場合は、はてな側でアプリケーションを作成した上でご自身のアプリケーションのキーを設定してください。
- 上記まで終わったらAndroid Studioでプロジェクトを開いてビルドしてください。

## 構成

```
me.rei_m.hbfavmaterial
├─ activities : Activity
├─ entities : Entity
└─ events : EventBusでやり取りするEventクラス
    ├─ ui : UI系のイベント。なんちゃらclickedとかなんちゃらselectedとか
    ├─ network : 通信系のイベント
├─ exeptions : カスタムExeption
├─ extensions : Kotlinの拡張関数
├─ fragments : Fragment
├─ managers : ModelLocatorとかRealmを管理するやつとか
├─ models : ドメインModel。ロジックはここに収めていく
├─ network : 通信系のモジュール
├─ utils : Utitliy群。なるべく増やさないようにする
└─ views
    ├─ adapters : アダプター
    └─ widgets : カスタムView
```

## Viewの命名規約
- レイアウト内のViewはファイル間で重複が起きないようにプレフィックスにファイル名をつけてください

```
<me.rei_m.hbfavmaterial.views.widgets.bookmark.BookmarkCountTextView
    android:id="@+id/fragment_bookmark_text_bookmark_count"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@drawable/bg_layout_pressed"
    android:paddingBottom="@dimen/margin"
    android:paddingLeft="@dimen/margin_outline"
    android:paddingRight="@dimen/margin_outline"
    android:paddingTop="@dimen/margin" />
```

## ほか
- TestやらCodeStyleやらは全然できていないので少しずつ整理してきます
