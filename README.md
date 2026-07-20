# [Unofficial] Sftpgo For Android

[English](./README.md) [简体中文](./README_zh.md)

Adapt a famous open source project [sftpgo](https://github.com/drakkan/sftpgo) to Android, for replacing my another repo [LeafSFTP](https://github.com/Leaf-Oct/LeafSFTP)

## Motivation 

I have always hoped to use an Android device as a NAS, as a file storage server, or to share files between multiple devices, but I have been struggling with the lack of suitable applications and protocols to achieve this.

Even if there are some, such as [ftpshare](https://github.com/ghmxr/ftpshare) or [droid-sftp](https://github.com/haakonleg/droid-sftp), they haven't been updated for a long time and cannot be adapted to the latest Android versions.

Therefore I developed [LeafSFTP](https://github.com/Leaf-Oct/LeafSFTP), using Apache MINA to work as FTP and SFTP server.

But this is a product of my student days and is not very satisfactory. Its stability also needs to be improved. There are more new features, but I have no time to update them.

SFTP is a very excellent project, but unfortunately it can only be used on Windows, Linux, and other desktop or server OS. To make up for this regret, I took action.

![](img/所以我出手了.webp)

## Project Status

Testing...

## Project Structure

sftpgo-android

├── app/	Android app code

├── build.gradle 

├── gradle/

├── gradle.properties

├── gradlew

├── gradlew.bat

├── img/	save README images

├── README.md

├── settings.gradle

└── sftpgo-2.7.0/ sftpgo source code

## What changed in SFTPGO? 

The main function of sftpgo locates in `main.go`, with a simple code.

```go
// main.go
func main() {
	cmd.Execute()
}
```

According to its performence and dependency in `go.mod`, we can know that it used cobra. Real entrance is in`internal/cmd/root.go`

```go
//internal/cmd/root.go
func Execute() {
	if err := rootCmd.Execute(); err != nil {
		fmt.Println(err)
		os.Exit(1)
	}
}
```

To run sftpgo in linux, generally, we must specify the path of resource, config and log. For example, 

```bash
sftpgo serve -c /path/to/conf -l /path/to/log
```

In Android, we must change this path to private space of app, which is `~/Android/data/{package name}/files/`. Therefore, parameters should be added to entrance of program.

So I append new boot parameters in `internal/cmd/root.go` to receive config path and log path.

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

In `main.go`, add 2 function. Separately use to launch service and stop service. The initial must be upper case, or it can't be see as public function, uncallable.

Launch function has 2 String parameters, receive when called by Java.

```go
//main.go
func SftpgoStart(config_path string, log_path string) {
	cmd.ExecuteWithArgs(config_path, log_path)
}

func SftpgoStop() {
	os.Exit(0)
}
```

## How did I realize it?

After modifying sftpgo code, use gomobile to build it to Android module.

First install gomobile and gobind.

```bash
go install golang.org/x/mobile/cmd/gomobile@latest
go install golang.org/x/mobile/cmd/gobind@latest
```

Execute the command in sftpgo source code dir.

```bash
gomobile init
```

Set environment variable to Android SDK dir. Note that you should have downloaded NDK.

```bash
export ANDROID_HOME=/opt/AndroidSDK
```

Use gomobile to produce aar.

-target Point out the target platform is Android (not iOS), and the architecture is arm64. No specify architecture is OK, but it will generate a universal module, integrating arm v7, arm v8, x86, amd64, considerable!

-androidapi Point out the lowest supported Android API version. It would be best to keep the same as the version in build.gradle

-o Output module name

-v Show detail

```bash
gomobile bind -target android/arm64 -androidapi 30 -o sftpgo.aar -v -ldflags "-s -w"
```

When starting compile, it will report `gomobile: binding "main" package (github.com/drakkan/sftpgo/v2) is not supported`. The reason is that module built for Android can't contain main package. Just change the package name in `main.go`. The main function can be removed, or kept. Anyway, it won't be called.

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

If it still report `unable to import bind: no Go package in golang.org/x/mobile/bind`, then execute:

```bash
go get golang.org/x/mobile/bind
```

Finally, get sftpgo.aar and sftpgo-sources.jar

## TODO

- Support more language. At least Chinese and English.
- Run with Root or Shizuku, acquire higher permission to read and write files. 
- Allow customize sftpgo boot parameter.
- Material Theme