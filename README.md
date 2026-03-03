# Sftpgo For Android

将知名开源项目[sftpgo](https://github.com/drakkan/sftpgo)适配到安卓系统上，以便取代我的另一个项目[LeafSFTP](https://github.com/Leaf-Oct/LeafSFTP)

Adapt a famous open source project [sftpgo](https://github.com/drakkan/sftpgo) to Android, for replacing my another repo [LeafSFTP](https://github.com/Leaf-Oct/LeafSFTP)

## Motivation 动机

一直以来，我都希望能够将安卓设备作为nas，作为文件存储服务器，或者多设备之间共享文件，但苦于没有合适的应用和协议来实现这一需求。

I have always hoped to use an Android device as a NAS, as a file storage server, or to share files between multiple devices, but I have been struggling with the lack of suitable applications and protocols to achieve this.

就算有，比如说[ftpshare](https://github.com/ghmxr/ftpshare)，或者[droid-sftp](https://github.com/haakonleg/droid-sftp)，也都许久未更新，不能适配最新的安卓版本。

Even if there are some, such as [ftpshare](https://github.com/ghmxr/ftpshare) or [droid-sftp](https://github.com/haakonleg/droid-sftp), they haven't been updated for a long time and cannot be adapted to the latest Android versions.

因此我开发了[LeafSFTP](https://github.com/Leaf-Oct/LeafSFTP)，利用了Apache MINA的库实现FTP和SFTP服务端。

Therefore I developed [LeafSFTP](https://github.com/Leaf-Oct/LeafSFTP), using Apache MINA to work as FTP and SFTP server.

但这是我学生时代的产物，并不怎么令人满意。以及其稳定性还有待提升。更多的新功能，我已无暇去更新。

But this is a product of my student days and is not very satisfactory. Its stability also needs to be improved. There are more new features, but I have no time to update them.

SFTP是非常优秀的项目，但可惜只能用在windows, linux等其他桌面端操作系统。为了弥补这一遗憾，所以，我出手了。

SFTP is a very excellent project, but unfortunately it can only be used on Windows, Linux, and other desktop or server OS. To make up for this regret, I took action.

![](img/所以我出手了.webp)

## Project Status 项目状态

开发中...

Developing...

## Project Structure 项目结构

sftpgo-android

├── app/	安卓项目代码 Android app code

├── build.gradle 

├── gradle/

├── gradle.properties

├── gradlew

├── gradlew.bat

├── img/	存放README图片 save README images

├── README.md

├── settings.gradle

└── sftpgo-2.7.0/ sftpgo源码 sftpgo source code

## What changed in SFTPGO? 对SFTPGO做了什么修改

sftpgo的主函数位于`main.go`中，只有简单的一句。

```go
// main.go
func main() {
	cmd.Execute()
}
```

根据该表现以及`go.mod`中的依赖项，看的出来其使用了cobra。真正的程序入口在`internal/cmd/root.go`中

```go
//internal/cmd/root.go
func Execute() {
	if err := rootCmd.Execute(); err != nil {
		fmt.Println(err)
		os.Exit(1)
	}
}
```

在linux上运行sftpgo时，一般需要指定资源路径、配置路径，以及日志路径。例如

```bash
sftpgo serve -c /path/to/conf -l /path/to/log
```

在安卓上，则需要将这里的路径指定到app的内部目录中，即`~/Android/data/{package name}/files/`。故需要在程序的入口处加参数。

因此我在`internal/cmd/root.go`里加了一个新启动函数，接收配置路径和日志路径

```go
//internal/cmd/root.go
func ExecuteWithArgs(config_path string, log_path string) {
	rootCmd.SetArgs([]string{"serve", "-c", config_path, "-l", log_path})
	if err := rootCmd.Execute(); err != nil {
		fmt.Println(err)
		os.Exit(1)
	}
}
```

在`main.go`中，新增两个函数，分别用于启动服务和停止服务。首字母要大写，否则无法被视为public的函数，无法调用。

启动的函数留两个字符串参数，给java调用时传入

```go
//main.go
func SftpgoStart(config_path string, log_path string) {
	cmd.ExecuteWithArgs(config_path, log_path)
}

func SftpgoStop() {
	os.Exit(0)
}
```

## How did I realize it? 我如何实现适配

修改完sftpgo的代码后，利用gomobile编译为安卓模块。

先安装gomobile和gobind

```bash
go install golang.org/x/mobile/cmd/gomobile@latest
go install golang.org/x/mobile/cmd/gobind@latest
```

在sftpgo代码目录下面执行

```bash
gomobile init
```

设置环境变量，指向安卓SDK目录。注意需要下载好NDK

```bash
export ANDROID_HOME=/opt/AndroidSDK
```

用gomobile编译aar。

-target 指明目标平台上安卓，指令集是arm64。不指定指令集也可以，会生产一个通用的模块，集成了arm, arm64, x86, amd64，非常大。

-androidapi 指明支持的最低安卓API版本，建议和build.gradle中的保持一致

-o 输出的模块名

-v 查看详细信息

```bash
gomobile bind -target android/arm64 -androidapi 24 -o sftpgo.aar -v
```

开始编译的时候会报`gomobile: binding "main" package (github.com/drakkan/sftpgo/v2) is not supported`。原因是编译给安卓的模块，不能有main包。把`main.go`中的包名改一改就行。主函数可以删掉，也可以保留，反正不会调用。

```go
//main.go
//package main
package sftpgo_android
import (
	"os"
	"github.com/drakkan/sftpgo/v2/internal/cmd"
)
func main() {
	cmd.Execute()
}
func SftpgoStart(config_path string, log_path string) {
	cmd.ExecuteWithArgs(config_path, log_path)
}
func SftpgoStop() {
	os.Exit(0)
}
```

如果还报错`unable to import bind: no Go package in golang.org/x/mobile/bind`，就根据提示执行

```bash
go get golang.org/x/mobile/bind
```

最后得到sftpgo.aar和sftpgo-sources.jar