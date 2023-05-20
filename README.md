# msa-rms-platform
> サービスに依らないRMSの基盤的な仕組みを提供するリポジトリ

## repository内の構成
msa-rms-platformはMavenのマルチモジュールによるmonorepoになっている。このmonorepoの内容は次のとおり。

|repository|内容|
|----------|----|
|[plarform-core](./platform-core/)|処理形態に依らずアプリケーションで必要となる機能を実装した共通ライブラリ|
|[plarform-fw](./platform-core/)| バックエンドアプリ間で共通となる制御構造や流れを実装したバックエンドアプリのFW的ライブラリ|
|[platform-fw-test-lib](./platform-fw-test-lib/)|バックエンドサービスの単体テストがしやすいように[plarform-fw](./platform-core/)のスタブなどを実装したテストライブラリ|
|[test-lib](./test-lib/)|junit5やAssertJの共通機能を実装した共通テストライブラリ|

