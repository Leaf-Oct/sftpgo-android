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

todo

## How did I realize it? 我如何实现适配

todo