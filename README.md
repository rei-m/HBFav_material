HBFav Material
========

はてなブックマークのお気に入りをTwitterのタイムラインみたいに読めるアプリです。iOSの[HBFav](https://github.com/naoya/HBFav2)のAndroid版となります。  
このアプリはJetBrainsが開発している言語の[Kotlin](https://kotlinlang.org/)を学習した結果をアウトプットするために作成しました。ふることりん。

## Contents
- お気に入りのユーザーのブックマークの閲覧
- 自分のブックマークの閲覧
- ホッテントリの閲覧
- 新着エントリーの閲覧
- ブックマークの追加、更新、削除（要OAuth認証）
- 他、comming soon。改善希望などはissueに書いておけば対応します。

## Environment
- 開発環境 : Android Studio 1.5.1
- 開発言語 : Kotlin beta-3595

## Build
- Android StudioにKotlinの開発用プラグインをインストールしてください。
  - 参考 : https://kotlinlang.org/docs/tutorials/getting-started.html
- プロジェクトをcloneした上で `/app/src/main/assets/hatena_dummy.json` をコピーして `hatena.json` を同じディレクトリに作成してください。OAuth認証用のキーを設定するファイルです。
  - OAuthの確認までする場合は、はてな側でアプリケーションを作成した上でご自身のアプリケーションのキーを設定してください。
- 上記まで終わったらAndroid Studioでプロジェクトを開いてビルドしてください。

## Licence

[MIT](https://github.com/rei-m/HBFav_material/blob/master/LICENCE.txt)

## Author

[rei-m](https://github.com/rei-m)
